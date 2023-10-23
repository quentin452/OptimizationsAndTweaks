package fr.iamacat.multithreading.mixins.common.farlanders;

import com.fabiulu.farlanders.common.entity.EntityFanEnderman;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFanEnderman.class)
public class MixinEntityFanEnderman extends EntityMob {
    public MixinEntityFanEnderman(World p_i1738_1_) {
        super(p_i1738_1_);
    }
    @Inject(method = "func_70639_aQ", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70639_aQ(CallbackInfoReturnable<String> cir) {
        if (MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod){
        }
        cir.setReturnValue(null);
    }
    @Inject(method = "func_70673_aS", at = @At("HEAD"), remap = false, cancellable = true)
    protected void func_70673_aS(CallbackInfoReturnable<String> cir) {
        if (MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod){
        this.worldObj.playSoundAtEntity(this, "farlanders:fandeath", 0.7F, 1.0F);
        cir.setReturnValue(null);
        }
    }

}
