package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.network.ServerStatusResponse;
import org.apache.logging.log4j.Level;
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
    @Overwrite
    public void beginMinecraftLoading(Minecraft minecraft, @SuppressWarnings("rawtypes") List resourcePackList, IReloadableResourceManager resourceManager)
    {
        detectOptifine();
        SplashProgress.start();
        client = minecraft;
        this.resourcePackList = resourcePackList;
        this.resourceManager = resourceManager;
        this.resourcePackMap = Maps.newHashMap();
        if (minecraft.isDemo())
        {
            FMLLog.severe("DEMO MODE DETECTED, FML will not work. Finishing now.");
            haltGame("FML will not run in demo mode", new RuntimeException());
            return;
        }

        FMLCommonHandler.instance().beginLoading(this);
        try
        {
            Loader.instance().loadMods();
        }
        catch (WrongMinecraftVersionException wrong)
        {
            wrongMC = wrong;
        }
        catch (DuplicateModsFoundException dupes)
        {
            dupesFound = dupes;
        }
        catch (MissingModsException missing)
        {
            modsMissing = missing;
        }
        catch (ModSortingException sorting)
        {
            modSorting = sorting;
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.ERROR, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        finally
        {
            client.refreshResources();
        }

        try
        {
            Loader.instance().preinitializeMods();
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.ERROR, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        Map<String,Map<String,String>> sharedModList = (Map<String, Map<String, String>>) Launch.blackboard.get("modList");
        if (sharedModList == null)
        {
            sharedModList = Maps.newHashMap();
            Launch.blackboard.put("modList", sharedModList);
        }
        for (ModContainer mc : Loader.instance().getActiveModList())
        {
            Map<String,String> sharedModDescriptor = mc.getSharedModDescriptor();
            if (sharedModDescriptor != null)
            {
                String sharedModId = "fml:"+mc.getModId();
                sharedModList.put(sharedModId, sharedModDescriptor);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    @Overwrite
    public void finishMinecraftLoading()
    {
        if (modsMissing != null || wrongMC != null || customError!=null || dupesFound!=null || modSorting!=null)
        {
            SplashProgress.finish();
            return;
        }
        try
        {
            Loader.instance().initializeMods();
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.ERROR, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
            SplashProgress.finish();
            return;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }

        // Reload resources
        client.refreshResources();
        RenderingRegistry.instance().loadEntityRenderers((Map<Class<? extends Entity>, Render>) RenderManager.instance.entityRenderMap);
        guiFactories = HashBiMap.create();
        for (ModContainer mc : Loader.instance().getActiveModList())
        {
            String className = mc.getGuiClassName();
            if (Strings.isNullOrEmpty(className))
            {
                continue;
            }
            try
            {
                Class<?> clazz = Class.forName(className, true, Loader.instance().getModClassLoader());
                Class<? extends IModGuiFactory> guiClassFactory = clazz.asSubclass(IModGuiFactory.class);
                IModGuiFactory guiFactory = guiClassFactory.newInstance();
                guiFactory.initialize(client);
                guiFactories.put(mc, guiFactory);
            } catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "A critical error occurred instantiating the gui factory for mod %s", mc.getModId());
            }
        }
        loading = false;
        client.gameSettings.loadOptions(); //Reload options to load any mod added keybindings.
    }
}
