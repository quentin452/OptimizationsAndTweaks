package fr.iamacat.multithreading.mixins.common.catwalks2;

import com.thecodewarrior.catwalks.ICustomLadder;
import com.thecodewarrior.catwalks.util.*;
import com.thecodewarrior.codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

@Mixin(CommonProxy.class)
public class MixinCommonProxy {
    @Shadow
    public boolean isClient = false;
    @Shadow
    public LinkedList<WeakReference<EntityLivingBase>> entities = new LinkedList();
    @Inject(method = "onLivingUpdate", at = @At("HEAD"), remap = false, cancellable = true)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinCommonProxyForCatWalks2){
        BlockCoord coord = this.getLadderCoord(event.entityLiving);
        EntityLivingBase e = event.entityLiving;
        CatwalkEntityProperties catwalkEP = CatwalkUtil.getOrCreateEP(event.entity);
        if (coord.y >= 0) {
            Block b = event.entity.worldObj.getBlock(coord.x, coord.y, coord.z);
            ICustomLadder icl = CustomLadderRegistry.getCustomLadderOrNull(b);
            double upSpeed = icl.getLadderVelocity(e.worldObj, coord.x, coord.y, coord.z, e);
            double downSpeed = icl.getLadderFallVelocity(e.worldObj, coord.x, coord.y, coord.z, e);
            double motY = e.posY - catwalkEP.lastPosY;
            double climbDownSpeed;
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
                climbDownSpeed = icl.getClimbDownVelocity(e.worldObj, coord.x, coord.y, coord.z, e);
                if (shouldStopOnLadder && !shouldClimbDown && e.motionY < 0.0) {
                    e.motionY = 0.0;
                }

                if (shouldClimbDown && e.motionY <= 0.0) {
                    e.motionY = -climbDownSpeed;
                }
            }

            if (motY >= 0.0) {
                e.fallDistance = 0.0F;
            }

            double dY = e.posY - catwalkEP.lastStepY;
            climbDownSpeed = Math.abs(dY);
            double distanceRequired = upSpeed * 10.0;
            if (catwalkEP.isSlidingDownLadder && dY >= 0.0) {
                distanceRequired = 0.0;
            }

            catwalkEP.isSlidingDownLadder = dY < 0.0;
            if (climbDownSpeed > distanceRequired && distanceRequired > 0.0) {
                catwalkEP.lastStepX = e.posX;
                catwalkEP.lastStepY = e.posY;
                catwalkEP.lastStepZ = e.posZ;
                boolean shouldPlay = dY < 0.0 ? icl.shouldPlayStepSound(e.worldObj, coord.x, coord.y, coord.z, e, true) : icl.shouldPlayStepSound(e.worldObj, coord.x, coord.y, coord.z, e, false);
                if (shouldPlay) {
                    Block.SoundType soundtype = e.worldObj.getBlock(coord.x, coord.y, coord.z).stepSound;
                    e.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
                }
            }
        }

        catwalkEP.lastPosX = e.posX;
        catwalkEP.lastPosY = e.posY;
        catwalkEP.lastPosZ = e.posZ;
        if (catwalkEP.highSpeedLadder && !event.entityLiving.isCollidedHorizontally) {
            if (event.entity.motionY > 0.2) {
                event.entity.motionY = 0.2;
            }

            catwalkEP.highSpeedLadder = false;
        }
        ci.cancel();
        }
    }
    @Unique
    public BlockCoord getLadderCoord(EntityLivingBase entity) {
        return this.findCollidingBlock(entity, new Predicate<BlockCoord>(new Object[]{entity}) {
            public boolean test(BlockCoord bc) {
                EntityLivingBase ent = (EntityLivingBase)this.args[0];
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

        for(int y2 = mY; (double)y2 < bb.maxY; ++y2) {
            for(int x2 = mX; (double)x2 < bb.maxX + buf; ++x2) {
                for(int z2 = mZ; (double)z2 < bb.maxZ + buf; ++z2) {
                    BlockCoord bc = new BlockCoord(x2, y2, z2);
                    if (mat.test(bc)) {
                        return bc;
                    }
                }
            }
        }

        return new BlockCoord(0, -1, 0);
    }
}
