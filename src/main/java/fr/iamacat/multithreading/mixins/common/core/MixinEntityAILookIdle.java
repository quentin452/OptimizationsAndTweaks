package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.math.fastrandom.FastRandom;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILookIdle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityAILookIdle.class)
public class MixinEntityAILookIdle {
    @Shadow
    private EntityLiving idleEntity;

    @Shadow
    private double lookX;

    @Shadow
    private double lookZ;

    @Shadow
    private int idleTime;

    @Unique
    private static final float RANDOM_FLOAT_THRESHOLD = 0.02F;
    @Unique
    private static final int IDLE_TIME_THRESHOLD = 20;
    @Unique
    private final FastRandom multithreadingandtweaks$fastRandom = new FastRandom() {

        @Override
        public void setSeed(long seed) {

        }

        @Override
        public void nextBytes(byte[] bytes) {

        }

        @Override
        public int nextInt() {
            return 0;
        }

        @Override
        public int nextInt(int n) {
            return 0;
        }

        @Override
        public long nextLong() {
            return 0;
        }

        @Override
        public boolean nextBoolean() {
            return false;
        }

        @Override
        public float nextFloat() {
            return 0;
        }

        @Override
        public double nextDouble() {
            return 0;
        }

        @Override
        public double nextGaussian() {
            return 0;
        }
    };

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean continueExecuting() {
        if (MultithreadingandtweaksConfig.enableMixinEntityAILookIdle) {
            return this.idleTime >= 0;
        }
        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean shouldExecute() {
        if (MultithreadingandtweaksConfig.enableMixinEntityAILookIdle) {
            return multithreadingandtweaks$fastRandom.nextFloat() < RANDOM_FLOAT_THRESHOLD;
        }
        return false;
    }

    @Inject(method = "startExecuting", at = @At("HEAD"), cancellable = true)
    public void startExecuting(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntityAILookIdle) {
            double d0 = (Math.PI * 2D) * multithreadingandtweaks$fastRandom.nextDouble();
            this.lookX = Math.cos(d0);
            this.lookZ = Math.sin(d0);
            this.idleTime = IDLE_TIME_THRESHOLD + multithreadingandtweaks$fastRandom.nextInt(IDLE_TIME_THRESHOLD);
        }
        ci.cancel();
    }

    @Inject(method = "updateTask", at = @At("HEAD"), cancellable = true)
    public void updateTask(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntityAILookIdle) {
            --this.idleTime;
            this.idleEntity.getLookHelper().setLookPosition(
                this.idleEntity.posX + this.lookX,
                this.idleEntity.posY + (double) this.idleEntity.getEyeHeight(),
                this.idleEntity.posZ + this.lookZ,
                10.0F,
                (float) this.idleEntity.getVerticalFaceSpeed()
            );
        }
        ci.cancel();
    }
}
