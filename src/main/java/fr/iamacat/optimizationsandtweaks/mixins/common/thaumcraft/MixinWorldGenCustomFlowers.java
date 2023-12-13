package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.common.lib.world.WorldGenCustomFlowers;

import java.util.Random;

@Mixin(WorldGenCustomFlowers.class)
public class MixinWorldGenCustomFlowers extends WorldGenerator {
    @Shadow
    private Block plantBlock;
    @Shadow
    private int plantBlockMeta;

    public MixinWorldGenCustomFlowers(Block bi, int md) {
        this.plantBlock = bi;
        this.plantBlockMeta = md;
    }
    @Override
    public boolean generate(World world, Random par2Random, int par3, int par4, int par5) {
        int numPlaced = 0;

        for (int var6 = 0; var6 < 18; ++var6) {
            int var7 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
            int var8 = par4 + par2Random.nextInt(4) - par2Random.nextInt(4);
            int var9 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);

            if (world.isAirBlock(var7, var8, var9) && (world.getBlock(var7, var8 - 1, var9) == Blocks.grass || world.getBlock(var7, var8 - 1, var9) == Blocks.sand)) {
                world.setBlock(var7, var8, var9, this.plantBlock, this.plantBlockMeta, 3);
                numPlaced++;
            }

            if (numPlaced >= 5) {
                break;
            }
        }

        return true;
    }

}
