package fr.iamacat.optimizationsandtweaks.mixins.common.KoRIN;

import net.KoRIN.BlueBedRock;
import net.KoRIN.KoRIN;
import net.KoRIN.KoRINEventHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mixin(KoRINEventHandler.class)
public class MixinKoRINEventHandler {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityLivingBase) || event.entity instanceof EntityPlayer) {
            return;
        }

        EntityLivingBase entity = (EntityLivingBase) event.entity;

        if (!(entity.worldObj.getBlock((int) entity.posX, 1, (int) entity.posZ) instanceof BlueBedRock)) {
            return;
        }

        if ((event.entity instanceof EntityAnimal && KoRIN.BluebedrockAnimals)
            || (event.entity instanceof EntityLivingBase && KoRIN.BluebedrockMonster)) {
            if (!KoRIN.BluebedrockLoot) {
                entity.setDead();
            } else {
                entity.attackEntityFrom(DamageSource.outOfWorld, 100.0F);
            }
        }
    }
}
