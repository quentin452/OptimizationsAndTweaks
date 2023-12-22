package fr.iamacat.optimizationsandtweaks.mixins.common.netherlicious;

import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.WorldGeneratorAdv;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(WorldGeneratorAdv.class)
public abstract class MixinWorldGeneratorAdv  extends WorldGenerator {
    @Shadow
    private boolean profiling = false;
    @Shadow
    private int blockcount;
    @Shadow
    private int blockcounttotal = 0;
    @Shadow
    private int callcount = 0;
    @Shadow
    private int fillcount = 0;

    public MixinWorldGeneratorAdv() {
    }
    @Shadow

    public void setProfiling(boolean profiling) {
        this.profiling = profiling;
    }
    @Shadow
    public final void noGen() {
        ++this.callcount;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public final boolean func_76484_a(World worldObj, Random rand, int x, int y, int z) {
        this.blockcount = 0;
        ++this.callcount;
        boolean flag = this.doGeneration(worldObj, rand, x, y, z);
        if (this.profiling && this.blockcount > 0) {
            ++this.fillcount;
        }

        return flag;
    }

    @Shadow
    public abstract boolean doGeneration(World var1, Random var2, int var3, int var4, int var5);


    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected final boolean placeBlock(World worldObj, int x, int y, int z, Block block, int metadata, int mask) {
        optimizationsAndTweaks$incrementBlockCounters();
        Chunk chunk = worldObj.getChunkFromBlockCoords(x, z);

        if (chunk != null && chunk.isChunkLoaded) {
            Block existingBlock = worldObj.getBlock(x, y, z);
            int existingMeta = worldObj.getBlockMetadata(x, y, z);

            if (optimizationsAndTweaks$shouldPlaceBlock(existingBlock, existingMeta, block, metadata, worldObj, x, y, z)) {
                optimizationsAndTweaks$placeNewBlock(worldObj, x, y, z, block, metadata, mask);
                return true;
            }
        }

        return false;
    }
    @Unique
    private boolean optimizationsAndTweaks$shouldPlaceBlock(Block existingBlock, int existingMeta, Block block, int metadata, World world, int x, int y, int z) {
        return (existingBlock != block || existingMeta != metadata) && optimizationsAndTweaks$canPlaceBlock(world, x, y, z);
    }
    @Unique
    private boolean optimizationsAndTweaks$canPlaceBlock(World world, int x, int y, int z) {
        return !world.isAirBlock(x, y, z);
    }
    @Unique
    private boolean optimizationsAndTweaks$isNearbyChunksLoaded(World world, int x, int z) {
        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k <= 1; k++) {
                if (!world.getChunkProvider().chunkExists(x + i, z + k)) {
                    return false;
                }
            }
        }
        return true;
    }
    @Unique
    private void optimizationsAndTweaks$placeNewBlock(World world, int x, int y, int z, Block block, int metadata, int mask) {
        if (!optimizationsAndTweaks$isNearbyChunksLoaded(world, x >> 4, z >> 4)) {
            return;
        }
        if (optimizationsAndTweaks$canPlaceBlock(world, x, y, z)) {
            world.setBlock(x, y, z, block, metadata, mask);
        } else {
            world.setBlock(x, y, z, block, metadata | 0x2, mask);
        }
    }

    @Unique
    private void optimizationsAndTweaks$incrementBlockCounters() {
        ++this.blockcounttotal;
        ++this.blockcount;
    }
}
