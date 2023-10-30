package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityList.class)
public class MixinEntityList {

    private static Object2ObjectHashMap optimizationsAndTweaks$stringToClassMapping = new Object2ObjectHashMap();
    @Unique
    private static Object2ObjectHashMap optimizationsAndTweaks$classToStringMapping = new Object2ObjectHashMap();
    @Unique
    private static Object2ObjectHashMap optimizationsAndTweaks$IDtoClassMapping = new Object2ObjectHashMap();
    @Unique
    private static Object2ObjectHashMap optimizationsAndTweaks$classToIDMapping = new Object2ObjectHashMap();
    @Unique
    private static Object2ObjectHashMap optimizationsAndTweaks$stringToIDMapping = new Object2ObjectHashMap();

    // todo fix when enabled crash on startup
/*
    @Redirect(
        method = "addMapping",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;stringToClassMapping:Ljava/util/Map;")
    )
    private static java.util.Map addMappingredirect1(Class p_75618_0_, String p_75618_1_, int p_75618_2_) {
    return optimizationsAndTweaks$stringToClassMapping;
    }

   @Redirect(
        method = "addMapping",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;IDtoClassMapping:Ljava/util/Map;")
       )
    private static java.util.Map addMappingredirect2(Class p_75618_0_, String p_75618_1_, int p_75618_2_) {
        return optimizationsAndTweaks$IDtoClassMapping;
    }
    @Redirect(
        method = "addMapping",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;classToStringMapping:Ljava/util/Map;")
       )
    private static java.util.Map addMappingredirect3(Class p_75618_0_, String p_75618_1_, int p_75618_2_) {
        return optimizationsAndTweaks$classToStringMapping;
    }
    @Redirect(
        method = "addMapping",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;classToIDMapping:Ljava/util/Map;")
       )
    private static java.util.Map addMappingredirect4(Class p_75618_0_, String p_75618_1_, int p_75618_2_) {
        return optimizationsAndTweaks$classToIDMapping;
    }
    @Redirect(
        method = "addMapping",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;stringToIDMapping:Ljava/util/Map;")
        )
    private static java.util.Map addMappingredirect5(Class p_75618_0_, String p_75618_1_, int p_75618_2_) {
        return optimizationsAndTweaks$stringToIDMapping;
    }

    @Redirect(
        method = "createEntityByName",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;stringToClassMapping:Ljava/util/Map;")
        )
    private static java.util.Map createEntityByNameredirect1(String p_75620_0_, World p_75620_1_) {
        return optimizationsAndTweaks$stringToClassMapping;
    }

        @Redirect(
            method = "createEntityFromNBT",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;stringToClassMapping:Ljava/util/Map;")
           )
        private static java.util.Map createEntityFromNBTredirect1(NBTTagCompound p_75615_0_, World p_75615_1_) {
        return optimizationsAndTweaks$stringToClassMapping;
    }
    @Redirect(
        method = "getEntityID",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;classToIDMapping:Ljava/util/Map;")
        )
    private static java.util.Map getEntityIDredirect4(Entity p_75619_0_) {
        return optimizationsAndTweaks$classToIDMapping;
    }

    @Redirect(
        method = "getClassFromID",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;IDtoClassMapping:Ljava/util/Map;")
    )
    private static java.util.Map getClassFromIDredirect2(int p_90035_0_) {
        return optimizationsAndTweaks$IDtoClassMapping;
    }
    @Redirect(
        method = "getEntityString",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;classToStringMapping:Ljava/util/Map;")
    )
    private static java.util.Map getEntityStringredirect3(Entity p_75621_0_) {
        return optimizationsAndTweaks$classToStringMapping;
    }
    @Redirect(
        method = "getStringFromID",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;classToStringMapping:Ljava/util/Map;")
    )
    private static java.util.Map getStringFromIDredirect3(int p_75617_0_) {
        return optimizationsAndTweaks$classToStringMapping;
    }
    @Redirect(
        method = "func_151515_b",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityList;stringToIDMapping:Ljava/util/Map;")
    )
    private static java.util.Map func_151515_bredirect5() {
        return optimizationsAndTweaks$stringToIDMapping;
    }

   */
}
