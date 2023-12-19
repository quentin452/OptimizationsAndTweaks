package fr.iamacat.optimizationsandtweaks.mixins.common.witchery;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.common.GenericEvents;
import com.emoniph.witchery.entity.ai.EntityAIDigBlocks;
import com.emoniph.witchery.network.PacketHowl;
import com.emoniph.witchery.network.PacketSelectPlayerAbility;
import com.emoniph.witchery.util.TransformCreature;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(GenericEvents.class)
public class MixinGenericEventsWitchery {
    /**
     * @author iamacatfr
     * @reason fix crash on interact event with manuals from Openblocks by example if ars magica mod and witchery is present by disabling
     *        PotionEffect effect = event.entityPlayer.getActivePotionEffect(Witchery.Potions.PARALYSED);
     *             if (effect != null && effect.getAmplifier() >= 4) {
     *              event.setCanceled(true);
     *                 return;
     *             }
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        optimizationsAndTweaks$handlePlayerInteract(event);
    }

    @Unique
    private void optimizationsAndTweaks$handlePlayerInteract(PlayerInteractEvent event) {
            if (event.entityPlayer == null) {
                return;
            }

          /*  PotionEffect effect = event.entityPlayer.getActivePotionEffect(Witchery.Potions.PARALYSED);
            if (effect != null && effect.getAmplifier() >= 4) {
             event.setCanceled(true);
                return;
            }

           */

            ExtendedPlayer playerEx = ExtendedPlayer.get(event.entityPlayer);
            if (playerEx == null) {
                return;
            }

            if (optimizationsAndTweaks$handleVampirePowers(event, playerEx)) {
                return;
            }

            if (optimizationsAndTweaks$handleHowling(event, playerEx)) {
                return;
            }

            if (optimizationsAndTweaks$handleBlockInteractions(event, playerEx)) {
                return;
            }
    }

    @Unique
    private boolean optimizationsAndTweaks$handleVampirePowers(PlayerInteractEvent event, ExtendedPlayer playerEx) {
        if (playerEx.getSelectedVampirePower() != ExtendedPlayer.VampirePower.NONE && (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
                switch (playerEx.getSelectedVampirePower()) {
                    case MESMERIZE:
                    case SPEED:
                    case BAT:
                    case ULTIMATE:
                        Witchery.packetPipeline.sendToServer(new PacketSelectPlayerAbility(playerEx, true));
                        return true;
                    default:
                        event.setCanceled(true);
                        return true;
                }

        }
        return false;
    }
    @Unique
    private boolean optimizationsAndTweaks$handleHowling(PlayerInteractEvent event, ExtendedPlayer playerEx) {
        if (event.entityPlayer.worldObj.isRemote && ((event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) && event.entityPlayer.rotationPitch == -90.0F && event.entityPlayer.isSneaking())) {
                Witchery.packetPipeline.sendToServer(new PacketHowl());
                return true;

        }
        return false;
    }
    @Unique
    private boolean optimizationsAndTweaks$handleBlockInteractions(PlayerInteractEvent event, ExtendedPlayer playerEx) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && !event.entityPlayer.capabilities.isCreativeMode) {
            if (optimizationsAndTweaks$handleGarlicGarland(event, playerEx)) {
                return true;
            }

            return optimizationsAndTweaks$handleHarvestingBlocks(event, playerEx);
        }
        return false;
    }
    @Unique
    private boolean optimizationsAndTweaks$handleGarlicGarland(PlayerInteractEvent event, ExtendedPlayer playerEx) {
        if (playerEx.isVampire() && event.world.getBlock(event.x, event.y, event.z) == Witchery.Blocks.GARLIC_GARLAND) {
            event.entityPlayer.setFire(1);
            event.setCanceled(true);
            return true;
        }
        return false;
    }
    @Unique
    private boolean optimizationsAndTweaks$handleHarvestingBlocks(PlayerInteractEvent event, ExtendedPlayer playerEx) {
        Block block;
        if (playerEx.getCreatureType() == TransformCreature.WOLF && playerEx.getWerewolfLevel() >= 3 && event.entityPlayer.isSneaking()) {
            block = event.world.getBlock(event.x, event.y, event.z);
            if (block == Blocks.grass || block == Blocks.sand || block == Blocks.dirt || block == Blocks.mycelium || block == Blocks.gravel) {
                EntityAIDigBlocks.tryHarvestBlock(event.world, event.x, event.y, event.z, event.entityPlayer, event.entityPlayer);
                event.setCanceled(true);
                return true;
            }
        } else if (playerEx.getVampireLevel() >= 6 && playerEx.getCreatureType() == TransformCreature.NONE && event.entityPlayer.isSneaking() && (event.entityPlayer.getHeldItem() == null || !Objects.requireNonNull(event.entityPlayer.getHeldItem().getItem()).func_150897_b(Blocks.stone)) && event.entityPlayer.getFoodStats().getFoodLevel() > 0) {
            block = event.world.getBlock(event.x, event.y, event.z);
            if (block == Blocks.stone || block == Blocks.netherrack || block == Blocks.cobblestone) {
                EntityAIDigBlocks.tryHarvestBlock(event.world, event.x, event.y, event.z, event.entityPlayer, event.entityPlayer);
                event.entityPlayer.addExhaustion(10.0F);
                event.setCanceled(true);
                return true;
            }
        }
        return false;
    }
}
