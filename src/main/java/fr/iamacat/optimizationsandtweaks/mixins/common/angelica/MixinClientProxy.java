package fr.iamacat.optimizationsandtweaks.mixins.common.angelica;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Original {@code onKeypress} dereferences {@code glsmKeyBinding} without a null check (NPEs before the
 * key binding is registered -- seen alongside Dynamic Light + Falsetweaks). It also swaps the
 * {@code GameSettings#isKeyDown(KeyBinding)} check for a raw {@code Keyboard#isKeyDown(int)} check. Both
 * deltas are surgical: a HEAD-cancel guard for the null case, and a {@link Redirect} on the
 * {@code isKeyDown} call for the raw-keyboard check; the toggle/{@code checkGLSM()} logic stays original
 * bytecode.
 *
 * @author quentin452
 * @reason Fix "java.lang.NullPointerException: Unexpected error" caused by onKeypress from the ClientProxy
 *         class of the Angelica Mod. This issue appears when the Dynamic Light mod and Falsetweaks are
 *         installed, and the Falsetweaks message suggests to "Remove Dynamic Light mod because Falsetweaks
 *         already has one built in."
 */
@Mixin(com.gtnewhorizons.angelica.proxy.ClientProxy.class)
public class MixinClientProxy {

    @Shadow
    private static KeyBinding glsmKeyBinding;

    @Inject(method = "onKeypress", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$guardNullBinding(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        if (glsmKeyBinding == null) {
            ci.cancel();
        }
    }

    @Redirect(
        method = "onKeypress",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/settings/GameSettings;isKeyDown(Lnet/minecraft/client/settings/KeyBinding;)Z"))
    private static boolean optimizationsandtweaks$useRawKeyboardCheck(KeyBinding kb) {
        return Keyboard.isKeyDown(kb.getKeyCode());
    }
}
