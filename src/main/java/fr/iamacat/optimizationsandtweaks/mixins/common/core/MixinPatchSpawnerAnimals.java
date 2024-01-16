package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import cpw.mods.fml.common.eventhandler.Event;
import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = SpawnerAnimals.class, priority = 999)
public class MixinPatchSpawnerAnimals {

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected static ChunkPosition func_151350_a(World world, int chunkX, int chunkZ) {
        int x = chunkX * 16 + world.rand.nextInt(16);
        int z = chunkZ * 16 + world.rand.nextInt(16);

        int minY = 0;
        int maxY = world.getActualHeight();

        int y = world.rand.nextInt(maxY - minY) + minY;

        return new ChunkPosition(x, y, z);

    }

    @Unique
    private Object2ObjectHashMap optimizationsAndTweaks$eligibleChunksForSpawning = new Object2ObjectHashMap();

    /**
     * @author iamacatfr
     * @reason greatly reduce TPS lags on VoidWorld and more
     */
    @Overwrite
    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y - 1, z);
        Block blockAbove = world.getBlock(x, y, z);
        Block blockBelow = world.getBlock(x, y + 1, z);

        Material creatureMaterial = creatureType.getCreatureMaterial();

        if (creatureMaterial == Material.water) {
            return optimizationsAndTweaks$canCreatureSpawnInWater(block, blockBelow, blockAbove);
        } else {
            return optimizationsAndTweaks$canCreatureSpawnOnLand(creatureType, world, x, y, z, block, blockAbove, blockBelow);
        }
    }

    @Unique
    private static boolean optimizationsAndTweaks$canCreatureSpawnInWater(Block block, Block blockBelow, Block blockAbove) {
        return block.getMaterial().isLiquid()
            && blockBelow.getMaterial().isLiquid()
            && !blockAbove.isNormalCube();
    }

    @Unique
    private static boolean optimizationsAndTweaks$canCreatureSpawnOnLand(EnumCreatureType creatureType, World world, int x, int y, int z,
                                                                         Block block, Block blockAbove, Block blockBelow) {
        if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
            return false;
        }

        boolean canSpawn = optimizationsAndTweaks$canCreatureSpawnOnBlock(creatureType, world, x, y - 1, z, block);
        return canSpawn && block != Blocks.bedrock
            && !blockAbove.isNormalCube()
            && !blockAbove.getMaterial().isLiquid()
            && !world.getBlock(x, y + 1, z).isNormalCube();
    }

    @Unique
    private static boolean optimizationsAndTweaks$canCreatureSpawnOnBlock(EnumCreatureType creatureType, World world, int x, int y, int z, Block block) {
        return block.canCreatureSpawn(creatureType, world, x, y, z);
    }

    /**
     * @author iamacatfr
     * @reason optimize findChunksForSpawning
     */
    @Overwrite
    public int findChunksForSpawning(WorldServer world, boolean peaceful, boolean hostile, boolean animals) {
        if (!peaceful && !hostile) {
            return 0;
        }

        optimizationsAndTweaks$clearEligibleChunksForSpawning();

        optimizationsAndTweaks$populateEligibleChunks(world);

        int spawnedEntities = 0;
        ChunkCoordinates spawnPoint = world.getSpawnPoint();

        EnumCreatureType[] creatureTypes = EnumCreatureType.values();
        for (EnumCreatureType creatureType : creatureTypes) {
            if (optimizationsAndTweaks$shouldSpawnCreatureType(creatureType, world, peaceful, hostile, animals)) {
                List<ChunkCoordIntPair> eligibleChunks = new ArrayList<>(optimizationsAndTweaks$eligibleChunksForSpawning.keySet());
                Collections.shuffle(eligibleChunks);

                spawnedEntities += optimizationsAndTweaks$spawnEntitiesInChunks(world, creatureType, eligibleChunks, spawnPoint);
            }
        }

        return spawnedEntities;
    }

    @Unique
    private void optimizationsAndTweaks$clearEligibleChunksForSpawning() {
        optimizationsAndTweaks$eligibleChunksForSpawning.clear();
    }

    @Unique
    private void optimizationsAndTweaks$populateEligibleChunks(WorldServer world) {
        for (Object playerObj : world.playerEntities) {
            if (playerObj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) playerObj;
                int chunkX = MathHelper.floor_double(player.posX / 16.0D);
                int chunkZ = MathHelper.floor_double(player.posZ / 16.0D);
                optimizationsAndTweaks$populateChunksAroundPlayer(chunkX, chunkZ);
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$populateChunksAroundPlayer(int chunkX, int chunkZ) {
        int b0 = 8;
        for (int l = -b0; l <= b0; ++l) {
            for (int i1 = -b0; i1 <= b0; ++i1) {
                boolean isEdge = l == -b0 || l == b0 || i1 == -b0 || i1 == b0;
                ChunkCoordIntPair chunkCoord = new ChunkCoordIntPair(l + chunkX, i1 + chunkZ);

                if (!isEdge) {
                    optimizationsAndTweaks$eligibleChunksForSpawning.put(chunkCoord, Boolean.FALSE);
                } else if (!optimizationsAndTweaks$eligibleChunksForSpawning.containsKey(chunkCoord)) {
                    optimizationsAndTweaks$eligibleChunksForSpawning.put(chunkCoord, Boolean.TRUE);
                }
            }
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$shouldSpawnCreatureType(EnumCreatureType creatureType, WorldServer world, boolean peaceful, boolean hostile, boolean animals) {
        return (!creatureType.getPeacefulCreature() || hostile)
            && (creatureType.getPeacefulCreature() || peaceful)
            && (!creatureType.getAnimal() || animals)
            && world.countEntities(creatureType, true) <= creatureType.getMaxNumberOfCreature() * optimizationsAndTweaks$eligibleChunksForSpawning.size() / 256;
    }

    @Unique
    private int optimizationsAndTweaks$spawnEntitiesInChunks(WorldServer world, EnumCreatureType creatureType, List<ChunkCoordIntPair> eligibleChunks, ChunkCoordinates spawnPoint) {
        int spawnedEntities = 0;

        for (ChunkCoordIntPair chunkCoord : eligibleChunks) {
            if (Boolean.FALSE.equals(optimizationsAndTweaks$eligibleChunksForSpawning.get(chunkCoord))) {
                spawnedEntities += optimizationsAndTweaks$spawnEntitiesInChunk(world, creatureType, chunkCoord, spawnPoint);
            }
        }

        return spawnedEntities;
    }

    @Unique
    private int optimizationsAndTweaks$spawnEntitiesInChunk(WorldServer world, EnumCreatureType creatureType, ChunkCoordIntPair chunkCoord, ChunkCoordinates spawnPoint) {
        ChunkPosition chunkPosition = func_151350_a(world, chunkCoord.chunkXPos, chunkCoord.chunkZPos);

        int i = 0;
        int maxSpawnAttempts = 3;

        float spawnX = 0.0F;
        float spawnY = 0.0F;
        float offsetX, offsetY, offsetZ;

        for (int attempt = 0; attempt < maxSpawnAttempts; attempt++) {
            int x = chunkPosition.chunkPosX + world.rand.nextInt(6) - world.rand.nextInt(6);
            int y = chunkPosition.chunkPosY + world.rand.nextInt(1) - world.rand.nextInt(1);
            int z = chunkPosition.chunkPosZ + world.rand.nextInt(6) - world.rand.nextInt(6);

            spawnX = x + 0.5F;
            spawnY = z + 0.5F;

            offsetX = spawnX - (float) spawnPoint.posX;
            offsetY = y - (float) spawnPoint.posY;
            offsetZ = spawnY - (float) spawnPoint.posZ;

            float distanceSquared = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;

            if (distanceSquared >= 576.0F && canCreatureTypeSpawnAtLocation(creatureType, world, x, y, z) && (world.getClosestPlayer(spawnX, (float) y, spawnY, 24.0D) == null)) {
                BiomeGenBase.SpawnListEntry spawnListEntry = world.spawnRandomCreature(creatureType, x, y, z);
                if (spawnListEntry != null) {
                    EntityLiving entityLiving = optimizationsAndTweaks$createEntityInstance(world, spawnListEntry);

                    assert entityLiving != null;
                    entityLiving.setLocationAndAngles(spawnX, (float) y, spawnY, world.rand.nextFloat() * 360.0F, 0.0F);

                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entityLiving, world, spawnX, (float) y, spawnY);
                    if (canSpawn == Event.Result.ALLOW || (canSpawn == Event.Result.DEFAULT && entityLiving.getCanSpawnHere())) {
                        i++;
                    }
                }
            }
        }

        if (i > 0) {
            optimizationsAndTweaks$spawnEntities(world, creatureType, spawnX, spawnY, i);
        }

        return i;
    }
    @Unique
    private void optimizationsAndTweaks$spawnEntities(WorldServer world, EnumCreatureType creatureType, float spawnX, float spawnY, int count) {
        for (int j = 0; j < count; j++) {
            BiomeGenBase.SpawnListEntry spawnListEntry = world.spawnRandomCreature(creatureType, (int) spawnX, (int) spawnY, (int) spawnX);
            if (spawnListEntry != null) {
                EntityLiving entityLiving = optimizationsAndTweaks$createEntityInstance(world, spawnListEntry);

                assert entityLiving != null;
                entityLiving.setLocationAndAngles(spawnX, spawnY, spawnX, world.rand.nextFloat() * 360.0F, 0.0F);

                world.spawnEntityInWorld(entityLiving);

                Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entityLiving, world, spawnX, spawnY, spawnX);
                if (canSpawn == Event.Result.ALLOW || (canSpawn == Event.Result.DEFAULT && entityLiving.getCanSpawnHere())) {
                    if (!ForgeEventFactory.doSpecialSpawn(entityLiving, world, spawnX, spawnY, spawnX)) {
                        entityLiving.onSpawnWithEgg(null);
                    }
                }
            }
        }
    }

    @Unique
    private EntityLiving optimizationsAndTweaks$createEntityInstance(WorldServer world, BiomeGenBase.SpawnListEntry spawnListEntry) {
        try {
            return (EntityLiving) spawnListEntry.entityClass.getConstructor(World.class).newInstance(world);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
