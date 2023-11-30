package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraftforge.common.ChestGenHooks.BONUS_CHEST;

@Mixin(value = WorldServer.class,priority = 999)
public abstract class MixinWorldServer extends World {

    @Unique
    private WorldServer worldServer;
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    @Shadow
    private final MinecraftServer mcServer;
    @Shadow
    private final EntityTracker theEntityTracker;
    @Shadow
    private final PlayerManager thePlayerManager;
    @Shadow
    private Set pendingTickListEntriesHashSet;

    /** All work to do in future ticks. */
    @Shadow
    private TreeSet pendingTickListEntriesTreeSet;
    @Shadow
    public ChunkProviderServer theChunkProviderServer;
    /** Whether or not level saving is enabled */
    @Shadow
    public boolean levelSaving;
    /** is false if there are no players */
    @Shadow
    private boolean allPlayersSleeping;
    @Shadow
    private int updateEntityTick;
    /** the teleporter to use when the entity is being transferred into the dimension */
    @Shadow
    private final Teleporter worldTeleporter;
    @Shadow
    private final SpawnerAnimals animalSpawner = new SpawnerAnimals();
    @Shadow
    private int blockEventCacheIndex;
    @Shadow
    public static final WeightedRandomChestContent[] bonusChestContent = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.stick, 0, 1, 3, 10), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.planks), 0, 1, 3, 10), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log), 0, 1, 3, 10), new WeightedRandomChestContent(Items.stone_axe, 0, 1, 1, 3), new WeightedRandomChestContent(Items.wooden_axe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.stone_pickaxe, 0, 1, 1, 3), new WeightedRandomChestContent(Items.wooden_pickaxe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.apple, 0, 2, 3, 5), new WeightedRandomChestContent(Items.bread, 0, 2, 3, 3), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log2), 0, 1, 3, 10)};
    @Shadow
    private List pendingTickListEntriesThisTick = new ArrayList();
    /** An IntHashMap of entity IDs (integers) to their Entity objects. */
    @Shadow
    private IntHashMap entityIdMap;

    /** Stores the recently processed (lighting) chunks */
    @Shadow
    protected Set<ChunkCoordIntPair> doneChunks = new HashSet<ChunkCoordIntPair>();
    @Shadow
    public List<Teleporter> customTeleporters = new ArrayList<Teleporter>();

    public MixinWorldServer(MinecraftServer p_i45284_1_, ISaveHandler p_i45284_2_, String p_i45284_3_, int p_i45284_4_, WorldSettings p_i45284_5_, Profiler p_i45284_6_)
    {
        super(p_i45284_2_, p_i45284_3_, p_i45284_5_, WorldProvider.getProviderForDimension(p_i45284_4_), p_i45284_6_);
        this.mcServer = p_i45284_1_;
        this.theEntityTracker = new EntityTracker(worldServer);
        this.thePlayerManager = new PlayerManager(worldServer);

        if (this.entityIdMap == null)
        {
            this.entityIdMap = new IntHashMap();
        }

        if (this.pendingTickListEntriesHashSet == null)
        {
            this.pendingTickListEntriesHashSet = new HashSet();
        }

        if (this.pendingTickListEntriesTreeSet == null)
        {
            this.pendingTickListEntriesTreeSet = new TreeSet();
        }

        this.worldTeleporter = new Teleporter(worldServer);
        this.worldScoreboard = new ServerScoreboard(p_i45284_1_);
        ScoreboardSaveData scoreboardsavedata = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");

        if (scoreboardsavedata == null)
        {
            scoreboardsavedata = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", scoreboardsavedata);
        }

        if (!(worldServer instanceof WorldServerMulti)) //Forge: We fix the global mapStorage, which causes us to share scoreboards early. So don't associate the save data with the temporary scoreboard
        {
            scoreboardsavedata.func_96499_a(this.worldScoreboard);
        }
        ((ServerScoreboard)this.worldScoreboard).func_96547_a(scoreboardsavedata);
        DimensionManager.setWorld(p_i45284_4_, worldServer);
    }

    /**
     * @author iamacatfr
     * @reason optimize func_147456_g/updateBlocks method
     */
    @Overwrite
    public void func_147456_g() {
        super.func_147456_g();
        for (Object o : this.activeChunkSet) {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) o;
            int k = chunkcoordintpair.chunkXPos * 16;
            int l = chunkcoordintpair.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunk = this.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);
            this.func_147467_a(k, l, chunk);
            this.theProfiler.endStartSection("tickChunk");
            chunk.func_150804_b(false);
            this.theProfiler.endStartSection("thunder");

            if (provider.canDoLightning(chunk) && this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                int randValue = this.rand.nextInt(100000);
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int i1 = this.updateLCG >> 2;
                int j1 = k + (i1 & 15);
                int k1 = l + (i1 >> 8 & 15);
                int l1 = this.getPrecipitationHeight(j1, k1);

                if (randValue == 0 && this.canLightningStrikeAt(j1, l1, k1)) {
                    this.addWeatherEffect(new EntityLightningBolt(this, j1, l1, k1));
                }
            }

            this.theProfiler.endStartSection("iceandsnow");

            if (provider.canDoRainSnowIce(chunk) && this.rand.nextInt(16) == 0) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int i1 = this.updateLCG >> 2;
                int j1 = i1 & 15;
                int k1 = i1 >> 8 & 15;
                int l1 = this.getPrecipitationHeight(j1 + k, k1 + l);
                Block blockBelow = this.getBlock(j1 + k, l1 - 1, k1 + l);

                if (blockBelow == Blocks.water && this.isBlockFreezableNaturally(j1 + k, l1 - 1, k1 + l)) {
                    this.setBlock(j1 + k, l1 - 1, k1 + l, Blocks.ice);
                }

                if (this.isRaining() && this.func_147478_e(j1 + k, l1, k1 + l, true)) {
                    this.setBlock(j1 + k, l1, k1 + l, Blocks.snow_layer);
                }

                if (this.isRaining()) {
                    BiomeGenBase biomegenbase = this.getBiomeGenForCoords(j1 + k, k1 + l);

                    if (biomegenbase.canSpawnLightningBolt()) {
                        blockBelow.fillWithRain(this, j1 + k, l1 - 1, k1 + l);
                    }
                }
            }

            this.theProfiler.endStartSection("tickBlocks");

            for (ExtendedBlockStorage extendedblockstorage : chunk.getBlockStorageArray()) {
                if (extendedblockstorage != null && extendedblockstorage.getNeedsRandomTick()) {
                    for (int i3 = 0; i3 < 3; ++i3) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int i2 = this.updateLCG >> 2;
                        int j2 = i2 & 15;
                        int k2 = i2 >> 8 & 15;
                        int l2 = i2 >> 16 & 15;
                        Block block = extendedblockstorage.getBlockByExtId(j2, l2, k2);

                        if (block.getTickRandomly()) {
                            block.updateTick(this, j2 + k, l2 + extendedblockstorage.getYLocation(), k2 + l, this.rand);
                        }
                    }
                }
            }
        }

        this.theProfiler.endSection();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void scheduleBlockUpdateWithPriority(int p_147454_1_, int p_147454_2_, int p_147454_3_, Block p_147454_4_, int p_147454_5_, int p_147454_6_)
    {
        NextTickListEntry nextticklistentry = new NextTickListEntry(p_147454_1_, p_147454_2_, p_147454_3_, p_147454_4_);
        //Keeping here as a note for future when it may be restored.
        //boolean isForced = getPersistentChunks().containsKey(new ChunkCoordIntPair(nextticklistentry.xCoord >> 4, nextticklistentry.zCoord >> 4));
        //byte b0 = isForced ? 0 : 8;
        byte b0 = 0;

        if (this.scheduledUpdatesAreImmediate && p_147454_4_.getMaterial() != Material.air)
        {
            if (p_147454_4_.func_149698_L())
            {
                b0 = 8;

                if (this.checkChunksExist(nextticklistentry.xCoord - b0, nextticklistentry.yCoord - b0, nextticklistentry.zCoord - b0, nextticklistentry.xCoord + b0, nextticklistentry.yCoord + b0, nextticklistentry.zCoord + b0))
                {
                    Block block1 = this.getBlock(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord);

                    if (block1.getMaterial() != Material.air && block1 == nextticklistentry.func_151351_a())
                    {
                        block1.updateTick(this, nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, this.rand);
                    }
                }

                return;
            }

            p_147454_5_ = 1;
        }

        if (this.checkChunksExist(p_147454_1_ - b0, p_147454_2_ - b0, p_147454_3_ - b0, p_147454_1_ + b0, p_147454_2_ + b0, p_147454_3_ + b0))
        {
            if (p_147454_4_.getMaterial() != Material.air)
            {
                nextticklistentry.setScheduledTime((long)p_147454_5_ + this.worldInfo.getWorldTotalTime());
                nextticklistentry.setPriority(p_147454_6_);
            }

            if (!this.pendingTickListEntriesHashSet.contains(nextticklistentry))
            {
                this.pendingTickListEntriesHashSet.add(nextticklistentry);
                this.pendingTickListEntriesTreeSet.add(nextticklistentry);
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void func_147446_b(int p_147446_1_, int p_147446_2_, int p_147446_3_, Block p_147446_4_, int p_147446_5_, int p_147446_6_)
    {
        NextTickListEntry nextticklistentry = new NextTickListEntry(p_147446_1_, p_147446_2_, p_147446_3_, p_147446_4_);
        nextticklistentry.setPriority(p_147446_6_);

        if (p_147446_4_.getMaterial() != Material.air)
        {
            nextticklistentry.setScheduledTime((long)p_147446_5_ + this.worldInfo.getWorldTotalTime());
        }

        if (!this.pendingTickListEntriesHashSet.contains(nextticklistentry))
        {
            this.pendingTickListEntriesHashSet.add(nextticklistentry);
            this.pendingTickListEntriesTreeSet.add(nextticklistentry);
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean tickUpdates(boolean p_72955_1_)
    {
        int i = this.pendingTickListEntriesTreeSet.size();

        if (i != this.pendingTickListEntriesHashSet.size())
        {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        else
        {
            if (i > 1000)
            {
                i = 1000;
            }

            this.theProfiler.startSection("cleaning");
            NextTickListEntry nextticklistentry;

            for (int j = 0; j < i; ++j)
            {
                nextticklistentry = (NextTickListEntry)this.pendingTickListEntriesTreeSet.first();

                if (!p_72955_1_ && nextticklistentry.scheduledTime > this.worldInfo.getWorldTotalTime())
                {
                    break;
                }

                this.pendingTickListEntriesTreeSet.remove(nextticklistentry);
                this.pendingTickListEntriesHashSet.remove(nextticklistentry);
                this.pendingTickListEntriesThisTick.add(nextticklistentry);
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("ticking");
            Iterator iterator = this.pendingTickListEntriesThisTick.iterator();

            while (iterator.hasNext())
            {
                nextticklistentry = (NextTickListEntry)iterator.next();
                iterator.remove();
                //Keeping here as a note for future when it may be restored.
                //boolean isForced = getPersistentChunks().containsKey(new ChunkCoordIntPair(nextticklistentry.xCoord >> 4, nextticklistentry.zCoord >> 4));
                //byte b0 = isForced ? 0 : 8;
                byte b0 = 0;

                if (this.checkChunksExist(nextticklistentry.xCoord - b0, nextticklistentry.yCoord - b0, nextticklistentry.zCoord - b0, nextticklistentry.xCoord + b0, nextticklistentry.yCoord + b0, nextticklistentry.zCoord + b0))
                {
                    Block block = this.getBlock(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord);

                    if (block.getMaterial() != Material.air && Block.isEqualTo(block, nextticklistentry.func_151351_a()))
                    {
                        try
                        {
                            block.updateTick(this, nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, this.rand);
                        }
                        catch (Throwable throwable1)
                        {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Exception while ticking a block");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being ticked");
                            int k;

                            try
                            {
                                k = this.getBlockMetadata(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord);
                            }
                            catch (Throwable throwable)
                            {
                                k = -1;
                            }

                            CrashReportCategory.func_147153_a(crashreportcategory, nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, block, k);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
                else
                {
                    this.scheduleBlockUpdate(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, nextticklistentry.func_151351_a(), 0);
                }
            }

            this.theProfiler.endSection();
            this.pendingTickListEntriesThisTick.clear();
            return !this.pendingTickListEntriesTreeSet.isEmpty();
        }
    }

    /**
     * @author iamacatfr
     * @reason optimize getPendingBlockUpdates from WorldServer to reduce Tps lags
     */
    @Overwrite
    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunk, boolean remove) {
        List<NextTickListEntry> result = new ArrayList<>();
        ChunkCoordIntPair chunkCoords = getChunkCoordinates(chunk);
        int minX = calculateMinX(chunkCoords);
        int maxX = calculateMaxX(minX);
        int minZ = calculateMinZ(chunkCoords);
        int maxZ = calculateMaxZ(minZ);

        for (Collection tickSet : Arrays.asList(pendingTickListEntriesTreeSet, pendingTickListEntriesThisTick)) {
            processTickSet(tickSet, minX, maxX, minZ, maxZ, result, remove,chunk);
        }

        return result.isEmpty() ? null : result;
    }
    @Unique
    private ChunkCoordIntPair getChunkCoordinates(Chunk chunk) {
        return chunk.getChunkCoordIntPair();
    }
    @Unique
    private int calculateMinX(ChunkCoordIntPair chunkCoords) {
        return (chunkCoords.chunkXPos << 4) - 2;
    }

    @Unique
    private int calculateMaxX(int minX) {
        return minX + 16 + 2;
    }
    @Unique
    private int calculateMinZ(ChunkCoordIntPair chunkCoords) {
        return (chunkCoords.chunkZPos << 4) - 2;
    }

    @Unique
    private int calculateMaxZ(int minZ) {
        return minZ + 16 + 2;
    }

    @Unique
    private void processTickSet(Collection<NextTickListEntry> tickSet, int minX, int maxX, int minZ, int maxZ, List<NextTickListEntry> result, boolean remove, Chunk chunk) {
        if (!chunk.isChunkLoaded) {
            return;
        }
        List<NextTickListEntry> filteredEntries = tickSet.parallelStream()
            .filter(entry -> entry.xCoord >= minX && entry.xCoord < maxX &&
                entry.zCoord >= minZ && entry.zCoord < maxZ)
            .collect(Collectors.toList());

        if (remove) {
            pendingTickListEntriesHashSet.removeAll(filteredEntries);
        }

        result.addAll(filteredEntries);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void initialize(WorldSettings p_72963_1_)
    {
        if (this.entityIdMap == null)
        {
            this.entityIdMap = new IntHashMap();
        }

        if (this.pendingTickListEntriesHashSet == null)
        {
            this.pendingTickListEntriesHashSet = new HashSet();
        }

        if (this.pendingTickListEntriesTreeSet == null)
        {
            this.pendingTickListEntriesTreeSet = new TreeSet();
        }

        this.createSpawnPosition(p_72963_1_);
        super.initialize(p_72963_1_);
    }
    @Shadow
    protected void createSpawnPosition(WorldSettings p_73052_1_)
    {
        if (!this.provider.canRespawnHere())
        {
            this.worldInfo.setSpawnPosition(0, this.provider.getAverageGroundLevel(), 0);
        }
        else
        {
            if (net.minecraftforge.event.ForgeEventFactory.onCreateWorldSpawn(this, p_73052_1_)) return;
            this.findingSpawnPoint = true;
            WorldChunkManager worldchunkmanager = this.provider.worldChunkMgr;
            List list = worldchunkmanager.getBiomesToSpawnIn();
            Random random = new Random(this.getSeed());
            ChunkPosition chunkposition = worldchunkmanager.findBiomePosition(0, 0, 256, list, random);
            int i = 0;
            int j = this.provider.getAverageGroundLevel();
            int k = 0;

            if (chunkposition != null)
            {
                i = chunkposition.chunkPosX;
                k = chunkposition.chunkPosZ;
            }
            else
            {
                logger.warn("Unable to find spawn biome");
            }

            int l = 0;

            while (!this.provider.canCoordinateBeSpawn(i, k))
            {
                i += random.nextInt(64) - random.nextInt(64);
                k += random.nextInt(64) - random.nextInt(64);
                ++l;

                if (l == 1000)
                {
                    break;
                }
            }

            this.worldInfo.setSpawnPosition(i, j, k);
            this.findingSpawnPoint = false;

            if (p_73052_1_.isBonusChestEnabled())
            {
                this.createBonusChest();
            }
        }
    }
    @Shadow
    protected void createBonusChest()
    {
        WorldGeneratorBonusChest worldgeneratorbonuschest = new WorldGeneratorBonusChest(ChestGenHooks.getItems(BONUS_CHEST, rand), ChestGenHooks.getCount(BONUS_CHEST, rand));

        for (int i = 0; i < 10; ++i)
        {
            int j = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int k = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int l = this.getTopSolidOrLiquidBlock(j, k) + 1;

            if (worldgeneratorbonuschest.generate(this, this.rand, j, l, k))
            {
                break;
            }
        }
    }
    @Shadow
    protected int func_152379_p()
    {
        return this.mcServer.getConfigurationManager().getViewDistance();
    }
    @Shadow
    public MinecraftServer func_73046_m()
    {
        return this.mcServer;
    }
}
