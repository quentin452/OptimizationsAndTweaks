package fr.iamacat.optimizationsandtweaks.mixins.common.mankini;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import matgm50.mankini.util.BatMankiniJump;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BatMankiniJump.class)
public class MixinBatMankiniJump {
    /**
     * @author iamacatfr
     * @reason disabling anti fall damage
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void PlayerFall(LivingFallEvent event) {
    }
}
