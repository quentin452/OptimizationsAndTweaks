package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.ForgeChunkManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer implements IChunkProvider {

    @Shadow
    private static final Logger logger = LogManager.getLogger();

    @Shadow
    private Set chunksToUnload = Collections.newSetFromMap(new ConcurrentHashMap());
    @Shadow
    private Chunk defaultEmptyChunk;
    @Shadow
    public IChunkProvider currentChunkProvider;
    @Shadow
    public IChunkLoader currentChunkLoader;
    /** if this is false, the defaultEmptyChunk will be returned by the provider */
    @Shadow
    public boolean loadChunkOnProvideRequest = true;
    @Shadow
    public LongHashMap loadedChunkHashMap = new LongHashMap();
    @Shadow
    public List loadedChunks = new ArrayList();
    @Shadow
    public WorldServer worldObj;
    @Shadow
    private Set<Long> loadingChunks = com.google.common.collect.Sets.newHashSet();

    @Shadow
    public Chunk loadChunk(int x, int z) {
        return null;
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "originalLoadChunk", at = @At("HEAD"), remap = false, cancellable = true)
    public void originalLoadChunk(int p_73158_1_, int p_73158_2_, CallbackInfoReturnable<Chunk> cir) {
        if (OptimizationsandTweaksConfig.enableMixinChunkProviderServer) {
            long k = ChunkCoordIntPair.chunkXZ2Int(p_73158_1_, p_73158_2_);
            this.chunksToUnload.remove(k);
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
                            crashreportcategory
                                .addCrashSection("Location", String.format("%d,%d", p_73158_1_, p_73158_2_));
                            crashreportcategory.addCrashSection("Position hash", k);
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

                cir.setReturnValue(chunk);
                cir.cancel();
            }
        }
    }

    /**
     * used by loadChunk, but catches any exceptions if the load fails.
     */
    @Overwrite
    private Chunk safeLoadChunk(int p_73239_1_, int p_73239_2_) {
        if (OptimizationsandTweaksConfig.enableMixinChunkProviderServer) {

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
        return null;
    }

    /**
     * used by saveChunks, but catches any exceptions if the save fails.
     */
    @Overwrite
    private void safeSaveExtraChunkData(Chunk p_73243_1_) {
        if (OptimizationsandTweaksConfig.enableMixinChunkProviderServer) {
            if (this.currentChunkLoader != null) {
                try {
                    this.currentChunkLoader.saveExtraChunkData(this.worldObj, p_73243_1_);
                } catch (Exception exception) {
                    logger.error("Couldn't save entities", exception);
                }
            }
        }
    }
}
