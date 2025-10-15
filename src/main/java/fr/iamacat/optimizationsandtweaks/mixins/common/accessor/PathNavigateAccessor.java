package fr.iamacat.optimizationsandtweaks.mixins.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.pathfinding.PathNavigate;

@Mixin(PathNavigate.class)
public interface PathNavigateAccessor {
    @Accessor("canSwim")
    boolean getCanSwim();
}
