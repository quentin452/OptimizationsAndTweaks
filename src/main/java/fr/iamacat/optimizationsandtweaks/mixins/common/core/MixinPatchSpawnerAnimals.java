package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import cpw.mods.fml.common.eventhandler.Event;
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
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

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

    @Shadow
    private HashMap eligibleChunksForSpawning = new HashMap();

    /**
     * @author iamacatfr
     * @reason greatly reduce TPS lags on VoidWorld and more
     */
    @Overwrite
    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y - 1, z);
        Block blockAbove = world.getBlock(x, y, z);
        Block blockBelow = world.getBlock(x, y + 1, z);
            if (creatureType.getCreatureMaterial() == Material.water) {
                return block.getMaterial().isLiquid()
                    && blockBelow.getMaterial().isLiquid()
                    && !blockAbove.isNormalCube();
            } else if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
                return false;
            } else {
                boolean canSpawn = block.canCreatureSpawn(creatureType, world, x, y - 1, z);
                return canSpawn && block != Blocks.bedrock
                    && !blockAbove.isNormalCube()
                    && !blockAbove.getMaterial().isLiquid()
                    && !world.getBlock(x, y + 1, z).isNormalCube();
            }
    }

    /**
     * @author iamacatfr
     * @reason optimize findChunksForSpawning
     */
    @Overwrite
    public int findChunksForSpawning(WorldServer p_77192_1_, boolean p_77192_2_, boolean p_77192_3_, boolean p_77192_4_) {
        if (!p_77192_2_ && !p_77192_3_) {
            return 0;
        } else {
            this.eligibleChunksForSpawning.clear();
            int i;
            int k;
            for (i = 0; i < p_77192_1_.playerEntities.size(); ++i)
            {
                EntityPlayer entityplayer = (EntityPlayer)p_77192_1_.playerEntities.get(i);
                int j = MathHelper.floor_double(entityplayer.posX / 16.0D);
                k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
                byte b0 = 8;
                for (int l = -b0; l <= b0; ++l)
                {
                    for (int i1 = -b0; i1 <= b0; ++i1)
                    {
                        boolean flag3 = l == -b0 || l == b0 || i1 == -b0 || i1 == b0;
                        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(l + j, i1 + k);
                        if (!flag3)
                        {
                            this.eligibleChunksForSpawning.put(chunkcoordintpair, Boolean.FALSE);
                        }
                        else if (!this.eligibleChunksForSpawning.containsKey(chunkcoordintpair))
                        {
                            this.eligibleChunksForSpawning.put(chunkcoordintpair, Boolean.TRUE);
                        }
                    }
                }
            }
            i = 0;
            ChunkCoordinates chunkcoordinates = p_77192_1_.getSpawnPoint();
            EnumCreatureType[] aenumcreaturetype = EnumCreatureType.values();
            k = aenumcreaturetype.length;
            for (int k3 = 0; k3 < k; ++k3)
            {
                EnumCreatureType enumcreaturetype = aenumcreaturetype[k3];
                if ((!enumcreaturetype.getPeacefulCreature() || p_77192_3_) && (enumcreaturetype.getPeacefulCreature() || p_77192_2_) && (!enumcreaturetype.getAnimal() || p_77192_4_) && p_77192_1_.countEntities(enumcreaturetype, true) <= enumcreaturetype.getMaxNumberOfCreature() * this.eligibleChunksForSpawning.size() / 256)
                {
                    Iterator<ChunkCoordIntPair> iterator = this.eligibleChunksForSpawning.keySet().iterator();
                    ArrayList eligibleChunks = new ArrayList<>(this.eligibleChunksForSpawning.keySet());
                    Collections.shuffle(eligibleChunks);
                    iterator = eligibleChunks.iterator();

                    while (iterator.hasNext()) {
                        ChunkCoordIntPair chunkcoordintpair1 = iterator.next();
                        if (Boolean.FALSE.equals(this.eligibleChunksForSpawning.get(chunkcoordintpair1))) {
                            ChunkPosition chunkposition = func_151350_a(p_77192_1_, chunkcoordintpair1.chunkXPos, chunkcoordintpair1.chunkZPos);
                            int j1 = chunkposition.chunkPosX;
                            int k1 = chunkposition.chunkPosY;
                            int l1 = chunkposition.chunkPosZ;

                            Block block = p_77192_1_.getBlock(j1, k1, l1);
                            Material blockMaterial = block.getMaterial();

                            if (!block.isNormalCube() && blockMaterial == enumcreaturetype.getCreatureMaterial()) {
                                int i2 = 0;
                                int maxSpawnAttempts = 3;

                                for (int j2 = 0; j2 < maxSpawnAttempts; j2++) {
                                    int k2 = j1 + p_77192_1_.rand.nextInt(6) - p_77192_1_.rand.nextInt(6);
                                    int l2 = k1 + p_77192_1_.rand.nextInt(1) - p_77192_1_.rand.nextInt(1);
                                    int i3 = l1 + p_77192_1_.rand.nextInt(6) - p_77192_1_.rand.nextInt(6);

                                    if (canCreatureTypeSpawnAtLocation(enumcreaturetype, p_77192_1_, k2, l2, i3)) {
                                        float f = k2 + 0.5F;
                                        float f2 = i3 + 0.5F;

                                        if (p_77192_1_.getClosestPlayer(f, (float) l2, f2, 24.0D) == null) {
                                            float f3 = f - (float) chunkcoordinates.posX;
                                            float f4 = l2 - (float) chunkcoordinates.posY;
                                            float f5 = f2 - (float) chunkcoordinates.posZ;
                                            float f6 = f3 * f3 + f4 * f4 + f5 * f5;

                                            if (f6 >= 576.0F) {
                                                if (i2 >= maxSpawnAttempts) {
                                                    break;
                                                }

                                                BiomeGenBase.SpawnListEntry spawnlistentry = p_77192_1_.spawnRandomCreature(enumcreaturetype, k2, l2, i3);
                                                if (spawnlistentry != null) {
                                                    EntityLiving entityliving;
                                                    try {
                                                        entityliving = (EntityLiving) spawnlistentry.entityClass.getConstructor(World.class).newInstance(p_77192_1_);
                                                    } catch (Exception exception) {
                                                        exception.printStackTrace();
                                                        return i;
                                                    }

                                                    entityliving.setLocationAndAngles(f, (float) l2, f2, p_77192_1_.rand.nextFloat() * 360.0F, 0.0F);

                                                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving, p_77192_1_, f, (float) l2, f2);
                                                    if (canSpawn == Event.Result.ALLOW || (canSpawn == Event.Result.DEFAULT && entityliving.getCanSpawnHere())) {
                                                        ++i2;
                                                        p_77192_1_.spawnEntityInWorld(entityliving);
                                                        if (!ForgeEventFactory.doSpecialSpawn(entityliving, p_77192_1_, f, (float) l2, f2)) {
                                                            entityliving.onSpawnWithEgg(null);
                                                        }
                                                        if (i2 >= ForgeEventFactory.getMaxSpawnPackSize(entityliving)) {
                                                            break;
                                                        }
                                                    }
                                                    i += i2;
                                                }
                                            }
                                        }
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
}
