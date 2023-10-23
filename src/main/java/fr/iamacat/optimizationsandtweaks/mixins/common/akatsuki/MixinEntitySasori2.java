package fr.iamacat.optimizationsandtweaks.mixins.common.akatsuki;

import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
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
import com.akazuki.entity.EntitySasori2;
import com.akazuki.entity.Puppet;
import com.akazuki.entity.Puppet2;
import com.akazuki.entity.PuppetKadz;

import fr.iamacat.optimizationsandtweaks.config.MultithreadingandtweaksConfig;

@Mixin(EntitySasori2.class)
public abstract class MixinEntitySasori2 extends EntityMob implements IMCAnimatedEntity, IBossDisplayData {

    @Unique
    protected AnimationHandler animHandler = new AnimationHandlerSasori2((IMCAnimatedEntity) this);
    @Shadow
    public int TimerFire;
    @Shadow
    public int TimerFireAnim;
    @Shadow
    public int Phase2;
    @Shadow
    public int Kykl100;
    @Shadow
    public int TimeKykla;

    public MixinEntitySasori2(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    public AnimationHandler getAnimationHandler() {
        return this.animHandler;
    }

    @Inject(method = "func_70071_h_", at = @At("HEAD"), remap = false, cancellable = true)
    public void func_70071_h_(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntitySasosri2) {

            if (this.TimeKykla == 0 && this.getHealth() <= 190.0F) {
                activateAnimationIfNeeded("prisiv", 0.0F);
                if (!this.worldObj.isRemote) {
                    spawnPuppetKadz();
                }
                this.TimeKykla = 1;
            }

            if (this.getHealth() <= 50.0F && this.Kykl100 == 0) {
                activateAnimationIfNeeded("100kykl", 0.0F);
                if (!this.worldObj.isRemote) {
                    spawnPuppets();
                }
                this.Kykl100 = 1;
            }

            if (this.getHealth() <= 100.0F && this.Phase2 == 1) {
                this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 60, 24));
                activateAnimationIfNeeded("krutilka", 0.0F);
            }

            if (this.getHealth() < 100.0F) {
                handleTimerFire();
            }

            if (this.getHealth() <= 200.0F && this.getHealth() > 100.0F) {
                handleTimerFireAnim();
            }

            applyPotionEffect();

            super.onUpdate();
            ci.cancel();
        }
    }

    @Unique
    private void activateAnimationIfNeeded(String animationName, float initialValue) {
        if (!this.getAnimationHandler()
            .isAnimationActive(animationName)) {
            this.getAnimationHandler()
                .activateAnimation(animationName, initialValue);
        }
    }

    @Unique
    private void spawnPuppetKadz() {
        PuppetKadz RNerubian1 = new PuppetKadz(this.worldObj);
        RNerubian1.setPosition(this.posX + 4.0, this.posY + 3.0, this.posZ);
        RNerubian1.spawnExplosionParticle();
        this.worldObj.spawnEntityInWorld(RNerubian1);
    }

    @Unique
    private void spawnPuppets() {
        for (int i = 0; i < 100; ++i) {
            int k = this.rand.nextInt(2);
            Puppet puppet = null;
            Puppet2 puppet2 = null;

            if (k == 0) {
                puppet = new Puppet(this.worldObj);
            } else {
                puppet2 = new Puppet2(this.worldObj);
            }

            if (puppet != null) {
                puppet.setPosition(this.posX, this.posY + 20.0, this.posZ);
                puppet.spawnExplosionParticle();
                this.worldObj.spawnEntityInWorld(puppet);
            }

            if (puppet2 != null) {
                puppet2.setPosition(this.posX, this.posY + 20.0, this.posZ);
                puppet2.spawnExplosionParticle();
                this.worldObj.spawnEntityInWorld(puppet2);
            }
        }
    }

    @Unique
    private void handleTimerFire() {
        ++this.TimerFire;
        if (this.TimerFire > 300) {
            activateAnimationIfNeeded("ognemot", 0.0F);
            if (!this.worldObj.isRemote) {
                this.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 40));
                double d0 = this.posX - this.posX;
                double d1 = this.boundingBox.minY + (double) (this.height / 2.0F)
                    - (this.posY + (double) (this.height / 2.0F));
                double d2 = this.posZ - this.posZ;
                float f1 = MathHelper.sqrt_float(5.0F) * 0.5F;
                EntitySmallFireball entitysmallfireball = new EntitySmallFireball(
                    this.worldObj,
                    this,
                    d0 + this.rand.nextGaussian() * (double) f1,
                    d1,
                    d2 + this.rand.nextGaussian() * (double) f1);
                entitysmallfireball.posY = this.posY + (double) (this.height / 2.0F) + 0.5;
                this.worldObj.spawnEntityInWorld(entitysmallfireball);
                if (this.TimerFire > 500) {
                    this.TimerFire = 0;
                    this.getAnimationHandler()
                        .stopAnimation("ognemot");
                }
            }
        }
    }

    @Unique
    private void handleTimerFireAnim() {
        ++this.TimerFireAnim;
        if (this.TimerFireAnim > 100) {
            activateAnimationIfNeeded("brosok", 0.0F);
            this.TimerFireAnim = 0;
        }
    }

    @Unique
    private void applyPotionEffect() {
        if (!this.worldObj.isRemote) {
            this.addPotionEffect(new PotionEffect(Potion.resistance.id, 60, 1));
        }
    }
}
