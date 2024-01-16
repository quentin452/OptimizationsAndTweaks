package fr.iamacat.optimizationsandtweaks.mixins.common.sgstreasure;

import com.someguyssoftware.mod.Coords;
import com.someguyssoftware.treasure.worldgen.chest.ObsidianChestGenerator;
import fr.iamacat.optimizationsandtweaks.utilsformods.sgstreasure.ObsidianChestGenerator2;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;


@Mixin(ObsidianChestGenerator.class)
public class MixinObsidianChestGenerator {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private Coords getNearestChestCoords(World world, int x, int y, int z) {
        return ObsidianChestGenerator2.getNearestChestCoords(world, x, y, z);
    }
}
