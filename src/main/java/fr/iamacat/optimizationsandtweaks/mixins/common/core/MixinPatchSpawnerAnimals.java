package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import static fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.CreatureCountTask.optimizationsAndTweaks$eligibleChunksForSpawning;
import static fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.CreatureCountTask.optimizationsAndTweaks$shouldSpawnCreature;

@Mixin(value = SpawnerAnimals.class, priority = 999)
public class MixinPatchSpawnerAnimals {
    @Unique
    private static float optimizationsAndTweaks$distanceSquared;
    @Unique
    private int optimizationsAndTweaks$spawnRadius;
    @Unique
    private static int optimizationsAndTweaks$spawnedEntities;
    @Unique
    private static int optimizationsAndTweaks$maxSpawnAttempts;
    @Unique
    private static int optimizationsAndTweaks$x;
    @Unique
    private static int optimizationsAndTweaks$z;
    @Unique
    private static int optimizationsAndTweaks$minY;
    @Unique
    private static int optimizationsAndTweaks$maxY;
    @Unique
    private static int optimizationsAndTweaks$y;
    @Unique
    private static float optimizationsAndTweaks$spawnX;
    @Unique
    private static float optimizationsAndTweaks$spawnZ;
    @Unique
    private int optimizationsAndTweaks$chunkX;
    @Unique
    private int optimizationsAndTweaks$chunkZ;
    @Unique
    private int optimizationsAndTweaks$l;
    @Unique
    private int optimizationsAndTweaks$i1;

    @Overwrite
    protected static ChunkPosition func_151350_a(World world, int chunkX, int chunkZ) {
        optimizationsAndTweaks$x = chunkX * 16 + world.rand.nextInt(16);
        optimizationsAndTweaks$z = chunkZ * 16 + world.rand.nextInt(16);
        optimizationsAndTweaks$minY = 0;
        optimizationsAndTweaks$maxY = world.getActualHeight();
        optimizationsAndTweaks$y = world.rand.nextInt(optimizationsAndTweaks$maxY - optimizationsAndTweaks$minY) + optimizationsAndTweaks$minY;
        return new ChunkPosition(optimizationsAndTweaks$x, optimizationsAndTweaks$y, optimizationsAndTweaks$z);
    }

    /**
     @author
     @reason
     */
    @Overwrite
    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, int x, int y, int z) {
        Material creatureMaterial = creatureType.getCreatureMaterial();

        if (creatureMaterial == Material.water) {
            return optimizationsAndTweaks$canCreatureSpawnInWater(world.getBlock(x, y, z), world.getBlock(x, y - 1, z), world.getBlock(x, y + 1, z));
        } else {
            return optimizationsAndTweaks$canCreatureSpawnOnLand(creatureType, world, x, y, z);
        }
    }

    @Unique
    private static boolean optimizationsAndTweaks$doesBlockHaveSolidTopSurface(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block.isOpaqueCube() && block.isNormalCube();
    }

    @Unique
    private static boolean optimizationsAndTweaks$canCreatureSpawnOnLand(EnumCreatureType creatureType, World world, int x, int y, int z) {
        boolean hasSolidTopSurface = false;

        if (y > 0) {
            hasSolidTopSurface = optimizationsAndTweaks$doesBlockHaveSolidTopSurface(world, x, y - 1, z);
        }

        if (!hasSolidTopSurface) {
            return false;
        }

        boolean isPeacefulCreature = creatureType.getPeacefulCreature();
        boolean isAnimal = creatureType.getAnimal();

        if ((!isPeacefulCreature || isAnimal) && optimizationsAndTweaks$shouldSpawnCreature(creatureType, world, optimizationsAndTweaks$eligibleChunksForSpawning)) {
            Block block = world.getBlock(x, y, z);
            Block blockAbove = world.getBlock(x, y + 1, z);

            return block != Blocks.bedrock && !blockAbove.isNormalCube() && !blockAbove.getMaterial().isLiquid();
        }

        return false;
    }

    @Unique
    private static boolean optimizationsAndTweaks$canCreatureSpawnInWater(Block block, Block blockBelow, Block blockAbove) {
        Material blockMaterial = block.getMaterial();
        Material blockBelowMaterial = blockBelow.getMaterial();
        boolean isNormalCubeAbove = blockAbove.isNormalCube();
        return blockMaterial.isLiquid() && blockBelowMaterial.isLiquid() && !isNormalCubeAbove;
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

        optimizationsAndTweaks$spawnedEntities = 0;

        for (EnumCreatureType creatureType : EnumCreatureType.values()) {
            if (optimizationsAndTweaks$shouldSpawnCreatureType(creatureType, world, peaceful, hostile, animals)) {
                for (ChunkCoordinates chunkCoord : optimizationsAndTweaks$eligibleChunksForSpawning) {
                    optimizationsAndTweaks$spawnedEntities = optimizationsAndTweaks$spawnEntitiesInChunk(world, creatureType, chunkCoord);
                }
            }
        }

        return optimizationsAndTweaks$spawnedEntities;
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
                optimizationsAndTweaks$chunkX = MathHelper.floor_double(player.posX / 16.0D);
                optimizationsAndTweaks$chunkZ = MathHelper.floor_double(player.posZ / 16.0D);
                optimizationsAndTweaks$populateChunksAroundPlayer(optimizationsAndTweaks$chunkX, optimizationsAndTweaks$chunkZ);
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$populateChunksAroundPlayer(int chunkX, int chunkZ) {
        optimizationsAndTweaks$spawnRadius = 8;

        for (optimizationsAndTweaks$l = -optimizationsAndTweaks$spawnRadius; optimizationsAndTweaks$l <= optimizationsAndTweaks$spawnRadius; ++optimizationsAndTweaks$l) {
            for (optimizationsAndTweaks$i1 = -optimizationsAndTweaks$spawnRadius; optimizationsAndTweaks$i1 <= optimizationsAndTweaks$spawnRadius; ++optimizationsAndTweaks$i1) {
                ChunkCoordinates chunkCoord = new ChunkCoordinates(optimizationsAndTweaks$l + chunkX, optimizationsAndTweaks$i1, chunkZ);
                optimizationsAndTweaks$eligibleChunksForSpawning.add(chunkCoord);
            }
        }

    }

    @Unique
    private boolean optimizationsAndTweaks$shouldSpawnCreatureType(EnumCreatureType creatureType, WorldServer world, boolean peaceful,
                                                                   boolean hostile, boolean animals) {
        return (!creatureType.getPeacefulCreature() || hostile) && (creatureType.getPeacefulCreature() || peaceful)
            && (!creatureType.getAnimal() || animals)
            && world.countEntities(creatureType, true) <= creatureType.getMaxNumberOfCreature()
            * optimizationsAndTweaks$eligibleChunksForSpawning.size() / 256;
    }

    // do not refactor this method into smaller methods: it can cause Entity is already tracked! errors
    // todo fix Entity is already tracked! errors while refactoring the method into smaller methods
    // why i want to refactor this method into smallers : because for maintanibility, detecting performances bottlenecks

    @Unique
    private static int optimizationsAndTweaks$spawnEntitiesInChunk(WorldServer world, EnumCreatureType creatureType, ChunkCoordinates chunkCoord) {
        ChunkPosition chunkPosition = func_151350_a(world, chunkCoord.posX, chunkCoord.posZ);

        optimizationsAndTweaks$spawnedEntities = 0;
        optimizationsAndTweaks$maxSpawnAttempts = 7;

        for (int attempt = 0; attempt < optimizationsAndTweaks$maxSpawnAttempts; attempt++) {
            int x = chunkPosition.chunkPosX + world.rand.nextInt(6) - world.rand.nextInt(6);
            int y = chunkPosition.chunkPosY + world.rand.nextInt(1) - world.rand.nextInt(1);
            int z = chunkPosition.chunkPosZ + world.rand.nextInt(6) - world.rand.nextInt(6);

            optimizationsAndTweaks$spawnX = x + 0.5F;
            optimizationsAndTweaks$spawnZ = z + 0.5F;

            optimizationsAndTweaks$distanceSquared = optimizationsAndTweaks$spawnX * optimizationsAndTweaks$spawnX + optimizationsAndTweaks$spawnZ * optimizationsAndTweaks$spawnZ;

            if (optimizationsAndTweaks$isSpawnLocationValid(creatureType, world, optimizationsAndTweaks$spawnX, y, optimizationsAndTweaks$spawnZ) && optimizationsAndTweaks$isPlayerCloseEnough(world, optimizationsAndTweaks$spawnX, y, optimizationsAndTweaks$spawnZ)) {
                BiomeGenBase.SpawnListEntry spawnListEntry = optimizationsAndTweaks$createSpawnListEntry(creatureType, world, x, y, z);

                if (spawnListEntry != null) {
                    EntityLiving entityLiving = optimizationsAndTweaks$createEntityInstance(world, spawnListEntry);

                    if (entityLiving != null) {
                        entityLiving.setLocationAndAngles(optimizationsAndTweaks$spawnX, y, optimizationsAndTweaks$spawnZ, world.rand.nextFloat() * 360.0F, 0.0F);

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

    @Unique
    private static BiomeGenBase.SpawnListEntry optimizationsAndTweaks$createSpawnListEntry(EnumCreatureType creatureType, World world, int x, int y, int z) {
        WorldServer worldServer = (WorldServer) world;
        return worldServer.spawnRandomCreature(creatureType, x, y, z);
    }

    @Unique
    private static boolean optimizationsAndTweaks$isSpawnLocationValid(EnumCreatureType creatureType, World world, float spawnX, float spawnY,
                                                                       float spawnZ) {
        boolean canSpawn = optimizationsAndTweaks$canCreatureTypeSpawnAtLocation(creatureType, world, spawnX, spawnY, spawnZ);
        return optimizationsAndTweaks$distanceSquared >= 576.0F && canSpawn;
    }

    @Unique
    private static boolean optimizationsAndTweaks$canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, float spawnX, float spawnY,
                                                                                 float spawnZ) {
        return canCreatureTypeSpawnAtLocation(creatureType, world, (int) spawnX, (int) spawnY, (int) spawnZ);
    }

    @Unique
    private static boolean optimizationsAndTweaks$isPlayerCloseEnough(World world, float spawnX, float spawnY, float spawnZ) {
        return world.getClosestPlayer(spawnX, spawnY, spawnZ, 24.0D) == null;
    }

    @Unique
    private static final Set<Constructor<? extends EntityLiving>> optimizationsAndTweaks$constructorCache = new HashSet<>();

    @Unique
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

    @Unique
    private static boolean optimizationsAndTweaks$canEntitySpawn(EntityLiving entity, World world, double x, double y, double z) {
        Chunk chunk = world.getChunkFromChunkCoords(MathHelper.floor_double(x) >> 4, MathHelper.floor_double(z) >> 4);
        if (!chunk.isChunkLoaded) {
            return false;
        }
        Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entity, world, (float) x, (float) y, (float) z);
        return canSpawn == Event.Result.ALLOW || (canSpawn == Event.Result.DEFAULT && entity.getCanSpawnHere());
    }

    @Unique
    private static boolean optimizationsAndTweaks$doSpecialSpawn(EntityLiving entity, World world, double x, double y, double z) {
        return ForgeEventFactory.doSpecialSpawn(entity, world, (float) x, (float) y, (float) z);
    }
}
