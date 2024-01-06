package fr.iamacat.optimizationsandtweaks.mixins.common.cofhcore;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.Proxy;
import cofh.core.entity.DropHandler;
import cofh.core.gui.GuiHandler;
import cofh.core.network.PacketHandler;
import cofh.core.util.ConfigHandler;
import cofh.core.util.FMLEventHandler;
import cofh.core.util.IBakeable;
import cofh.core.util.crafting.RecipeAugmentable;
import cofh.core.util.crafting.RecipeSecure;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.core.util.crafting.RecipeUpgradeOverride;
import cofh.core.util.energy.FurnaceFuelHandler;
import cofh.core.util.fluid.BucketHandler;
import cofh.core.world.FeatureParser;
import cofh.core.world.WorldHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mixin(CoFHCore.class)
public class MixinCOFHCORE {

    @Shadow
    public static Proxy proxy;
    @Shadow
    public static final Logger log = LogManager.getLogger("CoFHCore");
    @Shadow
    public static final ConfigHandler configCore = new ConfigHandler("1.7.10R3.1.4");
    @Shadow
    public static final ConfigHandler configLoot = new ConfigHandler("1.7.10R3.1.4");
    @Shadow
    public static final ConfigHandler configClient = new ConfigHandler("1.7.10R3.1.4");
    @Shadow
    public static final GuiHandler guiHandler = new GuiHandler();
    @Shadow
    public static MinecraftServer server;
    @Shadow
    private final ArrayList<IBakeable> oven = new ArrayList();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent var1) {
        CoFHProps.configDir = var1.getModConfigurationDirectory();
        // UpdateManager.registerUpdater(new UpdateManager(this, "https://raw.github.com/CoFH/VERSION/master/CoFHCore",
        // "http://teamcofh.com/downloads/"));
        configCore.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/core/common.cfg"), true));
        configClient.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/core/client.cfg"), true));
        MinecraftForge.EVENT_BUS.register(proxy);
        proxy.preInit();
        this.moduleCore();
        this.moduleLoot();
        FeatureParser.initialize();
        WorldHandler.initialize();
        FMLEventHandler.initialize();
        BucketHandler.initialize();
        FurnaceFuelHandler.initialize();
        PacketHandler.instance.initialize();
        RecipeSorter
            .register("cofh:augment", RecipeAugmentable.class, RecipeSorter.Category.SHAPED, "before:forge:shapedore");
        RecipeSorter.register("cofh:secure", RecipeSecure.class, RecipeSorter.Category.SHAPED, "before:cofh:upgrade");
        RecipeSorter
            .register("cofh:upgrade", RecipeUpgrade.class, RecipeSorter.Category.SHAPED, "before:forge:shapedore");
        RecipeSorter.register(
            "cofh:upgradeoverride",
            RecipeUpgradeOverride.class,
            RecipeSorter.Category.SHAPED,
            "before:forge:shapedore");
        this.registerOreDictionaryEntries();
    }

    @Shadow
    private boolean moduleCore() {
        String var2 = "General";
        String var1 = "Set to TRUE to be informed of non-critical updates. You will still receive critical update notifications.";
        CoFHProps.enableUpdateNotice = configCore.get(var2, "EnableUpdateNotifications", true, var1);
        var1 = "Set to TRUE for this to log when a block is dismantled.";
        CoFHProps.enableDismantleLogging = configCore.get(var2, "EnableDismantleLogging", false, var1);
        var1 = "Set to TRUE to display death messages for any named entity.";
        CoFHProps.enableLivingEntityDeathMessages = configCore.get(var2, "EnableGenericDeathMessage", true, var1);
        var1 = "Set to FALSE to disable items on the ground from trying to stack. This can improve server performance.";
        CoFHProps.enableItemStacking = configCore.get(var2, "EnableItemStacking", true, var1);
        var2 = "Holiday";
        var1 = "Set this to TRUE to disable April Foolishness.";
        CoFHProps.holidayAprilFools = !configCore.get(var2, "IHateApril", false, var1);
        var1 = "Set this to TRUE to disable Christmas cheer. Scrooge. :(";
        CoFHProps.holidayChristmas = !configCore.get(var2, "HoHoNo", false, var1);
        var2 = "Security";
        var1 = "Set to TRUE to allow for Server Ops to access 'secure' blocks. Your players will be warned upon server connection.";
        CoFHProps.enableOpSecureAccess = configCore.get(var2, "OpsCanAccessSecureBlocks", false, var1);
        var2 = "World.Tweaks";
        var1 = "Set this to a value > 1 to make trees grow more infrequently. Rate is 1 in N. Example: If this value is set to 3, trees will take 3x the time to grow, on average.";
        CoFHProps.treeGrowthChance = configCore.get(var2, "TreeGrowthChance", 1, var1);
        configCore.save();
        return true;
    }

    @Shadow
    private boolean moduleLoot() {
        configLoot.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/core/loot.cfg"), true));
        String var2 = "General";
        String var1 = "Set to false to disable this entire module.";
        boolean var3 = configLoot.get(var2, "EnableModule", true, var1);
        if (!var3) {
            configLoot.save();
            return false;
        } else {
            var2 = "Heads";
            var1 = "If enabled, mobs only drop heads when killed by players.";
            DropHandler.mobPvEOnly = configLoot.get(var2, "MobsDropOnPvEOnly", DropHandler.mobPvEOnly, var1);
            var1 = "If enabled, players only drop heads when killed by other players.";
            DropHandler.playerPvPOnly = configLoot.get(var2, "PlayersDropOnPvPOnly", DropHandler.playerPvPOnly, var1);
            var2 = "Heads.Players";
            DropHandler.playersEnabled = configLoot.get(var2, "Enabled", DropHandler.playersEnabled);
            DropHandler.playerChance = configLoot.get(var2, "Chance", DropHandler.playerChance);
            var2 = "Heads.Creepers";
            DropHandler.creeperEnabled = configLoot.get(var2, "Enabled", DropHandler.creeperEnabled);
            DropHandler.creeperChance = configLoot.get(var2, "Chance", DropHandler.creeperChance);
            var2 = "Heads.Skeletons";
            DropHandler.skeletonEnabled = configLoot.get(var2, "Enabled", DropHandler.skeletonEnabled);
            DropHandler.skeletonChance = configLoot.get(var2, "Chance", DropHandler.skeletonChance);
            var2 = "Heads.WitherSkeletons";
            DropHandler.skeletonEnabled = configLoot.get(var2, "Enabled", DropHandler.witherSkeletonEnabled);
            DropHandler.witherSkeletonChance = configLoot.get(var2, "Chance", DropHandler.witherSkeletonChance);
            var2 = "Heads.Zombies";
            DropHandler.zombieEnabled = configLoot.get(var2, "Enabled", DropHandler.zombieEnabled);
            DropHandler.zombieChance = configLoot.get(var2, "Chance", DropHandler.zombieChance);
            configLoot.save();
            MinecraftForge.EVENT_BUS.register(DropHandler.instance);
            return true;
        }
    }

    @Shadow
    public void registerOreDictionaryEntries() {
        this.registerOreDictionaryEntry("blockCloth", new ItemStack(Blocks.wool, 1, 32767));
        this.registerOreDictionaryEntry("coal", new ItemStack(Items.coal, 1, 0));
        this.registerOreDictionaryEntry("charcoal", new ItemStack(Items.coal, 1, 1));
    }

    @Shadow
    private boolean registerOreDictionaryEntry(String var1, ItemStack var2) {
        if (OreDictionary.getOres(var1)
            .isEmpty()) {
            OreDictionary.registerOre(var1, var2);
            return true;
        } else {
            return false;
        }
    }
}
