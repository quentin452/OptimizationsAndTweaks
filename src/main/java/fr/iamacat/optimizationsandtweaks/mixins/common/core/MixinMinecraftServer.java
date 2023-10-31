package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.StartupQuery;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;

import javax.imageio.ImageIO;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

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

    @Shadow
    private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", (IPlayerUsage) this, getSystemTimeMillis());

    public MixinMinecraftServer() {
        this.field_147144_o = new NetworkSystem(minecraftServer);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateTimeLightAndEntities() {
        if (OptimizationsandTweaksConfig.enableMixinMinecraftServer) {
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
    }

    @Unique
    public boolean getAllowNether() {
        return true;
    }

    @Unique
    public NetworkSystem func_147137_ag() {
        return this.field_147144_o;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void run()
    {
        try
        {
            if (this.startServer())
            {
                FMLCommonHandler.instance().handleServerStarted();
                long i = getSystemTimeMillis();
                long l = 0L;
                this.field_147147_p.func_151315_a(new ChatComponentText(this.motd));
                this.field_147147_p.func_151321_a(new ServerStatusResponse.MinecraftProtocolVersionIdentifier("1.7.10", 5));
                this.func_147138_a(this.field_147147_p);

                while (this.serverRunning)
                {
                    long j = getSystemTimeMillis();
                    long k = j - i;

                    if (k > 2000L && i - this.timeOfLastWarning >= 15000L)
                    {
                        logger.warn("Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", new Object[] {Long.valueOf(k), Long.valueOf(k / 50L)});
                        k = 2000L;
                        this.timeOfLastWarning = i;
                    }

                    if (k < 0L)
                    {
                        logger.warn("Time ran backwards! Did the system time change?");
                        k = 0L;
                    }

                    l += k;
                    i = j;

                    if (this.worldServers[0].areAllPlayersAsleep())
                    {
                        this.tick();
                        l = 0L;
                    }
                    else
                    {
                        while (l > 50L)
                        {
                            l -= 50L;
                            this.tick();
                        }
                    }

                    Thread.sleep(Math.max(1L, 50L - l));
                    this.serverIsRunning = true;
                }
                FMLCommonHandler.instance().handleServerStopping();
                FMLCommonHandler.instance().expectServerStopped(); // has to come before finalTick to avoid race conditions
            }
            else
            {
                FMLCommonHandler.instance().expectServerStopped(); // has to come before finalTick to avoid race conditions
                this.finalTick(null);
            }
        }
        catch (StartupQuery.AbortedException e)
        {
            // ignore silently
            FMLCommonHandler.instance().expectServerStopped(); // has to come before finalTick to avoid race conditions
        }
        catch (Throwable throwable1)
        {
            logger.error("Encountered an unexpected exception", throwable1);
            CrashReport crashreport;

            if (throwable1 instanceof ReportedException)
            {
                crashreport = this.addServerInfoToCrashReport(((ReportedException)throwable1).getCrashReport());
            }
            else
            {
                crashreport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
            }

            File file1 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (crashreport.saveToFile(file1))
            {
                logger.error("This crash report has been saved to: " + file1.getAbsolutePath());
            }
            else
            {
                logger.error("We were unable to save this crash report to disk.");
            }

            FMLCommonHandler.instance().expectServerStopped(); // has to come before finalTick to avoid race conditions
            this.finalTick(crashreport);
        }
        finally
        {
            try
            {
                this.stopServer();
                this.serverStopped = true;
            }
            catch (Throwable throwable)
            {
                logger.error("Exception stopping the server", throwable);
            }
            finally
            {
                FMLCommonHandler.instance().handleServerStopped();
                this.serverStopped = true;
                this.systemExitNow();
            }
        }
    }
    @Shadow
    public static long getSystemTimeMillis()
    {
        return System.currentTimeMillis();
    }
    @Shadow
    protected abstract boolean startServer() throws IOException;

    @Shadow
    public void stopServer()
    {
        if (!this.worldIsBeingDeleted && Loader.instance().hasReachedState(LoaderState.SERVER_STARTED) && !serverStopped) // make sure the save is valid and we don't save twice
        {
            logger.info("Stopping server");

            if (this.func_147137_ag() != null)
            {
                this.func_147137_ag().terminateEndpoints();
            }

            if (this.serverConfigManager != null)
            {
                logger.info("Saving players");
                this.serverConfigManager.saveAllPlayerData();
                this.serverConfigManager.removeAllPlayers();
            }

            if (this.worldServers != null)
            {
                logger.info("Saving worlds");
                this.saveAllWorlds(false);

                for (int i = 0; i < this.worldServers.length; ++i)
                {
                    WorldServer worldserver = this.worldServers[i];
                    MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(worldserver));
                    worldserver.flush();
                }

                WorldServer[] tmp = worldServers;
                for (WorldServer world : tmp)
                {
                    DimensionManager.setWorld(world.provider.dimensionId, null);
                }
            }

            if (this.usageSnooper.isSnooperRunning())
            {
                this.usageSnooper.stopSnooper();
            }
        }
    }
    @Shadow
    protected void finalTick(CrashReport report) {}

    @Shadow
    protected void saveAllWorlds(boolean dontLog)
    {
        if (!this.worldIsBeingDeleted)
        {
            WorldServer[] aworldserver = this.worldServers;
            if (aworldserver == null) return; //Forge: Just in case, NPE protection as it has been encountered.
            int i = aworldserver.length;

            for (int j = 0; j < i; ++j)
            {
                WorldServer worldserver = aworldserver[j];

                if (worldserver != null)
                {
                    if (!dontLog)
                    {
                        logger.info("Saving chunks for level \'" + worldserver.getWorldInfo().getWorldName() + "\'/" + worldserver.provider.getDimensionName());
                    }

                    try
                    {
                        worldserver.saveAllChunks(true, (IProgressUpdate)null);
                    }
                    catch (MinecraftException minecraftexception)
                    {
                        logger.warn(minecraftexception.getMessage());
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
    public CrashReport addServerInfoToCrashReport(CrashReport report)
    {
        report.getCategory().addCrashSectionCallable("Profiler Position", () -> theProfiler.profilingEnabled ? theProfiler.getNameOfLastSection() : "N/A (disabled)");

        if (this.worldServers != null && this.worldServers.length > 0 && this.worldServers[0] != null)
        {
            report.getCategory().addCrashSectionCallable("Vec3 Pool Size", () -> {
                byte b0 = 0;
                int i = 56 * b0;
                int j = i / 1024 / 1024;
                byte b1 = 0;
                int k = 56 * b1;
                int l = k / 1024 / 1024;
                return b0 + " (" + i + " bytes; " + j + " MB) allocated, " + b1 + " (" + k + " bytes; " + l + " MB) used";
            });
        }

        if (this.serverConfigManager != null)
        {
            report.getCategory().addCrashSectionCallable("Player Count", () -> serverConfigManager.getCurrentPlayerCount() + " / " + serverConfigManager.getMaxPlayers() + "; " + serverConfigManager.playerEntityList);
        }

        return report;
    }
    @Shadow
    private void func_147138_a(ServerStatusResponse response)
    {
        File file1 = this.getFile("server-icon.png");

        if (file1.isFile())
        {
            ByteBuf bytebuf = Unpooled.buffer();

            try
            {
                BufferedImage bufferedimage = ImageIO.read(file1);
                Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
                ByteBuf bytebuf1 = Base64.encode(bytebuf);
                response.func_151320_a("data:image/png;base64," + bytebuf1.toString(Charsets.UTF_8));
            }
            catch (Exception exception)
            {
                logger.error("Couldn\'t load server icon", exception);
            }
            finally
            {
                bytebuf.release();
            }
        }
    }
    @Shadow
    public File getFile(String fileName)
    {
        return new File(this.getDataDirectory(), fileName);
    }
    @Shadow
    protected File getDataDirectory()
    {
        return new File(".");
    }

    @Shadow
    public void tick()
    {
        long i = System.nanoTime();
        FMLCommonHandler.instance().onPreServerTick();
        ++this.tickCounter;

        if (this.startProfiling)
        {
            this.startProfiling = false;
            this.theProfiler.profilingEnabled = true;
            this.theProfiler.clearProfiling();
        }

        this.theProfiler.startSection("root");
        this.updateTimeLightAndEntities();

        if (i - this.field_147142_T >= 5000000000L)
        {
            this.field_147142_T = i;
            this.field_147147_p.func_151319_a(new ServerStatusResponse.PlayerCountData(this.getMaxPlayers(), this.getCurrentPlayerCount()));
            GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            int j = MathHelper.getRandomIntegerInRange(this.field_147146_q, 0, this.getCurrentPlayerCount() - agameprofile.length);

            for (int k = 0; k < agameprofile.length; ++k)
            {
                agameprofile[k] = ((EntityPlayerMP)this.serverConfigManager.playerEntityList.get(j + k)).getGameProfile();
            }

            Collections.shuffle(Arrays.asList(agameprofile));
            this.field_147147_p.func_151318_b().func_151330_a(agameprofile);
        }

        if (this.tickCounter % 900 == 0)
        {
            this.theProfiler.startSection("save");
            this.serverConfigManager.saveAllPlayerData();
            this.saveAllWorlds(true);
            this.theProfiler.endSection();
        }

        this.theProfiler.startSection("tallying");
        this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - i;
        this.theProfiler.endSection();
        this.theProfiler.startSection("snooper");

        if (!this.usageSnooper.isSnooperRunning() && this.tickCounter > 100)
        {
            this.usageSnooper.startSnooper();
        }

        if (this.tickCounter % 6000 == 0)
        {
            this.usageSnooper.addMemoryStatsToSnooper();
        }

        this.theProfiler.endSection();
        this.theProfiler.endSection();
        FMLCommonHandler.instance().onPostServerTick();
    }
     @Shadow
     protected void systemExitNow() {}

    @Shadow
    public int getMaxPlayers()
    {
        return this.serverConfigManager.getMaxPlayers();
    }
    @Shadow
    public int getCurrentPlayerCount()
    {
        return this.serverConfigManager.getCurrentPlayerCount();
    }
}
