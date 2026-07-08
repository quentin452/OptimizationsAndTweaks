package fr.iamacat.optimizationsandtweaks.mixins.client.goblins;

import net.minecraft.client.renderer.entity.RendererLivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes the Goblin mod incorrectly setting the nametag range, which made all nametags invisible.
 */
@Mixin(goblin.ClientProxy.class)
public class MixinGoblinClientProxy {

    @Inject(method = "registerRenderInformation", at = @At("TAIL"), remap = false)
    private void onRegisterRenderInformationTail(CallbackInfo ci) {
        RendererLivingEntity.NAME_TAG_RANGE = 64.0F;
        RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32.0F;
    }
}
