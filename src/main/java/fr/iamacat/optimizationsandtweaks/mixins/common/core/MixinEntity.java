package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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
    @Shadow
    protected Random rand;
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
    @Inject(method = "moveEntity", at = @At("HEAD"), remap = false, cancellable = true)
    public void moveEntity(double x, double y, double z,CallbackInfo ci)
    {
        if (entity.noClip)
        {
            this.boundingBox.offset(x, y, z);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)entity.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
        }
        else
        {
            this.worldObj.theProfiler.startSection("move");
            entity.ySize *= 0.4F;
            double d3 = this.posX;
            double d4 = this.posY;
            double d5 = this.posZ;

            if (this.isInWeb)
            {
                this.isInWeb = false;
                x *= 0.25D;
                y *= 0.05000000074505806D;
                z *= 0.25D;
                entity.motionX = 0.0D;
                entity.motionY = 0.0D;
                entity.motionZ = 0.0D;
            }

            double d6 = x;
            double d7 = y;
            double d8 = z;
            AxisAlignedBB axisalignedbb = this.boundingBox.copy();
            boolean flag = entity.onGround && entity.isSneaking() && entity instanceof EntityPlayer;

            if (flag)
            {
                double d9;

                for (d9 = 0.05D; x != 0.0D && this.worldObj.getCollidingBoundingBoxes(entity, this.boundingBox.getOffsetBoundingBox(x, -1.0D, 0.0D)).isEmpty(); d6 = x)
                {
                    if (x < d9 && x >= -d9)
                    {
                        x = 0.0D;
                    }
                    else if (x > 0.0D)
                    {
                        x -= d9;
                    }
                    else
                    {
                        x += d9;
                    }
                }

                for (; z != 0.0D && this.worldObj.getCollidingBoundingBoxes(entity, this.boundingBox.getOffsetBoundingBox(0.0D, -1.0D, z)).isEmpty(); d8 = z)
                {
                    if (z < d9 && z >= -d9)
                    {
                        z = 0.0D;
                    }
                    else if (z > 0.0D)
                    {
                        z -= d9;
                    }
                    else
                    {
                        z += d9;
                    }
                }

                while (x != 0.0D && z != 0.0D && this.worldObj.getCollidingBoundingBoxes(entity, this.boundingBox.getOffsetBoundingBox(x, -1.0D, z)).isEmpty())
                {
                    if (x < d9 && x >= -d9)
                    {
                        x = 0.0D;
                    }
                    else if (x > 0.0D)
                    {
                        x -= d9;
                    }
                    else
                    {
                        x += d9;
                    }

                    if (z < d9 && z >= -d9)
                    {
                        z = 0.0D;
                    }
                    else if (z > 0.0D)
                    {
                        z -= d9;
                    }
                    else
                    {
                        z += d9;
                    }

                    d6 = x;
                    d8 = z;
                }
            }

            List list = this.worldObj.getCollidingBoundingBoxes(entity, this.boundingBox.addCoord(x, y, z));

            for (int i = 0; i < list.size(); ++i)
            {
                y = ((AxisAlignedBB)list.get(i)).calculateYOffset(this.boundingBox, y);
            }

            this.boundingBox.offset(0.0D, y, 0.0D);

            if (!entity.field_70135_K && d7 != y)
            {
                z = 0.0D;
                y = 0.0D;
                x = 0.0D;
            }

            boolean flag1 = entity.onGround || d7 != y && d7 < 0.0D;
            int j;

            for (j = 0; j < list.size(); ++j)
            {
                x = ((AxisAlignedBB)list.get(j)).calculateXOffset(this.boundingBox, x);
            }

            this.boundingBox.offset(x, 0.0D, 0.0D);

            if (!entity.field_70135_K && d6 != x)
            {
                z = 0.0D;
                y = 0.0D;
                x = 0.0D;
            }

            for (j = 0; j < list.size(); ++j)
            {
                z = ((AxisAlignedBB)list.get(j)).calculateZOffset(this.boundingBox, z);
            }

            this.boundingBox.offset(0.0D, 0.0D, z);

            if (!entity.field_70135_K && d8 != z)
            {
                z = 0.0D;
                y = 0.0D;
                x = 0.0D;
            }

            double d10;
            double d11;
            int k;
            double d12;

            if (entity.stepHeight > 0.0F && flag1 && (flag || entity.ySize < 0.05F) && (d6 != x || d8 != z))
            {
                d12 = x;
                d10 = y;
                d11 = z;
                x = d6;
                y = (double)entity.stepHeight;
                z = d8;
                AxisAlignedBB axisalignedbb1 = this.boundingBox.copy();
                this.boundingBox.setBB(axisalignedbb);
                list = this.worldObj.getCollidingBoundingBoxes(entity, this.boundingBox.addCoord(d6, y, d8));

                for (k = 0; k < list.size(); ++k)
                {
                    y = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, y);
                }

                this.boundingBox.offset(0.0D, y, 0.0D);

                if (!entity.field_70135_K && d7 != y)
                {
                    z = 0.0D;
                    y = 0.0D;
                    x = 0.0D;
                }

                for (k = 0; k < list.size(); ++k)
                {
                    x = ((AxisAlignedBB)list.get(k)).calculateXOffset(this.boundingBox, x);
                }

                this.boundingBox.offset(x, 0.0D, 0.0D);

                if (!entity.field_70135_K && d6 != x)
                {
                    z = 0.0D;
                    y = 0.0D;
                    x = 0.0D;
                }

                for (k = 0; k < list.size(); ++k)
                {
                    z = ((AxisAlignedBB)list.get(k)).calculateZOffset(this.boundingBox, z);
                }

                this.boundingBox.offset(0.0D, 0.0D, z);

                if (!entity.field_70135_K && d8 != z)
                {
                    z = 0.0D;
                    y = 0.0D;
                    x = 0.0D;
                }

                if (!entity.field_70135_K && d7 != y)
                {
                    z = 0.0D;
                    y = 0.0D;
                    x = 0.0D;
                }
                else
                {
                    y = (double)(-entity.stepHeight);

                    for (k = 0; k < list.size(); ++k)
                    {
                        y = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, y);
                    }

                    this.boundingBox.offset(0.0D, y, 0.0D);
                }

                if (d12 * d12 + d11 * d11 >= x * x + z * z)
                {
                    x = d12;
                    y = d10;
                    z = d11;
                    this.boundingBox.setBB(axisalignedbb1);
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)entity.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
            entity.isCollidedHorizontally = d6 != x || d8 != z;
            entity.isCollidedVertically = d7 != y;
            entity.onGround = d7 != y && d7 < 0.0D;
            entity.isCollided = entity.isCollidedHorizontally || entity.isCollidedVertically;
            this.updateFallState(y, entity.onGround);

            if (d6 != x)
            {
                entity.motionX = 0.0D;
            }

            if (d7 != y)
            {
                entity.motionY = 0.0D;
            }

            if (d8 != z)
            {
                entity.motionZ = 0.0D;
            }

            d12 = this.posX - d3;
            d10 = this.posY - d4;
            d11 = this.posZ - d5;

            if (this.canTriggerWalking() && !flag && entity.ridingEntity == null)
            {
                int j1 = MathHelper.floor_double(this.posX);
                k = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
                int l = MathHelper.floor_double(this.posZ);
                Block block = this.worldObj.getBlock(j1, k, l);
                int i1 = this.worldObj.getBlock(j1, k - 1, l).getRenderType();

                if (i1 == 11 || i1 == 32 || i1 == 21)
                {
                    block = this.worldObj.getBlock(j1, k - 1, l);
                }

                if (block != Blocks.ladder)
                {
                    d10 = 0.0D;
                }

                entity.distanceWalkedModified = (float)((double)entity.distanceWalkedModified + (double)MathHelper.sqrt_double(d12 * d12 + d11 * d11) * 0.6D);
                entity.distanceWalkedOnStepModified = (float)((double)entity.distanceWalkedOnStepModified + (double)MathHelper.sqrt_double(d12 * d12 + d10 * d10 + d11 * d11) * 0.6D);

                if (entity.distanceWalkedOnStepModified > (float)this.nextStepDistance && block.getMaterial() != Material.air)
                {
                    this.nextStepDistance = (int)entity.distanceWalkedOnStepModified + 1;

                    if (this.isInWater(ci))
                    {
                        float f = MathHelper.sqrt_double(entity.motionX * entity.motionX * 0.20000000298023224D + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ * 0.20000000298023224D) * 0.35F;

                        if (f > 1.0F)
                        {
                            f = 1.0F;
                        }

                        entity.playSound(this.getSwimSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    this.func_145780_a(j1, k, l, block);
                    block.onEntityWalking(this.worldObj, j1, k, l, entity);
                }
            }

            try
            {
                this.func_145775_I();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                entity.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            boolean flag2 = entity.isWet();

            if (this.worldObj.func_147470_e(this.boundingBox.contract(0.001D, 0.001D, 0.001D)))
            {
                this.dealFireDamage(1);

                if (!flag2)
                {
                    ++this.fire;

                    if (this.fire == 0)
                    {
                        entity.setFire(8);
                    }
                }
            }
            else if (this.fire <= 0)
            {
                this.fire = -entity.fireResistance;
            }

            if (flag2 && this.fire > 0)
            {
                entity.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -entity.fireResistance;
            }

            this.worldObj.theProfiler.endSection();
        }
        ci.cancel();
    }

    @Shadow
    protected void dealFireDamage(int amount)
    {
        if (!this.isImmuneToFire)
        {
            entity.attackEntityFrom(DamageSource.inFire, (float)amount);
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
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
