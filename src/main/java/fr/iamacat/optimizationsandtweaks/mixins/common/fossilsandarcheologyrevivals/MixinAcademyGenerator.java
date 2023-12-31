package fr.iamacat.optimizationsandtweaks.mixins.common.fossilsandarcheologyrevivals;

import fossilsarcheology.Revival;
import fossilsarcheology.server.gen.structure.AcademyGenerator;
import fossilsarcheology.server.gen.structure.FossilStructureGenerator;
import fossilsarcheology.server.structure.util.Structure;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(AcademyGenerator.class)
public class MixinAcademyGenerator {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private final void generateStructure(World world, Random rand, int chunkX, int chunkZ) {
        // prevent generation on flatworld to prevent infinite loop caused by doesBlockHaveSolidTopSurface
        if (world.getWorldInfo().getTerrainType() == WorldType.FLAT) {
            return;
        }
        BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(chunkX, chunkZ);
        FossilStructureGenerator gen = new FossilStructureGenerator();
        int struct = rand.nextInt(FossilStructureGenerator.structures.size());

        int maxAttempts = 10;
        int attempts = 0;

        while (attempts < maxAttempts) {
            int x = chunkX + rand.nextInt(16);
            int z = chunkZ + rand.nextInt(16);
            int y = world.getHeightValue(x, z);

            y = optimizationsAndTweaks$findValidYPosition(world, x, y, z);

            if (y >= 0) {
                Revival.printDebug("Gen: Academy Spawn at " + x + ", " + y + ", " + z);
                Structure selectedStructure = FossilStructureGenerator.structures.get(struct);
                int widthX = selectedStructure.getWidthX();
                int widthZ = selectedStructure.getWidthZ();
                int height = selectedStructure.getHeight();
                gen.setStructure(selectedStructure);
                gen.setStructureFacing(rand.nextInt(4));
                gen.func_76484_a(world, rand, x, y, z);
                break; // Stop loop if structure is successfully generated
            }

            attempts++;
        }
    }

    @Unique
    private int optimizationsAndTweaks$findValidYPosition(World world, int x, int y, int z) {
        while (y > 0 && !optimizationsAndTweaks$isValidSpawnLocation(world, x, y, z)) {
            y--;
        }
        return (optimizationsAndTweaks$isValidSpawnLocation(world, x, y, z)) ? y : -1;
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidSpawnLocation(World world, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(world, x, y, z)
            && World.doesBlockHaveSolidTopSurface(world, x + 10, y, z + 11)
            && World.doesBlockHaveSolidTopSurface(world, x - 10, y, z - 11)
            && World.doesBlockHaveSolidTopSurface(world, x + 10, y, z - 11)
            && (World.doesBlockHaveSolidTopSurface(world, x - 10, y, z + 11)
            || !world.canBlockSeeTheSky(x, y, z))
            && Block.getIdFromBlock(world.getBlock(x, y + 1, z)) != Block.getIdFromBlock(Blocks.water);
    }
}
