package fr.iamacat.multithreading.mixins.common.adventurersamulet;

import com.eagle.adventurersamulets.common.entity.EntityEagle;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityEagle.class)
public abstract class MixinEntityEagle extends EntityAnimal {
    @Shadow
    public float field_70886_e;
    @Shadow
    public float destPos;
    @Shadow
    public float field_70884_g;
    @Shadow
    public float field_70888_h;
    @Shadow
    public float field_70889_i = 1.0F;

    public MixinEntityEagle(World p_i1681_1_) {
        super(p_i1681_1_);
    }

    /**
     * Update the entity's state.
     */
    @Inject(method = "func_70636_d", at = @At("HEAD"), remap = false, cancellable = true)
    public void onLivingUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntityEagle){
        super.onLivingUpdate();

        float previousDestPos = this.destPos;
        float previousField70889i = this.field_70889_i;

        this.field_70888_h = this.field_70886_e;
        this.field_70884_g = previousDestPos;

        this.destPos = (float)((double)previousDestPos + (double)(this.onGround ? -1 : 4) * 0.3);
        this.destPos = Math.max(0.0F, Math.min(1.0F, this.destPos));

        if (!this.onGround && previousField70889i < 1.0F) {
            previousField70889i = 1.0F;
        }

        this.field_70889_i = (float)((double)previousField70889i * 0.9);

        if (!this.onGround && this.motionY < 0.0) {
            this.motionY *= 0.6;
        }

        this.field_70886_e += this.field_70889_i * 2.0F;
            ci.cancel();
    }
    }
}
