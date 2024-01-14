package fr.iamacat.optimizationsandtweaks.mixins.common.coroutil;

import CoroUtil.ChunkCoordinatesSize;
import CoroUtil.DimensionChunkCache;
import CoroUtil.pathfinding.IPFCallback;
import CoroUtil.pathfinding.PFJobData;
import CoroUtil.pathfinding.PFQueue;
import CoroUtil.util.CoroUtilBlock;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.LinkedList;

@Mixin(PFQueue.class)
public abstract class MixinPFQueue implements Runnable {
    @Shadow
    public static PFQueue instance;
    @Shadow
    public static LinkedList<PFJobData> queue;
    @Shadow
    public static HashMap pfDelays = new HashMap();
    @Shadow
    public static long lastCacheUpdate = 0L;
    @Shadow
    public static boolean tryPath(Entity var1, int x, int y, int z, float var2, int priority, IPFCallback parCallback) {
        return tryPath(var1, x, y, z, var2, priority, parCallback, null);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean tryPath(PFJobData parJob) {
        parJob.initData();
        if (instance == null) {
            new PFQueue(null);
        }

        if (lastCacheUpdate < System.currentTimeMillis()) {
            lastCacheUpdate = System.currentTimeMillis() + 10000L;
            DimensionChunkCache.updateAllWorldCache();
        }

        int delay = 3000 + queue.size() * 20;
        boolean tryPath = parJob.sourceEntity == null || optimizationsAndTweaks$updateDelay(parJob.sourceEntity, delay);

        if (!tryPath && parJob.priority != -1) {
            return false;
        }

        try {
            optimizationsAndTweaks$addToQueue(parJob);
        } catch (Exception ignored) {
        }

        return true;
    }

    @Unique
    private static boolean optimizationsAndTweaks$updateDelay(Entity entity, int delay) {
        if (pfDelays.containsKey(entity)) {
            long time = pfDelays.get(entity) != null ? (Long) pfDelays.get(entity) : 0L;
            if (time < System.currentTimeMillis()) {
                pfDelays.put(entity, System.currentTimeMillis() + delay);
            } else {
                return false;
            }
        } else {
            pfDelays.put(entity, System.currentTimeMillis() + delay);
        }
        return true;
    }

    @Unique
    private static void optimizationsAndTweaks$addToQueue(PFJobData parJob) {
        if (parJob.priority == 0) {
            queue.addLast(parJob);
        } else if (parJob.priority == -1) {
            queue.addFirst(parJob);
        } else {
            int pos = 0;
            while (!queue.isEmpty() && parJob.priority < queue.get(pos).priority) {
                pos++;
            }
            queue.add(pos, parJob);
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean tryPath(Entity entity, int x, int y, int z, float var2, int priority, IPFCallback parCallback, ChunkCoordinatesSize parCoordSize) {
        if (entity != null && CoroUtilBlock.isAir(entity.worldObj.getBlock(x, y - 1, z))) {
            while(y > 0 && CoroUtilBlock.isAir(entity.worldObj.getBlock(x, y--, z))) {}
        }

        if (instance == null) {
            if (entity == null) {
                return false;
            }
            new PFQueue(null);
        }

        long currentTime = System.currentTimeMillis();
        if (lastCacheUpdate < currentTime) {
            lastCacheUpdate = currentTime + 10000L;
            DimensionChunkCache.updateAllWorldCache();
        }

        int delay = 3000 + queue.size() * 20;
        boolean tryPath = true;

        Long entityDelay = (Long) pfDelays.get(entity);
        if (entityDelay == null || entityDelay < currentTime) {
            pfDelays.put(entity, currentTime + delay);
        } else {
            tryPath = false;
        }

        if (!tryPath && priority != -1) {
            return false;
        }

        PFJobData job = optimizationsAndTweaks$createPFJob(entity, x, y, z, var2, parCallback, parCoordSize);

        try {
            if (priority == 0) {
                queue.addLast(job);
            } else if (priority == -1) {
                queue.addFirst(job);
            } else {
                int pos = 0;
                while (!queue.isEmpty() && priority < queue.get(pos).priority) {
                    pos++;
                }
                queue.add(pos, job);
            }
        } catch (Exception ignored) {
        }

        return true;
    }

    @Unique
    private static PFJobData optimizationsAndTweaks$createPFJob(Entity entity, int x, int y, int z, float var2, IPFCallback parCallback, ChunkCoordinatesSize parCoordSize) {
        PFJobData job;
        if (entity != null) {
            job = new PFJobData(entity, x, y, z, var2);
        } else if (parCoordSize != null) {
            job = new PFJobData(parCoordSize, x, y, z, var2);
        } else {
            System.out.println("Invalid use of PFQueue");
            return null;
        }

        job.callback = parCallback;
        job.canUseLadder = true;
        return job;
    }
}
