package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.StartupQuery;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.*;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Bootstrap;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.*;
import net.minecraft.util.Timer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.util.glu.GLU;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(value = Minecraft.class,priority = 999)
public abstract class MixinMinecraft  implements IPlayerUsage {
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    /** A 10MiB preallocation to ensure the heap is reasonably sized. */
    @Shadow
    public static byte[] memoryReserve = new byte[10485760];
    @Shadow
    private static final List macDisplayModes = Lists.newArrayList(new DisplayMode[] {new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
    @Shadow
    private final File fileResourcepacks;
    @Shadow
    private final Multimap field_152356_J;
    @Shadow
    private ServerData currentServerData;
    /** The RenderEngine instance used by Minecraft */
    @Shadow
    public TextureManager renderEngine;
    /** Set to 'this' in Minecraft constructor; used by some settings get methods */
    @Shadow
    private static Minecraft theMinecraft;
    @Shadow
    public PlayerControllerMP playerController;
    @Shadow
    private boolean fullscreen;
    @Shadow
    private boolean hasCrashed;

    /** Instance of CrashReport. */
    @Shadow
    private CrashReport crashReporter;
    @Shadow
    public int displayWidth;
    @Shadow
    public int displayHeight;
    @Shadow
    private Timer timer = new Timer(20.0F);
    /** Instance of PlayerUsageSnooper. */
    @Shadow
    private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getSystemTimeMillis());
    @Shadow
    public WorldClient theWorld;
    @Shadow
    public RenderGlobal renderGlobal;
    @Shadow
    public EntityClientPlayerMP thePlayer;
    /**
     * The Entity from which the renderer determines the render viewpoint. Currently is always the parent Minecraft
     * class's 'thePlayer' instance. Modification of its location, rotation, or other settings at render time will
     * modify the camera likewise, with the caveat of triggering chunk rebuilds as it moves, making it unsuitable for
     * changing the viewpoint mid-render.
     */
    @Shadow
    public EntityLivingBase renderViewEntity;
    @Shadow
    public Entity pointedEntity;
    @Shadow
    public EffectRenderer effectRenderer;
    @Shadow
    private final Session session;
    @Shadow
    private boolean isGamePaused;
    /** The font renderer used for displaying and measuring text. */
    @Shadow
    public FontRenderer fontRenderer;
    @Shadow
    public FontRenderer standardGalacticFontRenderer;
    /** The GuiScreen that's being displayed at the moment. */
    @Shadow
    public GuiScreen currentScreen;
    @Shadow
    public LoadingScreenRenderer loadingScreen;
    @Shadow
    public EntityRenderer entityRenderer;
    /** Mouse left click counter */
    @Shadow
    private int leftClickCounter;
    /** Display width */
    @Shadow
    private int tempDisplayWidth;
    /** Display height */
    @Shadow
    private int tempDisplayHeight;
    /** Instance of IntegratedServer. */
    @Shadow
    private IntegratedServer theIntegratedServer;
    /** Gui achievement */
    @Shadow
    public GuiAchievement guiAchievement;
    @Shadow
    public GuiIngame ingameGUI;
    /** Skip render world */
    @Shadow
    public boolean skipRenderWorld;
    /** The ray trace hit that the mouse is over. */
    @Shadow
    public MovingObjectPosition objectMouseOver;
    /** The game settings that currently hold effect. */
    @Shadow
    public GameSettings gameSettings;
    /** Mouse helper instance. */
    @Shadow
    public MouseHelper mouseHelper;
    @Shadow
    public final File mcDataDir;
    @Shadow
    private final File fileAssets;
    @Shadow
    private final String launchedVersion;
    @Shadow
    private final Proxy proxy;
    @Shadow
    private ISaveFormat saveLoader;
    /**
     * This is set to fpsCounter every debug screen update, and is shown on the debug screen. It's also sent as part of
     * the usage snooping.
     */
    @Shadow
    private static int debugFPS;
    /** When you place a block, it's set to 6, decremented once per tick, when it's 0, you can place another block. */
    @Shadow
    private int rightClickDelayTimer;
    /** Checked in Minecraft's while(running) loop, if true it's set to false and the textures refreshed. */
    @Shadow
    private boolean refreshTexturePacksScheduled;
    @Shadow
    private String serverName;
    @Shadow
    private int serverPort;
    /** Does the actual gameplay have focus. If so then mouse and keys will effect the player instead of menus. */
    @Shadow
    public boolean inGameHasFocus;
    @Shadow
    long systemTime = getSystemTime();
    /** Join player counter */
    @Shadow
    private int joinPlayerCounter;
    @Shadow
    private final boolean jvm64bit;
    @Shadow
    private final boolean isDemo;
    @Shadow
    private NetworkManager myNetworkManager;
    @Shadow
    private boolean integratedServerIsRunning;

    /** The profiler instance */
    @Shadow
    public final Profiler mcProfiler = new Profiler();
    @Shadow
    private long field_83002_am = -1L;
    @Shadow
    private IReloadableResourceManager mcResourceManager;
    @Shadow
    private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
    @Shadow
    private List defaultResourcePacks = Lists.newArrayList();
    @Shadow
    public DefaultResourcePack mcDefaultResourcePack;
    @Shadow
    private ResourcePackRepository mcResourcePackRepository;
    @Shadow
    private LanguageManager mcLanguageManager;
    @Shadow
    private IStream field_152353_at;
    @Shadow
    private Framebuffer framebufferMc;
    @Shadow
    private TextureMap textureMapBlocks;
    @Shadow
    private SoundHandler mcSoundHandler;
    @Shadow
    private MusicTicker mcMusicTicker;
    @Shadow
    private ResourceLocation field_152354_ay;
    @Shadow
    private final MinecraftSessionService field_152355_az;
    @Shadow
    private SkinManager field_152350_aA;
    @Shadow
    private final Queue field_152351_aB = Queues.newArrayDeque();
    @Shadow
    private final Thread field_152352_aC = Thread.currentThread();
    /** Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly. */
    @Shadow
    volatile boolean running = true;
    /** String that shows the debug information */
    @Shadow
    public String debug = "";
    /** Approximate time (in ms) of last update to debug string */
    @Shadow
    long debugUpdateTime = getSystemTime();
    /** holds the current fps */
    @Shadow
    int fpsCounter;
    @Shadow
    long prevFrameTime = -1L;
    /** Profiler currently displayed in the debug screen pie chart */
    @Shadow
    private String debugProfilerName = "root";

    protected MixinMinecraft(Session sessionIn, int displayWidth, int displayHeight, boolean fullscreen, boolean isDemo, File dataDir, File assetsDir, File resourcePackDir, Proxy proxy, String version, Multimap twitchDetails, String assetsJsonVersion)
    {
        this.mcDataDir = dataDir;
        this.fileAssets = assetsDir;
        this.fileResourcepacks = resourcePackDir;
        this.launchedVersion = version;
        this.field_152356_J = twitchDetails;
        this.mcDefaultResourcePack = new DefaultResourcePack((new ResourceIndex(assetsDir, assetsJsonVersion)).func_152782_a());
        this.addDefaultResourcePack();
        this.proxy = proxy == null ? Proxy.NO_PROXY : proxy;
        this.field_152355_az = (new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
        this.startTimerHackThread();
        this.session = sessionIn;
        logger.info("Setting user: " + sessionIn.getUsername());
        this.isDemo = isDemo;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.tempDisplayWidth = displayWidth;
        this.tempDisplayHeight = displayHeight;
        this.fullscreen = fullscreen;
        this.jvm64bit = isJvm64bit();
        ImageIO.setUseCache(false);
        Bootstrap.func_151354_b();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static boolean isJvm64bit()
    {
        String[] astring = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        int i = astring.length;

        for (int j = 0; j < i; ++j)
        {
            String s = astring[j];
            String s1 = System.getProperty(s);

            if (s1 != null && s1.contains("64"))
            {
                return true;
            }
        }

        return false;
    }
    @Shadow
    private void addDefaultResourcePack()
    {
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
    }
    @Shadow
    public static long getSystemTime()
    {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }
    @Shadow
    private ByteBuffer func_152340_a(InputStream imageStream) throws IOException
    {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[])null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j)
        {
            int k = aint1[j];
            bytebuffer.putInt(k << 8 | k >> 24 & 255);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

    // todo avoid the usage of Tread.sleep
    /**
     * @author
     * @reason
     */

    @Overwrite
    private void startTimerHackThread() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            while (running) {

            }
        });

        future.thenRun(() -> running = false);

        executor.schedule(() -> future.complete(null), Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void startGame() throws LWJGLException
    {
        this.gameSettings = new GameSettings(theMinecraft, this.mcDataDir);

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0)
        {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        if (this.fullscreen)
        {
            Display.setFullscreen(true);
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();

            if (this.displayWidth <= 0)
            {
                this.displayWidth = 1;
            }

            if (this.displayHeight <= 0)
            {
                this.displayHeight = 1;
            }
        }
        else
        {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }

        Display.setResizable(true);
        Display.setTitle("Minecraft 1.7.10");
        logger.info("LWJGL Version: " + Sys.getVersion());
        Util.EnumOS enumos = Util.getOSType();

        if (enumos != Util.EnumOS.OSX)
        {
            try
            {
                InputStream inputstream = this.mcDefaultResourcePack.func_152780_c(new ResourceLocation("icons/icon_16x16.png"));
                InputStream inputstream1 = this.mcDefaultResourcePack.func_152780_c(new ResourceLocation("icons/icon_32x32.png"));

                if (inputstream != null && inputstream1 != null)
                {
                    Display.setIcon(new ByteBuffer[] {this.func_152340_a(inputstream), this.func_152340_a(inputstream1)});
                }
            }
            catch (IOException ioexception)
            {
                logger.error("Couldn\'t set icon", ioexception);
            }
        }

        try
        {
            net.minecraftforge.client.ForgeHooksClient.createDisplay();
        }
        catch (LWJGLException lwjglexception)
        {
            logger.error("Couldn\'t set pixel format", lwjglexception);

            if (this.fullscreen)
            {
                this.updateDisplayMode();
            }

            Display.create();
        }

        OpenGlHelper.initializeTextures();

        try
        {
            this.field_152353_at = new TwitchStream(theMinecraft, (String) Iterables.getFirst(this.field_152356_J.get("twitch_access_token"), (Object)null));
        }
        catch (Throwable throwable)
        {
            this.field_152353_at = new NullStream(throwable);
            logger.error("Couldn\'t initialize twitch stream");
        }

        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.guiAchievement = new GuiAchievement(theMinecraft);
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        FMLClientHandler.instance().beginMinecraftLoading(theMinecraft, this.defaultResourcePacks, this.mcResourceManager);
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.field_152350_aA = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.field_152355_az);
        cpw.mods.fml.client.SplashProgress.drawVanillaScreen();
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(theMinecraft);
        this.fontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

        if (this.gameSettings.language != null)
        {
            this.fontRenderer.setUnicodeFlag(this.func_152349_b());
            this.fontRenderer.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRenderer);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        cpw.mods.fml.common.ProgressManager.ProgressBar bar= cpw.mods.fml.common.ProgressManager.push("Rendering Setup", 9, true);
        bar.step("Loading Render Manager");
        RenderManager.instance.itemRenderer = new ItemRenderer(theMinecraft);
        bar.step("Loading Entity Renderer");
        this.entityRenderer = new EntityRenderer(theMinecraft, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat()
        {
            /**
             * Formats the strings based on 'IStatStringFormat' interface.
             */
            public String formatString(String p_74535_1_)
            {
                try
                {
                    return String.format(p_74535_1_, new Object[] {GameSettings.getKeyDisplayString(gameSettings.keyBindInventory.getKeyCode())});
                }
                catch (Exception exception)
                {
                    return "Error: " + exception.getLocalizedMessage();
                }
            }
        });
        bar.step("Loading GL properties");
        this.mouseHelper = new MouseHelper();
        this.checkGLError("Pre startup");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        this.checkGLError("Startup");
        bar.step("Render Global instance");
        this.renderGlobal = new RenderGlobal(theMinecraft);
        bar.step("Building Blocks Texture");
        this.textureMapBlocks = new TextureMap(0, "textures/blocks", true);
        bar.step("Anisotropy and Mipmaps");
        this.textureMapBlocks.setAnisotropicFiltering(this.gameSettings.anisotropicFiltering);
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        bar.step("Loading Blocks Texture");
        this.renderEngine.loadTextureMap(TextureMap.locationBlocksTexture, this.textureMapBlocks);
        bar.step("Loading Items Texture");
        this.renderEngine.loadTextureMap(TextureMap.locationItemsTexture, new TextureMap(1, "textures/items", true));
        bar.step("Viewport");
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        cpw.mods.fml.common.ProgressManager.pop(bar);
        FMLClientHandler.instance().finishMinecraftLoading();
        this.checkGLError("Post startup");
        this.ingameGUI = new net.minecraftforge.client.GuiIngameForge(theMinecraft);

        if (this.serverName != null)
        {
            FMLClientHandler.instance().connectToServerAtStartup(this.serverName, this.serverPort);
        }
        else
        {
            this.displayGuiScreen(new GuiMainMenu());
        }

        cpw.mods.fml.client.SplashProgress.clearVanillaResources(renderEngine, field_152354_ay);
        this.field_152354_ay = null;
        this.loadingScreen = new LoadingScreenRenderer(theMinecraft);

        FMLClientHandler.instance().onInitializationComplete();
        if (this.gameSettings.fullScreen && !this.fullscreen)
        {
            this.toggleFullscreen();
        }

        try
        {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        }
        catch (OpenGLException openglexception)
        {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }
    }
    @Shadow
    public void toggleFullscreen()
    {
        try
        {
            this.fullscreen = !this.fullscreen;

            if (this.fullscreen)
            {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }
            else
            {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null)
            {
                this.resize(this.displayWidth, this.displayHeight);
            }
            else
            {
                this.updateFramebufferSize();
            }

            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.func_147120_f();
        }
        catch (Exception exception)
        {
            logger.error("Couldn\'t toggle fullscreen", exception);
        }
    }
    @Shadow
    public void resize(int width, int height)
    {
        this.displayWidth = width <= 0 ? 1 : width;
        this.displayHeight = height <= 0 ? 1 : height;

        if (this.currentScreen != null)
        {
            ScaledResolution scaledresolution = new ScaledResolution(theMinecraft, width, height);
            int k = scaledresolution.getScaledWidth();
            int l = scaledresolution.getScaledHeight();
            this.currentScreen.setWorldAndResolution(theMinecraft, k, l);
        }

        this.loadingScreen = new LoadingScreenRenderer(theMinecraft);
        this.updateFramebufferSize();
    }
    @Shadow
    public void func_147120_f()
    {
        Display.update();

        if (!this.fullscreen && Display.wasResized())
        {
            int i = this.displayWidth;
            int j = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();

            if (this.displayWidth != i || this.displayHeight != j)
            {
                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }
    @Shadow
    private void updateFramebufferSize()
    {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);

        if (this.entityRenderer != null)
        {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    @Shadow
    public boolean func_152349_b()
    {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }
    @Shadow
    public void displayGuiScreen(GuiScreen guiScreenIn)
    {
        if (guiScreenIn == null && this.theWorld == null)
        {
            guiScreenIn = new GuiMainMenu();
        }
        else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F)
        {
            guiScreenIn = new GuiGameOver();
        }

        GuiScreen old = this.currentScreen;
        net.minecraftforge.client.event.GuiOpenEvent event = new net.minecraftforge.client.event.GuiOpenEvent(guiScreenIn);

        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;

        guiScreenIn = event.gui;
        if (old != null && guiScreenIn != old)
        {
            old.onGuiClosed();
        }

        if (guiScreenIn instanceof GuiMainMenu)
        {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages();
        }

        this.currentScreen = (GuiScreen)guiScreenIn;

        if (guiScreenIn != null)
        {
            this.setIngameNotInFocus();
            ScaledResolution scaledresolution = new ScaledResolution(theMinecraft, this.displayWidth, this.displayHeight);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            ((GuiScreen)guiScreenIn).setWorldAndResolution(theMinecraft, i, j);
            this.skipRenderWorld = false;
        }
        else
        {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }
    }
    @Shadow
    public void setIngameNotInFocus()
    {
        if (this.inGameHasFocus)
        {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }
    @Shadow
    public void setIngameFocus()
    {
        if (Display.isActive())
        {
            if (!this.inGameHasFocus)
            {
                this.inGameHasFocus = true;
                this.mouseHelper.grabMouseCursor();
                this.displayGuiScreen((GuiScreen)null);
                this.leftClickCounter = 10000;
            }
        }
    }
    @Shadow
    private void checkGLError(String message)
    {
        int i = GL11.glGetError();

        if (i != 0)
        {
            String s1 = GLU.gluErrorString(i);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + message);
            logger.error(i + ": " + s1);
        }
    }

    @Shadow
    private void updateDisplayMode() throws LWJGLException
    {
        HashSet hashset = new HashSet();
        Collections.addAll(hashset, Display.getAvailableDisplayModes());
        DisplayMode displaymode = Display.getDesktopDisplayMode();

        if (!hashset.contains(displaymode) && Util.getOSType() == Util.EnumOS.OSX)
        {
            Iterator iterator = macDisplayModes.iterator();

            while (iterator.hasNext())
            {
                DisplayMode displaymode1 = (DisplayMode)iterator.next();
                boolean flag = true;
                Iterator iterator1 = hashset.iterator();
                DisplayMode displaymode2;

                while (iterator1.hasNext())
                {
                    displaymode2 = (DisplayMode)iterator1.next();

                    if (displaymode2.getBitsPerPixel() == 32 && displaymode2.getWidth() == displaymode1.getWidth() && displaymode2.getHeight() == displaymode1.getHeight())
                    {
                        flag = false;
                        break;
                    }
                }

                if (!flag)
                {
                    iterator1 = hashset.iterator();

                    while (iterator1.hasNext())
                    {
                        displaymode2 = (DisplayMode)iterator1.next();

                        if (displaymode2.getBitsPerPixel() == 32 && displaymode2.getWidth() == displaymode1.getWidth() / 2 && displaymode2.getHeight() == displaymode1.getHeight() / 2)
                        {
                            displaymode = displaymode2;
                            break;
                        }
                    }
                }
            }
        }

        Display.setDisplayMode(displaymode);
        this.displayWidth = displaymode.getWidth();
        this.displayHeight = displaymode.getHeight();
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
        FMLClientHandler.instance().startIntegratedServer(folderName, worldName, worldSettingsIn);
        this.loadWorld(null);
        System.gc();
        ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();

        if (worldinfo == null && worldSettingsIn != null) {
            worldinfo = new WorldInfo(worldSettingsIn, folderName);
            isavehandler.saveWorldInfo(worldinfo);
        }

        if (worldSettingsIn == null) {
            assert worldinfo != null;
            worldSettingsIn = new WorldSettings(worldinfo);
        }

        try {
            this.theIntegratedServer = new IntegratedServer(theMinecraft, folderName, worldName, worldSettingsIn);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
            crashreportcategory.addCrashSection("Level ID", folderName);
            crashreportcategory.addCrashSection("Level Name", worldName);
            throw new ReportedException(crashreport);
        }

        this.loadingScreen.displayProgressMessage(I18n.format("menu.loadingLevel"));

        while (!this.theIntegratedServer.serverIsInRunLoop()) {
            if (!StartupQuery.check()) {
                loadWorld(null);
                displayGuiScreen(null);
                return;
            }
            String s2 = this.theIntegratedServer.getUserMessage();

            if (s2 != null) {
                this.loadingScreen.resetProgresAndWorkingMessage(I18n.format(s2));
            } else {
                this.loadingScreen.resetProgresAndWorkingMessage("");
            }

            long startTime = System.currentTimeMillis();
            long delay = 200L;

            while (System.currentTimeMillis() - startTime < delay) {
                if (!StartupQuery.check()) {
                    loadWorld(null);
                    displayGuiScreen(null);
                    return;
                }
            }
        }

        this.displayGuiScreen(null);
        SocketAddress socketaddress = this.theIntegratedServer.func_147137_ag().addLocalEndpoint();
        NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
        networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, theMinecraft, null));
        networkmanager.scheduleOutboundPacket(new C00Handshake(5, socketaddress.toString(), 0, EnumConnectionState.LOGIN));
        networkmanager.scheduleOutboundPacket(new C00PacketLoginStart(this.getSession().func_148256_e()));
        this.myNetworkManager = networkmanager;
    }

    @Shadow
    public Session getSession()
    {
        return this.session;
    }

    @Shadow
    public void loadWorld(WorldClient worldClientIn)
    {
        this.loadWorld(worldClientIn, "");
    }

    /**
     * @author
     * @reason
     */
    public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
        if (theWorld != null) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(theWorld));
        }

        CompletableFuture<Void> shutdownFuture = CompletableFuture.runAsync(() -> {
            if (worldClientIn == null) {
                NetHandlerPlayClient nethandlerplayclient = this.getNetHandler();
                if (nethandlerplayclient != null) {
                    nethandlerplayclient.cleanup();
                }

                if (this.theIntegratedServer != null) {
                    this.theIntegratedServer.initiateShutdown();
                }

                this.theIntegratedServer = null;
                this.guiAchievement.func_146257_b();
                this.entityRenderer.getMapItemRenderer().func_148249_a();
            }
        });

        shutdownFuture.thenRun(() -> {
            if (this.loadingScreen != null) {
                this.loadingScreen.resetProgressAndMessage(loadingMessage);
                this.loadingScreen.resetProgresAndWorkingMessage("");
            }

            if (worldClientIn == null && this.theWorld != null) {
                if (this.mcResourcePackRepository.func_148530_e() != null) {
                    this.scheduleResourcesRefresh();
                }

                this.mcResourcePackRepository.func_148529_f();
                this.setServerData((ServerData) null);
                this.integratedServerIsRunning = false;
                FMLClientHandler.instance().handleClientWorldClosing(this.theWorld);
            }

            this.mcSoundHandler.stopSounds();
            this.theWorld = worldClientIn;

            if (worldClientIn != null) {
                if (this.renderGlobal != null) {
                    this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
                }

                if (this.effectRenderer != null) {
                    this.effectRenderer.clearEffects(worldClientIn);
                }

                if (this.thePlayer == null) {
                    this.thePlayer = this.playerController.func_147493_a(worldClientIn, new StatFileWriter());
                    this.playerController.flipPlayer(this.thePlayer);
                }

                this.thePlayer.preparePlayerToSpawn();
                worldClientIn.spawnEntityInWorld(this.thePlayer);
                this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
                this.playerController.setPlayerCapabilities(this.thePlayer);
                renderViewEntity = thePlayer;
            } else {
                saveLoader.flushCache();
                thePlayer = null;
            }

            System.gc();
            systemTime = 0L;
        });
    }
    @Shadow
    public void setServerData(ServerData serverDataIn)
    {
        this.currentServerData = serverDataIn;
    }
    @Shadow
    public NetHandlerPlayClient getNetHandler()
    {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }
    @Shadow
    public static Minecraft getMinecraft()
    {
        return theMinecraft;
    }
    @Shadow
    public void scheduleResourcesRefresh()
    {
        this.refreshTexturePacksScheduled = true;
    }
}
