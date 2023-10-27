package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.utils.fastrandom.XorShift128PlusRandom;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    private boolean multithreadingandtweaks$cachedIsInWater = false;
    @Unique
    private long multithreadingandtweaks$lastCheckTime = 0L;
    @Unique
    private static final long CACHE_EXPIRATION_TIME = 1000L;

    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    public boolean isInWater(CallbackInfoReturnable<Boolean> cir) {
        if (OptimizationsandTweaksConfig.enableMixinEntity) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - multithreadingandtweaks$lastCheckTime < CACHE_EXPIRATION_TIME) {
                return multithreadingandtweaks$cachedIsInWater;
            } else {
                boolean inWater = this.inWater;
                multithreadingandtweaks$cachedIsInWater = inWater;
                multithreadingandtweaks$lastCheckTime = currentTime;
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

    @Inject(method = "func_145775_I", at = @At("HEAD"), cancellable = true)
    protected void func_145775_I(CallbackInfo ci) {
        AxisAlignedBB boundingBox = this.boundingBox.expand(-0.001D, -0.001D, -0.001D);
        int minX = MathHelper.floor_double(boundingBox.minX);
        int minY = MathHelper.floor_double(boundingBox.minY);
        int minZ = MathHelper.floor_double(boundingBox.minZ);
        int maxX = MathHelper.floor_double(boundingBox.maxX - 1.0D);
        int maxY = MathHelper.floor_double(boundingBox.maxY - 1.0D);
        int maxZ = MathHelper.floor_double(boundingBox.maxZ - 1.0D);

        if (this.worldObj.checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ)) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        Block block = this.worldObj.getBlock(x, y, z);

                        try {
                            block.onEntityCollidedWithBlock(this.worldObj, x, y, z, entity);
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                            CrashReportCategory.func_147153_a(crashreportcategory, x, y, z, block, this.worldObj.getBlockMetadata(x, y, z));
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }
        ci.cancel();
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
    protected ConcurrentHashMap<String, IExtendedEntityProperties> multithreadingandtweaks$extendedProperties;

    @Redirect(
        method = "getExtendedProperties",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;extendedProperties:Ljava/util/Map;"),
        remap = false)
    private Map<String, IExtendedEntityProperties> redirectExtendedProperties(Entity entity) {
        return multithreadingandtweaks$extendedProperties;
    }

}
