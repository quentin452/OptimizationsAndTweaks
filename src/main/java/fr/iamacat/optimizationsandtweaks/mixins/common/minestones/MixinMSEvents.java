package fr.iamacat.optimizationsandtweaks.mixins.common.minestones;

import java.util.Random;

import net.minecraft.entity.monster.IMob;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import com.sinkillerj.minestones.ItemMinestone;
import com.sinkillerj.minestones.MSConfig;
import com.sinkillerj.minestones.MSEvents;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fr.iamacat.optimizationsandtweaks.utilsformods.minestones.Patcher;

@Mixin(MSEvents.class)
public class MixinMSEvents {

    @Unique
    private static Random rand = new Random();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        if (MSConfig.hostileDrop && event.entityLiving instanceof IMob
            && event.source.getDamageType()
                .equals("player")
            && !(event.source.getEntity() instanceof FakePlayer)
            && rand.nextDouble() <= Patcher.stoneDropRate) {
            event.entityLiving.entityDropItem(ItemMinestone.getRandomStone(), 1.0F);
        }
    }
}
