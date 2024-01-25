package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import static net.minecraftforge.common.ChestGenHooks.BONUS_CHEST;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;

import fr.iamacat.optimizationsandtweaks.utils.concurrentlinkedhashmap.ConcurrentHashMapV8;
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
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
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
    public List<NextTickListEntry> getPendingBlockUpdates(Chunk p_72920_1_, boolean p_72920_2_) {
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
}
