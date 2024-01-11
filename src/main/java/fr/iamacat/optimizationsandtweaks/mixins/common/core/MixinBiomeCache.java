package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.mixins.Classers;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.collections.maps.LongHashMap2;

@Mixin(BiomeCache.class)
public class MixinBiomeCache {

    @Shadow
    private final WorldChunkManager chunkManager;

    @Shadow
    private long lastCleanupTime;

    @Unique
    private LongHashMap2 optimizationsAndTweaks$cacheMap = new LongHashMap2();

    @Shadow
    private List cache = new ArrayList();

    public MixinBiomeCache(WorldChunkManager chunkManager, WorldChunkManager p_i1973_1_) {
        this.chunkManager = chunkManager;
        Classers.Block.chunkManager = p_i1973_1_;
    }

    @Unique
    public Classers.Block optimizationsAndTweaks$getBiomeCacheBlock(int p_76840_1_, int p_76840_2_) {
        p_76840_1_ >>= 4;
        p_76840_2_ >>= 4;
        long k = p_76840_1_ & 4294967295L | (p_76840_2_ & 4294967295L) << 32;
        Classers.Block block = (Classers.Block) this.optimizationsAndTweaks$cacheMap.getValueByKey(k);

        if (block == null) {
            block = new Classers.Block(p_76840_1_, p_76840_2_, chunkManager);
            this.optimizationsAndTweaks$cacheMap.add(k, block);
            this.cache.add(block);
        }

        block.lastAccessTime = MinecraftServer.getSystemTimeMillis();
        return block;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public BiomeGenBase getBiomeGenAt(int p_76837_1_, int p_76837_2_) {
        return this.optimizationsAndTweaks$getBiomeCacheBlock(p_76837_1_, p_76837_2_)
            .getBiomeGenAt(p_76837_1_, p_76837_2_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void cleanupCache() {
        long i = MinecraftServer.getSystemTimeMillis();
        long j = i - this.lastCleanupTime;

        if (j > 7500L || j < 0L) {
            this.lastCleanupTime = i;

            for (int k = 0; k < this.cache.size(); ++k) {
                Classers.Block block = (Classers.Block) this.cache.get(k);
                long l = i - block.lastAccessTime;

                if (l > 30000L || l < 0L) {
                    this.cache.remove(k--);
                    long i1 = (long) block.xPosition & 4294967295L | ((long) block.zPosition & 4294967295L) << 32;
                    this.optimizationsAndTweaks$cacheMap.remove(i1);
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public BiomeGenBase[] getCachedBiomes(int p_76839_1_, int p_76839_2_) {
        return this.optimizationsAndTweaks$getBiomeCacheBlock(p_76839_1_, p_76839_2_).biomes;
    }
}
