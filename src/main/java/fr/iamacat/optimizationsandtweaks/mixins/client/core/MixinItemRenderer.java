package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import net.minecraft.client.renderer.ItemRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    /**
     * Redirects the call to TextureUtil.func_152777_a() to prevent setting
     * texture parameters that are likely already set correctly. This avoids
     * expensive calls to glGetTexParameteri(), which is a major source of
     * performance degradation during item rendering.
     */
    @Redirect(
        method = "renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureUtil;func_152777_a(ZZF)V"),
        remap = false)
    private void redirectSetTextureBlur(boolean blur, boolean clamp, float mipmap) {}

    /**
     * Redirects the call to TextureUtil.func_147945_b() to prevent resetting
     * the texture state. Since we skipped the initial setup call, we must also
     * skip the corresponding reset call to maintain state consistency.
     */
    @Redirect(
        method = "renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureUtil;func_147945_b()V"),
        remap = false)
    private void redirectResetTextureBlur() {}
}
