package fr.iamacat.optimizationsandtweaks.mixins.common.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.devtools.AiEventTrace;

/**
 * Devtools-only trace of EntityAIPanic's lifecycle, applied only when {@code -Doat.aitrace=true}.
 *
 * <p>
 * Proves whether panic actually runs: {@code shouldExecute} sees the revenge target, {@code
 * startExecuting} issues the flee nav, and {@code continueExecuting} keeps it alive only while the
 * navigator has a path. With async pathfinding, the first flee request returns null (path in
 * flight), so {@code continueExecuting} sees {@code noPath()==true} the same tick and panic dies
 * before the async path arrives — visible here as a start immediately followed by continue=false.
 */
@Mixin(EntityAIPanic.class)
public abstract class MixinEntityAIPanic {

    @Shadow
    protected EntityCreature theEntityCreature;

    @Inject(method = "shouldExecute", at = @At("RETURN"), require = 0)
    private void oat$traceShouldExecute(CallbackInfoReturnable<Boolean> cir) {
        int id = theEntityCreature.getEntityId();
        if (AiEventTrace.watched(id)) {
            AiEventTrace.record(id, "panic.shouldExecute=" + cir.getReturnValue());
        }
    }

    @Inject(method = "startExecuting", at = @At("HEAD"), require = 0)
    private void oat$traceStartExecuting(CallbackInfo ci) {
        int id = theEntityCreature.getEntityId();
        if (AiEventTrace.watched(id)) {
            AiEventTrace.record(id, "panic.startExecuting");
        }
    }

    @Inject(method = "continueExecuting", at = @At("RETURN"), require = 0)
    private void oat$traceContinueExecuting(CallbackInfoReturnable<Boolean> cir) {
        int id = theEntityCreature.getEntityId();
        if (AiEventTrace.watched(id)) {
            AiEventTrace.record(
                id,
                "panic.continueExecuting=" + cir.getReturnValue()
                    + " noPath="
                    + theEntityCreature.getNavigator()
                        .noPath());
        }
    }
}
