package fr.iamacat.optimizationsandtweaks.mixins.common.buildcraft.addon.oiltweaks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import buildcraft.oiltweak.BuildCraftOilTweak;
import buildcraft.oiltweak.OilTweakEventHandler;
import buildcraft.oiltweak.OilTweakProperties;
import buildcraft.oiltweak.api.OilTweakAPI;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.utilsformods.buildcraft.InOil2;

@Mixin(OilTweakEventHandler.class)
public class MixinOilTweakEventHandler {

    /**
     * @author
     * @reason
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @Overwrite(remap = false)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent e) {
        if (!BuildCraftOilTweak.config.isOilDense()) {
            return;
        }
        EntityLivingBase entity = e.entityLiving;

        if (optimizationsAndTweaks$getInOil(entity).halfOfFull()) {
            entity.motionY = Math.min(0.0D, entity.motionY);
            if (entity.motionY < -0.05D) {
                entity.motionY *= 0.05D;
            }

            entity.motionX = Math.max(-0.05D, Math.min(0.05D, entity.motionX * 0.05D));
            entity.motionY -= 0.05D;
            entity.motionZ = Math.max(-0.05D, Math.min(0.05D, entity.motionZ * 0.05D));
            setStepHeight(entity, 0.0F);
        } else {
            setNotInOil(entity);
        }
    }

    /**
     * @author
     * @reason
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @Overwrite(remap = false)
    public void onPlayerUpdate(TickEvent.PlayerTickEvent e) {
        if (!BuildCraftOilTweak.config.isOilDense()) {
            return;
        }
        EntityPlayer player = e.player;
        if (!optimizationsAndTweaks$getInOil(player).halfOfFull()) {
            this.setNotInOil(player);
            return;
        }
        player.motionY = Math.min(0.0D, player.motionY);
        if (player.motionY < -0.05D) {
            player.motionY *= 0.05D;
        }

        player.motionX = Math.max(-0.05D, Math.min(0.05D, player.motionX * 0.05D));
        player.motionY -= 0.05D;
        player.motionZ = Math.max(-0.05D, Math.min(0.05D, player.motionZ * 0.05D));
        player.capabilities.isFlying = player.capabilities.isFlying && player.capabilities.isCreativeMode;
        setStepHeight(player, 0.0F);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SideOnly(Side.CLIENT)
    public void onPlayerClientUpdate(TickEvent.ClientTickEvent e) {
        if (OptimizationsandTweaksConfig.enableMixinOilTweakEventHandler) {
            if (!BuildCraftOilTweak.config.isOilDense()) {
                return;
            }
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            if (player == null) {
                return;
            }
            if (!optimizationsAndTweaks$getInOil(player).halfOfFull()) {
                this.setNotInOil(player);
                return;
            }
            player.motionY = Math.min(0.0D, player.motionY);
            if (player.motionY < -0.05D) {
                player.motionY *= 0.05D;
            }

            player.motionX = Math.max(-0.05D, Math.min(0.05D, player.motionX * 0.05D));
            player.motionY -= 0.05D;
            player.motionZ = Math.max(-0.05D, Math.min(0.05D, player.motionZ * 0.05D));
            player.capabilities.isFlying = player.capabilities.isFlying && player.capabilities.isCreativeMode;
            setStepHeight(player, 0.0F);
        }
    }

    /**
     * @author
     * @reason
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @Overwrite(remap = false)
    public void onBreakSpeed(PlayerEvent.BreakSpeed e) {
        if (OptimizationsandTweaksConfig.enableMixinOilTweakEventHandler) {
            if (!BuildCraftOilTweak.config.isOilDense()) {
                return;
            }
            EntityPlayer player = e.entityPlayer;
            if (optimizationsAndTweaks$getInOil(player).halfOfFull()) {
                e.newSpeed = e.originalSpeed <= e.newSpeed ? e.originalSpeed / 3f : e.newSpeed / 3f;
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @Overwrite(remap = false)
    public void onTeleportAttempt(EnderTeleportEvent e) {
        if (OptimizationsandTweaksConfig.enableMixinOilTweakEventHandler) {
            if (!BuildCraftOilTweak.config.isOilDense()) {
                return;
            }
            EntityLivingBase player = e.entityLiving;
            if (!(player instanceof EntityPlayer && ((EntityPlayer) player).capabilities.isCreativeMode)
                && optimizationsAndTweaks$getInOil(player).halfOfFull()) {
                e.setCanceled(true);
                e.setResult(Event.Result.DENY);

            }
        }
    }

    /**
     * @author
     * @reason
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @Overwrite(remap = false)
    public void onRightClick(PlayerInteractEvent e) {
        if (e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK || !BuildCraftOilTweak.config.isOilDense()) {
            return;
        }
        EntityPlayer player = e.entityPlayer;
        if (!player.capabilities.isCreativeMode && player.getCurrentEquippedItem() != null) {
            InOil2 inOil = optimizationsAndTweaks$getInOil(player);
            if (inOil.halfOfFull() && ((inOil == InOil2.FULL && !(player.getCurrentEquippedItem()
                .getItem() instanceof ItemBlock)) || OilTweakAPI.INSTANCE.getItemBlacklistRegistry()
                    .isBlacklisted(player, player.getCurrentEquippedItem()))) {
                player.addChatComponentMessage(
                    new ChatComponentTranslation(
                        inOil == InOil2.FULL ? "oiltweak.chat.tooDense.use" : "oiltweak.chat.tooDense.use.half"));
                e.setCanceled(true);
            }
        }
    }

    @Unique
    protected InOil2 optimizationsAndTweaks$getInOil(Entity entity) {
        AxisAlignedBB boundingBox = entity.boundingBox;
        int minX = MathHelper.floor_double(boundingBox.minX);
        int minY = MathHelper.floor_double(boundingBox.minY);
        int minZ = MathHelper.floor_double(boundingBox.minZ);
        int maxX = MathHelper.floor_double(boundingBox.maxX);
        int maxY = MathHelper.floor_double(boundingBox.maxY);
        int maxZ = MathHelper.floor_double(boundingBox.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = entity.worldObj.getBlock(x, y, z);
                    if (isOil(block)) {
                        Block blockAtMaxY = entity.worldObj.getBlock(x, maxY, z);
                        return maxY == minY || isOil(blockAtMaxY) ? InOil2.FULL : InOil2.HALF;
                    }
                }
            }
        }

        return InOil2.NONE;
    }

    @Shadow
    private void setStepHeight(EntityLivingBase entity, float height) {
        OilTweakProperties props = getProperties(entity);
        if (!props.inOil) {
            props.realStepHeight = entity.stepHeight;
            props.inOil = true;
        }
        entity.stepHeight = height;
    }

    @Shadow
    private OilTweakProperties getProperties(EntityLivingBase entity) {
        IExtendedEntityProperties ieep = entity.getExtendedProperties("oiltweak");
        if (!(ieep instanceof OilTweakProperties)) {
            ieep = new OilTweakProperties();
            ieep.init(entity, entity.worldObj);
            entity.registerExtendedProperties("oiltweak", ieep);
        }
        return (OilTweakProperties) ieep;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void setNotInOil(EntityLivingBase entity) {
        OilTweakProperties props = getProperties(entity);

        if (props.inOil) {
            entity.stepHeight = props.realStepHeight;
            props.inOil = false;
        }
    }

    @Unique
    private final Map<Block, Boolean> optimizationsAndTweaks$oilBlockCache = new HashMap<>();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private boolean isOil(Block block) {
        if (optimizationsAndTweaks$oilBlockCache.containsKey(block)) {
            return optimizationsAndTweaks$oilBlockCache.get(block);
        }

        boolean isOilBlock = false;

        if (block != null && block != Blocks.air) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (FluidRegistry.isFluidRegistered(fluid) && fluid.getName() != null
                && fluid.getName()
                    .equalsIgnoreCase("oil")) {
                isOilBlock = true;
            }
        }

        optimizationsAndTweaks$oilBlockCache.put(block, isOilBlock);
        return isOilBlock;
    }
}
