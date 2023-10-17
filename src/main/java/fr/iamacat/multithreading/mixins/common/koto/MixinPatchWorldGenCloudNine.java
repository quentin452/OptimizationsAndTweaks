package fr.iamacat.multithreading.mixins.common.koto;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.ani.koto.CloudNine.*;
import com.ani.koto.WorldGenCloudNine;

@Mixin(WorldGenCloudNine.class)
public class MixinPatchWorldGenCloudNine extends WorldGenerator {

    @Shadow
    public static int space = 1;
    @Shadow
    public static Block spawnedOn;

    /**
     * @author iamacatfr
     * @reason remove airBlock methode to reduce tps lags and shouldn't be used when generating structures in the air
     */
    @Override
    public boolean generate(World world, Random rand, int i, int j, int k) {
        if (world.getBlock(i, j, k) == spawnedOn && world.getBlock(i, j + 1, k) == Blocks.air
            && world.getBlock(i + space, j, k) == spawnedOn
            && world.getBlock(i + space, j, k + space) == spawnedOn
            && world.getBlock(i, j, k + space) == spawnedOn
            && world.getBlock(i + space, j + 1, k) == Blocks.air
            && world.getBlock(i + space, j + 1, k + space) == Blocks.air
            && world.getBlock(i, j + 1, k + space) == Blocks.air) {
            System.out.println("Generating Cloud 9");
            System.out.println(i + " " + j + " " + k);
            (new CloudNine1()).func_76484_a(world, rand, i, j + 70, k);
            (new CloudNine2()).func_76484_a(world, rand, i, j + 70, k);
            (new CloudNine3()).func_76484_a(world, rand, i, j + 70, k);
            (new CloudNine4()).func_76484_a(world, rand, i, j + 70, k);
            (new CloudNine5()).func_76484_a(world, rand, i, j + 70, k);
            (new CloudNine6()).func_76484_a(world, rand, i, j + 70, k);
            (new CloudNine7()).func_76484_a(world, rand, i, j + 70, k);
            (new CloudNine8()).func_76484_a(world, rand, i, j + 70, k);
            return true;
        } else {
            return false;
        }
    }

    static {
        spawnedOn = Blocks.grass;
    }
}
