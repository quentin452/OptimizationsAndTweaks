package fr.iamacat.optimizationsandtweaks.mixins.common.toomuchtnt;

import com.toomuchtnt.TooMuchTNT;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TooMuchTNT.class)
public class MixinTooMuchTNT {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void Set(Class<? extends Entity> par1, String par2, int par3) {
        int entityId;
        do {
            entityId = EntityRegistry.findGlobalUniqueEntityId();
        } while (entityId < 1001);
    }
}
