package fr.iamacat.optimizationsandtweaks.mixins.common.netherlicious;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.CrystalFormationHangingBig;
import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.WorldGeneratorAdv;

@Mixin(CrystalFormationHangingBig.class)
public abstract class MixinCrystalFormationHangingBig extends WorldGeneratorAdv {

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
    @Shadow
    int[] crystalbase;
    @Shadow
    int baserange;

    @Unique
    private boolean optimizationsAndTweaks$isNearbyChunksLoaded(World world, int x, int z) {
        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k <= 1; k++) {
                Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
                if (!chunk.isChunkLoaded) {
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
        if (!optimizationsAndTweaks$gisInvalidBlock(world, i, j, k)) {
            if (!optimizationsAndTweaks$isNearbyChunksLoaded(world, i >> 4, k >> 4)) {
                return;
            }
            Chunk chunk = world.getChunkFromBlockCoords(i, k);
            if (chunk != null && chunk.isChunkLoaded) {
                int counter = Math.abs(i ^ j ^ k);
                if (counter % 10 == 0) {
                    placeBlock(world, i, j, k, block, metaBud, mask);
                } else {
                    placeBlock(world, i, j, k, block, metaCrystal, mask);
                }
            }
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$gisInvalidBlock(World world, int x, int y, int z) {
        if (!optimizationsAndTweaks$isNearbyChunksLoaded(world, x >> 4, z >> 4)) {
            return false;
        }
        Block blockAtPos = world.getBlock(x, y, z);
        return blockAtPos == Blocks.bedrock || blockAtPos == block
            || blockAtPos == Blocks.nether_brick
            || blockAtPos == Blocks.nether_brick_fence
            || blockAtPos == Blocks.mob_spawner;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    void placeBlockLine(Random random, int[] ai, int[] ai1) {
        int[] ai2 = new int[] { 0, 0, 0 };
        byte byte0 = 0;

        byte j = 0;
        for (; byte0 < 3; ++byte0) {
            ai2[byte0] = ai1[byte0] - ai[byte0];
            if (Math.abs(ai2[byte0]) > Math.abs(ai2[j])) {
                j = byte0;
            }
        }

        if (ai2[j] != 0) {
            byte byte1 = otherCoordPairs[j];
            byte byte2 = otherCoordPairs[j + 3];
            byte byte3 = (byte) ((ai2[j] > 0) ? 1 : -1);

            double d = (double) ai2[byte1] / (double) ai2[j];
            double d1 = (double) ai2[byte2] / (double) ai2[j];
            int[] ai3 = new int[] { 0, 0, 0 };
            int k = 0;

            for (int l = ai2[j] + byte3; k != l; k += byte3) {
                ai3[j] = MathHelper.floor_double((ai[j] + k) + 0.5);
                ai3[byte1] = MathHelper.floor_double(ai[byte1] + k * d + 0.5);
                ai3[byte2] = MathHelper.floor_double(ai[byte2] + k * d1 + 0.5);

                if (optimizationsAndTweaks$isNearbyChunksLoaded(worldObj, ai3[0] >> 4, ai3[2] >> 4)) {
                    drawSphere(random, ai3[0], ai3[1], ai3[2]);
                } else {
                    break;
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    void drawSphere(Random random, int i, int j, int k) {
        Chunk chunk;

        chunk = worldObj.getChunkFromBlockCoords(i, k);
        if (chunk != null && chunk.isChunkLoaded) {
            this.setBlock(this.worldObj, random, i, j, k, 3);
            this.setBlock(this.worldObj, random, i + 1, j, k, 3);
            this.setBlock(this.worldObj, random, i - 1, j, k, 3);
            this.placeRandomBlock(this.worldObj, random, i + 1, j + 1, k, 3);
            this.placeRandomBlock(this.worldObj, random, i - 1, j + 1, k, 3);
            this.placeRandomBlock(this.worldObj, random, i + 1, j - 1, k, 3);
            this.placeRandomBlock(this.worldObj, random, i - 1, j - 1, k, 3);
            this.placeRandomBlock(this.worldObj, random, i + 1, j, k + 1, 3);
            this.placeRandomBlock(this.worldObj, random, i - 1, j, k + 1, 3);
            this.placeRandomBlock(this.worldObj, random, i + 1, j, k - 1, 3);
            this.placeRandomBlock(this.worldObj, random, i - 1, j, k - 1, 3);
            this.placeRandomBlock(this.worldObj, random, i + 2, j, k, 3);
            this.placeRandomBlock(this.worldObj, random, i - 2, j, k, 3);
            this.placeRandomBlock(this.worldObj, random, i, j + 1, k, 3);
            this.placeRandomBlock(this.worldObj, random, i, j - 1, k, 3);
            this.placeRandomBlock(this.worldObj, random, i, j + 2, k, 3);
            this.placeRandomBlock(this.worldObj, random, i, j - 2, k, 3);
            this.setBlock(this.worldObj, random, i, j, k + 1, 3);
            this.setBlock(this.worldObj, random, i, j, k - 1, 3);
            this.placeRandomBlock(this.worldObj, random, i, j + 1, k + 1, 3);
            this.placeRandomBlock(this.worldObj, random, i, j + 1, k - 1, 3);
            this.placeRandomBlock(this.worldObj, random, i, j - 1, k + 1, 3);
            this.placeRandomBlock(this.worldObj, random, i, j - 1, k - 1, 3);
            this.placeRandomBlock(this.worldObj, random, i, j, k + 2, 3);
            this.placeRandomBlock(this.worldObj, random, i, j, k - 2, 3);
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
        if (!optimizationsAndTweaks$gisInvalidBlock(world, i, j, k)) {
            Chunk chunk = world.getChunkFromBlockCoords(i, k);
            if (chunk != null && chunk.isChunkLoaded) {
                placeBlock(world, i, j, k, block, metaCrystal, mask);
            }
        }
    }
}
