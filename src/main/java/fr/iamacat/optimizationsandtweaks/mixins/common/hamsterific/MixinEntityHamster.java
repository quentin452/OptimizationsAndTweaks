package fr.iamacat.optimizationsandtweaks.mixins.common.hamsterific;

import java.util.List;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import es.razzleberri.hamsterrific.EntityHamster;

/**
 * Original {@code setInBall}/{@code setBallColor} store their flag via
 * {@code dataWatcher.updateObject(id, (byte) value)} -- boxed as {@link Byte}. Some other read path
 * expects an {@link Integer} there, causing a "java.lang.Integer cannot be cast to java.lang.Byte" crash.
 * The fix is only the boxed TYPE of the stored value (same numeric value, {@code Integer} instead of
 * {@code Byte}); a shared {@link ModifyArg} on the {@code updateObject} call in both setters reboxes it
 * without copying either method body. {@code isInBall}/{@code getBallColor} (read side,
 * {@code getWatchableObjectInt}) and {@code hamsterColorInitialize} were byte-for-byte behavioral copies
 * of the original -- deleted as dead dupes.
 * <p>
 * {@code getRandomHamsterColor} adds one real guard: an empty-list check (after
 * {@code hamsterColorInitialize()}) to avoid an {@link IndexOutOfBoundsException} when zero hamster color
 * textures were found; injected right after that call instead of copying the method.
 *
 * @author OptimizationsAndTweaks
 * @reason Fixes java.lang.Integer cannot be cast to java.lang.Byte caused by Hamsterific Restored mod.
 */
@Mixin(EntityHamster.class)
public abstract class MixinEntityHamster extends EntityTameable {

    @Shadow
    private static List<String> hamsterColorList;

    protected MixinEntityHamster(World p_i1604_1_) {
        super(p_i1604_1_);
    }

    @ModifyArg(
        method = { "setInBall", "setBallColor" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DataWatcher;updateObject(ILjava/lang/Object;)V"))
    private Object optimizationsandtweaks$reboxAsInteger(Object value) {
        return ((Number) value).intValue();
    }

    @Inject(
        method = "getRandomHamsterColor",
        at = @At(
            value = "INVOKE",
            target = "Les/razzleberri/hamsterrific/EntityHamster;hamsterColorInitialize()V",
            shift = At.Shift.AFTER),
        cancellable = true,
        remap = false)
    private void optimizationsandtweaks$guardEmptyColorList(CallbackInfoReturnable<String> cir) {
        if (hamsterColorList.isEmpty()) {
            cir.setReturnValue("");
        }
    }
}
