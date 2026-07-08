package fr.iamacat.optimizationsandtweaks.mixins.common.buildcraft.addon.oiltweaks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
import fr.iamacat.optimizationsandtweaks.utilsformods.buildcraft.InOil2;

@Mixin(OilTweakEventHandler.class)
public class MixinOilTweakEventHandler {

    /**
     * @author iamacatfr
     * @reason reclassify COMPLEX (not mechanically convertible): routes oil detection through
     *         {@link #optimizationsAndTweaks$getInOil} which returns this mod's own {@code InOil2} enum instead of
     *         the vanilla private nested {@code InOil} enum returned by {@code getInOil}. A @Redirect on the
     *         vanilla {@code getInOil} call would need to return the SAME type the original bytecode expects at
     *         that call site (private {@code InOil}), so it cannot return {@code InOil2} without a verifier type
     *         mismatch -- the type swap forces a full method rewrite, only expressible as @Overwrite. Left as-is;
     *         if/else restructuring vs vanilla's early-return is otherwise behavior-identical.
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
     * @author iamacatfr
     * @reason reclassify COMPLEX: see {@link #onLivingUpdate} -- same InOil-vs-InOil2 type swap, not mechanically
     *         convertible via injectors.
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
     * @author iamacatfr
     * @reason reclassify COMPLEX: see {@link #onLivingUpdate} -- same InOil-vs-InOil2 type swap, not mechanically
     *         convertible via injectors.
     */
    @Overwrite(remap = false)
    @SideOnly(Side.CLIENT)
    public void onPlayerClientUpdate(TickEvent.ClientTickEvent e) {
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

    /**
     * @author iamacatfr
     * @reason reclassify COMPLEX: see {@link #onLivingUpdate} -- same InOil-vs-InOil2 type swap, not mechanically
     *         convertible via injectors.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @Overwrite(remap = false)
    public void onBreakSpeed(PlayerEvent.BreakSpeed e) {
        if (!BuildCraftOilTweak.config.isOilDense()) {
            return;
        }
        EntityPlayer player = e.entityPlayer;
        if (optimizationsAndTweaks$getInOil(player).halfOfFull()) {
            e.newSpeed = e.originalSpeed <= e.newSpeed ? e.originalSpeed / 3f : e.newSpeed / 3f;
        }
    }

    /**
     * @author iamacatfr
     * @reason reclassify COMPLEX: see {@link #onLivingUpdate} -- same InOil-vs-InOil2 type swap, not mechanically
     *         convertible via injectors.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @Overwrite(remap = false)
    public void onTeleportAttempt(EnderTeleportEvent e) {
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

    /**
     * @author iamacatfr
     * @reason reclassify COMPLEX: see {@link #onLivingUpdate} -- same InOil-vs-InOil2 type swap, not mechanically
     *         convertible via injectors.
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
     * @author iamacatfr
     * @reason no real change vs vanilla (early-return rewritten as an if-block, behavior-identical) -- deleted, now
     *         shadows the vanilla implementation directly.
     */
    @Shadow
    private void setNotInOil(EntityLivingBase entity) {
        OilTweakProperties props = getProperties(entity);
        if (!props.inOil) {
            return;
        }
        entity.stepHeight = props.realStepHeight;
        props.inOil = false;
    }

    @Unique
    private final Map<Block, Boolean> optimizationsAndTweaks$oilBlockCache = new HashMap<>();

    /**
     * @author iamacatfr
     * @reason memoizes the per-block oil lookup (was recomputed on every call from every entity's per-tick
     *         collision scan); converted from @Overwrite to a HEAD/RETURN cache wrapper around the untouched
     *         vanilla body -- cache-hit short-circuits at HEAD, cache-miss falls through to the real vanilla
     *         fluid-registry lookup and the result is captured at RETURN.
     */
    @Inject(method = "isOil", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$isOilCacheRead(Block block, CallbackInfoReturnable<Boolean> cir) {
        Boolean cached = optimizationsAndTweaks$oilBlockCache.get(block);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "isOil", at = @At("RETURN"), remap = false)
    private void optimizationsandtweaks$isOilCacheWrite(Block block, CallbackInfoReturnable<Boolean> cir) {
        optimizationsAndTweaks$oilBlockCache.put(block, cir.getReturnValue());
    }

    // present as a @Shadow stub (never executes; the target's own compiled method runs at runtime, wrapped by the
    // 2 @Inject handlers above) purely so optimizationsAndTweaks$getInOil's call to isOil(...) resolves for javac.
    @Shadow
    private boolean isOil(Block block) {
        return false;
    }
}
