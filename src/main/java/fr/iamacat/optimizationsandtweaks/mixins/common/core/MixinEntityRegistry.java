package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.google.common.primitives.UnsignedBytes;

@Mixin(EntityRegistry.class)
public abstract class MixinEntityRegistry {

    @Unique
    private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger(0);

    @Unique
    private static boolean isEndlessIDsLoaded() {
        return Loader.isModLoaded("endlessids");
    }

    @Redirect(
        method = "doModEntityRegistration",
        at = @At(
            value = "INVOKE",
            target = "Lcpw/mods/fml/common/FMLLog;fine(Ljava/lang/String;[Ljava/lang/Object;)V"
        ),
        remap = false
    )
    private static void suppressSkipLog(String message, Object[] params) { }

    @Inject(
        method = "registerGlobalEntityID(Ljava/lang/Class;Ljava/lang/String;I)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void interceptEntityRegistration(Class<?> entityClass, String entityName, int id, CallbackInfo ci) {
        String finalName = resolveEntityNameConflict(entityName);
        EntityRegistry registryInstance = EntityRegistry.instance();
        int finalId = ((MixinEntityRegistry)(Object) registryInstance).resolveEntityIdConflict(id);
        EntityList.addMapping(entityClass, finalName, finalId);
        ci.cancel();
    }

    @Inject(
        method = "registerGlobalEntityID(Ljava/lang/Class;Ljava/lang/String;III)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void interceptEntityRegistrationEgg(Class<?> entityClass, String entityName, int id, int eggPrimary, int eggSecondary, CallbackInfo ci) {
        String finalName = resolveEntityNameConflict(entityName);
        EntityRegistry registryInstance = EntityRegistry.instance();
        int finalId = ((MixinEntityRegistry)(Object) registryInstance).resolveEntityIdConflict(id);
        EntityList.addMapping(entityClass, finalName, finalId, eggPrimary, eggSecondary);
        ci.cancel();
    }

    @Unique
    private static String resolveEntityNameConflict(String entityName) {
        String finalName = entityName;
        if (EntityList.stringToClassMapping.containsKey(finalName)) {
            finalName = entityName + "_" + ENTITY_COUNTER.incrementAndGet();
            LanguageRegistry.instance().addStringLocalization("entity." + finalName + ".name", "en_US", entityName);
        }
        return finalName;
    }

    @Unique
    private int resolveEntityIdConflict(int id) {
        if (isEndlessIDsLoaded()) {
            // If EndlessIDs is loaded
            int finalId = id;
            if (EntityList.IDtoClassMapping.containsKey(finalId)) {
                finalId = EntityRegistry.instance().findGlobalUniqueEntityId();
                FMLLog.log(Level.WARN, "Entity ID %d is already taken, using ID %d instead (EndlessIDs mode)", id, finalId);
            }
            return finalId;
        } else {
            // If EndlessIDs is not loaded
            try {
                BitSet availableIndices = EntityRegistry.instance().availableIndicies;
                
                int realId = optimizationsandtweaks$validateAndClaimId(id);
                
                if (availableIndices.get(realId)) {
                    availableIndices.clear(realId);
                    return realId;
                } else {
                    FMLLog.log(Level.WARN, "Entity ID %d (adjusted to %d) is already reserved, finding alternative", id, realId);
                    return EntityRegistry.instance().findGlobalUniqueEntityId();
                }
            } catch (Exception e) {
                FMLLog.log(Level.WARN, e, "Error during entity ID allocation, using fallback");
                return EntityRegistry.instance().findGlobalUniqueEntityId();
            }
        }
    }

    @Unique
    public int optimizationsandtweaks$validateAndClaimId(int id)
    {
        int realId = id;
        if (id < Byte.MIN_VALUE)
        {
            FMLLog.warning("Compensating for modloader out of range compensation by mod : entityId %d for mod %s is now %d", id, Loader.instance().activeModContainer().getModId(), realId);
            realId += 3000;
        }
        try
        {
            UnsignedBytes.checkedCast(realId);
        }
        catch (IllegalArgumentException e)
        {
            FMLLog.log(Level.ERROR, "The entity ID %d for mod %s is not an unsigned byte and may not work", id, Loader.instance().activeModContainer().getModId());
        }

        if (!EntityRegistry.instance().availableIndicies.get(realId))
        {
            FMLLog.severe("The mod %s has attempted to register an entity ID %d which is already reserved. This could cause severe problems", Loader.instance().activeModContainer().getModId(), id);
        }
        EntityRegistry.instance().availableIndicies.clear(realId);
        return realId;
    }
}