package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.World;

import java.util.Collections;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {

    @Shadow
    public Map mapSpecialRenderers;

    @Shadow
    public World field_147550_f;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSynchronizedMap(CallbackInfo ci) {
        this.mapSpecialRenderers = Collections.synchronizedMap(this.mapSpecialRenderers);
    }

    @Overwrite
    public void func_147543_a(World world) {
        this.field_147550_f = world;

        synchronized (mapSpecialRenderers) {
            for (Object obj : mapSpecialRenderers.values()) {
                TileEntitySpecialRenderer renderer = (TileEntitySpecialRenderer) obj;
                if (renderer != null) {
                    renderer.func_147496_a(world);
                }
            }
        }
    }
}