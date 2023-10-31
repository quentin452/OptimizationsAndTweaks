package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.storage.ISaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World {
    protected MixinWorldServer(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_, WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }

    /**
     * @author iamacatfr
     * @reason optimize func_147456_g/updateBlocks method
     */
    @Overwrite
    public void func_147456_g() {
        super.func_147456_g();
        for (Object o : this.activeChunkSet) {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) o;
            int k = chunkcoordintpair.chunkXPos * 16;
            int l = chunkcoordintpair.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunk = this.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);
            this.func_147467_a(k, l, chunk);
            this.theProfiler.endStartSection("tickChunk");
            chunk.func_150804_b(false);
            this.theProfiler.endStartSection("thunder");

            if (provider.canDoLightning(chunk) && this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                int randValue = this.rand.nextInt(100000);
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int i1 = this.updateLCG >> 2;
                int j1 = k + (i1 & 15);
                int k1 = l + (i1 >> 8 & 15);
                int l1 = this.getPrecipitationHeight(j1, k1);

                if (randValue == 0 && this.canLightningStrikeAt(j1, l1, k1)) {
                    this.addWeatherEffect(new EntityLightningBolt(this, j1, l1, k1));
                }
            }

            this.theProfiler.endStartSection("iceandsnow");

            if (provider.canDoRainSnowIce(chunk) && this.rand.nextInt(16) == 0) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int i1 = this.updateLCG >> 2;
                int j1 = i1 & 15;
                int k1 = i1 >> 8 & 15;
                int l1 = this.getPrecipitationHeight(j1 + k, k1 + l);
                Block blockBelow = this.getBlock(j1 + k, l1 - 1, k1 + l);

                if (blockBelow == Blocks.water && this.isBlockFreezableNaturally(j1 + k, l1 - 1, k1 + l)) {
                    this.setBlock(j1 + k, l1 - 1, k1 + l, Blocks.ice);
                }

                if (this.isRaining() && this.func_147478_e(j1 + k, l1, k1 + l, true)) {
                    this.setBlock(j1 + k, l1, k1 + l, Blocks.snow_layer);
                }

                if (this.isRaining()) {
                    BiomeGenBase biomegenbase = this.getBiomeGenForCoords(j1 + k, k1 + l);

                    if (biomegenbase.canSpawnLightningBolt()) {
                        blockBelow.fillWithRain(this, j1 + k, l1 - 1, k1 + l);
                    }
                }
            }

            this.theProfiler.endStartSection("tickBlocks");

            for (ExtendedBlockStorage extendedblockstorage : chunk.getBlockStorageArray()) {
                if (extendedblockstorage != null && extendedblockstorage.getNeedsRandomTick()) {
                    for (int i3 = 0; i3 < 3; ++i3) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int i2 = this.updateLCG >> 2;
                        int j2 = i2 & 15;
                        int k2 = i2 >> 8 & 15;
                        int l2 = i2 >> 16 & 15;
                        Block block = extendedblockstorage.getBlockByExtId(j2, l2, k2);

                        if (block.getTickRandomly()) {
                            block.updateTick(this, j2 + k, l2 + extendedblockstorage.getYLocation(), k2 + l, this.rand);
                        }
                    }
                }
            }
        }

        this.theProfiler.endSection();
    }
}
