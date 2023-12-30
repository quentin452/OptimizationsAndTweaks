package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.google.gson.JsonObject;
import cpw.mods.fml.client.*;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.toposort.ModSortingException;
import net.minecraft.client.Minecraft;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.*;
import net.minecraft.entity.Entity;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FMLClientHandler.class)
public abstract class MixinFMLClientHandler implements IFMLSidedHandler {

    /**
     * The singleton
     */
    @Shadow
    private static final FMLClientHandler INSTANCE = new FMLClientHandler();

    @Shadow
    private Table<String, String, Set<ResourceLocation>> brokenTextures = HashBasedTable.create();
    @Shadow
    private SetMultimap<String,ResourceLocation> missingTextures = HashMultimap.create();
    /**
     * A reference to the server itself
     */
    @Shadow
    private Minecraft client;
    @Shadow
    private DummyModContainer optifineContainer;
    @Shadow
    @SuppressWarnings("unused")
    private boolean guiLoaded;
    @Shadow
    @SuppressWarnings("unused")
    private boolean serverIsRunning;
    @Shadow
    private MissingModsException modsMissing;
    @Shadow
    private ModSortingException modSorting;
    @Shadow
    private boolean loading = true;
    @Shadow
    private WrongMinecraftVersionException wrongMC;
    @Shadow
    private CustomModLoadingErrorDisplayException customError;
    @Shadow
    private DuplicateModsFoundException dupesFound;
    @Shadow
    private boolean serverShouldBeKilledQuietly;
    @Shadow
    private List<IResourcePack> resourcePackList;
    @Shadow
    @SuppressWarnings("unused")
    private IReloadableResourceManager resourceManager;
    @Shadow
    private Map<String, IResourcePack> resourcePackMap;
    @Shadow
    private BiMap<ModContainer, IModGuiFactory> guiFactories;
    @Shadow
    private Map<ServerStatusResponse, JsonObject> extraServerListData;
    @Shadow
    private Map<ServerData, ExtendedServerListData> serverDataTag;
    @Shadow
    private WeakReference<NetHandlerPlayClient> currentPlayClient;
    @Unique
    private final Object guiLock = new Object();

    @Override
    public void queryUser(StartupQuery query) throws InterruptedException {
        if (query.getResult() == null) {
            client.displayGuiScreen(new GuiNotification(query));
        } else {
            client.displayGuiScreen(new GuiConfirmation(query));
        }

        if (query.isSynchronous()) {
            CompletableFuture<Void> waitForGui = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                synchronized (guiLock) {
                    while (client.currentScreen instanceof GuiNotification) {
                        client.loadingScreen.resetProgresAndWorkingMessage("");
                        try {
                            guiLock.wait(50);
                        } catch (InterruptedException e) {
                            waitForGui.completeExceptionally(e);
                            return;
                        }
                    }
                }
                waitForGui.complete(null);
            });

            try {
                waitForGui.join();
            } catch (CompletionException e) {
                if (e.getCause() instanceof InterruptedException) {
                    throw (InterruptedException) e.getCause();
                }
            }

            client.loadingScreen.resetProgresAndWorkingMessage("");
        }
    }
    @Shadow
    private void detectOptifine()
    {
        try
        {
            Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
            String optifineVersion = (String) optifineConfig.getField("VERSION").get(null);
            Map<String,Object> dummyOptifineMeta = ImmutableMap.<String,Object>builder().put("name", "Optifine").put("version", optifineVersion).build();
            ModMetadata optifineMetadata = MetadataCollection.from(getClass().getResourceAsStream("optifinemod.info"),"optifine").getMetadataForId("optifine", dummyOptifineMeta);
            optifineContainer = new DummyModContainer(optifineMetadata);
            FMLLog.info("Forge Mod Loader has detected optifine %s, enabling compatibility features",optifineContainer.getVersion());
        }
        catch (Exception e)
        {
            optifineContainer = null;
        }
    }
    /**
     * @author
     * @reason
     */
    @SuppressWarnings("unchecked")
    @Overwrite(remap = false)
    public void beginMinecraftLoading(Minecraft minecraft, List<?> resourcePackList, IReloadableResourceManager resourceManager) {
        optimizationsAndTweaks$setupInitialSettings(minecraft, resourcePackList, resourceManager);
        optimizationsAndTweaks$checkDemoMode();
        try {
            optimizationsAndTweaks$loadMods();
            optimizationsAndTweaks$preInitializeMods();
            optimizationsAndTweaks$populateSharedModList();
        } catch (LoaderException le) {
            optimizationsAndTweaks$handleLoaderException(le);
        }
    }
    @Unique
    private void optimizationsAndTweaks$setupInitialSettings(Minecraft minecraft, List resourcePackList, IReloadableResourceManager resourceManager) {
        detectOptifine();
        SplashProgress.start();
        client = minecraft;
        this.resourcePackList = resourcePackList;
        this.resourceManager = resourceManager;
        this.resourcePackMap = Maps.newHashMap();
    }


    @Unique
    private void optimizationsAndTweaks$checkDemoMode() {
        if (client.isDemo()) {
            FMLLog.severe("DEMO MODE DETECTED, FML will not work. Finishing now.");
            haltGame("FML will not run in demo mode", new RuntimeException());
            return;
        }
        FMLCommonHandler.instance().beginLoading(this);
    }
    @Unique
    private void optimizationsAndTweaks$loadMods() throws LoaderException {
        try {
            Loader.instance().loadMods();
        } catch (WrongMinecraftVersionException wrong) {
            wrongMC = wrong;
        } catch (DuplicateModsFoundException dupes) {
            dupesFound = dupes;
        } catch (MissingModsException missing) {
            modsMissing = missing;
        } catch (ModSortingException sorting) {
            modSorting = sorting;
        } catch (CustomModLoadingErrorDisplayException custom) {
            FMLLog.log(Level.ERROR, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
        }
        client.refreshResources();
    }
    @Unique
    private void optimizationsAndTweaks$preInitializeMods() throws LoaderException {
        try {
            Loader.instance().preinitializeMods();
        } catch (CustomModLoadingErrorDisplayException custom) {
            FMLLog.log(Level.ERROR, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
        }
    }
    @Unique
    private void optimizationsAndTweaks$populateSharedModList() {
        Map<String, Map<String, String>> sharedModList = (Map<String, Map<String, String>>) Launch.blackboard.get("modList");
        if (sharedModList == null) {
            sharedModList = Maps.newHashMap();
            Launch.blackboard.put("modList", sharedModList);
        }
        for (ModContainer mc : Loader.instance().getActiveModList()) {
            Map<String, String> sharedModDescriptor = mc.getSharedModDescriptor();
            if (sharedModDescriptor != null) {
                String sharedModId = "fml:" + mc.getModId();
                sharedModList.put(sharedModId, sharedModDescriptor);
            }
        }
    }
    @Unique
    private void optimizationsAndTweaks$handleLoaderException(LoaderException le) {
        haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
    }

    /**
     * @author
     * @reason
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    @Overwrite(remap = false)
    public void finishMinecraftLoading() {
        if (optimizationsAndTweaks$shouldFinishLoading()) {
            return;
        }

        optimizationsAndTweaks$loadMods2();
        optimizationsAndTweaks$reloadResources();
        optimizationsAndTweaks$initializeGuiFactories();
        optimizationsAndTweaks$finalizeLoading();
    }
    @Unique
    private boolean optimizationsAndTweaks$shouldFinishLoading() {
        return modsMissing != null || wrongMC != null || customError != null || dupesFound != null || modSorting != null;
    }
    @Unique
    private void optimizationsAndTweaks$loadMods2() {
        try {
            Loader.instance().initializeMods();
        } catch (CustomModLoadingErrorDisplayException custom) {
            optimizationsAndTweaks$handleCustomLoadingError(custom);
        } catch (LoaderException le) {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
        }
    }
    @Unique
    private void optimizationsAndTweaks$handleCustomLoadingError(CustomModLoadingErrorDisplayException custom) {
        FMLLog.log(Level.ERROR, custom, "A custom exception was thrown by a mod, the game will now halt");
        customError = custom;
        SplashProgress.finish();
    }
    @Unique
    private void optimizationsAndTweaks$reloadResources() {
        client.refreshResources();
        RenderingRegistry.instance().loadEntityRenderers((Map<Class<? extends Entity>, Render>) RenderManager.instance.entityRenderMap);
    }
    @Unique
    private void optimizationsAndTweaks$initializeGuiFactories() {
        guiFactories = HashBiMap.create();
        for (ModContainer mc : Loader.instance().getActiveModList()) {
            String className = mc.getGuiClassName();
            if (!Strings.isNullOrEmpty(className)) {
                try {
                    Class<?> clazz = Class.forName(className, true, Loader.instance().getModClassLoader());
                    Class<? extends IModGuiFactory> guiClassFactory = clazz.asSubclass(IModGuiFactory.class);
                    IModGuiFactory guiFactory = guiClassFactory.newInstance();
                    guiFactory.initialize(client);
                    guiFactories.put(mc, guiFactory);
                } catch (Exception e) {
                    FMLLog.log(Level.ERROR, e, "A critical error occurred instantiating the gui factory for mod %s", mc.getModId());
                }
            }
        }
    }
    @Unique
    private void optimizationsAndTweaks$finalizeLoading() {
        loading = false;
        client.gameSettings.loadOptions(); // Reload options to load any mod-added keybindings.
        SplashProgress.finish();
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void onInitializationComplete()
    {
        if (wrongMC != null)
        {
            showGuiScreen(new GuiWrongMinecraft(wrongMC));
        }
        else if (modsMissing != null)
        {
            showGuiScreen(new GuiModsMissing(modsMissing));
        }
        else if (dupesFound != null)
        {
            showGuiScreen(new GuiDupesFound(dupesFound));
        }
        else if (modSorting != null)
        {
            showGuiScreen(new GuiSortingProblem(modSorting));
        }
        else if (customError != null)
        {
            showGuiScreen(new GuiCustomModLoadingErrorScreen(customError));
        }
        else
        {
            Loader.instance().loadingComplete();
       //     SplashProgress.finish();
        }
        logMissingTextureErrors();
    }
    @Shadow
    public void logMissingTextureErrors()
    {
        if (missingTextures.isEmpty() && brokenTextures.isEmpty())
        {
            return;
        }
        Logger logger = LogManager.getLogger("TEXTURE ERRORS");
        logger.error(Strings.repeat("+=", 25));
        logger.error("The following texture errors were found.");
        Map<String, FallbackResourceManager> resManagers = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, (SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager(), "domainResourceManagers", "field_110548"+"_a");
        for (String resourceDomain : missingTextures.keySet())
        {
            Set<ResourceLocation> missing = missingTextures.get(resourceDomain);
            logger.error(Strings.repeat("=", 50));
            logger.error("  DOMAIN {}", resourceDomain);
            logger.error(Strings.repeat("-", 50));
            logger.error("  domain {} is missing {} texture{}",resourceDomain, missing.size(),missing.size()!=1 ? "s" : "");
            FallbackResourceManager fallbackResourceManager = resManagers.get(resourceDomain);
            if (fallbackResourceManager == null)
            {
                logger.error("    domain {} is missing a resource manager - it is probably a side-effect of automatic texture processing", resourceDomain);
            }
            else
            {
                List<IResourcePack> resPacks = ObfuscationReflectionHelper.getPrivateValue(FallbackResourceManager.class, fallbackResourceManager, "resourcePacks","field_110540"+"_a");
                logger.error("    domain {} has {} location{}:",resourceDomain, resPacks.size(), resPacks.size() != 1 ? "s" :"");
                for (IResourcePack resPack : resPacks)
                {
                    if (resPack instanceof FMLContainerHolder) {
                        FMLContainerHolder containerHolder = (FMLContainerHolder) resPack;
                        ModContainer fmlContainer = containerHolder.getFMLContainer();
                        logger.error("      mod {} resources at {}", fmlContainer.getModId(), fmlContainer.getSource().getPath());
                    }
                    else if (resPack instanceof AbstractResourcePack)
                    {
                        AbstractResourcePack resourcePack = (AbstractResourcePack) resPack;
                        File resPath = ObfuscationReflectionHelper.getPrivateValue(AbstractResourcePack.class, resourcePack, "resourcePackFile","field_110597"+"_b");
                        logger.error("      resource pack at path {}",resPath.getPath());
                    }
                    else
                    {
                        logger.error("      unknown resourcepack type {} : {}", resPack.getClass().getName(), resPack.getPackName());
                    }
                }
            }
            logger.error(Strings.repeat("-", 25));
            logger.error("    The missing resources for domain {} are:",resourceDomain);
            for (ResourceLocation rl : missing)
            {
                logger.error("      {}",rl.getResourcePath());
            }
            logger.error(Strings.repeat("-", 25));
            if (!brokenTextures.containsRow(resourceDomain))
            {
                logger.error("    No other errors exist for domain {}", resourceDomain);
            }
            else
            {
                logger.error("    The following other errors were reported for domain {}:",resourceDomain);
                Map<String, Set<ResourceLocation>> resourceErrs = brokenTextures.row(resourceDomain);
                for (String error: resourceErrs.keySet())
                {
                    logger.error(Strings.repeat("-", 25));
                    logger.error("    Problem: {}", error);
                    for (ResourceLocation rl : resourceErrs.get(error))
                    {
                        logger.error("      {}",rl.getResourcePath());
                    }
                }
            }
            logger.error(Strings.repeat("=", 50));
        }
        logger.error(Strings.repeat("+=", 25));
    }
}
