package fr.iamacat.optimizationsandtweaks.mixins.common.ragdollcorpse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.gobbob.RagdollCorpses.event.EventHandler_Ragdoll;
import net.gobbob.RagdollCorpses.physics.RagdollCorpse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EventHandler_Ragdoll.class)
public class MixinEventHandler_Ragdoll {
    /**
     * @author
     * @reason
     */
    @SubscribeEvent
    @Overwrite(remap = false)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if(world != null && player != null) {
            for(int i = 0; i < RagdollCorpse.corpseList.size(); ++i) {
                RagdollCorpse corpse = RagdollCorpse.corpseList.get(i);
                if(corpse != null) {
                    if (corpse.getDistanceToPlayer() > RagdollCorpse.despawnRange
                        || corpse.ticksExisted > RagdollCorpse.despawnAge) {
                        RagdollCorpse.corpseList.remove(i);
                        --i;
                    }
                    corpse.update();
                }
            }
        }
    }
}
