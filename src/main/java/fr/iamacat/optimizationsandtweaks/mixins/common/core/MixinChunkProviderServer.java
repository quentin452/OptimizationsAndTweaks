package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer implements IChunkProvider {
        @Shadow
        private static final Logger logger = LogManager.getLogger();
        /**
         * used by unload100OldestChunks to iterate the loadedChunkHashMap for unload (underlying assumption, first in,
         * first out)
         */
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


    // Remove all EntityItem during initial chunk generation
    // to prevent lags caused by a large amount of EntityItem on mod packs.
    // todo remove all loggings/try and catch when i am sure that this method is work well
    // todo fix seem to be doesn't work
    @Unique
    private int optimizationsAndTweaks$clearEntityItemsDelay = 60;

    @Unique
    private void optimizationsAndTweaks$clearEntityItems(Chunk chunk, int x, int z) {
        if (chunk == null || !chunk.isTerrainPopulated || chunk.worldObj == null) {
            return;
        }

        World world = chunk.worldObj;
        int chunkMinX = x * 16;
        int chunkMinZ = z * 16;
        int chunkMaxX = chunkMinX + 16;
        int chunkMaxZ = chunkMinZ + 16;

        List<Entity> entitiesToRemove = new ArrayList<>();

        for (Object obj : world.loadedEntityList) {
            if (obj instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) obj;

                int entityX = MathHelper.floor_double(entityItem.posX);
                int entityZ = MathHelper.floor_double(entityItem.posZ);

                if (entityX >= chunkMinX && entityX < chunkMaxX && entityZ >= chunkMinZ && entityZ < chunkMaxZ) {
                    entitiesToRemove.add(entityItem);
                    System.out.println("EntityItem marked for removal by optimizationsAndTweaks$clearEntityItems: " + entityItem);
                }
            }
        }

        if (optimizationsAndTweaks$clearEntityItemsDelay <= 0) {
            for (Entity entity : entitiesToRemove) {
                world.removeEntity(entity);
                System.out.println("EntityItem removed 1 by optimizationsAndTweaks$clearEntityItems: ");
            }
            System.out.println("EntityItem removed 2 by optimizationsAndTweaks$clearEntityItems: ");

            optimizationsAndTweaks$clearEntityItemsDelay = 60; // 3 seconds
        } else {
            optimizationsAndTweaks$clearEntityItemsDelay--;
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public Chunk originalLoadChunk(int p_73158_1_, int p_73158_2_)
    {
        long k = ChunkCoordIntPair.chunkXZ2Int(p_73158_1_, p_73158_2_);
        this.chunksToUnload.remove(k);
        Chunk chunk = (Chunk)this.loadedChunkHashMap.getValueByKey(k);

        if (chunk == null)
        {
            boolean added = loadingChunks.add(k);
            if (!added)
            {
                cpw.mods.fml.common.FMLLog.bigWarning("There is an attempt to load a chunk (%d,%d) in dimension %d that is already being loaded. This will cause weird chunk breakages.", p_73158_1_, p_73158_2_, worldObj.provider.dimensionId);
            }
            chunk = ForgeChunkManager.fetchDormantChunk(k, this.worldObj);
            if (chunk == null)
            {
                chunk = this.safeLoadChunk(p_73158_1_, p_73158_2_);
            }

            if (chunk == null)
            {
                if (this.currentChunkProvider == null)
                {
                    chunk = this.defaultEmptyChunk;
                }
                else
                {
                    try
                    {
                        chunk = this.currentChunkProvider.provideChunk(p_73158_1_, p_73158_2_);
                    }
                    catch (Throwable throwable)
                    {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                        crashreportcategory.addCrashSection("Location", String.format("%d,%d", p_73158_1_, p_73158_2_));
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
            optimizationsAndTweaks$clearEntityItems(chunk,p_73158_1_, p_73158_2_);
        }
        return chunk;
    }
    @Shadow
    private Chunk safeLoadChunk(int p_73239_1_, int p_73239_2_)
    {
        if (this.currentChunkLoader == null)
        {
            return null;
        }
        else
        {
            try
            {
                Chunk chunk = this.currentChunkLoader.loadChunk(this.worldObj, p_73239_1_, p_73239_2_);

                if (chunk != null)
                {
                    chunk.lastSaveTime = this.worldObj.getTotalWorldTime();

                    if (this.currentChunkProvider != null)
                    {
                        this.currentChunkProvider.recreateStructures(p_73239_1_, p_73239_2_);
                    }
                }

                return chunk;
            }
            catch (Exception exception)
            {
                logger.error("Couldn't load chunk", exception);
                return null;
            }
        }
    }
}
