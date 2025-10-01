package fr.iamacat.optimizationsandtweaks.mixins.client.goblins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.RendererLivingEntity;

@Mixin(goblin.ClientProxy.class)
public class MixinGoblinClientProxy {
    
    @Inject(method = "registerRenderInformation", at = @At("TAIL"), remap = false)
    private void onRegisterRenderInformationTail(CallbackInfo ci) {
        RendererLivingEntity.NAME_TAG_RANGE = 64.0F;
        RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32.0F;
    }
}