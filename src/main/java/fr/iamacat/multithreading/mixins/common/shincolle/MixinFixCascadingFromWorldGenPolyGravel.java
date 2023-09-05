package fr.iamacat.multithreading.mixins.common.shincolle;

import com.lulan.shincolle.worldgen.WorldGenPolyGravel;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(WorldGenPolyGravel.class)
public abstract class MixinFixCascadingFromWorldGenPolyGravel extends WorldGenerator {

    /**
     * @author iamacatfr
     * @reason disabling Poly Gravel because its useless + making cascading lags
     */
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinFixCascadingFromShinColleWorldGen) {
        return false;
        }
        return false;
    }
}
