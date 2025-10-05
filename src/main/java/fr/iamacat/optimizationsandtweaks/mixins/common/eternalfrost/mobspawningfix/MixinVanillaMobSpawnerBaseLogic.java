package fr.iamacat.optimizationsandtweaks.mixins.common.eternalfrost.mobspawningfix;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobSpawnerBaseLogic.class) 
public class MixinVanillaMobSpawnerBaseLogic {
    @Redirect(method = "updateSpawner", 
              at = @At(value = "INVOKE", 
                      target = "net.minecraft.entity.EntityList.createEntityByName(Ljava/lang/String;Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"))
    private Entity fixEternalFrostEntityNames(String entityName, World world) {
        // Try original name first
        Entity entity = EntityList.createEntityByName(entityName, world);
        if (entity != null) return entity;
        
        // Strip "eternalfrost." prefix if present
        if (entityName.startsWith("eternalfrost.")) {
            String cleanName = entityName.substring("eternalfrost.".length());
            entity = EntityList.createEntityByName(cleanName, world);
            if (entity != null) {
                return entity;
            }
        }
        
        System.err.println("[Fix] Failed to spawn: " + entityName);
        throw new RuntimeException("Failed to spawn entity: " + entityName);
    }
}