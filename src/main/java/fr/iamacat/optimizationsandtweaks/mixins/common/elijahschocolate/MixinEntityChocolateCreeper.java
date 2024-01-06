package fr.iamacat.optimizationsandtweaks.mixins.common.elijahschocolate;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.elijah.Main.MainRegistry;
import com.elijah.mob.EntityChocolateCreeper;

import cpw.mods.fml.common.registry.EntityRegistry;

@Mixin(EntityChocolateCreeper.class)
public class MixinEntityChocolateCreeper {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void createEntity(Class entityClass, String entityName, int solidColor, int spotColor) {
        int entityId;
        do {
            entityId = EntityRegistry.findGlobalUniqueEntityId();
        } while (entityId < 1001);
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, entityId);
        EntityRegistry.registerModEntity(entityClass, entityName, entityId, MainRegistry.modInstance, 8, 1, true);
        EntityRegistry
            .addSpawn(entityClass, 2, 1, 3, EnumCreatureType.creature, new BiomeGenBase[] { BiomeGenBase.plains });
        createEgg(entityId, solidColor, spotColor);
    }

    @Shadow
    private static void createEgg(int randomId, int solidColor, int spotColor) {
        EntityList.entityEggs.put(randomId, new EntityList.EntityEggInfo(randomId, solidColor, spotColor));
    }
}
