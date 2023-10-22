package fr.iamacat.multithreading.mixins.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(Entity.class)
public class MixinEntity {

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

    public MixinEntity(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float p_70070_1_) {
        if (MultithreadingandtweaksConfig.enableMixinEntity) {
            int i = MathHelper.floor_double(this.posX);
            int j = MathHelper.floor_double(this.posZ);

            if (this.worldObj.blockExists(i, 0, j)) {
                double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
                int k = MathHelper.floor_double(this.posY - (double) this.yOffset + d0);
                return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Unique
    private boolean cachedIsInWater = false;
    @Unique
    private long lastCheckTime = 0L;
    @Unique
    private static final long CACHE_EXPIRATION_TIME = 1000L;

    @Inject(method = "isInWater", at = @At("HEAD"),remap = false, cancellable = true)
    public boolean isInWater(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEntity) {
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

    @Unique
    protected ConcurrentHashMap<String, IExtendedEntityProperties> multithreadingandtweaks$extendedProperties;
    @Redirect(
        method = "getExtendedProperties",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;extendedProperties:Ljava/util/Map;"),
        remap = false
    )
    private Map<String, IExtendedEntityProperties> redirectExtendedProperties(Entity entity) {
        return multithreadingandtweaks$extendedProperties;
    }

}
