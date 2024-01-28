package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Mixin(value = WorldServer.class, priority = 999)
public abstract class MixinWorldServer extends World {

    @Unique
    private WorldServer optimizationsAndTweaks$worldServer;
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
    @Shadow
    private TreeSet pendingTickListEntriesTreeSet;
    @Shadow
    private final Teleporter worldTeleporter;
    @Shadow
    private List pendingTickListEntriesThisTick = new ArrayList();
    @Shadow
    private IntHashMap entityIdMap;

    public MixinWorldServer(MinecraftServer p_i45284_1_, ISaveHandler p_i45284_2_, String p_i45284_3_, int p_i45284_4_,
                            WorldSettings p_i45284_5_, Profiler p_i45284_6_) {
        super(p_i45284_2_, p_i45284_3_, p_i45284_5_, WorldProvider.getProviderForDimension(p_i45284_4_), p_i45284_6_);
        this.mcServer = p_i45284_1_;
        this.theEntityTracker = new EntityTracker(optimizationsAndTweaks$worldServer);
        this.thePlayerManager = new PlayerManager(optimizationsAndTweaks$worldServer);

        if (this.entityIdMap == null) {
            this.entityIdMap = new IntHashMap();
        }

        if (this.pendingTickListEntriesHashSet == null) {
            this.pendingTickListEntriesHashSet = new HashSet();
        }

        if (this.pendingTickListEntriesTreeSet == null) {
            this.pendingTickListEntriesTreeSet = new TreeSet();
        }

        this.worldTeleporter = new Teleporter(optimizationsAndTweaks$worldServer);
        this.worldScoreboard = new ServerScoreboard(p_i45284_1_);
        ScoreboardSaveData scoreboardsavedata = (ScoreboardSaveData) this.mapStorage
            .loadData(ScoreboardSaveData.class, "scoreboard");

        if (scoreboardsavedata == null) {
            scoreboardsavedata = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", scoreboardsavedata);
        }

        if (!(optimizationsAndTweaks$worldServer instanceof WorldServerMulti)) // Forge: We fix the global mapStorage, which causes us to share
        // scoreboards early. So don't associate the save data with the
        // temporary scoreboard
        {
            scoreboardsavedata.func_96499_a(this.worldScoreboard);
        }
        ((ServerScoreboard) this.worldScoreboard).func_96547_a(scoreboardsavedata);
        DimensionManager.setWorld(p_i45284_4_, optimizationsAndTweaks$worldServer);
    }

    /**
     * @author iamacatfr
     * @reason optimize getPendingBlockUpdates from WorldServer to reduce Tps lags
     */
    @Overwrite
    public synchronized List<NextTickListEntry> getPendingBlockUpdates(Chunk p_72920_1_, boolean p_72920_2_) {
        List<NextTickListEntry> pendingBlockUpdates = optimizationsAndTweaks$collectPendingBlockUpdates(p_72920_1_, p_72920_2_);
        optimizationsAndTweaks$removeProcessedEntries(p_72920_2_);
        return pendingBlockUpdates;
    }

    @Unique
    private List<NextTickListEntry> optimizationsAndTweaks$collectPendingBlockUpdates(Chunk p_72920_1_, boolean p_72920_2_) {
        List<NextTickListEntry> pendingBlockUpdates = new ArrayList<>();
        ChunkCoordIntPair chunkcoordintpair = p_72920_1_.getChunkCoordIntPair();
        int minX = (chunkcoordintpair.chunkXPos << 4) - 2;
        int maxX = minX + 16 + 2;
        int minZ = (chunkcoordintpair.chunkZPos << 4) - 2;
        int maxZ = minZ + 16 + 2;
        for (int i = 0; i < 2; ++i) {
            if (i == 1 && !this.pendingTickListEntriesThisTick.isEmpty()) {
                logger.debug("toBeTicked = " + this.pendingTickListEntriesThisTick.size());
            }

            Iterator<NextTickListEntry> iterator = this.pendingTickListEntriesThisTick.iterator();
            while (iterator.hasNext()) {
                NextTickListEntry nextticklistentry = iterator.next();
                iterator.remove();
                if (optimizationsAndTweaks$isWithinBounds(nextticklistentry, minX, maxX, minZ, maxZ)) {
                    if (p_72920_2_) {
                        iterator.remove();
                    }

                    pendingBlockUpdates.add(nextticklistentry);
                }
            }
        }
        return pendingBlockUpdates;
    }

    @Unique
    private boolean optimizationsAndTweaks$isWithinBounds(NextTickListEntry entry, int minX, int maxX, int minZ, int maxZ) {
        return entry.xCoord >= minX && entry.xCoord < maxX &&
            entry.zCoord >= minZ && entry.zCoord < maxZ;
    }

    @Unique
    private void optimizationsAndTweaks$removeProcessedEntries(boolean p_72920_2_) {
        if (p_72920_2_) {
            this.pendingTickListEntriesThisTick.clear();
            this.pendingTickListEntriesTreeSet.clear();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateWeather()
    {
        boolean flag = this.isRaining();
        super.updateWeather();

        if (this.prevRainingStrength != this.rainingStrength)
        {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.dimensionId);
        }

        if (this.prevThunderingStrength != this.thunderingStrength)
        {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.dimensionId);
        }

        if (flag != this.isRaining())
        {
            if (flag)
            {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(2, 0.0F), this.provider.dimensionId);
            }
            else
            {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(1, 0.0F), this.provider.dimensionId);
            }

            this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.dimensionId);
            this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.dimensionId);
        }
    }
    @Overwrite
    public synchronized boolean tickUpdates(boolean p_72955_1_)
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
     * @author
     * @reason
     * @method func_147456_g = onTick
     */
    // todo fix lags caused by ExtendedBlockStorage.getID when using 32 chunks of render distance
    @Overwrite
    public void func_147456_g() {
        super.func_147456_g();

        for (Object o : this.activeChunkSet) {
            processChunk((ChunkCoordIntPair) o);
        }
    }

    @Unique
    private void processChunk(ChunkCoordIntPair chunkCoordIntPair) {
        int chunkX = chunkCoordIntPair.chunkXPos * 16;
        int chunkZ = chunkCoordIntPair.chunkZPos * 16;
        Chunk chunk = this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);

        optimizationsAndTweaks$handleChunk(chunkX, chunkZ, chunk);
        optimizationsAndTweaks$handleBlockTicks(chunk,chunkX,chunkZ);
    }
    @Unique
    private void optimizationsAndTweaks$handleChunk(int chunkX, int chunkZ, Chunk chunk) {
        this.theProfiler.startSection("getChunk");
        this.func_147467_a(chunkX, chunkZ, chunk);
        this.theProfiler.endStartSection("tickChunk");
        chunk.func_150804_b(false);
        this.theProfiler.endStartSection("thunder");
        optimizationsAndTweaks$handleThunder(chunkX, chunkZ, chunk);
        this.theProfiler.endStartSection("iceandsnow");
        optimizationsAndTweaks$handleIceAndSnow(chunkX, chunkZ, chunk);
    }
    @Unique
    private void optimizationsAndTweaks$handleThunder(int chunkX, int chunkZ, Chunk chunk) {
        if (provider.canDoLightning(chunk) && this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int i1 = this.updateLCG >> 2;
            int j1 = chunkX + (i1 & 15);
            int k1 = chunkZ + (i1 >> 8 & 15);
            int l1 = this.getPrecipitationHeight(j1, k1);

            if (this.canLightningStrikeAt(j1, l1, k1)) {
                this.addWeatherEffect(new EntityLightningBolt(this, j1, l1, k1));
            }
        }
    }
    @Unique
    private void optimizationsAndTweaks$handleIceAndSnow(int chunkX, int chunkZ, Chunk chunk) {
        if (provider.canDoRainSnowIce(chunk) && this.rand.nextInt(16) == 0) {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int i1 = this.updateLCG >> 2;
            int j1 = i1 & 15;
            int k1 = i1 >> 8 & 15;
            int l1 = this.getPrecipitationHeight(j1 + chunkX, k1 + chunkZ);

            if (this.isBlockFreezableNaturally(j1 + chunkX, l1 - 1, k1 + chunkZ)) {
                this.setBlock(j1 + chunkX, l1 - 1, k1 + chunkZ, Blocks.ice);
            }

            if (this.isRaining() && this.func_147478_e(j1 + chunkX, l1, k1 + chunkZ, true)) {
                this.setBlock(j1 + chunkX, l1, k1 + chunkZ, Blocks.snow_layer);
            }

            if (this.isRaining()) {
                BiomeGenBase biomegenbase = this.getBiomeGenForCoords(j1 + chunkX, k1 + chunkZ);

                if (biomegenbase.canSpawnLightningBolt()) {
                    this.getBlock(j1 + chunkX, l1 - 1, k1 + chunkZ).fillWithRain(this, j1 + chunkX, l1 - 1, k1 + chunkZ);
                }
            }
        }
    }
    @Unique
    private void optimizationsAndTweaks$handleBlockTicks(Chunk chunk, int chunkX, int chunkZ) {
        this.theProfiler.endStartSection("tickBlocks");
        ExtendedBlockStorage[] aextendedblockstorage = chunk.getBlockStorageArray();

        for (ExtendedBlockStorage extendedblockstorage : aextendedblockstorage) {
            if (extendedblockstorage != null && extendedblockstorage.getNeedsRandomTick()) {
                for (int i3 = 0; i3 < 3; ++i3) {
                    optimizationsAndTweaks$handleBlockTick(extendedblockstorage,chunkX,chunkZ);
                }
            }
        }

        this.theProfiler.endSection();
    }
    @Unique
    private void optimizationsAndTweaks$handleBlockTick(ExtendedBlockStorage extendedblockstorage, int chunkX, int chunkZ) {
        this.updateLCG = this.updateLCG * 3 + 1013904223;
        int i2 = this.updateLCG >> 2;
        int j2 = i2 & 15;
        int k2 = i2 >> 8 & 15;
        int l2 = i2 >> 16 & 15;
        Block block = extendedblockstorage.getBlockByExtId(j2, l2, k2);

        if (block.getTickRandomly()) {
            block.updateTick(this, j2 + chunkX, l2 + extendedblockstorage.getYLocation(), k2 + chunkZ, this.rand);
        }
    }
}
