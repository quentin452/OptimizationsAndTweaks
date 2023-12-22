package fr.iamacat.optimizationsandtweaks.mixins.common.netherlicious;

import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.CrystalFormationHangingBig;
import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.WorldGeneratorAdv;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
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
        if (world.getBlock(i, j, k) != Blocks.bedrock && world.getBlock(i, j, k) != this.block && world.getBlock(i, j, k) != Blocks.nether_brick && world.getBlock(i, j, k) != Blocks.nether_brick_fence && world.getBlock(i, j, k) != Blocks.mob_spawner) {
            if (random.nextInt(10) == 0) {
                this.placeBlock(world, i, j, k, this.block, this.metaBud, mask);
            } else {
                this.placeBlock(world, i, j, k, this.block, this.metaCrystal, mask);
            }
        }
    }
}
