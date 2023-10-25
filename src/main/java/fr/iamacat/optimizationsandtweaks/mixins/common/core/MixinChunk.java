package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

@Mixin(value = Chunk.class, priority = 999)
public class MixinChunk {

    @Unique
    private Chunk chunk;
    @Shadow
    public List[] entityLists;
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    /** Determines if the chunk is lit or not at a light value greater than 0. */
    @Shadow
    public static boolean isLit;
    /**
     * Used to store block IDs, block MSBs, Sky-light maps, Block-light maps, and metadata. Each entry corresponds to a
     * logical segment of 16x16x16 blocks, stacked vertically.
     */
    @Shadow
    private ExtendedBlockStorage[] storageArrays;
    /** Contains a 16x16 mapping on the X/Z plane of the biome ID to which each colum belongs. */
    @Shadow
    private byte[] blockBiomeArray;
    /** A map, similar to heightMap, that tracks how far down precipitation can fall. */
    @Shadow
    public int[] precipitationHeightMap;
    /** Which columns need their skylightMaps updated. */
    @Shadow
    public boolean[] updateSkylightColumns;
    /** Whether or not this Chunk is currently loaded into the World */
    @Shadow
    public boolean isChunkLoaded;
    /** Reference to the World object. */
    @Shadow
    public World worldObj;
    @Shadow
    public int[] heightMap;
    /** The x coordinate of the chunk. */
    @Shadow
    public final int xPosition;
    /** The z coordinate of the chunk. */
    @Shadow
    public final int zPosition;
    @Shadow
    private boolean isGapLightingUpdated;

    /** A Map of ChunkPositions to TileEntities in this chunk */
    @Shadow
    public Map chunkTileEntityMap;
    /** Boolean value indicating if the terrain is populated. */
    @Shadow
    public boolean isTerrainPopulated;
    @Shadow
    public boolean isLightPopulated;
    @Shadow
    public boolean field_150815_m;
    /** Set to true if the chunk has been modified and needs to be updated internally. */
    @Shadow
    public boolean isModified;
    /** Whether this Chunk has any Entities and thus requires saving on every tick */
    @Shadow
    public boolean hasEntities;
    /** The time according to World.worldTime when this chunk was last saved */
    @Shadow
    public long lastSaveTime;

    /**
     * Updates to this chunk will not be sent to clients if this is false. This field is set to true the first time the
     * chunk is sent to a client, and never set to false.
     */
    @Shadow
    public boolean sendUpdates;
    /** Lowest value in the heightmap. */
    @Shadow
    public int heightMapMinimum;
    /** the cumulative number of ticks players have been in this chunk */
    @Shadow
    public long inhabitedTime;
    /** Contains the current round-robin relight check index, and is implied as the relight check location as well. */
    @Shadow
    private int queuedLightChecks;

    public MixinChunk(Chunk chunk, World p_i1995_1_, int p_i1995_2_, int p_i1995_3_) {
        this.chunk = chunk;
        this.storageArrays = new ExtendedBlockStorage[16];
        this.blockBiomeArray = new byte[256];
        this.precipitationHeightMap = new int[256];
        this.updateSkylightColumns = new boolean[256];
        this.chunkTileEntityMap = new HashMap();
        this.queuedLightChecks = 4096;
        this.entityLists = new List[16];
        this.worldObj = p_i1995_1_;
        this.xPosition = p_i1995_2_;
        this.zPosition = p_i1995_3_;
        this.heightMap = new int[256];

        for (int k = 0; k < this.entityLists.length; ++k) {
            this.entityLists[k] = new ArrayList();
        }

        Arrays.fill(this.precipitationHeightMap, -999);
        Arrays.fill(this.blockBiomeArray, (byte) -1);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getHeightValue(int x, int z) {
        if (x >= 0 && x < 16 && z >= 0 && z < 16) {
            int index = z * 16 + x;
            return heightMap[index];
        } else {
            throw new IllegalArgumentException("Invalid coordinates (x, z)");
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getTopFilledSegment() {
        int low = 0;
        int high = this.storageArrays.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (this.storageArrays[mid] == null) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        if (low > 0) {
            return this.storageArrays[low - 1].getYLocation();
        } else {
            return 0;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void generateHeightMap() {
        int topFilledSegment = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int height = topFilledSegment + 16 - 1;

                while (height > 0) {
                    if (func_150808_b(x, height - 1, z) == 0) {
                        --height;
                    } else {
                        break;
                    }
                }

                this.precipitationHeightMap[x + (z << 4)] = -999;
                this.heightMap[z << 4 | x] = height;

                if (height < this.heightMapMinimum) {
                    this.heightMapMinimum = height;
                }
            }
        }

        this.isModified = true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void getEntitiesWithinAABBForEntity(Entity p_76588_1_, AxisAlignedBB p_76588_2_, List<Entity> p_76588_3_,
                                               IEntitySelector p_76588_4_) {
        if (OptimizationsandTweaksConfig.enableMixinChunk) {
            int i = multithreadingandtweaks$calculateMinIndex(p_76588_2_);
            int j = multithreadingandtweaks$calculateMaxIndex(p_76588_2_);
            i = MathHelper.clamp_int(i, 0, this.entityLists.length - 1);
            j = MathHelper.clamp_int(j, 0, this.entityLists.length - 1);

            multithreadingandtweaks$processEntitiesInRange(i, j, p_76588_1_, p_76588_2_, p_76588_3_, p_76588_4_);
        }
    }

    @Unique
    private int multithreadingandtweaks$calculateMinIndex(AxisAlignedBB aabb) {
        return MathHelper.floor_double((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
    }

    @Unique
    private int multithreadingandtweaks$calculateMaxIndex(AxisAlignedBB aabb) {
        return MathHelper.floor_double((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
    }

    @Unique
    private void multithreadingandtweaks$processEntitiesInRange(int minIndex, int maxIndex, Entity entity, AxisAlignedBB aabb, List<Entity> entityList, IEntitySelector selector) {
        for (int k = minIndex; k <= maxIndex; ++k) {
            if (this.isChunkLoaded) {
                List list1 = this.entityLists[k];

                for (Object o : list1) {
                    Entity entity1 = (Entity) o;

                    if (entity1 != entity && entity1.boundingBox.intersectsWith(aabb) && (selector == null || selector.isEntityApplicable(entity1))) {
                        entityList.add(entity1);
                    }
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void generateSkylightMap() {
        int topFilledSegment = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;

        // Iterate through each chunk section
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                this.precipitationHeightMap[x + (z << 4)] = -999;
                int y = topFilledSegment + 16 - 1;

                // Find the highest non-air block
                while (y > 0 && this.func_150808_b(x, y - 1, z) == 0) {
                    --y;
                }

                this.heightMap[z << 4 | x] = y;

                if (y < this.heightMapMinimum) {
                    this.heightMapMinimum = y;
                }

                if (!this.worldObj.provider.hasNoSky) {
                    y = 15;
                    int blockY = topFilledSegment + 16 - 1;

                    // Update skylight values
                    do {
                        int lightValue = this.func_150808_b(x, blockY, z);

                        if (lightValue == 0 && y != 15) {
                            lightValue = 1;
                        }

                        y -= lightValue;

                        if (y > 0) {
                            ExtendedBlockStorage blockStorage = this.storageArrays[blockY >> 4];

                            if (blockStorage != null) {
                                blockStorage.setExtSkylightValue(x, blockY & 15, z, y);
                                this.worldObj
                                    .func_147479_m((this.xPosition << 4) + x, blockY, (this.zPosition << 4) + z);
                            }
                        }

                        --blockY;
                    } while (blockY > 0 && y > 0);
                }
            }
        }

        this.isModified = true;
    }

    @Shadow
    public int func_150808_b(int p_150808_1_, int p_150808_2_, int p_150808_3_) {
        int x = (xPosition << 4) + p_150808_1_;
        int z = (zPosition << 4) + p_150808_3_;
        return this.getBlock(p_150808_1_, p_150808_2_, p_150808_3_)
            .getLightOpacity(worldObj, x, p_150808_2_, z);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Block getBlock(final int p_150810_1_, final int p_150810_2_, final int p_150810_3_) {
        if (p_150810_2_ >= 0 && p_150810_2_ >> 4 < this.storageArrays.length) {
            ExtendedBlockStorage extendedBlockStorage = this.storageArrays[p_150810_2_ >> 4];
            if (extendedBlockStorage != null) {
                try {
                    return extendedBlockStorage.getBlockByExtId(p_150810_1_, p_150810_2_ & 15, p_150810_3_);
                } catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Getting block");
                    CrashReportCategory blockCategory = crashReport.makeCategory("Block being got");
                    blockCategory.addCrashSection(
                        "Location",
                        CrashReportCategory.getLocationInfo(p_150810_1_, p_150810_2_, p_150810_3_));
                    throw new ReportedException(crashReport);
                }
            }
        }
        return Blocks.air;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void recheckGaps(boolean p_150803_1_) {
        this.worldObj.theProfiler.startSection("recheckGaps");

        if (!this.worldObj.doChunksNearChunkExist(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8, 16)) {
            this.worldObj.theProfiler.endSection();
            return;
        }

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                if (this.updateSkylightColumns[x + z * 16]) {
                    this.updateSkylightColumns[x + z * 16] = false;
                    int y = this.getHeightValue(x, z);
                    int blockX = this.xPosition * 16 + x;
                    int blockZ = this.zPosition * 16 + z;

                    int minY = this.worldObj.getChunkHeightMapMinimum(blockX - 1, blockZ);
                    int maxY = this.worldObj.getChunkHeightMapMinimum(blockX + 1, blockZ);
                    int minX = this.worldObj.getChunkHeightMapMinimum(blockX, blockZ - 1);
                    int maxX = this.worldObj.getChunkHeightMapMinimum(blockX, blockZ + 1);

                    int minHeight = Math.min(minY, Math.min(maxY, Math.min(minX, maxX)));
                    this.checkSkylightNeighborHeight(blockX, blockZ, minHeight);

                    if (p_150803_1_) {
                        this.worldObj.theProfiler.endSection();
                        return;
                    }
                }
            }
        }

        this.isGapLightingUpdated = false;
        this.worldObj.theProfiler.endSection();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void checkSkylightNeighborHeight(int p_76599_1_, int p_76599_2_, int p_76599_3_) {
        int height = this.worldObj.getHeightValue(p_76599_1_, p_76599_2_);
        if (height > p_76599_3_) {
            this.updateSkylightNeighborHeight(p_76599_1_, p_76599_2_, p_76599_3_, height + 1);
        } else if (height < p_76599_3_) {
            this.updateSkylightNeighborHeight(p_76599_1_, p_76599_2_, height, p_76599_3_ + 1);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void updateSkylightNeighborHeight(int p_76609_1_, int p_76609_2_, int p_76609_3_, int p_76609_4_) {
        if (p_76609_4_ > p_76609_3_ && this.worldObj.doChunksNearChunkExist(p_76609_1_, 0, p_76609_2_, 16)) {
            for (int y = p_76609_3_; y < p_76609_4_; ++y) {
                this.worldObj.updateLightByType(EnumSkyBlock.Sky, p_76609_1_, y, p_76609_2_);
            }
            this.isModified = true;
        }
    }

    @Unique
    private void multithreadingandtweaks$updateHeightMap(int x, int z, int newHeight, int currentHeight) {
        this.heightMap[z << 4 | x] = newHeight;
        this.worldObj
            .markBlocksDirtyVertical(x + this.xPosition * 16, z + this.zPosition * 16, newHeight, currentHeight);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getBlockMetadata(int x, int y, int z) {
        if (multithreadingandtweaks$isChunkWithinBounds(y)) {
            ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y >> 4];
            if (extendedBlockStorage != null) {
                return extendedBlockStorage.getExtBlockMetadata(x, y & 15, z);
            }
        }
        return 0;
    }

    @Unique
    private boolean multithreadingandtweaks$isChunkWithinBounds(int y) {
        return (y >> 4) < this.storageArrays.length;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean setBlockMetadata(int x, int y, int z, int newMetadata) {
        ExtendedBlockStorage extendedBlockStorage = this.multithreadingandtweaks$getOrCreateExtendedBlockStorage(y);

        if (extendedBlockStorage == null) {
            return false;
        }

        int currentMetadata = extendedBlockStorage.getExtBlockMetadata(x, y & 15, z);

        if (currentMetadata == newMetadata) {
            return false;
        }

        extendedBlockStorage.setExtBlockMetadata(x, y & 15, z, newMetadata);

        isModified = true;

        Block block = extendedBlockStorage.getBlockByExtId(x, y & 15, z);

        if (block.hasTileEntity(newMetadata)) {
            TileEntity tileEntity = this.getTileEntityUnsafe(x, y, z);

            if (tileEntity != null) {
                tileEntity.blockMetadata = newMetadata;
                tileEntity.updateContainingBlockInfo();
            }
        }

        return true;
    }

    /**
     * Gets the amount of light saved in this block (doesn't adjust for daylight)
     */
    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getSavedLightValue(EnumSkyBlock p_76614_1, int p_76614_2, int p_76614_3, int p_76614_4) {
        ExtendedBlockStorage extendedBlockStorage = this.multithreadingandtweaks$getExtendedBlockStorage(p_76614_3);

        if (extendedBlockStorage == null) {
            return (p_76614_1 == EnumSkyBlock.Sky && !this.worldObj.provider.hasNoSky) ? 0
                : p_76614_1.defaultLightValue;
        }

        return (p_76614_1 == EnumSkyBlock.Sky && !this.worldObj.provider.hasNoSky)
            ? extendedBlockStorage.getExtSkylightValue(p_76614_2, p_76614_3 & 15, p_76614_4)
            : (p_76614_1 == EnumSkyBlock.Block)
                ? extendedBlockStorage.getExtBlocklightValue(p_76614_2, p_76614_3 & 15, p_76614_4)
                : p_76614_1.defaultLightValue;
    }

    /**
     * Sets the light value at the coordinate. If enumskyblock is set to sky it sets it in the skylightmap and if it's a
     * block then into the blocklightmap.
     */
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setLightValue(EnumSkyBlock p_76633_1, int p_76633_2, int p_76633_3, int p_76633_4, int p_76633_5) {
        ExtendedBlockStorage extendedBlockStorage = this
            .multithreadingandtweaks$getOrCreateExtendedBlockStorage(p_76633_3);

        if (p_76633_1 == EnumSkyBlock.Sky && !this.worldObj.provider.hasNoSky) {
            extendedBlockStorage.setExtSkylightValue(p_76633_2, p_76633_3 & 15, p_76633_4, p_76633_5);
        } else if (p_76633_1 == EnumSkyBlock.Block) {
            extendedBlockStorage.setExtBlocklightValue(p_76633_2, p_76633_3 & 15, p_76633_4, p_76633_5);
        }
    }

    /**
     * Gets the amount of light on a block taking into account sunlight
     */
    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getBlockLightValue(int p_76629_1, int p_76629_2, int p_76629_3, int p_76629_4) {
        ExtendedBlockStorage extendedBlockStorage = this.multithreadingandtweaks$getExtendedBlockStorage(p_76629_2);

        if (extendedBlockStorage == null) {
            return (this.worldObj.provider.hasNoSky || p_76629_4 >= EnumSkyBlock.Sky.defaultLightValue) ? 0
                : EnumSkyBlock.Sky.defaultLightValue - p_76629_4;
        }

        int skyLightValue = (this.worldObj.provider.hasNoSky) ? 0
            : extendedBlockStorage.getExtSkylightValue(p_76629_1, p_76629_2 & 15, p_76629_3);

        if (skyLightValue > 0) {
            isLit = true;
        }

        skyLightValue -= p_76629_4;
        int blockLightValue = extendedBlockStorage.getExtBlocklightValue(p_76629_1, p_76629_2 & 15, p_76629_3);

        if (blockLightValue > skyLightValue) {
            skyLightValue = blockLightValue;
        }

        return skyLightValue;
    }

    @Unique
    private ExtendedBlockStorage multithreadingandtweaks$getExtendedBlockStorage(int blockY) {
        return this.storageArrays[blockY >> 4];
    }

    @Unique
    private ExtendedBlockStorage multithreadingandtweaks$getOrCreateExtendedBlockStorage(int blockY) {
        ExtendedBlockStorage extendedBlockStorage = this.storageArrays[blockY >> 4];
        if (extendedBlockStorage == null) {
            extendedBlockStorage = this.storageArrays[blockY >> 4] = new ExtendedBlockStorage(
                blockY >> 4 << 4,
                !this.worldObj.provider.hasNoSky);
            this.generateSkylightMap();
        }
        return extendedBlockStorage;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void addEntity(Entity entity) {
        this.hasEntities = true;
        int x = MathHelper.floor_double(entity.posX / 16.0D);
        int z = MathHelper.floor_double(entity.posZ / 16.0D);

        if (x != this.xPosition || z != this.zPosition) {
            logger.warn(
                "Wrong location! " + entity
                    + " (at "
                    + x
                    + ", "
                    + z
                    + " instead of "
                    + this.xPosition
                    + ", "
                    + this.zPosition
                    + ")");
            Thread.dumpStack();
        }

        int y = MathHelper.floor_double(entity.posY / 16.0D);
        y = MathHelper.clamp_int(y, 0, this.entityLists.length - 1);

        MinecraftForge.EVENT_BUS.post(
            new EntityEvent.EnteringChunk(
                entity,
                this.xPosition,
                this.zPosition,
                entity.chunkCoordX,
                entity.chunkCoordZ));
        entity.addedToChunk = true;
        entity.chunkCoordX = this.xPosition;
        entity.chunkCoordY = y;
        entity.chunkCoordZ = this.zPosition;
        this.entityLists[y].add(entity);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void removeEntity(Entity entity) {
        this.removeEntityAtIndex(entity, entity.chunkCoordY);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void removeEntityAtIndex(Entity entity, int y) {
        y = MathHelper.clamp_int(y, 0, this.entityLists.length - 1);
        this.entityLists[y].remove(entity);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean canBlockSeeTheSky(int x, int y, int z) {
        return y >= this.heightMap[z << 4 | x];
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public TileEntity func_150806_e(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);

        if (tileentity != null && tileentity.isInvalid()) {
            chunkTileEntityMap.remove(chunkposition);
            tileentity = null;
        }

        if (tileentity == null) {
            Block block = this.getBlock(x, y, z);
            int meta = this.getBlockMetadata(x, y, z);

            if (block != null && block.hasTileEntity(meta)) {
                tileentity = block.createTileEntity(worldObj, meta);

                if (tileentity != null) {
                    tileentity.setWorldObj(this.worldObj);
                    tileentity.xCoord = this.xPosition * 16 + x;
                    tileentity.yCoord = y;
                    tileentity.zCoord = this.zPosition * 16 + z;
                    this.worldObj.setTileEntity(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord, tileentity);
                    this.chunkTileEntityMap.put(chunkposition, tileentity);
                }
            }
        }

        return tileentity;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void addTileEntity(TileEntity tileEntity) {
        int x = tileEntity.xCoord - this.xPosition * 16;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord - this.zPosition * 16;
        this.func_150812_a(x, y, z, tileEntity);

        if (this.isChunkLoaded) {
            this.worldObj.addTileEntity(tileEntity);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void func_150812_a(int x, int y, int z, TileEntity tileEntity) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        tileEntity.setWorldObj(this.worldObj);
        tileEntity.xCoord = this.xPosition * 16 + x;
        tileEntity.yCoord = y;
        tileEntity.zCoord = this.zPosition * 16 + z;
        int metadata = getBlockMetadata(x, y, z);

        if (this.getBlock(x, y, z)
            .hasTileEntity(metadata)) {
            if (this.chunkTileEntityMap.containsKey(chunkposition)) {
                ((TileEntity) this.chunkTileEntityMap.get(chunkposition)).invalidate();
            }

            tileEntity.validate();
            this.chunkTileEntityMap.put(chunkposition, tileEntity);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void removeTileEntity(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);

        if (this.isChunkLoaded) {
            TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.remove(chunkposition);

            if (tileentity != null) {
                tileentity.invalidate();
                this.worldObj.func_147457_a(tileentity);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setChunkModified() {
        this.isModified = true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void getEntitiesOfTypeWithinAAAB(Class<?> entityType, AxisAlignedBB boundingBox, List<Entity> resultEntities,
        IEntitySelector entitySelector) {
        int minYChunk = MathHelper.floor_double((boundingBox.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
        int maxYChunk = MathHelper.floor_double((boundingBox.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
        minYChunk = MathHelper.clamp_int(minYChunk, 0, this.entityLists.length - 1);
        maxYChunk = MathHelper.clamp_int(maxYChunk, 0, this.entityLists.length - 1);

        for (int chunkY = minYChunk; chunkY <= maxYChunk; ++chunkY) {
            List<Entity> entityList = this.entityLists[chunkY];

            for (Entity entity : entityList) {
                if (entityType.isAssignableFrom(entity.getClass()) && entity.boundingBox.intersectsWith(boundingBox)
                    && (entitySelector == null || entitySelector.isEntityApplicable(entity))) {
                    resultEntities.add(entity);
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean needsSaving(boolean p_76601_1_) {
        long worldTime = this.worldObj.getTotalWorldTime();
        if (p_76601_1_) {
            if ((this.hasEntities || this.isModified) && worldTime != this.lastSaveTime) {
                return true;
            }
        } else if (this.hasEntities && worldTime >= this.lastSaveTime + 600L) {
            return true;
        }
        return this.isModified;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Random getRandomWithSeed(long p_76617_1_) {
        long seed = this.worldObj.getSeed() + (long) (this.xPosition * this.xPosition * 4987142)
            + (long) (this.xPosition * 5947611)
            + (long) (this.zPosition * this.zPosition) * 4392871L
            + (long) (this.zPosition * 389711) ^ p_76617_1_;
        return new Random(seed);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isEmpty() {
        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void populateChunk(IChunkProvider p_76624_1_, IChunkProvider p_76624_2_, int p_76624_3_, int p_76624_4_) {
        if (!this.isTerrainPopulated) {
            if (p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_ + 1)
                && p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ + 1)
                && p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_)) {
                p_76624_1_.populate(p_76624_2_, p_76624_3_, p_76624_4_);
            }

            if (p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_)) {
                Chunk chunk = p_76624_1_.provideChunk(p_76624_3_ - 1, p_76624_4_);
                if (!chunk.isTerrainPopulated && p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_ + 1)
                    && p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ + 1)
                    && p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_ + 1)) {
                    p_76624_1_.populate(p_76624_2_, p_76624_3_ - 1, p_76624_4_);
                }
            }

            if (p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ - 1)) {
                Chunk chunk = p_76624_1_.provideChunk(p_76624_3_, p_76624_4_ - 1);
                if (!chunk.isTerrainPopulated && p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_ - 1)
                    && p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_ - 1)
                    && p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_)) {
                    p_76624_1_.populate(p_76624_2_, p_76624_3_, p_76624_4_ - 1);
                }
            }

            if (p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_ - 1)) {
                Chunk chunk = p_76624_1_.provideChunk(p_76624_3_ - 1, p_76624_4_ - 1);
                if (!chunk.isTerrainPopulated && p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ - 1)
                    && p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_)) {
                    p_76624_1_.populate(p_76624_2_, p_76624_3_ - 1, p_76624_4_ - 1);
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getPrecipitationHeight(int p_76626_1_, int p_76626_2_) {
        int chunkIdx = p_76626_1_ | (p_76626_2_ << 4);
        int height = this.precipitationHeightMap[chunkIdx];

        if (height == -999) {
            int topFilledSegment = this.getTopFilledSegment() + 15;
            height = -1;

            while (topFilledSegment > 0 && height == -1) {
                Block block = this.getBlock(p_76626_1_, topFilledSegment, p_76626_2_);
                Material material = block.getMaterial();

                if (!material.blocksMovement() && !material.isLiquid()) {
                    --topFilledSegment;
                } else {
                    height = topFilledSegment + 1;
                }
            }

            this.precipitationHeightMap[chunkIdx] = height;
        }

        return height;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void func_150804_b(boolean p_150804_1_) {
        if (this.isGapLightingUpdated && !this.worldObj.provider.hasNoSky && !p_150804_1_) {
            this.recheckGaps(this.worldObj.isRemote);
        }

        this.field_150815_m = true;

        if (this.isTerrainPopulated && !this.isLightPopulated) {
            this.func_150809_p();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean func_150802_k() {
        return this.field_150815_m && this.isTerrainPopulated && this.isLightPopulated;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public ChunkCoordIntPair getChunkCoordIntPair() {
        return new ChunkCoordIntPair(this.xPosition, this.zPosition);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean getAreLevelsEmpty(int p_76606_1_, int p_76606_2_) {
        p_76606_1_ = Math.max(p_76606_1_, 0);
        p_76606_2_ = Math.min(p_76606_2_, 255);

        for (int k = p_76606_1_; k <= p_76606_2_; k += 16) {
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[k >> 4];

            if (extendedblockstorage != null && !extendedblockstorage.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setStorageArrays(ExtendedBlockStorage[] p_76602_1_) {
        this.storageArrays = p_76602_1_;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void fillChunk(byte[] data, int blockMask, int metadataMask, boolean p_76607_4_) {
        Iterator<TileEntity> iterator = chunkTileEntityMap.values()
            .iterator();

        while (iterator.hasNext()) {
            TileEntity tileEntity = iterator.next();
            tileEntity.updateContainingBlockInfo();
        }

        int k = 0;
        boolean flag1 = !this.worldObj.provider.hasNoSky;

        for (int l = 0; l < this.storageArrays.length; ++l) {
            if ((blockMask & 1 << l) != 0) {
                if (this.storageArrays[l] == null) {
                    this.storageArrays[l] = new ExtendedBlockStorage(l << 4, flag1);
                }

                byte[] blockData = this.storageArrays[l].getBlockLSBArray();
                System.arraycopy(data, k, blockData, 0, blockData.length);
                k += blockData.length;

                if ((metadataMask & 1 << l) != 0) {
                    NibbleArray nibblearray = this.storageArrays[l].getMetadataArray();
                    System.arraycopy(data, k, nibblearray.data, 0, nibblearray.data.length);
                    k += nibblearray.data.length;
                }
            } else if (p_76607_4_ && this.storageArrays[l] != null) {
                this.storageArrays[l] = null;
            }
        }

        if (flag1) {
            for (int l = 0; l < this.storageArrays.length; ++l) {
                if ((blockMask & 1 << l) != 0 && this.storageArrays[l] != null) {
                    NibbleArray nibblearray = this.storageArrays[l].getSkylightArray();
                    System.arraycopy(data, k, nibblearray.data, 0, nibblearray.data.length);
                    k += nibblearray.data.length;
                }
            }
        }

        k = processBlockMSBArray(data, k, blockMask, p_76607_4_);
        k = processBlockBiomeArray(data, k, p_76607_4_);

        this.isLightPopulated = true;
        this.isTerrainPopulated = true;
        this.generateHeightMap();

        removeInvalidTileEntities(p_76607_4_);
    }

    private int processBlockMSBArray(byte[] data, int k, int blockMask, boolean p_76607_4_) {
        for (int l = 0; l < this.storageArrays.length; ++l) {
            if ((blockMask & 1 << l) != 0) {
                if (this.storageArrays[l] == null) {
                    k += 2048;
                } else {
                    NibbleArray nibblearray = this.storageArrays[l].getBlockMSBArray();

                    if (nibblearray == null) {
                        nibblearray = this.storageArrays[l].createBlockMSBArray();
                    }

                    System.arraycopy(data, k, nibblearray.data, 0, nibblearray.data.length);
                    k += nibblearray.data.length;
                }
            } else
                if (p_76607_4_ && this.storageArrays[l] != null && this.storageArrays[l].getBlockMSBArray() != null) {
                    this.storageArrays[l].clearMSBArray();
                }
        }
        return k;
    }

    @Unique
    private int processBlockBiomeArray(byte[] data, int k, boolean p_76607_4_) {
        if (p_76607_4_) {
            System.arraycopy(data, k, this.blockBiomeArray, 0, this.blockBiomeArray.length);
            return k + this.blockBiomeArray.length;
        }
        return k;
    }

    @Unique
    private void removeInvalidTileEntities(boolean p_76607_4_) {
        List<TileEntity> invalidList = new ArrayList<TileEntity>();
        Iterator<TileEntity> iterator = this.chunkTileEntityMap.values()
            .iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = iterator.next();
            int x = tileentity.xCoord & 15;
            int y = tileentity.yCoord;
            int z = tileentity.zCoord & 15;
            Block block = tileentity.getBlockType();

            if ((block != getBlock(x, y, z) || tileentity.blockMetadata != this.getBlockMetadata(x, y, z))
                && tileentity.shouldRefresh(
                    block,
                    getBlock(x, y, z),
                    tileentity.blockMetadata,
                    this.getBlockMetadata(x, y, z),
                    worldObj,
                    x,
                    y,
                    z)) {
                invalidList.add(tileentity);
            }
            tileentity.updateContainingBlockInfo();
        }

        for (TileEntity te : invalidList) {
            te.invalidate();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public byte[] getBiomeArray() {
        return this.blockBiomeArray;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setBiomeArray(byte[] biomeArray) {
        this.blockBiomeArray = biomeArray;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void resetRelightChecks() {
        this.queuedLightChecks = 0;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void enqueueRelightChecks() {
        for (int i = 0; i < 8; ++i) {
            if (this.queuedLightChecks >= 4096) {
                return;
            }

            int j = this.queuedLightChecks % 16;
            int k = this.queuedLightChecks / 16 % 16;
            int l = this.queuedLightChecks / 256;
            ++this.queuedLightChecks;
            int x = (this.xPosition << 4) + k;
            int z = (this.zPosition << 4) + l;

            for (int y = 0; y < 16; ++y) {
                int blockX = (j << 4) + y;

                if ((this.storageArrays[j] == null && (y == 0 || y == 15 || k == 0 || k == 15 || l == 0 || l == 15))
                    || (this.storageArrays[j] != null && this.storageArrays[j].getBlockByExtId(k, y, l)
                        .getMaterial() == Material.air)) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                int neighborX = x + dx;
                                int neighborY = blockX + dy;
                                int neighborZ = z + dz;

                                if (this.worldObj.getBlock(neighborX, neighborY, neighborZ)
                                    .getLightValue() > 0) {
                                    this.worldObj.func_147451_t(neighborX, neighborY, neighborZ);
                                }
                            }
                        }
                    }

                    this.worldObj.func_147451_t(x, blockX, z);
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void func_150809_p() {
        this.isTerrainPopulated = true;
        this.isLightPopulated = true;

        if (!this.worldObj.provider.hasNoSky && this.worldObj.checkChunksExist(
            this.xPosition * 16 - 1,
            0,
            this.zPosition * 16 - 1,
            this.xPosition * 16 + 17,
            63,
            this.zPosition * 16 + 17)) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (!this.func_150811_f(i, j)) {
                        this.isLightPopulated = false;
                        break;
                    }
                }
            }

            if (this.isLightPopulated) {
                multithreadingandtweaks$updateNeighborChunks();
            }
        } else {
            this.isLightPopulated = false;
        }
    }

    @Unique
    private void multithreadingandtweaks$updateNeighborChunks() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i == 0) {
                    this.func_150811_f(15, j);
                } else if (i == 1) {
                    this.func_150811_f(0, j);
                } else if (i == 2) {
                    this.func_150811_f(j, 15);
                } else if (i == 3) {
                    this.func_150811_f(j, 0);
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private boolean func_150811_f(int x, int z) {
        int topSegment = this.getTopFilledSegment();
        boolean hasBlocks = false;
        boolean allAir = false;

        for (int y = topSegment + 16 - 1; y > 63 || (y > 0 && !allAir); --y) {
            int blockLight = this.func_150808_b(x, y, z);

            if (blockLight == 255 && y < 63) {
                allAir = true;
            }

            if (!hasBlocks && blockLight > 0) {
                hasBlocks = true;
            } else if (hasBlocks && blockLight == 0
                && !this.worldObj.func_147451_t(this.xPosition * 16 + x, y, this.zPosition * 16 + z)) {
                    return false;
                }
        }

        for (; topSegment > 0; --topSegment) {
            if (this.getBlock(x, topSegment, z)
                .getLightValue() > 0) {
                this.worldObj.func_147451_t(this.xPosition * 16 + x, topSegment, this.zPosition * 16 + z);
            }
        }

        return true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public TileEntity getTileEntityUnsafe(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);

        if (tileentity != null && tileentity.isInvalid()) {
            removeInvalidTileEntity(x, y, z);
            tileentity = null;
        }

        return tileentity;
    }

    /**
     * Removes the tile entity at the specified position, only if it's
     * marked as invalid.
     *
     * @param x
     * @param y
     * @param z
     */
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void removeInvalidTileEntity(int x, int y, int z) {
        if (isChunkLoaded) {
            ChunkPosition position = new ChunkPosition(x, y, z);
            TileEntity entity = (TileEntity) this.chunkTileEntityMap.get(position);
            if (entity != null && entity.isInvalid()) {
                chunkTileEntityMap.remove(position);
            }
        }
    }
}
