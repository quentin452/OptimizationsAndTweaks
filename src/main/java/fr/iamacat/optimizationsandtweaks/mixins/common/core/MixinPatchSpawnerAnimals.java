package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.lang.reflect.Constructor;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.common.eventhandler.Event;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;

@Mixin(value = SpawnerAnimals.class, priority = 999)
public class MixinPatchSpawnerAnimals {

    @Shadow
    protected static ChunkPosition func_151350_a(World p_151350_0_, int p_151350_1_, int p_151350_2_) {
        Chunk chunk = p_151350_0_.getChunkFromChunkCoords(p_151350_1_, p_151350_2_);
        int k = p_151350_1_ * 16 + p_151350_0_.rand.nextInt(16);
        int l = p_151350_2_ * 16 + p_151350_0_.rand.nextInt(16);

        if (chunk != null) {
            int i1 = p_151350_0_.rand.nextInt(chunk.getTopFilledSegment() + 16 - 1);
            return new ChunkPosition(k, i1, l);
        } else {
            return new ChunkPosition(k, p_151350_0_.rand.nextInt(p_151350_0_.getActualHeight()), l);
        }
    }

    @Unique
    private Object2ObjectHashMap<ChunkCoordIntPair, Boolean> multithreadingandtweaks$eligibleChunksForSpawning = new Object2ObjectHashMap<>();

    /**
     * @author iamacatfr
     * @reason greatly reduce TPS lags on VoidWorld and more
     */
    @Overwrite
    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType p_77190_0_, World p_77190_1_, int p_77190_2_,
        int p_77190_3_, int p_77190_4_) {
        if (OptimizationsandTweaksConfig.enableMixinPatchSpawnerAnimals) {
            if (p_77190_0_.getCreatureMaterial() == Material.water) {
                return p_77190_1_.getBlock(p_77190_2_, p_77190_3_, p_77190_4_)
                    .getMaterial()
                    .isLiquid()
                    && p_77190_1_.getBlock(p_77190_2_, p_77190_3_ - 1, p_77190_4_)
                        .getMaterial()
                        .isLiquid()
                    && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_ + 1, p_77190_4_)
                        .isNormalCube();
            } else if (!World.doesBlockHaveSolidTopSurface(p_77190_1_, p_77190_2_, p_77190_3_ - 1, p_77190_4_)) {
                return false;
            } else {
                Block block = p_77190_1_.getBlock(p_77190_2_, p_77190_3_ - 1, p_77190_4_);
                boolean spawnBlock = block
                    .canCreatureSpawn(p_77190_0_, p_77190_1_, p_77190_2_, p_77190_3_ - 1, p_77190_4_);
                return spawnBlock && block != Blocks.bedrock
                    && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_, p_77190_4_)
                        .isNormalCube()
                    && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_, p_77190_4_)
                        .getMaterial()
                        .isLiquid()
                    && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_ + 1, p_77190_4_)
                        .isNormalCube();
            }
        }
        return false;
    }

    @Unique
    private Object2ObjectHashMap<Class<? extends EntityLiving>, Constructor<? extends EntityLiving>> constructorCache = new Object2ObjectHashMap<>();

    /**
     * @author iamacatfr
     * @reason optimize findChunksForSpawning
     */
    @Overwrite
    public int findChunksForSpawning(WorldServer p_77192_1_, boolean p_77192_2_, boolean p_77192_3_,
        boolean p_77192_4_) {
        if (OptimizationsandTweaksConfig.enableMixinPatchSpawnerAnimals) {
            if (!p_77192_2_ && !p_77192_3_) {
                return 0;
            } else {
                this.multithreadingandtweaks$eligibleChunksForSpawning.clear();
                int i;
                int k;

                for (i = 0; i < p_77192_1_.playerEntities.size(); ++i) {
                    EntityPlayer entityplayer = (EntityPlayer) p_77192_1_.playerEntities.get(i);
                    int j = MathHelper.floor_double(entityplayer.posX / 16.0D);
                    k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
                    byte b0 = 8;

                    for (int l = -b0; l <= b0; ++l) {
                        for (int i1 = -b0; i1 <= b0; ++i1) {
                            boolean flag3 = l == -b0 || l == b0 || i1 == -b0 || i1 == b0;
                            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(l + j, i1 + k);

                            if (!flag3) {
                                this.multithreadingandtweaks$eligibleChunksForSpawning
                                    .put(chunkcoordintpair, Boolean.FALSE);
                            } else if (!this.multithreadingandtweaks$eligibleChunksForSpawning
                                .containsKey(chunkcoordintpair)) {
                                    this.multithreadingandtweaks$eligibleChunksForSpawning
                                        .put(chunkcoordintpair, Boolean.TRUE);
                                }
                        }
                    }
                }

                i = 0;
                ChunkCoordinates chunkcoordinates = p_77192_1_.getSpawnPoint();
                EnumCreatureType[] aenumcreaturetype = EnumCreatureType.values();
                k = aenumcreaturetype.length;

                for (int k3 = 0; k3 < k; ++k3) {
                    EnumCreatureType enumcreaturetype = aenumcreaturetype[k3];

                    if ((!enumcreaturetype.getPeacefulCreature() || p_77192_3_)
                        && (enumcreaturetype.getPeacefulCreature() || p_77192_2_)
                        && (!enumcreaturetype.getAnimal() || p_77192_4_)
                        && p_77192_1_.countEntities(enumcreaturetype, true) <= enumcreaturetype.getMaxNumberOfCreature()
                            * this.multithreadingandtweaks$eligibleChunksForSpawning.size()
                            / 256) {
                        this.multithreadingandtweaks$eligibleChunksForSpawning.keySet()
                            .iterator();
                        Iterator<ChunkCoordIntPair> iterator;
                        ArrayList<ChunkCoordIntPair> tmp = new ArrayList<>(
                            multithreadingandtweaks$eligibleChunksForSpawning.keySet());
                        Collections.shuffle(tmp);
                        iterator = tmp.iterator();

                        while (iterator.hasNext()) {
                            ChunkCoordIntPair chunkcoordintpair1 = iterator.next();

                            if (!this.multithreadingandtweaks$eligibleChunksForSpawning.get(chunkcoordintpair1)) {
                                ChunkPosition chunkposition = func_151350_a(
                                    p_77192_1_,
                                    chunkcoordintpair1.chunkXPos,
                                    chunkcoordintpair1.chunkZPos);
                                int j1 = chunkposition.chunkPosX;
                                int k1 = chunkposition.chunkPosY;
                                int l1 = chunkposition.chunkPosZ;

                                if (!p_77192_1_.getBlock(j1, k1, l1)
                                    .isNormalCube()
                                    && p_77192_1_.getBlock(j1, k1, l1)
                                        .getMaterial() == enumcreaturetype.getCreatureMaterial()) {
                                    int i2 = 0;
                                    int j2 = 0;

                                    while (j2 < 3) {
                                        int k2 = j1;
                                        int l2 = k1;
                                        int i3 = l1;
                                        byte b1 = 6;
                                        BiomeGenBase.SpawnListEntry spawnlistentry = null;
                                        IEntityLivingData ientitylivingdata = null;
                                        int j3 = 0;

                                        while (true) {
                                            if (j3 < 4) {
                                                {
                                                    k2 += p_77192_1_.rand.nextInt(b1);
                                                    l2 += 0;
                                                    i3 += p_77192_1_.rand.nextInt(b1);

                                                    if (canCreatureTypeSpawnAtLocation(
                                                        enumcreaturetype,
                                                        p_77192_1_,
                                                        k2,
                                                        l2,
                                                        i3)) {
                                                        float f = k2 + 0.5F;
                                                        float f2 = i3 + 0.5F;

                                                        if (p_77192_1_.getClosestPlayer(f, (float) l2, f2, 24.0D)
                                                            == null) {
                                                            float f3 = f - (float) chunkcoordinates.posX;
                                                            float f4 = l2 - (float) chunkcoordinates.posY;
                                                            float f5 = f2 - (float) chunkcoordinates.posZ;
                                                            float f6 = f3 * f3 + f4 * f4 + f5 * f5;

                                                            if (f6 >= 576.0F) {
                                                                if (spawnlistentry == null) {
                                                                    spawnlistentry = p_77192_1_.spawnRandomCreature(
                                                                        enumcreaturetype,
                                                                        k2,
                                                                        l2,
                                                                        i3);

                                                                    if (spawnlistentry == null) {
                                                                        break;
                                                                    }
                                                                }

                                                                EntityLiving entityliving;

                                                                if (constructorCache
                                                                    .containsKey(spawnlistentry.entityClass)) {
                                                                    try {
                                                                        entityliving = constructorCache
                                                                            .get(spawnlistentry.entityClass)
                                                                            .newInstance(p_77192_1_);
                                                                    } catch (Exception exception) {
                                                                        exception.printStackTrace();
                                                                        return i;
                                                                    }
                                                                } else {
                                                                    try {
                                                                        Constructor<? extends EntityLiving> constructor = spawnlistentry.entityClass
                                                                            .getConstructor(World.class);
                                                                        entityliving = constructor
                                                                            .newInstance(p_77192_1_);
                                                                        constructorCache.put(
                                                                            spawnlistentry.entityClass,
                                                                            constructor);
                                                                    } catch (Exception exception) {
                                                                        exception.printStackTrace();
                                                                        return i;
                                                                    }
                                                                }

                                                                entityliving.setLocationAndAngles(
                                                                    f,
                                                                    (float) l2,
                                                                    f2,
                                                                    p_77192_1_.rand.nextFloat() * 360.0F,
                                                                    0.0F);

                                                                Event.Result canSpawn = ForgeEventFactory
                                                                    .canEntitySpawn(
                                                                        entityliving,
                                                                        p_77192_1_,
                                                                        f,
                                                                        (float) l2,
                                                                        f2);
                                                                if (canSpawn == Event.Result.ALLOW
                                                                    || (canSpawn == Event.Result.DEFAULT
                                                                        && entityliving.getCanSpawnHere())) {
                                                                    ++i2;
                                                                    p_77192_1_.spawnEntityInWorld(entityliving);
                                                                    if (!ForgeEventFactory.doSpecialSpawn(
                                                                        entityliving,
                                                                        p_77192_1_,
                                                                        f,
                                                                        (float) l2,
                                                                        f2)) {
                                                                        ientitylivingdata = entityliving
                                                                            .onSpawnWithEgg(ientitylivingdata);
                                                                    }

                                                                    if (j2 >= ForgeEventFactory
                                                                        .getMaxSpawnPackSize(entityliving)) {
                                                                        continue;
                                                                    }
                                                                }

                                                                i += i2;
                                                            }
                                                        }
                                                    }

                                                    ++j3;
                                                    continue;
                                                }
                                            }

                                            ++j2;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                return i;
            }
        }

        return 0;
    }

    /**
     * @author iamacatfr
     * @reason optimize findChunksForSpawning
     */
    // todo fix when using this maintainable code i got this errors : Entity is already tracked! in game
    // i cannot figure out of this issue, i want to destroy my computer
    /*
     * @Overwrite
     * public int findChunksForSpawning(WorldServer p_77192_1_, boolean p_77192_2_, boolean p_77192_3_, boolean
     * p_77192_4_) {
     * if (!shouldSpawnAnimals(p_77192_2_, p_77192_3_)) {
     * return 0;
     * }
     * this.clearEligibleChunksForSpawning();
     * int playerCount = p_77192_1_.playerEntities.size();
     * // Loop through players
     * for (int i = 0; i < playerCount; ++i) {
     * handlePlayerForSpawning(p_77192_1_, i);
     * }
     * int spawnedEntities = 0;
     * ChunkCoordinates spawnPoint = p_77192_1_.getSpawnPoint();
     * EnumCreatureType[] creatureTypes = EnumCreatureType.values();
     * // Loop through creature types
     * for (EnumCreatureType creatureType : creatureTypes) {
     * if (shouldSpawnCreatureType(p_77192_1_, p_77192_2_, p_77192_3_, p_77192_4_, creatureType)) {
     * spawnedEntities += spawnCreatures(p_77192_1_, spawnPoint, creatureType);
     * }
     * }
     * return spawnedEntities;
     * }
     * @Unique
     * private boolean shouldSpawnAnimals(boolean peaceful, boolean hostile) {
     * return peaceful || hostile;
     * }
     * @Unique
     * private void clearEligibleChunksForSpawning() {
     * this.multithreadingandtweaks$eligibleChunksForSpawning.clear();
     * }
     * @Unique
     * private void handlePlayerForSpawning(WorldServer p_77192_1_, int i) {
     * EntityPlayer entityplayer = (EntityPlayer) p_77192_1_.playerEntities.get(i);
     * int j = MathHelper.floor_double(entityplayer.posX / 16.0D);
     * int k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
     * byte b0 = 8;
     * // Loop through chunks around the player
     * for (int l = -b0; l <= b0; ++l) {
     * for (int i1 = -b0; i1 <= b0; ++i1) {
     * handleChunk(entityplayer, j, k, l, i1);
     * }
     * }
     * }
     * @Unique
     * private void handleChunk(EntityPlayer entityplayer, int j, int k, int l, int i1) {
     * boolean flag3 = l == -8 || l == 8 || i1 == -8 || i1 == 8;
     * ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(l + j, i1 + k);
     * if (!flag3) {
     * this.multithreadingandtweaks$eligibleChunksForSpawning.put(chunkcoordintpair, Boolean.FALSE);
     * } else if (!this.multithreadingandtweaks$eligibleChunksForSpawning.containsKey(chunkcoordintpair)) {
     * this.multithreadingandtweaks$eligibleChunksForSpawning.put(chunkcoordintpair, Boolean.TRUE);
     * }
     * }
     * @Unique
     * private boolean shouldSpawnCreatureType(WorldServer p_77192_1_, boolean peaceful, boolean hostile, boolean
     * animals, EnumCreatureType creatureType) {
     * return ((!creatureType.getPeacefulCreature() || hostile) &&
     * (creatureType.getPeacefulCreature() || peaceful) &&
     * (!creatureType.getAnimal() || animals) &&
     * p_77192_1_.countEntities(creatureType, true) <= creatureType.getMaxNumberOfCreature() *
     * this.multithreadingandtweaks$eligibleChunksForSpawning.size() / 256);
     * }
     * @Unique
     * private int spawnCreatures(WorldServer p_77192_1_, ChunkCoordinates spawnPoint, EnumCreatureType creatureType) {
     * int spawnedEntities = 0;
     * List<ChunkCoordIntPair> eligibleChunks = new
     * ArrayList<>(multithreadingandtweaks$eligibleChunksForSpawning.keySet());
     * Collections.shuffle(eligibleChunks);
     * // Loop through eligible chunks
     * for (ChunkCoordIntPair chunkcoordintpair1 : eligibleChunks) {
     * if (!this.multithreadingandtweaks$eligibleChunksForSpawning.get(chunkcoordintpair1)) {
     * ChunkPosition chunkposition = func_151350_a(p_77192_1_, chunkcoordintpair1.chunkXPos,
     * chunkcoordintpair1.chunkZPos);
     * int j1 = chunkposition.chunkPosX;
     * int k1 = chunkposition.chunkPosY;
     * int l1 = chunkposition.chunkPosZ;
     * // Check if the block is suitable for creature spawning
     * if (!p_77192_1_.getBlock(j1, k1, l1).isNormalCube() &&
     * p_77192_1_.getBlock(j1, k1, l1).getMaterial() == creatureType.getCreatureMaterial()) {
     * spawnedEntities += spawnEntitiesInChunk(p_77192_1_, spawnPoint, creatureType, j1, k1, l1);
     * }
     * }
     * }
     * return spawnedEntities;
     * }
     * @Unique
     * private int spawnEntitiesInChunk(WorldServer p_77192_1_, ChunkCoordinates spawnPoint, EnumCreatureType
     * creatureType, int j1, int k1, int l1) {
     * int i2 = 0;
     * for (int j2 = 0; j2 < 3; ++j2) {
     * int k2 = j1;
     * int i3 = l1;
     * byte b1 = 6;
     * BiomeGenBase.SpawnListEntry spawnlistentry = null;
     * IEntityLivingData ientitylivingdata = null;
     * for (int j3 = 0; j3 < 4; ++j3) {
     * k2 += p_77192_1_.rand.nextInt(b1) - p_77192_1_.rand.nextInt(b1);
     * i3 += p_77192_1_.rand.nextInt(b1) - p_77192_1_.rand.nextInt(b1);
     * i2 += attemptSpawnEntity(p_77192_1_, creatureType, k2, k1, i3, spawnlistentry, ientitylivingdata,spawnPoint);
     * }
     * }
     * return i2;
     * }
     * @Unique
     * private int attemptSpawnEntity(WorldServer p_77192_1_, EnumCreatureType creatureType, int k2, int k1, int i3,
     * BiomeGenBase.SpawnListEntry spawnlistentry, IEntityLivingData ientitylivingdata, ChunkCoordinates spawnPoint) {
     * k2 += p_77192_1_.rand.nextInt(6) - p_77192_1_.rand.nextInt(6);
     * i3 += p_77192_1_.rand.nextInt(6) - p_77192_1_.rand.nextInt(6);
     * if (canCreatureTypeSpawnAtLocation(creatureType, p_77192_1_, k2, k1, i3)) {
     * float f = k2 + 0.5F;
     * float f2 = i3 + 0.5F;
     * if (p_77192_1_.getClosestPlayer(f, (float) k1, f2, 24.0D) == null) {
     * float f3 = f - (float) spawnPoint.posX;
     * float f4 = k1 - (float) spawnPoint.posY;
     * float f5 = f2 - (float) spawnPoint.posZ;
     * float f6 = f3 * f3 + f4 * f4 + f5 * f5;
     * if (f6 >= 576.0F) {
     * if (spawnlistentry == null) {
     * spawnlistentry = p_77192_1_.spawnRandomCreature(creatureType, k2, k1, i3);
     * if (spawnlistentry == null) {
     * return 0;
     * }
     * }
     * return spawnEntityAndHandleEvents(p_77192_1_, creatureType, k2, k1, i3, spawnlistentry, ientitylivingdata);
     * }
     * }
     * }
     * return 0;
     * }
     * @Unique
     * private int spawnEntityAndHandleEvents(WorldServer p_77192_1_, EnumCreatureType creatureType, int k2, int k1, int
     * i3, BiomeGenBase.SpawnListEntry spawnlistentry, IEntityLivingData ientitylivingdata) {
     * EntityLiving entityliving = createEntity(p_77192_1_, spawnlistentry.entityClass);
     * if (entityliving == null) {
     * return 0;
     * }
     * entityliving.setLocationAndAngles(k2 + 0.5F, (float) k1, i3 + 0.5F, p_77192_1_.rand.nextFloat() * 360.0F, 0.0F);
     * Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving, p_77192_1_, k2 + 0.5F, (float) k1, i3 +
     * 0.5F);
     * if (canSpawn == Event.Result.ALLOW || (canSpawn == Event.Result.DEFAULT && entityliving.getCanSpawnHere())) {
     * int maxPackSize = ForgeEventFactory.getMaxSpawnPackSize(entityliving);
     * int i2 = 0;
     * for (int j2 = 0; j2 < maxPackSize; ++j2) {
     * if (p_77192_1_.spawnEntityInWorld(entityliving)) {
     * ientitylivingdata = entityliving.onSpawnWithEgg(ientitylivingdata);
     * i2++;
     * }
     * }
     * return i2;
     * }
     * return 0;
     * }
     * @Unique
     * private EntityLiving createEntity(WorldServer p_77192_1_, Class<? extends EntityLiving> entityClass) {
     * try {
     * return entityClass.getConstructor(World.class).newInstance(p_77192_1_);
     * } catch (Exception exception) {
     * exception.printStackTrace();
     * return null;
     * }
     * }
     */
}
