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
    public LinkedList<WeakReference<EntityLivingBase>> entities = new LinkedList<>();
    @Inject(method = "onLivingUpdate", at = @At("HEAD"), remap = false, cancellable = true)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinCommonProxyForCatWalks2) {

        EntityLivingBase entity = event.entityLiving;
        CatwalkEntityProperties catwalkEP = CatwalkUtil.getOrCreateEP(entity);
        BlockCoord coord = getLadderCoord(entity);

        if (coord.y < 0) {
            ci.cancel();
            return;
        }

        Block block = entity.worldObj.getBlock(coord.x, coord.y, coord.z);
        ICustomLadder icl = CustomLadderRegistry.getCustomLadderOrNull(block);

        double upSpeed = icl.getLadderVelocity(entity.worldObj, coord.x, coord.y, coord.z, entity);
        double downSpeed = icl.getLadderFallVelocity(entity.worldObj, coord.x, coord.y, coord.z, entity);
        double motY = entity.posY - catwalkEP.lastPosY;

        if (entity.isCollidedHorizontally && entity.motionY < upSpeed) {
            entity.motionY = upSpeed;
            catwalkEP.highSpeedLadder = true;
        } else if (downSpeed > 0.0) {
            entity.fallDistance = 0.0F;
            if (entity.motionY < -downSpeed) {
                entity.motionY = -downSpeed;
            }

            boolean shouldStopOnLadder = icl.shouldHoldOn(entity.worldObj, coord.x, coord.y, coord.z, entity);
            boolean shouldClimbDown = icl.shouldClimbDown(entity.worldObj, coord.x, coord.y, coord.z, entity);
            double climbDownSpeed = icl.getClimbDownVelocity(entity.worldObj, coord.x, coord.y, coord.z, entity);

            if (shouldStopOnLadder && !shouldClimbDown && entity.motionY < 0.0) {
                entity.motionY = 0.0;
            }

            if (shouldClimbDown && entity.motionY <= 0.0) {
                entity.motionY = -climbDownSpeed;
            }
        }

        if (motY >= 0.0) {
            entity.fallDistance = 0.0F;
        }

        double dY = entity.posY - catwalkEP.lastStepY;
        double climbDownSpeed = Math.abs(dY);
        double distanceRequired = upSpeed * 10.0;

        if (catwalkEP.isSlidingDownLadder && dY >= 0.0) {
            distanceRequired = 0.0;
        }

        catwalkEP.isSlidingDownLadder = dY < 0.0;

        if (climbDownSpeed > distanceRequired && distanceRequired > 0.0) {
            catwalkEP.lastStepX = entity.posX;
            catwalkEP.lastStepY = entity.posY;
            catwalkEP.lastStepZ = entity.posZ;

            boolean shouldPlay = dY < 0.0 ? icl.shouldPlayStepSound(entity.worldObj, coord.x, coord.y, coord.z, entity, true) : icl.shouldPlayStepSound(entity.worldObj, coord.x, coord.y, coord.z, entity, false);

            if (shouldPlay) {
                Block.SoundType soundtype = block.stepSound;
                entity.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
        }

        catwalkEP.lastPosX = entity.posX;
        catwalkEP.lastPosY = entity.posY;
        catwalkEP.lastPosZ = entity.posZ;

        if (catwalkEP.highSpeedLadder && !entity.isCollidedHorizontally) {
            if (entity.motionY > 0.2) {
                entity.motionY = 0.2;
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
