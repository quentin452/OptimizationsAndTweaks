package fr.iamacat.optimizationsandtweaks.mixins.common.runicdungeons;

import cpw.mods.fml.common.registry.EntityRegistry;
import mrcomputerghost.runicdungeons.entity.EntityGuardian;
import mrcomputerghost.runicdungeons.proxy.CommonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CommonProxy.class)
public class MixinCommonProxyRunicDungeons {

    /**
     * @author
     * @reason made sure that the entity id is above 1000 to fix crash with Minenautica (require ConfigHelper)
     */
    @Overwrite(remap = false)
    public void registerRenderInformation() {
        int entityId;
        do {
            entityId = EntityRegistry.findGlobalUniqueEntityId();
        } while(entityId < 1001);
        EntityRegistry.registerGlobalEntityID(EntityGuardian.class, "Guardian", entityId, 10, 0);
    }
}
