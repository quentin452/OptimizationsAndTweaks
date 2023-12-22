package fr.iamacat.optimizationsandtweaks.mixins.common.netherlicious;

import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.CrystalFormationHangingBig;
import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.WorldGeneratorAdv;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(CrystalFormationHangingBig.class)
public abstract class MixinCrystalFormationHangingBig extends WorldGeneratorAdv {
    @Shadow
    static final byte[] otherCoordPairs = new byte[]{2, 0, 0, 1, 2, 1};
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
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void placeRandomBlock(World world, Random random, int i, int j, int k, int mask) {
        if (world.getBlock(i, j, k) != Blocks.bedrock
            && world.getBlock(i, j, k) != this.block
            && world.getBlock(i, j, k) != Blocks.nether_brick
            && world.getBlock(i, j, k) != Blocks.nether_brick_fence
            && world.getBlock(i, j, k) != Blocks.mob_spawner) {

            Chunk chunk = world.getChunkFromBlockCoords(i, k);
            if (chunk != null && chunk.isChunkLoaded) {
                if (random.nextInt(10) == 0) {
                    this.placeBlock(world, i, j, k, this.block, this.metaBud, mask);
                } else {
                    this.placeBlock(world, i, j, k, this.block, this.metaCrystal, mask);
                }
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    void placeBlockLine(Random random, int[] ai, int[] ai1) {
        int[] ai2 = new int[]{0, 0, 0};
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
            int[] ai3 = new int[]{0, 0, 0};
            int k = 0;

            for (int l = ai2[j] + byte3; k != l; k += byte3) {
                ai3[j] = MathHelper.floor_double((double) (ai[j] + k) + 0.5);
                ai3[byte1] = MathHelper.floor_double((double) ai[byte1] + (double) k * d + 0.5);
                ai3[byte2] = MathHelper.floor_double((double) ai[byte2] + (double) k * d1 + 0.5);

                Chunk chunk = worldObj.getChunkFromBlockCoords(ai3[0], ai3[2]);
                if (chunk != null && chunk.isChunkLoaded) {
                    this.drawSphere(random, ai3[0], ai3[1], ai3[2]);
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
        if (this.worldObj.getBlock(i, j, k) != Blocks.bedrock
            && this.worldObj.getBlock(i, j, k) != this.block
            && this.worldObj.getBlock(i, j, k) != Blocks.nether_brick
            && this.worldObj.getBlock(i, j, k) != Blocks.nether_brick_fence
            && this.worldObj.getBlock(i, j, k) != Blocks.mob_spawner) {

            Chunk chunk = worldObj.getChunkFromBlockCoords(i, k);
            if (chunk != null && chunk.isChunkLoaded) {
                this.placeBlock(this.worldObj, i, j, k, this.block, this.metaCrystal, mask);
            }
        }
    }
}
