package fr.iamacat.optimizationsandtweaks.mixins.common.fossilsandarcheologyrevivals;

import fossilsarcheology.Revival;
import fossilsarcheology.server.gen.structure.FossilWaterStructureGenerator;
import fossilsarcheology.server.gen.structure.ShipWreckGenerator;
import fossilsarcheology.server.structure.util.Structure;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(ShipWreckGenerator.class)
public class MixinShipWreckGenerator {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void generateStructure(World world, Random rand, int chunkX, int chunkZ) {
        FossilWaterStructureGenerator gen = new FossilWaterStructureGenerator();
        int struct = rand.nextInt(FossilWaterStructureGenerator.structures.size());
        int x = chunkX + rand.nextInt(16);
        int z = chunkZ + rand.nextInt(16);

        int y = world.getHeightValue(x, z);
        while (y > 0 && !world.getBlock(x, y, z).getMaterial().isSolid()) {
            y--;
        }

        if (world.getBlock(x, y, z).getMaterial().isSolid() && world.getBlock(x, y + 2, z) == Blocks.water) {
            Revival.printDebug("Gen: Shipwreck Spawn at " + x + ", " + y + ", " + z);
            Structure selectedStructure = (Structure) FossilWaterStructureGenerator.structures.get(struct);
            int widthX = selectedStructure.getWidthX();
            int widthZ = selectedStructure.getWidthZ();
            int height = selectedStructure.getHeight();
            gen.setStructure(selectedStructure);
            gen.setStructureFacing(rand.nextInt(4));
            gen.func_76484_a(world, rand, x, y - (rand.nextInt(3) + 3), z);
        }
    }
}
