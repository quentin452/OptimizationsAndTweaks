package fr.iamacat.optimizationsandtweaks.utilsformods.vanilla;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Unique;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class CreatureCountTask implements Runnable {
    public static final Object2ObjectHashMap optimizationsAndTweaks$eligibleChunksForSpawning = new Object2ObjectHashMap();
    private static final Thread countThread = new Thread(new CreatureCountTask(), "CreatureCountThread");
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

    public CreatureCountTask(EnumCreatureType creatureType, World world, Object2ObjectHashMap<ChunkCoordIntPair, Boolean> eligibleChunks, AtomicInteger result) {
        this.creatureType = creatureType;
        this.world = world;
        this.eligibleChunks = eligibleChunks;
        this.result = result;
    }

    @Override
    public void run() {
        int totalCreatureCount = 0;
        assert creatureType != null;
        assert eligibleChunks != null;
        int maxCreatureCount = optimizationsAndTweaks$getMaxCreatureCount(creatureType, eligibleChunks);
        assert world != null;
        Iterator<?> entityIterator = world.loadedEntityList.iterator();
        Class<?> creatureClass = Objects.requireNonNull(creatureType.getCreatureClass());

        while (entityIterator.hasNext() && totalCreatureCount < maxCreatureCount) {
            Object entity = entityIterator.next();
            if (creatureClass.isInstance(entity)) {
                double entityPosX = ((Entity) entity).posX;
                double entityPosZ = ((Entity) entity).posZ;
                int chunkX = MathHelper.floor_double(entityPosX) >> 4;
                int chunkZ = MathHelper.floor_double(entityPosZ) >> 4;
                ChunkCoordIntPair chunkCoord = new ChunkCoordIntPair(chunkX, chunkZ);
                Boolean isChunkEligible = eligibleChunks.get(chunkCoord);
                if (isChunkEligible != null && isChunkEligible) {
                    ++totalCreatureCount;
                }
            }
        }

        assert result != null;
        result.set(totalCreatureCount);
    }

    private static int optimizationsAndTweaks$getMaxCreatureCount(EnumCreatureType creatureType, Object2ObjectHashMap<ChunkCoordIntPair, Boolean> eligibleChunks) {
        return creatureType.getMaxNumberOfCreature() * eligibleChunks.size() / 256;
    }

    public int getTotalCreatureCount() {
        assert result != null;
        return result.get();
    }

    public static boolean optimizationsAndTweaks$shouldSpawnCreature(EnumCreatureType creatureType, World world, Object2ObjectHashMap<ChunkCoordIntPair, Boolean> eligibleChunks) {
        if (!threadStarted) {
            countThread.start();
            threadStarted = true;
        }

        int totalCreatureCount = new CreatureCountTask(creatureType, world, eligibleChunks, new AtomicInteger()).getTotalCreatureCount();
        int maxCreatureCount = optimizationsAndTweaks$getMaxCreatureCount(creatureType, eligibleChunks);
        return totalCreatureCount <= maxCreatureCount;
    }
    @Unique
    public static CompletableFuture<ChunkCoordinates> optimizationsAndTweaks$getSpawnPoint(WorldServer world) {
        return CompletableFuture.supplyAsync(world::getSpawnPoint);
    }
}
