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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRegistry.class)
public abstract class MixinEntityRegistry {

    @Unique
    private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger(0);

    @Accessor("availableIndicies")
    abstract BitSet getAvailableIndices();

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
        int finalId = resolveEntityIdConflict(id);

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
        int finalId = resolveEntityIdConflict(id);

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
    private static int resolveEntityIdConflict(int id) {
        if (isEndlessIDsLoaded()) {
            // If EndlessIDs is loaded, use simple ID checking
            int finalId = id;
            if (EntityList.IDtoClassMapping.containsKey(finalId)) {
                finalId = EntityRegistry.instance().findGlobalUniqueEntityId();
                FMLLog.log(Level.WARN, "Entity ID %d is already taken, using ID %d instead (EndlessIDs mode)", id, finalId);
            }
            return finalId;
        } else {
            // If EndlessIDs is not loaded, use the original BitSet-based logic
            try {
                // Get the instance and check if the ID is available using the bitset
                EntityRegistry instance = EntityRegistry.instance();
                BitSet availableIndices = ((MixinEntityRegistry) (Object) instance).getAvailableIndices();
                
                // Apply the same ID adjustment logic as the original validateAndClaimId
                int realId = adjustEntityId(id);
                
                if (availableIndices.get(realId)) {
                    availableIndices.clear(realId);
                    return realId;
                } else {
                    FMLLog.log(Level.WARN, "Entity ID %d (adjusted to %d) is already reserved, finding alternative", id, realId);
                    return instance.findGlobalUniqueEntityId();
                }
            } catch (Exception e) {
                FMLLog.log(Level.WARN, e, "Error during entity ID allocation, using fallback");
                return EntityRegistry.instance().findGlobalUniqueEntityId();
            }
        }
    }

    @Unique
    private static int adjustEntityId(int id) {
        // Replicate the ID adjustment logic from validateAndClaimId
        int realId = id;
        if (id < Byte.MIN_VALUE) {
            realId += 3000;
        }

        if (realId < 0) {
            realId += Byte.MAX_VALUE;
        }
        
        return realId;
    }
}