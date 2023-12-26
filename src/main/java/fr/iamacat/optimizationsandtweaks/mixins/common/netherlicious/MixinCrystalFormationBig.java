package fr.iamacat.optimizationsandtweaks.mixins.common.netherlicious;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.CrystalFormationBig;
import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.WorldGeneratorAdv;

@Mixin(CrystalFormationBig.class)
public abstract class MixinCrystalFormationBig extends WorldGeneratorAdv {

    @Shadow
    static final byte[] otherCoordPairs = new byte[] { 2, 0, 0, 1, 2, 1 };
    @Shadow
    private World worldObj;
    @Shadow
    private Block block;
    @Shadow
    private int metaCrystal;
    @Shadow
    private int metaBud;
    int[] crystalbase;
    @Shadow
    int baserange;

    @Unique
    private boolean optimizationsAndTweaks$isNearbyChunksLoaded(World world, int x, int z) {
        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k <= 1; k++) {
                if (!world.getChunkProvider()
                    .chunkExists(x + i, z + k)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void placeRandomBlock(World world, Random random, int i, int j, int k, int mask) {
        if (!optimizationsAndTweaks$isNearbyChunksLoaded(world, i >> 4, k >> 4)) {
            return;
        }

        Block blockAtPos = this.worldObj.getBlock(i, j, k);
        if (blockAtPos != Blocks.bedrock && blockAtPos != this.block
            && blockAtPos != Blocks.nether_brick
            && blockAtPos != Blocks.nether_brick_fence
            && blockAtPos != Blocks.mob_spawner) {
            int var999 = random.nextInt(10);
            if (var999 == 0) {
                this.placeBlock(this.worldObj, i, j, k, this.block, this.metaBud, mask);
            } else {
                this.placeBlock(this.worldObj, i, j, k, this.block, this.metaCrystal, mask);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void setBlock(World world, Random random, int i, int j, int k, int mask) {
        if (!optimizationsAndTweaks$isNearbyChunksLoaded(world, i >> 4, k >> 4)) {
            return;
        }

        Block blockAtPos = this.worldObj.getBlock(i, j, k);
        if (blockAtPos != Blocks.bedrock && blockAtPos != this.block
            && blockAtPos != Blocks.nether_brick
            && blockAtPos != Blocks.nether_brick_fence
            && blockAtPos != Blocks.mob_spawner) {
            this.placeBlock(this.worldObj, i, j, k, this.block, this.metaCrystal, mask);
        }
    }
}
