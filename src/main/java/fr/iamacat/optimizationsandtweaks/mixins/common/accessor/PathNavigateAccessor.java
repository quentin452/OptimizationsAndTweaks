package fr.iamacat.optimizationsandtweaks.mixins.common.accessor;

import net.minecraft.pathfinding.PathNavigate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PathNavigate.class)
public interface PathNavigateAccessor {

    @Accessor("canSwim")
    boolean getCanSwim();
}
