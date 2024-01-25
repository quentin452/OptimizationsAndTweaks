package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import static fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla.SpawnerAnimalsTask.*;

@Mixin(value = SpawnerAnimals.class, priority = 999)
public class MixinPatchSpawnerAnimals {
    @Unique
    private int optimizationsAndTweaks$spawnRadius;
    @Unique
    private static int optimizationsAndTweaks$spawnedEntities;
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
}
