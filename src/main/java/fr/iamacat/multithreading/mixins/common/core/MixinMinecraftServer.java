package fr.iamacat.multithreading.mixins.common.core;

import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import net.minecraft.crash.CrashReport;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Unique
    private MinecraftServer minecraftServer;
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    @Shadow
    public static final File field_152367_a = new File("usercache.json");

    /** Instance of Minecraft Server. */

    /** Collection of objects to update every tick. Type: List<IUpdatePlayerListBox> */
    @Shadow
    private final List tickables = new ArrayList();

    @Shadow
    public final Profiler theProfiler = new Profiler();
    @Shadow
    private final NetworkSystem field_147144_o;
    @Shadow
    private final ServerStatusResponse field_147147_p = new ServerStatusResponse();
    @Shadow
    private final Random field_147146_q = new Random();

    /** The server's hostname. */
    @Shadow
    @SideOnly(Side.SERVER)
    private String hostname;
    /** The server's port. */
    @Shadow
    private int serverPort = -1;
    /** The server world instances. */
    @Shadow
    public WorldServer[] worldServers = new WorldServer[0];
    /** The ServerConfigurationManager instance. */
    @Shadow
    private ServerConfigurationManager serverConfigManager;
    /** Indicates whether the server is running or not. Set to false to initiate a shutdown. */
    @Shadow
    private boolean serverRunning = true;
    /** Indicates to other classes that the server is safely stopped. */
    @Shadow
    private boolean serverStopped;
    /** Incremented every tick. */
    @Shadow
    private int tickCounter;

    /** The task the server is currently working on(and will output on outputPercentRemaining). */
    @Shadow
    public String currentTask;
    /** The percentage of the current task finished so far. */
    @Shadow
    public int percentDone;

    /** True if the server is in online mode. */
    @Shadow
    private boolean onlineMode;
    /** True if the server has animals turned on. */
    @Shadow
    private boolean canSpawnAnimals;
    @Shadow
    private boolean canSpawnNPCs;
    /** Indicates whether PvP is active on the server or not. */
    @Shadow
    private boolean pvpEnabled;
    /** Determines if flight is allowed or not. */
    @Shadow
    private boolean allowFlight;
    /** The server MOTD string. */
    @Shadow
    private String motd;
    /** Maximum build height. */
    @Shadow
    private int buildLimit;
    @Shadow
    private int field_143008_E = 0;
    @Shadow
    public final long[] tickTimeArray = new long[100];
    // public long[][] timeOfLastDimensionTick;
    @Shadow
    public Hashtable<Integer, long[]> worldTickTimes = new Hashtable<Integer, long[]>();
    @Shadow
    private KeyPair serverKeyPair;
    /** Username of the server owner (for integrated servers) */
    @Shadow
    private String serverOwner;
    @Shadow
    private String folderName;
    @Shadow
    @SideOnly(Side.CLIENT)
    private String worldName;
    @Shadow
    private boolean isDemo;
    @Shadow
    private boolean enableBonusChest;
    /** If true, there is no need to save chunks or stop the server, because that is already being done. */
    @Shadow
    private boolean worldIsBeingDeleted;
    @Shadow
    private String field_147141_M = "";
    @Shadow
    private boolean serverIsRunning;

    /** Set when warned for "Can't keep up", which triggers again after 15 seconds. */
    @Shadow
    private long timeOfLastWarning;
    @Shadow
    private String userMessage;
    @Shadow
    private boolean startProfiling;
    @Shadow
    private boolean isGamemodeForced;
    @Shadow
    private long field_147142_T = 0L;

    public MixinMinecraftServer() {
        this.field_147144_o = new NetworkSystem(minecraftServer);
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At("HEAD"), remap = false, cancellable = true)
    public void updateTimeLightAndEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinMinecraftServer) {
            this.theProfiler.startSection("levels");
            net.minecraftforge.common.chunkio.ChunkIOExecutor.tick();
            int i;

            Integer[] ids = DimensionManager.getIDs(this.tickCounter % 200 == 0);
            for (int id : ids) {
                long j = System.nanoTime();

                if (id == 0 || this.getAllowNether()) {
                    WorldServer worldserver = DimensionManager.getWorld(id);
                    this.theProfiler.startSection(
                        worldserver.getWorldInfo()
                            .getWorldName());
                    this.theProfiler.startSection("pools");
                    this.theProfiler.endSection();

                    if (this.tickCounter % 20 == 0) {
                        this.theProfiler.startSection("timeSync");
                        this.serverConfigManager.sendPacketToAllPlayersInDimension(
                            new S03PacketTimeUpdate(
                                worldserver.getTotalWorldTime(),
                                worldserver.getWorldTime(),
                                worldserver.getGameRules()
                                    .getGameRuleBooleanValue("doDaylightCycle")),
                            worldserver.provider.dimensionId);
                        this.theProfiler.endSection();
                    }

                    this.theProfiler.startSection("tick");
                    FMLCommonHandler.instance()
                        .onPreWorldTick(worldserver);
                    CrashReport crashreport;

                    try {
                        worldserver.tick();
                    } catch (Throwable throwable1) {
                        crashreport = CrashReport.makeCrashReport(throwable1, "Exception ticking world");
                        worldserver.addWorldInfoToCrashReport(crashreport);
                        throw new ReportedException(crashreport);
                    }

                    try {
                        worldserver.updateEntities();
                    } catch (Throwable throwable) {
                        crashreport = CrashReport.makeCrashReport(throwable, "Exception ticking world entities");
                        worldserver.addWorldInfoToCrashReport(crashreport);
                        throw new ReportedException(crashreport);
                    }

                    FMLCommonHandler.instance()
                        .onPostWorldTick(worldserver);
                    this.theProfiler.endSection();
                    this.theProfiler.startSection("tracker");
                    worldserver.getEntityTracker()
                        .updateTrackedEntities();
                    this.theProfiler.endSection();
                    this.theProfiler.endSection();
                }

                worldTickTimes.get(id)[this.tickCounter % 100] = System.nanoTime() - j;
            }

            this.theProfiler.endStartSection("dim_unloading");
            DimensionManager.unloadWorlds(worldTickTimes);
            this.theProfiler.endStartSection("connection");
            this.func_147137_ag()
                .networkTick();
            this.theProfiler.endStartSection("players");
            this.serverConfigManager.sendPlayerInfoToAllPlayers();
            this.theProfiler.endStartSection("tickables");

            for (i = 0; i < this.tickables.size(); ++i) {
                ((IUpdatePlayerListBox) this.tickables.get(i)).update();
            }

            this.theProfiler.endSection();
        }
        ci.cancel();
    }

    @Unique
    public boolean getAllowNether() {
        return true;
    }

    @Unique
    public NetworkSystem func_147137_ag() {
        return this.field_147144_o;
    }
}
