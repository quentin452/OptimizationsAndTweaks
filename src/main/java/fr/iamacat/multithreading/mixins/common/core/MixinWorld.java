package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(value = World.class, priority = 999)
public abstract class MixinWorld implements IBlockAccess {

    @Shadow
    public final WorldProvider provider;
    @Unique
    private World multithreadingandtweaks$world;
    @Shadow
    public final Profiler theProfiler;
    @Shadow
    protected List worldAccesses = new ArrayList();
    @Shadow
    protected IChunkProvider chunkProvider;
    @Shadow
    private ArrayList collidingBoundingBoxes;

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
    public List selectEntitiesWithinAABB(Class clazz, AxisAlignedBB bb, IEntitySelector selector) {
        if (MultithreadingandtweaksConfig.enableMixinWorld) {
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
    public void playSoundAtEntity(Entity p_72956_1_, String p_72956_2_, float p_72956_3_, float p_72956_4_) {
        if (MultithreadingandtweaksConfig.enableMixinWorld) {
            PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(p_72956_1_, p_72956_2_, p_72956_3_, p_72956_4_);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }
            p_72956_2_ = event.name;
            for (Object worldAccess : this.worldAccesses) {
                ((IWorldAccess) worldAccess).playSound(
                    p_72956_2_,
                    p_72956_1_.posX,
                    p_72956_1_.posY - (double) p_72956_1_.yOffset,
                    p_72956_1_.posZ,
                    p_72956_3_,
                    p_72956_4_);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public List getCollidingBoundingBoxes(Entity p_72945_1_, AxisAlignedBB p_72945_2_) {
        if (MultithreadingandtweaksConfig.enableMixinWorld) {
            this.collidingBoundingBoxes.clear();
            int i = MathHelper.floor_double(p_72945_2_.minX);
            int j = MathHelper.floor_double(p_72945_2_.maxX + 1.0D);
            int k = MathHelper.floor_double(p_72945_2_.minY);
            int l = MathHelper.floor_double(p_72945_2_.maxY + 1.0D);
            int i1 = MathHelper.floor_double(p_72945_2_.minZ);
            int j1 = MathHelper.floor_double(p_72945_2_.maxZ + 1.0D);

            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = i1; l1 < j1; ++l1) {
                    if (this.multithreadingandtweaks$blockExists(k1, 64, l1)) {
                        for (int i2 = k - 1; i2 < l; ++i2) {
                            Block block;

                            if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
                                block = this.getBlock(k1, i2, l1);
                            } else {
                                block = Blocks.stone;
                            }
                            World world = p_72945_1_.worldObj;
                            block.addCollisionBoxesToList(
                                world,
                                k1,
                                i2,
                                l1,
                                p_72945_2_,
                                this.collidingBoundingBoxes,
                                p_72945_1_);
                        }
                    }
                }
            }

            double d0 = 0.25D;
            List list = this.multithreadingandtweaks$getEntitiesWithinAABBExcludingEntity(
                p_72945_1_,
                p_72945_2_.expand(d0, d0, d0));

            for (int j2 = 0; j2 < list.size(); ++j2) {
                AxisAlignedBB axisalignedbb1 = ((Entity) list.get(j2)).getBoundingBox();

                if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(p_72945_2_)) {
                    this.collidingBoundingBoxes.add(axisalignedbb1);
                }

                axisalignedbb1 = p_72945_1_.getCollisionBox((Entity) list.get(j2));

                if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(p_72945_2_)) {
                    this.collidingBoundingBoxes.add(axisalignedbb1);
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

    /**
     * Returns whether a chunk exists at chunk coordinates x, y
     */
    /**
     * Returns the block corresponding to the given coordinates inside a chunk.
     */
    @Overwrite
    public Block getBlock(int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        if (MultithreadingandtweaksConfig.enableMixinWorld) {
            if (p_147439_1_ >= -30000000 && p_147439_3_ >= -30000000
                && p_147439_1_ < 30000000
                && p_147439_3_ < 30000000
                && p_147439_2_ >= 0
                && p_147439_2_ < 256) {
                Chunk chunk = null;

                try {
                    chunk = this.multithreadingandtweaks$getChunkFromChunkCoords(p_147439_1_ >> 4, p_147439_3_ >> 4);
                    return chunk.getBlock(p_147439_1_ & 15, p_147439_2_, p_147439_3_ & 15);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport
                        .makeCrashReport(throwable, "Exception getting block type in world");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
                    crashreportcategory.addCrashSection("Found chunk", chunk == null);
                    crashreportcategory.addCrashSection(
                        "Location",
                        CrashReportCategory.getLocationInfo(p_147439_1_, p_147439_2_, p_147439_3_));
                    throw new ReportedException(crashreport);
                }
            } else {
                return Blocks.air;
            }
        }
        return null;
    }

    /**
     * Will get all entities within the specified AABB excluding the one passed into it. Args: entityToExclude, aabb
     */
    @Unique
    public List multithreadingandtweaks$getEntitiesWithinAABBExcludingEntity(Entity p_72839_1_,
        AxisAlignedBB p_72839_2_) {
        return this.multithreadingandtweaks$getEntitiesWithinAABBExcludingEntity(
            p_72839_1_,
            p_72839_2_,
            (IEntitySelector) null);
    }

    @Unique
    public List multithreadingandtweaks$getEntitiesWithinAABBExcludingEntity(Entity p_94576_1_,
        AxisAlignedBB p_94576_2_, IEntitySelector p_94576_3_) {
        ArrayList arraylist = new ArrayList();
        int i = MathHelper.floor_double((p_94576_2_.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int j = MathHelper.floor_double((p_94576_2_.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int k = MathHelper.floor_double((p_94576_2_.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int l = MathHelper.floor_double((p_94576_2_.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                if (this.multithreadingandtweaks$chunkExists(i1, j1)) {
                    this.multithreadingandtweaks$getChunkFromChunkCoords(i1, j1)
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
        if (MultithreadingandtweaksConfig.enableMixinWorld) {
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
        if (MultithreadingandtweaksConfig.enableMixinWorld) {

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
        if (!MultithreadingandtweaksConfig.enableMixinWorld) {
            return;
        }

        int x = MathHelper.floor_double(entity.posX);
        int z = MathHelper.floor_double(entity.posZ);
        boolean isForced = multithreadingandtweaks$getPersistentChunks().containsKey(new ChunkCoordIntPair(x >> 4, z >> 4));
        byte chunkCheckRadius = isForced ? (byte) 0 : 32;
        boolean canUpdate = !forceUpdate || this.checkChunksExist(x - chunkCheckRadius, 0, z - chunkCheckRadius, x + chunkCheckRadius, 0, z + chunkCheckRadius);

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
                if (entity.addedToChunk && this.multithreadingandtweaks$chunkExists(entity.chunkCoordX, entity.chunkCoordZ)) {
                    this.multithreadingandtweaks$getChunkFromChunkCoords(entity.chunkCoordX, entity.chunkCoordZ).removeEntityAtIndex(entity, entity.chunkCoordY);
                }

                if (this.multithreadingandtweaks$chunkExists(chunkX, chunkZ)) {
                    entity.addedToChunk = true;
                    this.multithreadingandtweaks$getChunkFromChunkCoords(chunkX, chunkZ).addEntity(entity);
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
    public void updateEntity(Entity p_72870_1_)
    {
        this.updateEntityWithOptionalForce(p_72870_1_, true);
    }
}
