package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class CreatureCountTask implements Runnable {
    public static final Object2ObjectHashMap optimizationsAndTweaks$eligibleChunksForSpawning = new Object2ObjectHashMap();
    private static final Thread countThread = new Thread(new CreatureCountTask(), "SpawnerAnimals-Thread");
    private static boolean threadStarted = false;

    private final EnumCreatureType creatureType;
    private final World world;
    private final Object2ObjectHashMap<ChunkCoordIntPair, Boolean> eligibleChunks;
    private final AtomicInteger result;

    private CreatureCountTask() {
        this.creatureType = null;
        this.world = null;
        this.eligibleChunks = null;
        this.result = null;
    }

    public CreatureCountTask(EnumCreatureType creatureType, World world, Object2ObjectHashMap<ChunkCoordIntPair,Boolean> eligibleChunks, AtomicInteger result) {
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

    private static int optimizationsAndTweaks$getMaxCreatureCount(EnumCreatureType creatureType, Object2ObjectHashMap<ChunkCoordIntPair, Boolean> eligibleChunks) {
        return creatureType.getMaxNumberOfCreature() * eligibleChunks.size() / 256;
    }


    public int getTotalCreatureCount() {
        assert result != null;
        return result.get();
    }

    public static boolean optimizationsAndTweaks$shouldSpawnCreature(EnumCreatureType creatureType, World world, Object2ObjectHashMap<ChunkCoordIntPair,Boolean> eligibleChunks) {
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
        private final Object2ObjectHashMap<ChunkCoordIntPair,Boolean> eligibleChunks;

        public CreatureCountRecursiveTask(Iterator<?> entityIterator, EnumCreatureType creatureType, int maxCreatureCount, Object2ObjectHashMap<ChunkCoordIntPair,Boolean> eligibleChunks) {
            this.entityIterator = entityIterator;
            this.creatureType = creatureType;
            this.maxCreatureCount = maxCreatureCount;
            this.eligibleChunks = eligibleChunks;
        }

        @Override
        protected Integer compute() {
            int totalCreatureCount = 0;
            Class<?> creatureClass = Objects.requireNonNull(creatureType.getCreatureClass());

            while (entityIterator.hasNext() && totalCreatureCount < maxCreatureCount) {
                Object entity = entityIterator.next();
                if (creatureClass.isInstance(entity) && isEntityInEligibleChunk((Entity) entity, eligibleChunks)) {
                    ++totalCreatureCount;
                }
            }

            return totalCreatureCount;
        }

        private boolean isEntityInEligibleChunk(Entity entity, Object2ObjectHashMap<ChunkCoordIntPair,Boolean> eligibleChunks) {
            double entityPosX = entity.posX;
            double entityPosZ = entity.posZ;
            int chunkX = MathHelper.floor_double(entityPosX) >> 4;
            int chunkZ = MathHelper.floor_double(entityPosZ) >> 4;
            ChunkCoordIntPair chunkCoord = new ChunkCoordIntPair(chunkX, chunkZ);
            Boolean isChunkEligible = eligibleChunks.get(chunkCoord);
            return isChunkEligible != null && isChunkEligible;
        }
    }
}
