package fr.iamacat.optimizationsandtweaks.mixins.common.sgstreasure;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.someguyssoftware.plans.Plans;
import com.someguyssoftware.plans.PlansProcessor;
import com.someguyssoftware.plans.exception.ConstructionFailedException;

import fr.iamacat.optimizationsandtweaks.utilsformods.sgstreasure.PlansProcessor2;

@Mixin(PlansProcessor.class)
public class MixinPlansProcessor {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void construct(World world, int x, int y, int z, Plans plans) throws ConstructionFailedException {
        PlansProcessor2 processor2 = new PlansProcessor2();
        processor2.construct(world, x, y, z, plans);
    }
}
