package fr.iamacat.multithreading.mixins.client.core;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.world.EnumSkyBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(WorldRenderer.class)
public class MixinRemoveLightning {

    private EnumSkyBlock skyBlockType;
    private int posX;
    private int posY;
    private int posZ;

    @Inject(method = "func_147436_a", at = @At("HEAD"), cancellable = true)
    private void preUpdateLightByType(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.disablelightningupdate) {
            ci.cancel();
        }
    }

    @Inject(method = "func_147436_a", at = @At("HEAD"))
    private void onPreUpdateLightByType(CallbackInfo ci) {
        try {
            skyBlockType = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, null, "field_147472_a");
            posX = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, null, "field_147470_b");
            posY = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, null, "field_147471_c");
            posZ = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, null, "field_147468_d");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
