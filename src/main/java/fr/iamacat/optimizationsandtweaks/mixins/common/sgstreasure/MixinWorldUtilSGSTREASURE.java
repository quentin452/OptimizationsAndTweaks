package fr.iamacat.optimizationsandtweaks.mixins.common.sgstreasure;

import com.someguyssoftware.mod.util.WorldUtil;
import fr.iamacat.optimizationsandtweaks.utilsformods.sgstreasure.WorldUtil2SGSTREASURE;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldUtil.class)
public class MixinWorldUtilSGSTREASURE {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean isSolidBase(World world, int x, int y, int z, int width, int depth, int percentRequired) {
        return WorldUtil2SGSTREASURE.isSolidBase(world, x, y, z, width, depth, percentRequired);
    }
}
