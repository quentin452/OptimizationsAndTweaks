package fr.iamacat.optimizationsandtweaks.mixins.common.farlanders;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabiulu.farlanders.common.entity.EntityEnderGolem;

@Mixin(EntityEnderGolem.class)
public class MixinEntityEnderGolem extends EntityMob {

    public MixinEntityEnderGolem(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "func_70639_aQ", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70639_aQ(CallbackInfoReturnable<String> cir) {
        if (this.rand.nextInt(2) == 0) {
            this.worldObj.playSoundAtEntity(this, "farlanders:titanIdle", 1.0F, 1.2F);
        }
        cir.setReturnValue(null);
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "func_70673_aS", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70673_aS(CallbackInfoReturnable<String> cir) {
        this.worldObj.playSoundAtEntity(this, "farlanders:titanDeathEcho", 1.5F, 1.2F);
        cir.setReturnValue(null);
    }
}
