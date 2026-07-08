package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Optimizes Explosion class.
 */
@Mixin(Explosion.class)
public abstract class MixinExplosion {

    @Shadow
    public double explosionX;
    @Shadow
    public double explosionY;
    @Shadow
    public double explosionZ;
    @Shadow
    public float explosionSize;

    @Unique
    private final Map<Integer, Float> blockDensityCache = new HashMap<>();

    @Unique
    private int densityCallCount = 0;

    @Redirect(
        method = "doExplosionA",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBlockDensity(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/AxisAlignedBB;)F"))
    private float optimizeBlockDensity(World world, Vec3 vec3, AxisAlignedBB aabb) {
        densityCallCount++;

        int hash = (int) (aabb.minX * 73856093) ^ (int) (aabb.minY * 19349663) ^ (int) (aabb.minZ * 83492791);

        Float cached = blockDensityCache.get(hash);
        if (cached != null) {
            return cached;
        }

        float density = world.getBlockDensity(vec3, aabb);
        blockDensityCache.put(hash, density);
        return density;
    }

    @Redirect(
        method = "doExplosionA",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;",
            ordinal = 0))
    private List resetDensityCache(World world, Entity entity, AxisAlignedBB aabb) {
        blockDensityCache.clear();
        densityCallCount = 0;
        return world.getEntitiesWithinAABBExcludingEntity(entity, aabb);
    }
}
