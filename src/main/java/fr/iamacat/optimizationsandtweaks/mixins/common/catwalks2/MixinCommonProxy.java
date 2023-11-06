package fr.iamacat.optimizationsandtweaks.mixins.common.catwalks2;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.catwalks.ICustomLadder;
import com.thecodewarrior.catwalks.block.BlockCatwalk;
import com.thecodewarrior.catwalks.block.BlockScaffold;
import com.thecodewarrior.catwalks.util.*;

import codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(CommonProxy.class)
public class MixinCommonProxy {

    @Shadow
    public boolean isClient = false;
    @Shadow
    public LinkedList<WeakReference<EntityLivingBase>> entities = new LinkedList<>();

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), remap = false, cancellable = true)
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinCommonProxyForCatWalks2) {

            EntityLivingBase e = event.entityLiving;
            BlockCoord coord = this.getLadderCoord(e);
            CatwalkEntityProperties catwalkEP = CatwalkUtil.getOrCreateEP(e);

            if (coord.y < 0) {
                return;
            }

            Block b = e.worldObj.getBlock(coord.x, coord.y, coord.z);
            ICustomLadder icl = CustomLadderRegistry.getCustomLadderOrNull(b);
            double upSpeed = icl.getLadderVelocity(e.worldObj, coord.x, coord.y, coord.z, e);
            double downSpeed = icl.getLadderFallVelocity(e.worldObj, coord.x, coord.y, coord.z, e);

            double motY = e.posY - catwalkEP.lastPosY;
            double dY = e.posY - catwalkEP.lastStepY;

            if (e.isCollidedHorizontally) {
                if (e.motionY < upSpeed) {
                    e.motionY = upSpeed;
                    catwalkEP.highSpeedLadder = true;
                }
            } else {
                if (downSpeed > 0.0) {
                    e.fallDistance = 0.0F;
                }

                if (downSpeed > 0.0 && e.motionY < -downSpeed) {
                    e.motionY = -downSpeed;
                }

                boolean shouldStopOnLadder = icl.shouldHoldOn(e.worldObj, coord.x, coord.y, coord.z, e);
                boolean shouldClimbDown = icl.shouldClimbDown(e.worldObj, coord.x, coord.y, coord.z, e);
                double climbDownSpeed = icl.getClimbDownVelocity(e.worldObj, coord.x, coord.y, coord.z, e);

                if (shouldStopOnLadder && !shouldClimbDown && e.motionY < 0.0) {
                    e.motionY = 0.0;
                }

                if (shouldClimbDown && e.motionY <= 0.0) {
                    e.motionY = -climbDownSpeed;
                }

                if (motY >= 0.0) {
                    e.fallDistance = 0.0F;
                }

                double climbDownSpeedAbs = Math.abs(dY);
                double distanceRequired = upSpeed * 10.0;

                if (catwalkEP.isSlidingDownLadder && dY >= 0.0) {
                    distanceRequired = 0.0;
                }

                catwalkEP.isSlidingDownLadder = dY < 0.0;

                if (climbDownSpeedAbs > distanceRequired && distanceRequired > 0.0) {
                    catwalkEP.lastStepX = e.posX;
                    catwalkEP.lastStepY = e.posY;
                    catwalkEP.lastStepZ = e.posZ;
                    boolean shouldPlay = dY < 0.0
                        ? icl.shouldPlayStepSound(e.worldObj, coord.x, coord.y, coord.z, e, true)
                        : icl.shouldPlayStepSound(e.worldObj, coord.x, coord.y, coord.z, e, false);

                    if (shouldPlay) {
                        Block.SoundType soundtype = b.stepSound;
                        e.playSound(
                            soundtype.getStepResourcePath(),
                            soundtype.getVolume() * 0.15F,
                            soundtype.getPitch());
                    }
                }
            }

            catwalkEP.lastPosX = e.posX;
            catwalkEP.lastPosY = e.posY;
            catwalkEP.lastPosZ = e.posZ;

            if (catwalkEP.highSpeedLadder && !e.isCollidedHorizontally) {
                if (e.motionY > 0.2) {
                    e.motionY = 0.2;
                }

                catwalkEP.highSpeedLadder = false;
            }

            ci.cancel();
        }
    }

    @Unique
    public BlockCoord getLadderCoord(EntityLivingBase entity) {
        return this.findCollidingBlock(entity, new Predicate<BlockCoord>(entity) {

            public boolean test(BlockCoord bc) {
                EntityLivingBase ent = (EntityLivingBase) this.args[0];
                Block b = ent.worldObj.getBlock(bc.x, bc.y, bc.z);
                if (b == null) {
                    return false;
                } else {
                    ICustomLadder icl = CustomLadderRegistry.getCustomLadderOrNull(b);
                    return icl == null ? false : icl.isOnLadder(ent.worldObj, bc.x, bc.y, bc.z, ent);
                }
            }
        });
    }

    @Unique
    public BlockCoord findCollidingBlock(EntityLivingBase entity, Predicate<BlockCoord> mat) {
        World world = entity.worldObj;
        AxisAlignedBB bb = entity.boundingBox;
        double buf = 9.765625E-4;
        int mX = MathHelper.floor_double(bb.minX - buf);
        int mY = MathHelper.floor_double(bb.minY);
        int mZ = MathHelper.floor_double(bb.minZ - buf);

        for (int y2 = mY; (double) y2 < bb.maxY; ++y2) {
            for (int x2 = mX; (double) x2 < bb.maxX + buf; ++x2) {
                for (int z2 = mZ; (double) z2 < bb.maxZ + buf; ++z2) {
                    BlockCoord bc = new BlockCoord(x2, y2, z2);
                    if (mat.test(bc)) {
                        return bc;
                    }
                }
            }
        }

        return new BlockCoord(0, -1, 0);
    }

    @Unique
    private boolean isPlayerOnCatwalk(EntityPlayerMP player) {
        BlockCoord catwalkCoords = this.findCollidingBlock(player, new Predicate<BlockCoord>(player) {

            public boolean test(BlockCoord bc) {
                EntityPlayerMP player = (EntityPlayerMP) this.args[0];
                Block b = player.worldObj.getBlock(bc.x, bc.y, bc.z);
                return b instanceof BlockCatwalk;
            }
        });
        return catwalkCoords.y != -1;
    }

    @Inject(method = "onServerTick", at = @At("HEAD"), remap = false, cancellable = true)
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinCommonProxyForCatWalks2) {
            double catwalkSpeedBonus = CatwalkMod.speedModifier.getAmount()
                * (double) CatwalkMod.options.speedPotionLevel;
            if (event.phase == TickEvent.Phase.END) {
                List<EntityPlayerMP> players = MinecraftServer.getServer()
                    .getConfigurationManager().playerEntityList;
                players.forEach(player -> {
                    boolean shouldHaveModifier = this.isPlayerOnCatwalk(player);
                    IAttributeInstance playerSpeedAttribute = player
                        .getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                    AttributeModifier catwalksModifier = playerSpeedAttribute
                        .getModifier(CatwalkMod.speedModifier.getID());
                    boolean hasModifier = catwalksModifier != null;

                    if (hasModifier) {
                        if (!shouldHaveModifier || catwalksModifier.getAmount() != catwalkSpeedBonus) {
                            playerSpeedAttribute.removeModifier(CatwalkMod.speedModifier);
                            hasModifier = false;
                        }
                    }

                    if (shouldHaveModifier && !hasModifier) {
                        catwalksModifier = new AttributeModifier(
                            CatwalkMod.speedModifier.getID(),
                            "catwalkmod.speedup",
                            catwalkSpeedBonus,
                            2);
                        catwalksModifier.setSaved(false);
                        playerSpeedAttribute.applyModifier(catwalksModifier);
                    }
                });
            }
            ci.cancel();
        }
    }

    @Inject(method = "blockPlaceEvent", at = @At("HEAD"), remap = false, cancellable = true)
    @SubscribeEvent
    public void blockPlaceEvent(BlockEvent.PlaceEvent event, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinCommonProxyForCatWalks2) {
            if (event.blockSnapshot.replacedBlock instanceof BlockScaffold) {
                CatwalkUtil.giveItemsToPlayer(
                    event.player,
                    event.blockSnapshot.replacedBlock
                        .getDrops(event.world, event.x, event.y, event.z, event.blockMetadata, 0));
            }
            ci.cancel();
        }
    }

}
