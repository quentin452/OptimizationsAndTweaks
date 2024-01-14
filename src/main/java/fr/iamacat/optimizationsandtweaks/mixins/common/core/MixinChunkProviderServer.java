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
