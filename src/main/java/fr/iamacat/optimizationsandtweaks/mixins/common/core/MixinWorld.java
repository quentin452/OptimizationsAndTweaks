package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.eventshandler.TidyChunkBackportEventHandler;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.CachedEntitySearch;

@Mixin(value = World.class, priority = 999)
public abstract class MixinWorld {

    @Shadow
    public boolean isRemote;

    @Shadow
    public final WorldProvider provider;

    @Shadow
    protected IChunkProvider chunkProvider;

    @Shadow
    public final Profiler theProfiler;

    @Unique
    private static final Map<Integer, CachedEntitySearch> entitySearchCache = new ConcurrentHashMap<>();

    @Unique
    private static final int CACHE_DURATION_TICKS = 40;

    @Unique
    private static long lastCacheCleanup = 0;

    @Unique
    private static final int CLEANUP_INTERVAL = 200;

    @Inject(method = "tick", at = @At(value = "INVOKE"))
    private void onTickInject(CallbackInfo info) {
        if (OptimizationsandTweaksConfig.enableTidyChunkBackport) {
            TidyChunkBackportEventHandler.injectInWorldTick((World) (Object) this);
        }
    }

    public MixinWorld(WorldProvider provider, Profiler theProfiler) {
        this.provider = provider;
        this.theProfiler = theProfiler;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Block getBlock(int x, int y, int z) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
            if (chunk != null) {
                int clampedY = MathHelper.clamp_int(y, 0, 255);
                return chunk.getBlock(x & 15, clampedY, z & 15);
            }
        }
        return Blocks.air;
    }

    @Shadow
    public boolean blockExists(int p_72899_1_, int p_72899_2_, int p_72899_3_) {
        return p_72899_2_ >= 0 && p_72899_2_ < 256 ? this.chunkExists(p_72899_1_ >> 4, p_72899_3_ >> 4) : false;
    }

    @Shadow
    public Chunk getChunkFromChunkCoords(int p_72964_1_, int p_72964_2_) {
        return this.chunkProvider.provideChunk(p_72964_1_, p_72964_2_);
    }

    @Shadow
    protected boolean chunkExists(int p_72916_1_, int p_72916_2_) {
        return this.chunkProvider.chunkExists(p_72916_1_, p_72916_2_);
    }

    @Inject(method = "getEntitiesWithinAABBExcludingEntity", at = @At("HEAD"), cancellable = true)
    private void cacheEntitySearchForMinions(Entity entity, AxisAlignedBB aabb, CallbackInfoReturnable<List> cir) {
        if (entity == null) {
            return;
        }
        World world = (World) (Object) this;
        long currentTick = world.getTotalWorldTime();

        if (currentTick - lastCacheCleanup > CLEANUP_INTERVAL) {
            lastCacheCleanup = currentTick;
            entitySearchCache.entrySet()
                .removeIf(entry -> (currentTick - entry.getValue().timestamp) > CACHE_DURATION_TICKS * 2);
        }

        int cacheKey = generateCacheKey(entity, aabb);
        CachedEntitySearch cached = entitySearchCache.get(cacheKey);

        if (cached != null && (currentTick - cached.timestamp) < CACHE_DURATION_TICKS) {
            cir.setReturnValue(new ArrayList<>(cached.entities));
            return;
        }
    }

    @Inject(method = "getEntitiesWithinAABBExcludingEntity", at = @At("RETURN"))
    private void cacheEntitySearchResult(Entity entity, AxisAlignedBB aabb, CallbackInfoReturnable<List> cir) {
        if (entity == null) {
            return;
        }

        World world = (World) (Object) this;
        long currentTick = world.getTotalWorldTime();

        int cacheKey = generateCacheKey(entity, aabb);
        List result = cir.getReturnValue();

        entitySearchCache.put(cacheKey, new CachedEntitySearch(new ArrayList<>(result), currentTick));
    }

    @Unique
    private static int generateCacheKey(Entity entity, AxisAlignedBB aabb) {
        int hash = entity.getEntityId();
        hash = 31 * hash + (int) aabb.minX;
        hash = 31 * hash + (int) aabb.minY;
        hash = 31 * hash + (int) aabb.minZ;
        hash = 31 * hash + (int) (aabb.maxX - aabb.minX);
        hash = 31 * hash + (int) (aabb.maxY - aabb.minY);
        hash = 31 * hash + (int) (aabb.maxZ - aabb.minZ);
        return hash;
    }
}
