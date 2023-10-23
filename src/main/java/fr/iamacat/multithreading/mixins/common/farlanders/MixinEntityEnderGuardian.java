package fr.iamacat.multithreading.mixins.common.farlanders;

import com.fabiulu.farlanders.common.entity.EntityEnderGuardian;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityEnderGuardian.class)
public abstract class MixinEntityEnderGuardian extends EntityMob implements IRangedAttackMob {
    public MixinEntityEnderGuardian(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    @Inject(method = "func_70639_aQ", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70639_aQ(CallbackInfoReturnable<String> cir) {
        if (this.rand.nextInt(3) == 0) {
            this.worldObj.playSoundAtEntity(this, "farlanders:titanIdle", 1.0F, 1.7F);
        }

        cir.setReturnValue(null);
    }
    @Inject(method = "func_70673_aS", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70673_aS(CallbackInfoReturnable<String> cir) {
        this.worldObj.playSoundAtEntity(this, "farlanders:titanDeath", 1.5F, 1.7F);
        cir.setReturnValue(null);
    }

}
