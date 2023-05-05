package fr.iamacat.multithreading.mixins.client.core;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OpenGlHelper.class)
public abstract class MixinHUDCaching {

    private static Map<String, Integer> uniformCache = new HashMap<>();

    @Redirect(
        method = "glGetUniformLocation",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL20;glGetUniformLocation(ILjava/lang/CharSequence;)I"))
    private static int redirect_glGetUniformLocation(int program, CharSequence name) {
        String uniformName = name.toString();
        Integer location = uniformCache.get(uniformName);

        if (location == null) {
            location = GL20.glGetUniformLocation(program, name);
            uniformCache.put(uniformName, location);
        }

        return location;
    }
}
