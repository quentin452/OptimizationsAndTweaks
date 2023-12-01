package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.chunkio.ChunkIOExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer implements IChunkProvider {

    @Shadow
    public LongHashMap loadedChunkHashMap = new LongHashMap();
    @Shadow
    private static final Logger logger = LogManager.getLogger();

    private ChunkProviderServer chunkProviderServer;

    @Unique
    private Map<Object, Object> optimizationsAndTweaks$chunksToUnload = new Object2ObjectHashMap<>();

    @Shadow
    private Chunk defaultEmptyChunk;
    @Shadow
    public IChunkProvider currentChunkProvider;
    @Shadow
    public IChunkLoader currentChunkLoader;
    /**
     * used by unload100OldestChunks to iterate the loadedChunkHashMap for unload (underlying assumption, first in,
     * first out)
     */
    @Shadow
    private Set chunksToUnload = Collections.newSetFromMap(new ConcurrentHashMap());
    /** if this is false, the defaultEmptyChunk will be returned by the provider */
    @Shadow
    public boolean loadChunkOnProvideRequest = true;

    @Shadow
    public List loadedChunks = new ArrayList();
    @Shadow
    public WorldServer worldObj;
    @Shadow
    private Set<Long> loadingChunks = com.google.common.collect.Sets.newHashSet();

    protected MixinChunkProviderServer(ChunkProviderServer chunkProviderServer) {
        this.chunkProviderServer = chunkProviderServer;
    }

    @Shadow
    public Chunk loadChunk(int x, int z) {
        return null;
    }

    /**
     * used by loadChunk, but catches any exceptions if the load fails.
     */
    @Overwrite
    private Chunk safeLoadChunk(int p_73239_1_, int p_73239_2_) {
        if (this.currentChunkLoader == null) {
            return null;
        } else {
            try {
                Chunk chunk = this.currentChunkLoader.loadChunk(this.worldObj, p_73239_1_, p_73239_2_);

                if (chunk != null) {
                    chunk.lastSaveTime = this.worldObj.getTotalWorldTime();

                    if (this.currentChunkProvider != null) {
                        this.currentChunkProvider.recreateStructures(p_73239_1_, p_73239_2_);
                    }
                }

                return chunk;
            } catch (Exception exception) {
                logger.error("Couldn't load chunk", exception);
                return null;
            }
        }
    }

    /**
     * used by saveChunks, but catches any exceptions if the save fails.
     */
    @Overwrite
    private void safeSaveExtraChunkData(Chunk p_73243_1_) {
        if (this.currentChunkLoader != null) {
            try {
                this.currentChunkLoader.saveExtraChunkData(this.worldObj, p_73243_1_);
            } catch (Exception exception) {
                logger.error("Couldn't save entities", exception);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void unloadChunksIfNotNearSpawn(int p_73241_1_, int p_73241_2_) {
        if (this.worldObj.provider.canRespawnHere()
            && DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId)) {
            ChunkCoordinates chunkcoordinates = this.worldObj.getSpawnPoint();
            int k = p_73241_1_ * 16 + 8 - chunkcoordinates.posX;
            int l = p_73241_2_ * 16 + 8 - chunkcoordinates.posZ;
            short short1 = 128;

            if (k < -short1 || k > short1 || l < -short1 || l > short1) {
                this.optimizationsAndTweaks$chunksToUnload
                    .put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(p_73241_1_, p_73241_2_)), null);
            }
        } else {
            this.optimizationsAndTweaks$chunksToUnload
                .put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(p_73241_1_, p_73241_2_)), null);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
        return this.loadedChunkHashMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(p_73149_1_, p_73149_2_));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public Chunk loadChunk(int par1, int par2, Runnable runnable) {
        long k = ChunkCoordIntPair.chunkXZ2Int(par1, par2);
        this.optimizationsAndTweaks$chunksToUnload.remove(Long.valueOf(k));
        Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(k);
        AnvilChunkLoader loader = null;

        if (this.currentChunkLoader instanceof AnvilChunkLoader) {
            loader = (AnvilChunkLoader) this.currentChunkLoader;
        }

        // We can only use the queue for already generated chunks
        if (chunk == null && loader != null && loader.chunkExists(this.worldObj, par1, par2)) {
            if (runnable != null) {
                ChunkIOExecutor.queueChunkLoad(this.worldObj, loader, chunkProviderServer, par1, par2, runnable);
                return null;
            } else {
                chunk = ChunkIOExecutor.syncChunkLoad(this.worldObj, loader, chunkProviderServer, par1, par2);
            }
        } else if (chunk == null) {
            chunk = this.originalLoadChunk(par1, par2);
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null) {
            runnable.run();
        }

        return chunk;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public Chunk originalLoadChunk(int p_73158_1_, int p_73158_2_) {
        long k = ChunkCoordIntPair.chunkXZ2Int(p_73158_1_, p_73158_2_);
        this.optimizationsAndTweaks$chunksToUnload.remove(Long.valueOf(k));
        Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(k);

        if (chunk == null) {
            boolean added = loadingChunks.add(k);
            if (!added) {
                cpw.mods.fml.common.FMLLog.bigWarning(
                    "There is an attempt to load a chunk (%d,%d) in dimension %d that is already being loaded. This will cause weird chunk breakages.",
                    p_73158_1_,
                    p_73158_2_,
                    worldObj.provider.dimensionId);
            }
            chunk = ForgeChunkManager.fetchDormantChunk(k, this.worldObj);
            if (chunk == null) {
                chunk = this.safeLoadChunk(p_73158_1_, p_73158_2_);
            }

            if (chunk == null) {
                if (this.currentChunkProvider == null) {
                    chunk = this.defaultEmptyChunk;
                } else {
                    try {
                        chunk = this.currentChunkProvider.provideChunk(p_73158_1_, p_73158_2_);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport
                            .makeCrashReport(throwable, "Exception generating new chunk");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                        crashreportcategory.addCrashSection(
                            "Location",
                            String.format(
                                "%d,%d",
                                new Object[] { Integer.valueOf(p_73158_1_), Integer.valueOf(p_73158_2_) }));
                        crashreportcategory.addCrashSection("Position hash", Long.valueOf(k));
                        crashreportcategory.addCrashSection("Generator", this.currentChunkProvider.makeString());
                        throw new ReportedException(crashreport);
                    }
                }
            }

            this.loadedChunkHashMap.add(k, chunk);
            this.loadedChunks.add(chunk);
            loadingChunks.remove(k);
            chunk.onChunkLoad();
            chunk.populateChunk(this, this, p_73158_1_, p_73158_2_);
        }

        return chunk;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean unloadQueuedChunks() {
        if (!this.worldObj.levelSaving) {
            for (ChunkCoordIntPair forced : this.worldObj.getPersistentChunks()
                .keySet()) {
                this.chunksToUnload.remove(ChunkCoordIntPair.chunkXZ2Int(forced.chunkXPos, forced.chunkZPos));
            }

            for (int i = 0; i < 100; ++i) {
                if (!this.chunksToUnload.isEmpty()) {
                    Long olong = (Long) this.chunksToUnload.iterator()
                        .next();
                    Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(olong.longValue());

                    if (chunk != null) {
                        chunk.onChunkUnload();
                        this.safeSaveChunk(chunk);
                        this.safeSaveExtraChunkData(chunk);
                        this.loadedChunks.remove(chunk);
                        ForgeChunkManager
                            .putDormantChunk(ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition), chunk);
                        if (loadedChunks.size() == 0 && ForgeChunkManager.getPersistentChunksFor(this.worldObj)
                            .size() == 0 && !DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId)) {
                            DimensionManager.unloadWorld(this.worldObj.provider.dimensionId);
                            return currentChunkProvider.unloadQueuedChunks();
                        }
                    }

                    this.chunksToUnload.remove(olong);
                    this.loadedChunkHashMap.remove(olong.longValue());
                }
            }

            if (this.currentChunkLoader != null) {
                this.currentChunkLoader.chunkTick();
            }
        }

        return this.currentChunkProvider.unloadQueuedChunks();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Chunk provideChunk(int p_73154_1_, int p_73154_2_) {
        Chunk chunk = (Chunk) this.loadedChunkHashMap
            .getValueByKey(ChunkCoordIntPair.chunkXZ2Int(p_73154_1_, p_73154_2_));
        return chunk == null
            ? (!this.worldObj.findingSpawnPoint && !this.loadChunkOnProvideRequest ? this.defaultEmptyChunk
                : this.loadChunk(p_73154_1_, p_73154_2_))
            : chunk;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public String makeString() {
        return "ServerChunkCache: " + this.loadedChunkHashMap.getNumHashElements()
            + " Drop: "
            + this.optimizationsAndTweaks$chunksToUnload.size();
    }

    @Shadow
    private void safeSaveChunk(Chunk p_73242_1_) {
        if (this.currentChunkLoader != null) {
            try {
                p_73242_1_.lastSaveTime = this.worldObj.getTotalWorldTime();
                this.currentChunkLoader.saveChunk(this.worldObj, p_73242_1_);
            } catch (IOException ioexception) {
                logger.error("Couldn't save chunk", ioexception);
            } catch (MinecraftException minecraftexception) {
                logger
                    .error("Couldn't save chunk; already in use by another instance of Minecraft?", minecraftexception);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getLoadedChunkCount() {
        return this.loadedChunkHashMap.getNumHashElements();
    }

}
