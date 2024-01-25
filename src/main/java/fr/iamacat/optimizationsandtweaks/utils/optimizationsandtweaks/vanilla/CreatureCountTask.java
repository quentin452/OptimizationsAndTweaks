package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class CreatureCountTask implements Runnable {
    public static final Set<ChunkCoordinates> optimizationsAndTweaks$eligibleChunksForSpawning = ConcurrentHashMap.newKeySet();
    private static final Thread countThread = new Thread(new CreatureCountTask(), "SpawnerAnimals-Thread");
    private static boolean threadStarted = false;

    private final EnumCreatureType creatureType;
    private final World world;
    private final Set<ChunkCoordinates> eligibleChunks;
    private final AtomicInteger result;
    private static int chunkX;
    private static int chunkZ;
    private static int totalCreatureCount;

    private CreatureCountTask() {
        this.creatureType = null;
        this.world = null;
        this.eligibleChunks = null;
        this.result = null;
    }

    public CreatureCountTask(EnumCreatureType creatureType, World world, Set<ChunkCoordinates> eligibleChunks, AtomicInteger result) {
        this.creatureType = creatureType;
        this.world = world;
        this.eligibleChunks = eligibleChunks;
        this.result = result;
    }

    @Override
    public void run() {
        if (creatureType == null || eligibleChunks == null || world == null || result == null) {
            System.err.println("SpawnerAnimalsThread.run(): One or more required fields are null:");
            if (creatureType == null) System.err.println(" - creatureType is null");
            if (eligibleChunks == null) System.err.println(" - eligibleChunks is null");
            if (world == null) System.err.println(" - world is null");
            if (result == null) System.err.println(" - result is null");
            return;
        }
        int maxCreatureCount = optimizationsAndTweaks$getMaxCreatureCount(creatureType, eligibleChunks);

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        int totalCreatureCount = forkJoinPool.invoke(new CreatureCountRecursiveTask(world.loadedEntityList.iterator(), creatureType, maxCreatureCount, eligibleChunks));

        result.set(totalCreatureCount);

    }

    private static int optimizationsAndTweaks$getMaxCreatureCount(EnumCreatureType creatureType, Set<ChunkCoordinates> eligibleChunks) {
        return creatureType.getMaxNumberOfCreature() * eligibleChunks.size() / 256;
    }

    public int getTotalCreatureCount() {
        assert result != null;
        return result.get();
    }

    public static boolean optimizationsAndTweaks$shouldSpawnCreature(EnumCreatureType creatureType, World world, Set<ChunkCoordinates> eligibleChunks) {
        if (!threadStarted) {
            countThread.start();
            threadStarted = true;
        }

        int totalCreatureCount = new CreatureCountTask(creatureType, world, eligibleChunks, new AtomicInteger()).getTotalCreatureCount();
        int maxCreatureCount = optimizationsAndTweaks$getMaxCreatureCount(creatureType, eligibleChunks);
        return totalCreatureCount <= maxCreatureCount;

    }

    private static class CreatureCountRecursiveTask extends RecursiveTask<Integer> {
        private final transient Iterator<?> entityIterator;
        private final EnumCreatureType creatureType;
        private final int maxCreatureCount;
        private final Set<ChunkCoordinates> eligibleChunks;

        public CreatureCountRecursiveTask(Iterator<?> entityIterator, EnumCreatureType creatureType, int maxCreatureCount, Set<ChunkCoordinates> eligibleChunks) {
            this.entityIterator = entityIterator;
            this.creatureType = creatureType;
            this.maxCreatureCount = maxCreatureCount;
            this.eligibleChunks = eligibleChunks;
        }

        @Override
        protected Integer compute() {
            totalCreatureCount = 0;
            Class<?> creatureClass = Objects.requireNonNull(creatureType.getCreatureClass());

            while (entityIterator.hasNext() && totalCreatureCount < maxCreatureCount) {
                Object entity = entityIterator.next();
                if (creatureClass.isInstance(entity) && isEntityInEligibleChunk((Entity) entity, eligibleChunks)) {
                    ++totalCreatureCount;
                }
            }

            return totalCreatureCount;
        }

        private boolean isEntityInEligibleChunk(Entity entity, Set<ChunkCoordinates> eligibleChunks) {
            double entityPosX = entity.posX;
            double entityPosZ = entity.posZ;
            chunkX = MathHelper.floor_double(entityPosX) >> 4;
            chunkZ = MathHelper.floor_double(entityPosZ) >> 4;
            ChunkCoordinates chunkCoord = new ChunkCoordinates(chunkX,0, chunkZ);
            return eligibleChunks.contains(chunkCoord);
        }
    }
}
