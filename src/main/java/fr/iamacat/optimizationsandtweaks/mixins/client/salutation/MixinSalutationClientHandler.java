package fr.iamacat.optimizationsandtweaks.mixins.client.salutation;

import net.minecraftforge.client.event.GuiOpenEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import speiger.src.salutation.client.ClientHandler;

/**
 * Original {@code onGuiOpen} runs its GUI-swap logic unconditionally on whichever side dispatches the
 * event, which can stack-overflow when a dedicated/integrated server context reaches it (and conflicts
 * with chunkpregen's own GUI handling). The fix is two added early-return guards
 * (effective-side-is-client, chunkpregen not loaded); this injects them at HEAD instead of copying the
 * whole method -- the GUI-swap logic itself is untouched original bytecode.
 *
 * @author OptimizationsAndTweaks
 * @reason Fixes Stackoverflow caused by Salutation mod on servers.
 */
@Mixin(ClientHandler.class)
public class MixinSalutationClientHandler {

    @Inject(method = "onGuiOpen", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$guardServerAndChunkpregen(GuiOpenEvent event, CallbackInfo ci) {
        if (!FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient() || Loader.isModLoaded("chunkpregen")) {
            ci.cancel();
        }
    }
}
