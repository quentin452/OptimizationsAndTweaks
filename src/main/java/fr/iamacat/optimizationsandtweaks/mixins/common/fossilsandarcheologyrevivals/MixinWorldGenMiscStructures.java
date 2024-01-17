package fr.iamacat.optimizationsandtweaks.mixins.common.fossilsandarcheologyrevivals;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import fossilsarcheology.Revival;
import fossilsarcheology.server.gen.WorldGenMiscStructures;
import fr.iamacat.optimizationsandtweaks.utilsformods.fossilandarchaeologyrevival.WorldGenMiscStructures2;

@Mixin(WorldGenMiscStructures.class)
public class MixinWorldGenMiscStructures {

    @Unique
    private WorldGenMiscStructures2 optimizationsAndTweaks$worldGenMiscStructures2 = new WorldGenMiscStructures2();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        random.setSeed(world.getSeed() * (long) chunkX * (long) chunkZ);
        if (Revival.CONFIG.generateHellShips && random.nextInt(100) == 0) {
            optimizationsAndTweaks$worldGenMiscStructures2
                .optimizationsAndTweaks$generateHellShips(random, chunkX, chunkZ, world);
        }
        if (Revival.CONFIG.generateMoai) {
            optimizationsAndTweaks$worldGenMiscStructures2
                .optimizationsAndTweaks$generateMoaiStructures(random, chunkX, chunkZ, world);
        }
    }
}
