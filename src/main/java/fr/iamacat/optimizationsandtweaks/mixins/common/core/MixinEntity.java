package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.utils.fastrandom.XorShift128PlusRandom;

@Mixin(value = Entity.class, priority = 999)
public class MixinEntity {

    @Shadow
    protected boolean isImmuneToFire;

    @Shadow
    public Entity riddenByEntity;
    @Shadow
    private int nextStepDistance;
    @Shadow
    private int fire;
    @Unique
    protected XorShift128PlusRandom rand;
    @Shadow
    protected boolean isInWeb;
    @Unique
    private Entity entity;

    @Shadow
    public World worldObj;
    @Shadow
    public double prevPosX;
    @Shadow
    public double prevPosY;
    @Shadow
    public double prevPosZ;

    /** Entity position X */
    @Shadow
    public double posX;
    /** Entity position Y */
    @Shadow
    public double posY;
    /** Entity position Z */
    @Shadow
    public double posZ;
    @Shadow
    public final AxisAlignedBB boundingBox;
    @Shadow
    public float yOffset;
    @Shadow
    protected boolean inWater;

    public MixinEntity(int fire, Entity entity, AxisAlignedBB boundingBox) {
        this.entity = entity;
        this.boundingBox = boundingBox;
    }

    @Unique
    private boolean optimizationsAndTweaks$cachedIsInWater = false;
    @Unique
    private long optimizationsAndTweaks$lastCheckTime = 0L;
    @Unique
    private static final long CACHE_EXPIRATION_TIME = 1000L;

    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    public boolean isInWater(CallbackInfoReturnable<Boolean> cir) {
        if (OptimizationsandTweaksConfig.enableMixinEntity) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - optimizationsAndTweaks$lastCheckTime < CACHE_EXPIRATION_TIME) {
                return optimizationsAndTweaks$cachedIsInWater;
            } else {
                boolean inWater = this.inWater;
                optimizationsAndTweaks$cachedIsInWater = inWater;
                optimizationsAndTweaks$lastCheckTime = currentTime;
                return inWater;
            }
        } else {
            cir.cancel();
            cir.setReturnValue(false);
        }
        return false;
    }

    @Shadow
    protected void dealFireDamage(int amount) {
        if (!this.isImmuneToFire) {
            entity.attackEntityFrom(DamageSource.inFire, (float) amount);
        }
    }

    @Shadow
    protected void func_145780_a(int x, int y, int z, Block blockIn) {
        Block.SoundType soundtype = blockIn.stepSound;

        if (this.worldObj.getBlock(x, y + 1, z) == Blocks.snow_layer) {
            soundtype = Blocks.snow_layer.stepSound;
            entity.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
        } else if (!blockIn.getMaterial()
            .isLiquid()) {
                entity.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
    }

    @Shadow
    protected void updateFallState(double distanceFallenThisTick, boolean isOnGround) {
        if (isOnGround) {
            if (entity.fallDistance > 0.0F) {
                this.fall(entity.fallDistance);
                entity.fallDistance = 0.0F;
            }
        } else if (distanceFallenThisTick < 0.0D) {
            entity.fallDistance = (float) ((double) entity.fallDistance - distanceFallenThisTick);
        }
    }

    @Shadow
    protected void fall(float distance) {
        if (this.riddenByEntity != null) {
            try {
                Method fallMethod = Entity.class.getDeclaredMethod("fall", float.class);

                fallMethod.setAccessible(true);
                fallMethod.invoke(this.riddenByEntity, distance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Shadow
    protected boolean canTriggerWalking() {
        return true;
    }

    @Shadow
    protected String getSwimSound() {
        return "game.neutral.swim";
    }

    @Unique
    protected ConcurrentHashMap<String, IExtendedEntityProperties> optimizationsAndTweaks$extendedProperties;

    @Redirect(
        method = "getExtendedProperties",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;extendedProperties:Ljava/util/Map;"),
        remap = false)
    private Map<String, IExtendedEntityProperties> redirectExtendedProperties(Entity entity) {
        return optimizationsAndTweaks$extendedProperties;
    }

}
