package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.world.SpawnerAnimals.canCreatureTypeSpawnAtLocation;

public class SpawnerAnimalsTask implements Runnable {
    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();
    public static final Set<ChunkCoordinates> optimizationsAndTweaks$eligibleChunksForSpawning = new HashSet<>();
    private static final Set<Constructor<? extends EntityLiving>> optimizationsAndTweaks$constructorCache = new HashSet<>();
    private static final Thread countThread = new Thread(new SpawnerAnimalsTask(), "SpawnerAnimals-Thread");
    private static boolean threadStarted = false;

    private final EnumCreatureType creatureType;
    private final World world;
    private final Set<ChunkCoordinates> eligibleChunks;
    private final AtomicInteger result;
    private static int chunkX;
    private static int chunkZ;
    private static int totalCreatureCount;
    private static int maxCreatureCount;
    private static float optimizationsAndTweaks$distanceSquared;
    private static int optimizationsAndTweaks$spawnedEntities;
    private static int optimizationsAndTweaks$maxSpawnAttempts;
    private static int optimizationsAndTweaks$x;
    private static int optimizationsAndTweaks$z;
    private static int optimizationsAndTweaks$minY;
    private static int optimizationsAndTweaks$maxY;
    private static int optimizationsAndTweaks$y;
    private static float optimizationsAndTweaks$spawnX;
    private static float optimizationsAndTweaks$spawnZ;

    private SpawnerAnimalsTask() {
        this.creatureType = null;
        this.world = null;
        this.eligibleChunks = null;
        this.result = null;
    }

    public SpawnerAnimalsTask(EnumCreatureType creatureType, World world, Set<ChunkCoordinates> eligibleChunks, AtomicInteger result) {
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
        maxCreatureCount = optimizationsAndTweaks$getMaxCreatureCount(creatureType, eligibleChunks);

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        totalCreatureCount = forkJoinPool.invoke(new CreatureCountRecursiveTask(world.loadedEntityList.iterator(), creatureType, maxCreatureCount, eligibleChunks));

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

        totalCreatureCount = new SpawnerAnimalsTask(creatureType, world, eligibleChunks, new AtomicInteger()).getTotalCreatureCount();
        maxCreatureCount = optimizationsAndTweaks$getMaxCreatureCount(creatureType, eligibleChunks);
        return totalCreatureCount <= maxCreatureCount;
    }

    // do not refactor this method into smaller methods: it can cause Entity is already tracked! errors
    // todo fix Entity is already tracked! errors while refactoring the method into smaller methods
    // why i want to refactor this method into smallers : because for maintanibility, detecting performances bottlenecks
    public static int optimizationsAndTweaks$spawnEntitiesInChunk(WorldServer world, EnumCreatureType creatureType, ChunkCoordinates chunkCoord) {
        ChunkPosition chunkPosition = func_151350_a(world, chunkCoord.posX, chunkCoord.posZ);

        optimizationsAndTweaks$spawnedEntities = 0;
        optimizationsAndTweaks$maxSpawnAttempts = 15;

        for (int attempt = 0; attempt < optimizationsAndTweaks$maxSpawnAttempts; attempt++) {
            int x = chunkPosition.chunkPosX + rand.nextInt(6) - rand.nextInt(6);
            int y = chunkPosition.chunkPosY + rand.nextInt(1) - rand.nextInt(1);
            int z = chunkPosition.chunkPosZ + rand.nextInt(6) - rand.nextInt(6);

            optimizationsAndTweaks$spawnX = x + 0.5F;
            optimizationsAndTweaks$spawnZ = z + 0.5F;

            optimizationsAndTweaks$distanceSquared = optimizationsAndTweaks$spawnX * optimizationsAndTweaks$spawnX + optimizationsAndTweaks$spawnZ * optimizationsAndTweaks$spawnZ;

            if (optimizationsAndTweaks$isSpawnLocationValid(creatureType, world, optimizationsAndTweaks$spawnX, y, optimizationsAndTweaks$spawnZ) && optimizationsAndTweaks$isPlayerCloseEnough(world, optimizationsAndTweaks$spawnX, y, optimizationsAndTweaks$spawnZ)) {
                BiomeGenBase.SpawnListEntry spawnListEntry = optimizationsAndTweaks$createSpawnListEntry(creatureType, world, x, y, z);

                if (spawnListEntry != null) {
                    EntityLiving entityLiving = optimizationsAndTweaks$createEntityInstance(world, spawnListEntry);

                    if (entityLiving != null) {
                        entityLiving.setLocationAndAngles(optimizationsAndTweaks$spawnX, y, optimizationsAndTweaks$spawnZ, rand.nextFloat() * 360.0F, 0.0F);

                        if (optimizationsAndTweaks$canEntitySpawn(entityLiving, world, optimizationsAndTweaks$spawnX, y, optimizationsAndTweaks$spawnZ)) {
                            world.spawnEntityInWorld(entityLiving);

                            if (!optimizationsAndTweaks$doSpecialSpawn(entityLiving, world, optimizationsAndTweaks$spawnX, y, optimizationsAndTweaks$spawnZ)) {
                                entityLiving.onSpawnWithEgg(null);
                            }

                            optimizationsAndTweaks$spawnedEntities++;
                        }
                    }
                }
            }
        }

        return optimizationsAndTweaks$spawnedEntities;
    }

    private static boolean optimizationsAndTweaks$isSpawnLocationValid(EnumCreatureType creatureType, World world, float spawnX, float spawnY,
                                                                       float spawnZ) {
        boolean canSpawn = optimizationsAndTweaks$canCreatureTypeSpawnAtLocation(creatureType, world, spawnX, spawnY, spawnZ);
        return optimizationsAndTweaks$distanceSquared >= 576.0F && canSpawn;
    }

    private static boolean optimizationsAndTweaks$canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, float spawnX, float spawnY,
                                                                                 float spawnZ) {
        return canCreatureTypeSpawnAtLocation(creatureType, world, (int) spawnX, (int) spawnY, (int) spawnZ);
    }

    private static boolean optimizationsAndTweaks$canEntitySpawn(EntityLiving entity, World world, double x, double y, double z) {
        Chunk chunk = world.getChunkFromChunkCoords(MathHelper.floor_double(x) >> 4, MathHelper.floor_double(z) >> 4);
        if (!chunk.isChunkLoaded) {
            return false;
        }
        Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entity, world, (float) x, (float) y, (float) z);
        return canSpawn == Event.Result.ALLOW || (canSpawn == Event.Result.DEFAULT && entity.getCanSpawnHere());
    }

    private static boolean optimizationsAndTweaks$doSpecialSpawn(EntityLiving entity, World world, double x, double y, double z) {
        return ForgeEventFactory.doSpecialSpawn(entity, world, (float) x, (float) y, (float) z);
    }

    private static EntityLiving optimizationsAndTweaks$createEntityInstance(WorldServer world, BiomeGenBase.SpawnListEntry spawnListEntry) {
        Class<? extends EntityLiving> entityClass = spawnListEntry.entityClass;
        Constructor<? extends EntityLiving> constructor = optimizationsAndTweaks$constructorCache.stream()
            .filter(c -> c.getDeclaringClass() == entityClass)
            .findFirst()
            .orElseGet(() -> {
                try {
                    Constructor<? extends EntityLiving> newConstructor = entityClass.getConstructor(World.class);
                    optimizationsAndTweaks$constructorCache.add(newConstructor);
                    return newConstructor;
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return null;
                }
            });

        if (constructor == null) {
            return null;
        }

        try {
            return constructor.newInstance(world);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static BiomeGenBase.SpawnListEntry optimizationsAndTweaks$createSpawnListEntry(EnumCreatureType creatureType, World world, int x, int y, int z) {
        WorldServer worldServer = (WorldServer) world;
        return worldServer.spawnRandomCreature(creatureType, x, y, z);
    }

    private static boolean optimizationsAndTweaks$isPlayerCloseEnough(World world, float spawnX, float spawnY, float spawnZ) {
        return world.getClosestPlayer(spawnX, spawnY, spawnZ, 24.0D) == null;
    }

    protected static ChunkPosition func_151350_a(World world, int chunkX, int chunkZ) {
        optimizationsAndTweaks$x = chunkX * 16 + rand.nextInt(16);
        optimizationsAndTweaks$z = chunkZ * 16 + rand.nextInt(16);
        optimizationsAndTweaks$minY = 0;
        optimizationsAndTweaks$maxY = world.getActualHeight();
        optimizationsAndTweaks$y = rand.nextInt(optimizationsAndTweaks$maxY - optimizationsAndTweaks$minY) + optimizationsAndTweaks$minY;
        return new ChunkPosition(optimizationsAndTweaks$x, optimizationsAndTweaks$y, optimizationsAndTweaks$z);
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

        private static boolean isEntityInEligibleChunk(Entity entity, Set<ChunkCoordinates> eligibleChunks) {
            double entityPosX = entity.posX;
            double entityPosZ = entity.posZ;
            chunkX = MathHelper.floor_double(entityPosX) >> 4;
            chunkZ = MathHelper.floor_double(entityPosZ) >> 4;
            ChunkCoordinates chunkCoord = new ChunkCoordinates(chunkX,0, chunkZ);
            return eligibleChunks.contains(chunkCoord);
        }
    }
}
