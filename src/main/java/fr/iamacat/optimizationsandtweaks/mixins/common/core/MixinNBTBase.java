package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.nbt.NBTBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NBTBase.class)
public abstract class MixinNBTBase {

    @Shadow
    public abstract byte getId();

    /**
     * @reason fast-path a self-reference check before vanilla's instanceof + getId() comparison. HEAD-cancel
     *         only on the added condition instead of a full-method replace so any other transform on this
     *         method still applies.
     */
    @Inject(method = "equals", at = @At("HEAD"), cancellable = true)
    private void equals(Object p_equals_1_, CallbackInfoReturnable<Boolean> cir) {
        if (this == p_equals_1_) {
            cir.setReturnValue(true);
        }
    }
}
