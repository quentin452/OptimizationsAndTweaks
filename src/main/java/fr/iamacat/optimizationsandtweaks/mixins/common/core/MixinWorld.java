package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.google.common.collect.ImmutableSetMultimap;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(value = World.class, priority = 999)
public abstract class MixinWorld implements IBlockAccess {

    @Shadow
    public int skylightSubtracted;
    @Shadow
    int[] lightUpdateBlockList;
    @Shadow
    private int ambientTickCountdown;
    @Shadow
    protected Set<ChunkCoordIntPair> activeChunkSet = new HashSet<ChunkCoordIntPair>();
    @Shadow
    public Random rand = new Random();
    @Shadow
    public List<EntityPlayer> playerEntities = new ArrayList<>();
    @Shadow
    public final WorldProvider provider;
    @Unique
    private World multithreadingandtweaks$world;
    @Shadow
    public final Profiler theProfiler;
    @Shadow
    protected List<IWorldAccess> worldAccesses = new ArrayList<IWorldAccess>();
    @Shadow
    protected IChunkProvider chunkProvider;
    @Shadow
    private ArrayList<AxisAlignedBB> collidingBoundingBoxes;

    @Shadow
    public static double MAX_ENTITY_RADIUS = 2.0D;

    protected MixinWorld(WorldProvider provider, World world, Profiler theProfiler) {
        this.provider = provider;
        this.multithreadingandtweaks$world = world;
        this.theProfiler = theProfiler;
    }

    /**
     * @author iamacatfr
     * @reason optimize selectEntitiesWithinAABB
     */
    @Overwrite
    public List<Entity> selectEntitiesWithinAABB(Class clazz, AxisAlignedBB bb, IEntitySelector selector) {
        if (OptimizationsandTweaksConfig.enableMixinWorld) {
            int minXChunk = MathHelper.floor_double((bb.minX - MAX_ENTITY_RADIUS) / 16.0D);
            int maxXChunk = MathHelper.floor_double((bb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
            int minZChunk = MathHelper.floor_double((bb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
            int maxZChunk = MathHelper.floor_double((bb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

            List<Entity> entityList = new ArrayList<>();

            for (int chunkX = minXChunk; chunkX <= maxXChunk; ++chunkX) {
                for (int chunkZ = minZChunk; chunkZ <= maxZChunk; ++chunkZ) {
                    if (this.multithreadingandtweaks$chunkExists(chunkX, chunkZ)) {
                        Chunk chunk = this.multithreadingandtweaks$getChunkFromChunkCoords(chunkX, chunkZ);
                        chunk.getEntitiesOfTypeWithinAAAB(clazz, bb, entityList, selector);
                    }
                }
            }

            return entityList;
        }
        return null;
    }

    /**
     * Returns whether a chunk exists at chunk coordinates x, y
     */
    @Unique
    protected boolean multithreadingandtweaks$chunkExists(int p_72916_1_, int p_72916_2_) {
        return this.chunkProvider.chunkExists(p_72916_1_, p_72916_2_);
    }

    /**
     * Returns back a chunk looked up by chunk coordinates Args: x, y
     */
    @Unique
    public Chunk multithreadingandtweaks$getChunkFromChunkCoords(int p_72964_1_, int p_72964_2_) {
        return this.chunkProvider.provideChunk(p_72964_1_, p_72964_2_);
    }

    /**
     * Plays a sound at the entity's position. Args: entity, sound, volume (relative to 1.0), and frequency (or pitch,
     * also relative to 1.0).
     */
    @Overwrite
    public void playSoundAtEntity(Entity entity, String soundName, float volume, float pitch) {
        if (OptimizationsandTweaksConfig.enableMixinWorld) {
            PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(entity, soundName, volume, pitch);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }
            soundName = event.name;
            for (IWorldAccess worldAccess : this.worldAccesses) {
                playSoundAtEntity(worldAccess, entity, soundName, volume, pitch);
            }
        }
    }

    @Unique
    private void playSoundAtEntity(IWorldAccess worldAccess, Entity entity, String soundName, float volume,
        float pitch) {
        double x = entity.posX;
        double y = entity.posY - (double) entity.yOffset;
        double z = entity.posZ;
        worldAccess.playSound(soundName, x, y, z, volume, pitch);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity p_72945_1_, AxisAlignedBB p_72945_2_) {
        if (OptimizationsandTweaksConfig.enableMixinWorld) {
            this.collidingBoundingBoxes.clear();
            double minX = p_72945_2_.minX;
            double maxX = p_72945_2_.maxX + 1.0D;
            double minY = p_72945_2_.minY;
            double maxY = p_72945_2_.maxY + 1.0D;
            double minZ = p_72945_2_.minZ;
            double maxZ = p_72945_2_.maxZ + 1.0D;

            for (int x = MathHelper.floor_double(minX); x < MathHelper.floor_double(maxX); ++x) {
                for (int z = MathHelper.floor_double(minZ); z < MathHelper.floor_double(maxZ); ++z) {
                    if (this.multithreadingandtweaks$blockExists(x, 64, z)) {
                        double blockMinY = Math.max(minY - 1, 0); // Avoid negative values for minY
                        double blockMaxY = Math.min(maxY, 256); // Avoid exceeding world height

                        for (int y = MathHelper.floor_double(blockMinY); y < MathHelper.floor_double(blockMaxY); ++y) {
                            Block block = this.getBlock(x, y, z);
                            World world = p_72945_1_.worldObj;
                            block.addCollisionBoxesToList(
                                world,
                                x,
                                y,
                                z,
                                p_72945_2_,
                                this.collidingBoundingBoxes,
                                p_72945_1_);
                        }
                    }
                }
            }

            List<Entity> list = this
                .multithreadingandtweaks$getEntitiesWithinAABBExcludingEntity(p_72945_1_, p_72945_2_);

            for (Entity entity : list) {
                AxisAlignedBB entityAABB = entity.getBoundingBox();
                AxisAlignedBB collisionBox = p_72945_1_.getCollisionBox(entity);

                if (entityAABB != null && entityAABB.intersectsWith(p_72945_2_)) {
                    this.collidingBoundingBoxes.add(entityAABB);
                }

                if (collisionBox != null && collisionBox.intersectsWith(p_72945_2_)) {
                    this.collidingBoundingBoxes.add(collisionBox);
                }
            }

            return this.collidingBoundingBoxes;
        }
        return null;
    }

    /**
     * Returns whether a block exists at world coordinates x, y, z
     */
    @Unique
    public boolean multithreadingandtweaks$blockExists(int p_72899_1_, int p_72899_2_, int p_72899_3_) {
        return p_72899_2_ >= 0 && p_72899_2_ < 256
            && this.multithreadingandtweaks$chunkExists(p_72899_1_ >> 4, p_72899_3_ >> 4);
    }

    @Unique
    public List<Entity> multithreadingandtweaks$getEntitiesWithinAABBExcludingEntity(Entity p_72839_1_,
        AxisAlignedBB p_72839_2_) {
        return this.multithreadingandtweaks$getEntitiesWithinAABBExcludingEntity(
            p_72839_1_,
            p_72839_2_,
            (IEntitySelector) null);
    }

    @Unique
    public List<Entity> multithreadingandtweaks$getEntitiesWithinAABBExcludingEntity(Entity p_94576_1_,
        AxisAlignedBB p_94576_2_, IEntitySelector p_94576_3_) {
        ArrayList<Entity> arraylist = new ArrayList<>();

        double centerX = (p_94576_2_.minX + p_94576_2_.maxX) * 0.5D;
        double centerZ = (p_94576_2_.minZ + p_94576_2_.maxZ) * 0.5D;

        int minChunkX = MathHelper.floor_double((centerX - MAX_ENTITY_RADIUS) / 16.0D);
        int maxChunkX = MathHelper.floor_double((centerX + MAX_ENTITY_RADIUS) / 16.0D);
        int minChunkZ = MathHelper.floor_double((centerZ - MAX_ENTITY_RADIUS) / 16.0D);
        int maxChunkZ = MathHelper.floor_double((centerZ + MAX_ENTITY_RADIUS) / 16.0D);

        for (int i = minChunkX; i <= maxChunkX; ++i) {
            for (int j = minChunkZ; j <= maxChunkZ; ++j) {
                if (this.multithreadingandtweaks$chunkExists(i, j)) {
                    this.multithreadingandtweaks$getChunkFromChunkCoords(i, j)
                        .getEntitiesWithinAABBForEntity(p_94576_1_, p_94576_2_, arraylist, p_94576_3_);
                }
            }
        }

        return arraylist;
    }

    @Unique
    private void multithreadingandtweaks$handleChunkChange(Entity entity, int newChunkX, int newChunkZ,
        boolean addedToChunk, int oldChunkX, int oldChunkZ) {
        if (addedToChunk && (newChunkX != oldChunkX || newChunkZ != oldChunkZ)) {
            int oldChunkY = MathHelper.floor_double(entity.posY / 16.0D);
            Chunk oldChunk = multithreadingandtweaks$getChunkFromChunkCoords(oldChunkX, oldChunkZ);
            Chunk newChunk = multithreadingandtweaks$getChunkFromChunkCoords(newChunkX, newChunkZ);

            if (oldChunk != null) {
                oldChunk.removeEntityAtIndex(entity, oldChunkY);
            }

            if (newChunk != null) {
                entity.addedToChunk = true;
                newChunk.addEntity(entity);
            } else {
                entity.addedToChunk = false;
            }
        }
    }

    /**
     * Get the persistent chunks for this world
     *
     * @return
     */
    @Unique
    public ImmutableSetMultimap<ChunkCoordIntPair, ForgeChunkManager.Ticket> multithreadingandtweaks$getPersistentChunks() {
        return ForgeChunkManager.getPersistentChunksFor(multithreadingandtweaks$world);
    }

    /**
     * Checks between a min and max all the chunks inbetween actually exist. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    @Overwrite
    public boolean checkChunksExist(int p_72904_1_, int p_72904_2_, int p_72904_3_, int p_72904_4_, int p_72904_5_,
        int p_72904_6_) {
        if (OptimizationsandTweaksConfig.enableMixinWorld) {
            if (p_72904_5_ >= 0 && p_72904_2_ < 256) {
                p_72904_1_ >>= 4;
                p_72904_3_ >>= 4;
                p_72904_4_ >>= 4;
                p_72904_6_ >>= 4;

                for (int k1 = p_72904_1_; k1 <= p_72904_4_; ++k1) {
                    for (int l1 = p_72904_3_; l1 <= p_72904_6_; ++l1) {
                        if (!this.multithreadingandtweaks$chunkExists(k1, l1)) {
                            return false;
                        }
                    }
                }

                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Returns saved light value without taking into account the time of day. Either looks in the sky light map or
     * block light map based on the enumSkyBlock arg.
     */
    @Overwrite
    public int getSavedLightValue(EnumSkyBlock lightType, int x, int y, int z) {
        if (OptimizationsandTweaksConfig.enableMixinWorld) {

            // Ensure y is within the valid range [0, 255]
            y = Math.min(255, Math.max(0, y));

            // Check if the coordinates are within a valid range
            if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
                int chunkX = x >> 4;
                int chunkZ = z >> 4;

                if (!this.multithreadingandtweaks$chunkExists(chunkX, chunkZ)) {
                    return lightType.defaultLightValue;
                } else {
                    Chunk chunk = this.multithreadingandtweaks$getChunkFromChunkCoords(chunkX, chunkZ);
                    return chunk.getSavedLightValue(lightType, x & 15, y, z & 15);
                }
            }

            return lightType.defaultLightValue;
        }
        return x;
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */ // todo
    @Overwrite
    public void updateEntityWithOptionalForce(Entity entity, boolean forceUpdate) {
        if (!OptimizationsandTweaksConfig.enableMixinWorld) {
            return;
        }

        int x = MathHelper.floor_double(entity.posX);
        int z = MathHelper.floor_double(entity.posZ);
        boolean isForced = multithreadingandtweaks$getPersistentChunks()
            .containsKey(new ChunkCoordIntPair(x >> 4, z >> 4));
        byte chunkCheckRadius = isForced ? (byte) 0 : 32;
        boolean canUpdate = !forceUpdate || this.checkChunksExist(
            x - chunkCheckRadius,
            0,
            z - chunkCheckRadius,
            x + chunkCheckRadius,
            0,
            z + chunkCheckRadius);

        if (!canUpdate) {
            EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(entity);
            MinecraftForge.EVENT_BUS.post(event);
            canUpdate = event.canUpdate;
        }

        if (canUpdate) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
            entity.prevRotationYaw = entity.rotationYaw;
            entity.prevRotationPitch = entity.rotationPitch;

            if (forceUpdate && entity.addedToChunk) {
                ++entity.ticksExisted;
                if (entity.ridingEntity != null) {
                    entity.updateRidden();
                } else {
                    entity.onUpdate();
                }
            }

            int chunkX = MathHelper.floor_double(entity.posX / 16.0D);
            int chunkZ = MathHelper.floor_double(entity.posZ / 16.0D);

            if (!entity.addedToChunk || entity.chunkCoordX != chunkX || entity.chunkCoordZ != chunkZ) {
                if (entity.addedToChunk
                    && this.multithreadingandtweaks$chunkExists(entity.chunkCoordX, entity.chunkCoordZ)) {
                    this.multithreadingandtweaks$getChunkFromChunkCoords(entity.chunkCoordX, entity.chunkCoordZ)
                        .removeEntityAtIndex(entity, entity.chunkCoordY);
                }

                if (this.multithreadingandtweaks$chunkExists(chunkX, chunkZ)) {
                    entity.addedToChunk = true;
                    this.multithreadingandtweaks$getChunkFromChunkCoords(chunkX, chunkZ)
                        .addEntity(entity);
                } else {
                    entity.addedToChunk = false;
                }
            }

            if (forceUpdate && entity.addedToChunk && entity.riddenByEntity != null) {
                if (!entity.riddenByEntity.isDead && entity.riddenByEntity.ridingEntity == entity) {
                    this.updateEntity(entity.riddenByEntity);
                } else {
                    entity.riddenByEntity.ridingEntity = null;
                    entity.riddenByEntity = null;
                }
            }
        }
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded. Args: entity
     */
    @Unique
    public void updateEntity(Entity p_72870_1_) {
        this.updateEntityWithOptionalForce(p_72870_1_, true);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setActivePlayerChunksAndCheckLight() {
        this.activeChunkSet.clear();
        this.theProfiler.startSection("buildList");

        // Collect chunks from persistent chunks
        this.activeChunkSet.addAll(getPersistentChunks().keySet());

        this.theProfiler.endStartSection("playerCheckLight");

        if (!this.playerEntities.isEmpty()) {
            for (EntityPlayer entityplayer : this.playerEntities) {
                int j = MathHelper.floor_double(entityplayer.posX / 16.0D);
                int k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
                int l = this.func_152379_p();

                for (int i1 = -l; i1 <= l; ++i1) {
                    for (int j1 = -l; j1 <= l; ++j1) {
                        this.activeChunkSet.add(new ChunkCoordIntPair(i1 + j, j1 + k));
                    }
                }
            }

            EntityPlayer randomPlayer = this.playerEntities.get(this.rand.nextInt(this.playerEntities.size()));
            int x = MathHelper.floor_double(randomPlayer.posX) + this.rand.nextInt(11) - 5;
            int y = MathHelper.floor_double(randomPlayer.posY) + this.rand.nextInt(11) - 5;
            int z = MathHelper.floor_double(randomPlayer.posZ) + this.rand.nextInt(11) - 5;

            this.func_147451_t(x, y, z);
        }

        this.theProfiler.endSection();
    }

    @Unique
    public ImmutableSetMultimap<ChunkCoordIntPair, ForgeChunkManager.Ticket> getPersistentChunks() {
        return ForgeChunkManager.getPersistentChunksFor(multithreadingandtweaks$world);
    }

    @Shadow
    protected abstract int func_152379_p();

    @Unique
    public boolean func_147451_t(int x, int y, int z) {
        boolean flag = false;

        if (!this.provider.hasNoSky) {
            flag |= this.updateLightByType(EnumSkyBlock.Sky, x, y, z);
        }

        flag |= this.updateLightByType(EnumSkyBlock.Block, x, y, z);
        return flag;
    }

    @Unique
    public boolean updateLightByType(EnumSkyBlock p_147463_1_, int p_147463_2_, int p_147463_3_, int p_147463_4_) {
        if (!this.doChunksNearChunkExist(p_147463_2_, p_147463_3_, p_147463_4_, 17)) {
            return false;
        } else {
            int l = 0;
            int i1 = 0;
            this.theProfiler.startSection("getBrightness");
            int j1 = this.getSavedLightValue(p_147463_1_, p_147463_2_, p_147463_3_, p_147463_4_);
            int k1 = this.computeLightValue(p_147463_2_, p_147463_3_, p_147463_4_, p_147463_1_);
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            int i3;
            int j3;
            int k3;
            int l3;

            if (k1 > j1) {
                this.lightUpdateBlockList[i1++] = 133152;
            } else if (k1 < j1) {
                this.lightUpdateBlockList[i1++] = 133152 | j1 << 18;

                while (l < i1) {
                    l1 = this.lightUpdateBlockList[l++];
                    i2 = (l1 & 63) - 32 + p_147463_2_;
                    j2 = (l1 >> 6 & 63) - 32 + p_147463_3_;
                    k2 = (l1 >> 12 & 63) - 32 + p_147463_4_;
                    l2 = l1 >> 18 & 15;
                    i3 = this.getSavedLightValue(p_147463_1_, i2, j2, k2);

                    if (i3 == l2) {
                        this.setLightValue(p_147463_1_, i2, j2, k2, 0);

                        if (l2 > 0) {
                            j3 = MathHelper.abs_int(i2 - p_147463_2_);
                            k3 = MathHelper.abs_int(j2 - p_147463_3_);
                            l3 = MathHelper.abs_int(k2 - p_147463_4_);

                            if (j3 + k3 + l3 < 17) {
                                for (int i4 = 0; i4 < 6; ++i4) {
                                    int j4 = i2 + Facing.offsetsXForSide[i4];
                                    int k4 = j2 + Facing.offsetsYForSide[i4];
                                    int l4 = k2 + Facing.offsetsZForSide[i4];
                                    int i5 = Math.max(
                                        1,
                                        this.getBlock(j4, k4, l4)
                                            .getLightOpacity(this, j4, k4, l4));
                                    i3 = this.getSavedLightValue(p_147463_1_, j4, k4, l4);

                                    if (i3 == l2 - i5 && i1 < this.lightUpdateBlockList.length) {
                                        this.lightUpdateBlockList[i1++] = j4 - p_147463_2_ + 32
                                            | k4 - p_147463_3_ + 32 << 6
                                            | l4 - p_147463_4_ + 32 << 12
                                            | l2 - i5 << 18;
                                    }
                                }
                            }
                        }
                    }
                }

                l = 0;
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("checkedPosition < toCheckCount");

            while (l < i1) {
                l1 = this.lightUpdateBlockList[l++];
                i2 = (l1 & 63) - 32 + p_147463_2_;
                j2 = (l1 >> 6 & 63) - 32 + p_147463_3_;
                k2 = (l1 >> 12 & 63) - 32 + p_147463_4_;
                l2 = this.getSavedLightValue(p_147463_1_, i2, j2, k2);
                i3 = this.computeLightValue(i2, j2, k2, p_147463_1_);

                if (i3 != l2) {
                    this.setLightValue(p_147463_1_, i2, j2, k2, i3);

                    if (i3 > l2) {
                        j3 = Math.abs(i2 - p_147463_2_);
                        k3 = Math.abs(j2 - p_147463_3_);
                        l3 = Math.abs(k2 - p_147463_4_);
                        boolean flag = i1 < this.lightUpdateBlockList.length - 6;

                        if (j3 + k3 + l3 < 17 && flag) {
                            if (this.getSavedLightValue(p_147463_1_, i2 - 1, j2, k2) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - 1
                                    - p_147463_2_
                                    + 32
                                    + (j2 - p_147463_3_ + 32 << 6)
                                    + (k2 - p_147463_4_ + 32 << 12);
                            }

                            if (this.getSavedLightValue(p_147463_1_, i2 + 1, j2, k2) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 + 1
                                    - p_147463_2_
                                    + 32
                                    + (j2 - p_147463_3_ + 32 << 6)
                                    + (k2 - p_147463_4_ + 32 << 12);
                            }

                            if (this.getSavedLightValue(p_147463_1_, i2, j2 - 1, k2) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - p_147463_2_
                                    + 32
                                    + (j2 - 1 - p_147463_3_ + 32 << 6)
                                    + (k2 - p_147463_4_ + 32 << 12);
                            }

                            if (this.getSavedLightValue(p_147463_1_, i2, j2 + 1, k2) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - p_147463_2_
                                    + 32
                                    + (j2 + 1 - p_147463_3_ + 32 << 6)
                                    + (k2 - p_147463_4_ + 32 << 12);
                            }

                            if (this.getSavedLightValue(p_147463_1_, i2, j2, k2 - 1) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - p_147463_2_
                                    + 32
                                    + (j2 - p_147463_3_ + 32 << 6)
                                    + (k2 - 1 - p_147463_4_ + 32 << 12);
                            }

                            if (this.getSavedLightValue(p_147463_1_, i2, j2, k2 + 1) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - p_147463_2_
                                    + 32
                                    + (j2 - p_147463_3_ + 32 << 6)
                                    + (k2 + 1 - p_147463_4_ + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.theProfiler.endSection();
            return true;
        }
    }

    @Unique
    public boolean doChunksNearChunkExist(int p_72873_1_, int p_72873_2_, int p_72873_3_, int p_72873_4_) {
        return this.checkChunksExist(
            p_72873_1_ - p_72873_4_,
            p_72873_2_ - p_72873_4_,
            p_72873_3_ - p_72873_4_,
            p_72873_1_ + p_72873_4_,
            p_72873_2_ + p_72873_4_,
            p_72873_3_ + p_72873_4_);
    }

    private int computeLightValue(int x, int y, int z, EnumSkyBlock p_98179_4_) {
        if (p_98179_4_ == EnumSkyBlock.Sky && this.canBlockSeeTheSky(x, y, z)) {
            return 15;
        } else {
            Block block = this.getBlock(x, y, z);
            int blockLight = block.getLightValue(this, x, y, z);
            int l = p_98179_4_ == EnumSkyBlock.Sky ? 0 : blockLight;
            int i1 = block.getLightOpacity(this, x, y, z);

            if (i1 >= 15 && blockLight > 0) {
                i1 = 1;
            }

            if (i1 < 1) {
                i1 = 1;
            }

            if (i1 >= 15) {
                return 0;
            } else if (l >= 14) {
                return l;
            } else {
                for (int j1 = 0; j1 < 6; ++j1) {
                    int k1 = x + Facing.offsetsXForSide[j1];
                    int l1 = y + Facing.offsetsYForSide[j1];
                    int i2 = z + Facing.offsetsZForSide[j1];
                    int j2 = this.getSavedLightValue(p_98179_4_, k1, l1, i2) - i1;

                    if (j2 > l) {
                        l = j2;
                    }

                    if (l >= 14) {
                        return l;
                    }
                }

                return l;
            }
        }
    }

    @Unique
    public void setLightValue(EnumSkyBlock p_72915_1_, int p_72915_2_, int p_72915_3_, int p_72915_4_, int p_72915_5_) {
        if (p_72915_2_ >= -30000000 && p_72915_4_ >= -30000000 && p_72915_2_ < 30000000 && p_72915_4_ < 30000000) {
            if (p_72915_3_ >= 0) {
                if (p_72915_3_ < 256) {
                    if (this.chunkExists(p_72915_2_ >> 4, p_72915_4_ >> 4)) {
                        Chunk chunk = this.getChunkFromChunkCoords(p_72915_2_ >> 4, p_72915_4_ >> 4);
                        chunk.setLightValue(p_72915_1_, p_72915_2_ & 15, p_72915_3_, p_72915_4_ & 15, p_72915_5_);

                        for (IWorldAccess worldAccess : this.worldAccesses) {
                            worldAccess.markBlockForRenderUpdate(p_72915_2_, p_72915_3_, p_72915_4_);
                        }
                    }
                }
            }
        }
    }

    @Unique
    protected boolean chunkExists(int p_72916_1_, int p_72916_2_) {
        return this.chunkProvider.chunkExists(p_72916_1_, p_72916_2_);
    }

    @Unique
    public Chunk getChunkFromChunkCoords(int p_72964_1_, int p_72964_2_) {
        return this.chunkProvider.provideChunk(p_72964_1_, p_72964_2_);
    }

    @Unique
    public boolean canBlockSeeTheSky(int p_72937_1_, int p_72937_2_, int p_72937_3_) {
        return this.getChunkFromChunkCoords(p_72937_1_ >> 4, p_72937_3_ >> 4)
            .canBlockSeeTheSky(p_72937_1_ & 15, p_72937_2_, p_72937_3_ & 15);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getBlockLightValue_do(int x, int y, int z, boolean useNeighborBrightness) {
        if (OptimizationsandTweaksConfig.enableMixinWorld) {

            if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000) {
                return 15;
            }

            if (useNeighborBrightness && this.getBlock(x, y, z)
                .getUseNeighborBrightness()) {
                int maxLight = 0;

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            int neighborLight = this.getBlockLightValue_do(x + dx, y + dy, z + dz, false);
                            maxLight = Math.max(maxLight, neighborLight);
                        }
                    }
                }

                return maxLight;
            } else if (y < 0) {
                return 0;
            } else if (y >= 256) {
                y = 255;
            }

            Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
            x &= 15;
            z &= 15;

            return chunk.getBlockLightValue(x, y, z, this.skylightSubtracted);
        }
        return x;

    }

    @Unique
    private static final Block AIR_BLOCK = Blocks.air;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Block getBlock(int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        if (p_147439_1_ >= -30000000 && p_147439_3_ >= -30000000
            && p_147439_1_ < 30000000
            && p_147439_3_ < 30000000
            && p_147439_2_ >= 0
            && p_147439_2_ < 256) {
            int chunkX = p_147439_1_ >> 4;
            int chunkZ = p_147439_3_ >> 4;

            Chunk chunk = this.getChunkFromChunkCoords(chunkX, chunkZ);
            Block block = chunk.getBlock(p_147439_1_ & 15, p_147439_2_, p_147439_3_ & 15);

            if (block == null) {
                return AIR_BLOCK;
            } else {
                return block;
            }
        } else {
            return AIR_BLOCK;
        }
    }
}
