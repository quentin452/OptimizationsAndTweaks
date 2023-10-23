package fr.iamacat.optimizationsandtweaks.mixins.common.akatsuki;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.akazuki.animation.common.MCACommonLibrary.IMCAnimatedEntity;
import com.akazuki.animation.common.MCACommonLibrary.animation.AnimationHandler;
import com.akazuki.animation.common.animations.Kadz.AnimationHandlerKykl3;
import com.akazuki.entity.PuppetKadz;
import com.akazuki.entitygun.EntityPlevok;

import fr.iamacat.optimizationsandtweaks.config.MultithreadingandtweaksConfig;

@Mixin(PuppetKadz.class)
public abstract class MixinPuppetKadz extends EntityMob implements IMCAnimatedEntity {

    @Unique
    protected AnimationHandler animationHandler = new AnimationHandlerKykl3((IMCAnimatedEntity) this);
    @Unique
    public int Fire;
    @Unique
    public int field_70729_aU;

    public MixinPuppetKadz(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    @Inject(method = "func_70071_h_", at = @At("HEAD"), remap = false, cancellable = true)
    public void func_70071_h_(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinPuppetKadz) {

            ++this.Fire;

            if (this.getHealth() < 10.0F) {
                if (!animationHandler.isAnimationActive("dead")) {
                    animationHandler.activateAnimation("dead", 0.0F);
                }

                ++this.field_70729_aU;

                if (this.field_70729_aU > 40) {
                    this.attackEntityFrom(DamageSource.cactus, 11111.0F);
                }
            }

            if (this.Fire == 200) {
                if (!animationHandler.isAnimationActive("plivok")) {
                    animationHandler.activateAnimation("plivok", 0.0F);
                }

                if (!this.worldObj.isRemote) {
                    double yOffset = this.boundingBox.minY + (double) (this.height / 2.0F)
                        - (this.posY + (double) (this.height / 2.0F));
                    float f1 = MathHelper.sqrt_float(5.0F) * 0.5F;
                    EntityPlevok entitysmallfireball = new EntityPlevok(this.worldObj, this, 10);
                    entitysmallfireball.posY = this.posY + (double) (this.height / 2.0F) + 0.5;
                    this.worldObj.spawnEntityInWorld(entitysmallfireball);
                    this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 200, 15));
                    this.addPotionEffect(new PotionEffect(Potion.resistance.id, 80, 30));
                }

                this.Fire = 0;
            }

            if (!animationHandler.isAnimationActive("hodba")) {
                animationHandler.activateAnimation("hodba", 0.0F);
            }

            super.onUpdate();
        }
        ci.cancel();
    }
}
