package fr.iamacat.multithreading.mixins.common.akatsuki;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.akazuki.animation.common.MCACommonLibrary.IMCAnimatedEntity;
import com.akazuki.animation.common.MCACommonLibrary.animation.AnimationHandler;
import com.akazuki.animation.common.animation2.Sasori2.AnimationHandlerSasori2;
import com.akazuki.entity.EntitySasori;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(EntitySasori.class)
public abstract class MixinEntitySasori extends EntityMob implements IMCAnimatedEntity, IBossDisplayData {

    @Unique
    protected AnimationHandler animHandler = new AnimationHandlerSasori2((IMCAnimatedEntity) this);
    @Shadow
    public int timerattack;
    @Shadow
    public int plevok;

    public MixinEntitySasori(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    public AnimationHandler getAnimationHandler() {
        return this.animHandler;
    }

    @Inject(method = "func_70071_h_", at = @At("HEAD"), remap = false, cancellable = true)
    public void func_70071_h_(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntitySasosri) {
            AnimationHandler animationHandler = this.getAnimationHandler();

            if (!animationHandler.isAnimationActive("hodba")) {
                animationHandler.activateAnimation("hodba", 0.0F);
            }

            if (--this.plevok <= 0) {
                if (!animationHandler.isAnimationActive("plivok")) {
                    animationHandler.activateAnimation("plivok", 0.0F);
                }

                double r = 6.0;
                AxisAlignedBB aabb = AxisAlignedBB
                    .getBoundingBox(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ)
                    .expand(r, r, r);
                List list = this.worldObj.getEntitiesWithinAABB(Entity.class, aabb);
                Iterator iterator = list.iterator();

                for (Entity entity = null; iterator.hasNext(); this.plevok = this.rand.nextInt(200) + 100) {
                    entity = (Entity) iterator.next();
                    double disEnE = entity.getDistance(entity.posX, entity.posY, entity.posZ);
                    if (entity != null && disEnE <= r) {
                        double velX = entity.posX - this.posX;
                        double velY = entity.posY - this.posY;
                        double velZ = entity.posZ - this.posZ;
                        double vr = (r - disEnE) / r;
                        vr *= 1.1;
                        double var10000 = velX * vr;
                        var10000 = velY * vr;
                        var10000 = velZ * vr;
                        if (entity instanceof EntityLivingBase && !(entity instanceof EntitySasori)
                            && !this.worldObj.isRemote) {
                            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.id, 140, 3));
                        }
                    }
                }
            }

            super.onUpdate();
            ci.cancel();
        }
    }
}
