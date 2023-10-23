package fr.iamacat.multithreading.mixins.common.farlanders;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabiulu.farlanders.common.entity.EntityWanderer;

@Mixin(EntityWanderer.class)
public abstract class MixinEntityWanderer extends EntityAgeable implements INpc, IMerchant {

    public MixinEntityWanderer(World p_i1578_1_) {
        super(p_i1578_1_);
    }

    @Inject(method = "func_70639_aQ", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70639_aQ(CallbackInfoReturnable<String> cir) {
        if (this.rand.nextInt(2) == 0) {
            this.worldObj.playSoundAtEntity(this, "farlanders:farlanderidle", 1.0F, 1.0F);
        }
        cir.setReturnValue(null);
    }

    @Inject(method = "func_70621_aR", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70621_aR(CallbackInfoReturnable<String> cir) {
        this.worldObj.playSoundAtEntity(this, "farlanders:farlanderhit", 1.0F, 1.0F);
        cir.setReturnValue(null);
    }

    @Inject(method = "func_70673_aS", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70673_aS(CallbackInfoReturnable<String> cir) {
        this.worldObj.playSoundAtEntity(this, "farlanders:farlanderdeath", 1.0F, 1.0F);
        cir.setReturnValue(null);
    }
}
