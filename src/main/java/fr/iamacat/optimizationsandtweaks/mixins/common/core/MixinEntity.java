package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;

import fr.iamacat.optimizationsandtweaks.utils.fastrandom.XorShift128PlusRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(value = Entity.class,priority = 999)
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
    private boolean cachedIsInWater = false;
    @Unique
    private long lastCheckTime = 0L;
    @Unique
    private static final long CACHE_EXPIRATION_TIME = 1000L;

    @Inject(method = "isInWater", at = @At("HEAD"), remap = false, cancellable = true)
    public boolean isInWater(CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinEntity) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastCheckTime < CACHE_EXPIRATION_TIME) {
                return cachedIsInWater;
            } else {
                boolean inWater = this.inWater;
                cachedIsInWater = inWater;
                lastCheckTime = currentTime;
                return inWater;
            }
        } else {
            ci.cancel();
            return false;
        }
    }

    @Shadow
    protected void dealFireDamage(int amount)
    {
        if (!this.isImmuneToFire)
        {
            entity.attackEntityFrom(DamageSource.inFire, (float)amount);
        }
    }
    @Shadow
    protected void func_145775_I()
    {
        int i = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
        int j = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
        int k = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
        int l = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
        int i1 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
        int j1 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);

        if (this.worldObj.checkChunksExist(i, j, k, l, i1, j1))
        {
            for (int k1 = i; k1 <= l; ++k1)
            {
                for (int l1 = j; l1 <= i1; ++l1)
                {
                    for (int i2 = k; i2 <= j1; ++i2)
                    {
                        Block block = this.worldObj.getBlock(k1, l1, i2);

                        try
                        {
                            block.onEntityCollidedWithBlock(this.worldObj, k1, l1, i2, entity);
                        }
                        catch (Throwable throwable)
                        {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                            CrashReportCategory.func_147153_a(crashreportcategory, k1, l1, i2, block, this.worldObj.getBlockMetadata(k1, l1, i2));
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }
    }
    @Shadow
    protected void func_145780_a(int x, int y, int z, Block blockIn)
    {
        Block.SoundType soundtype = blockIn.stepSound;

        if (this.worldObj.getBlock(x, y + 1, z) == Blocks.snow_layer)
        {
            soundtype = Blocks.snow_layer.stepSound;
            entity.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
        }
        else if (!blockIn.getMaterial().isLiquid())
        {
            entity.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
        }
    }

    @Shadow
    protected void updateFallState(double distanceFallenThisTick, boolean isOnGround)
    {
        if (isOnGround)
        {
            if (entity.fallDistance > 0.0F)
            {
                this. fall(entity.fallDistance);
                entity.fallDistance = 0.0F;
            }
        }
        else if (distanceFallenThisTick < 0.0D)
        {
            entity.fallDistance = (float)((double)entity.fallDistance - distanceFallenThisTick);
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
    protected boolean canTriggerWalking()
    {
        return true;
    }
    @Shadow
    protected String getSwimSound()
    {
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
