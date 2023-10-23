package fr.iamacat.multithreading.mixins.common.farlanders;

import com.fabiulu.farlanders.common.entity.EntityMysticEnderminion;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMysticEnderminion.class)
public abstract class MixinEntityMysticEnderminion  extends EntityTameable implements IRangedAttackMob {

    public MixinEntityMysticEnderminion(World p_i1604_1_) {
        super(p_i1604_1_);
    }
    @Inject(method = "func_70639_aQ", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70639_aQ(CallbackInfoReturnable<String> cir) {
        if (this.rand.nextInt(2) == 0) {
            this.worldObj.playSoundAtEntity(this, "farlanders:mysticenderminionidle", 0.5F, 1.4F);
        }

        cir.setReturnValue(null);
    }
    @Inject(method = "func_70621_aR", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70621_aR(CallbackInfoReturnable<String> cir) {
        this.worldObj.playSoundAtEntity(this, "farlanders:mysticenderminionhit", 0.5F, 1.6F);
        cir.setReturnValue(null);
    }
    @Inject(method = "func_70673_aS", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70673_aS(CallbackInfoReturnable<String> cir) {
        this.worldObj.playSoundAtEntity(this, "farlanders:mysticenderminiondeath", 0.5F, 1.4F);
        cir.setReturnValue(null);
    }
}
