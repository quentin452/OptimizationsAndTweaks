package fr.iamacat.multithreading.mixins.common.farlanders;

import com.fabiulu.farlanders.common.entity.EntityTitan;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityTitan.class)
public class MixinEntityTitan extends EntityMob {


    public MixinEntityTitan(World p_i1738_1_) {
        super(p_i1738_1_);
    }
    @Inject(method = "func_70639_aQ", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70639_aQ(CallbackInfoReturnable<String> cir) {
        if (this.rand.nextInt(2) == 0) {
            this.worldObj.playSoundAtEntity(this, "farlanders:titanIdle", 1.0F, 0.8F);
        }
        cir.setReturnValue(null);
    }
    @Inject(method = "func_70673_aS", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70673_aS(CallbackInfoReturnable<String> cir) {
        this.worldObj.playSoundAtEntity(this, "farlanders:titanDeathEcho", 1.5F, 0.8F);
        cir.setReturnValue(null);
    }
}
