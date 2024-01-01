package fr.iamacat.optimizationsandtweaks.mixins.common.minegicka;

import com.williameze.minegicka3.CommonProxy;
import com.williameze.minegicka3.CreativeTabCustom;
import com.williameze.minegicka3.ModBase;
import com.williameze.minegicka3.PotionCustom;
import com.williameze.minegicka3.functional.CoreBridge;
import com.williameze.minegicka3.main.entities.SpawnBiomesList;
import com.williameze.minegicka3.main.entities.fx.FXEProjectileCharge;
import com.williameze.minegicka3.main.entities.fx.FXESimpleParticle;
import com.williameze.minegicka3.main.entities.living.Entity888;
import com.williameze.minegicka3.main.entities.living.EntityMage;
import com.williameze.minegicka3.main.entities.magic.*;
import com.williameze.minegicka3.main.objects.blocks.*;
import com.williameze.minegicka3.main.objects.items.*;
import com.williameze.minegicka3.main.packets.PacketHandler;
import com.williameze.minegicka3.main.worldgen.ChestGenHook;
import com.williameze.minegicka3.mechanics.ClickCraft;
import com.williameze.minegicka3.mechanics.Element;
import com.williameze.minegicka3.mechanics.Enchant;
import com.williameze.minegicka3.mechanics.SpellDamageModifier;
import com.williameze.minegicka3.mechanics.magicks.Magicks;
import com.williameze.minegicka3.mechanics.spells.SpellExecute;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minegicka.MinegickaConfigPotionID;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ModBase.class)
public class MixinModBaseMinegicka {
    @Shadow
    public static ModBase instance;

    @Shadow
    public static CommonProxy proxy;
    @Shadow
    public static PacketHandler packetPipeline;
    @Shadow
    public static Material magical;
    @Shadow
    public static CreativeTabCustom modCreativeTab;
    @Shadow
    public static Potion coldResistance;
    @Shadow
    public static Potion lifeBoost;
    @Shadow
    public static Potion arcaneResistance;
    @Shadow
    public static Potion lightningResistance;
    @Shadow
    public static Block shieldBlock;
    @Shadow
    public static Block wallBlock;
    @Shadow
    public static Block craftStation;
    @Shadow
    public static Block enchantStaff;
    @Shadow
    public static Item thingy;
    @Shadow
    public static Item thingyGood;
    @Shadow
    public static Item thingySuper;
    @Shadow
    public static Item stick;
    @Shadow
    public static Item stickGood;
    @Shadow
    public static Item stickSuper;
    @Shadow
    public static Item magicApple;
    @Shadow
    public static Item magicGoodApple;
    @Shadow
    public static Item magicSuperApple;
    @Shadow
    public static Item magicCookie;
    @Shadow
    public static Item magicGoodCookie;
    @Shadow
    public static Item magicSuperCookie;
    @Shadow
    public static Item essenceArcane;
    @Shadow
    public static Item essenceCold;
    @Shadow
    public static Item essenceEarth;
    @Shadow
    public static Item essenceFire;
    @Shadow
    public static Item essenceIce;
    @Shadow
    public static Item essenceLife;
    @Shadow
    public static Item essenceLightning;
    @Shadow
    public static Item essenceShield;
    @Shadow
    public static Item essenceSteam;
    @Shadow
    public static Item essenceWater;
    @Shadow
    public static Item magickTablet;
    @Shadow
    public static Item magickPedia;
    @Shadow
    public static Item matResistance;
    @Shadow
    public static Item staff;
    @Shadow
    public static Item staffGrand;
    @Shadow
    public static Item staffSuper;
    @Shadow
    public static Item hemmyStaff;
    @Shadow
    public static Item staffBlessing;
    @Shadow
    public static Item staffDestruction;
    @Shadow
    public static Item staffTelekinesis;
    @Shadow
    public static Item staffManipulation;
    @Shadow
    public static Item hat;
    @Shadow
    public static Item hatImmunity;
    @Shadow
    public static Item hatRisk;
    @Shadow
    public static Item hatResistance;
    /**
     * @author
     * @reason
     */
    @Inject(method = "preInit", at = @At("HEAD"),remap = false)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event, CallbackInfo ci) {
        MinegickaConfigPotionID.setupAndLoad(event);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void initObjects() {
        String themodid = "minegicka3-";
        magical = new Material(MapColor.purpleColor);
        modCreativeTab = new CreativeTabCustom("Minegicka 3");
        coldResistance = (new PotionCustom(MinegickaConfigPotionID.coldResistanceId, false, 15658751)).setCustomIconIndex(6, 1).setPotionName("potion.coldresistance");
        lifeBoost = (new PotionCustom(MinegickaConfigPotionID.lifeBoostId, false, 65280)).setCustomIconIndex(7, 0).setPotionName("potion.lifeboost");
        arcaneResistance = (new PotionCustom(MinegickaConfigPotionID.arcaneResistanceId, false, 15597568)).setCustomIconIndex(6, 1).setPotionName("potion.arcaneresistance");
        lightningResistance = (new PotionCustom(MinegickaConfigPotionID.lightningResistanceId, false, 16720639)).setCustomIconIndex(6, 1).setPotionName("potion.lightningresistance");
        shieldBlock = (new BlockShield()).setBlockName(themodid + "ShieldBlock").setBlockTextureName("glass");
        wallBlock = (new BlockWall()).setBlockName(themodid + "WallBlock").setBlockTextureName("glass");
        craftStation = (new BlockCraftStation()).setBlockName(themodid + "CraftStation").setBlockTextureName("obsidian").setCreativeTab(modCreativeTab);
        enchantStaff = (new BlockEnchantStaff()).setBlockName(themodid + "EnchantStaff").setBlockTextureName("obsidian").setCreativeTab(modCreativeTab);
        thingy = (new Item()).setUnlocalizedName(themodid + "Thingy").setTextureName("apple").setCreativeTab(modCreativeTab);
        thingyGood = (new Item()).setUnlocalizedName(themodid + "ThingyGood").setTextureName("apple").setCreativeTab(modCreativeTab);
        thingySuper = (new Item()).setUnlocalizedName(themodid + "ThingySuper").setTextureName("apple").setCreativeTab(modCreativeTab);
        stick = (new Item()).setUnlocalizedName(themodid + "TheStick").setTextureName("apple").setCreativeTab(modCreativeTab);
        stickGood = (new Item()).setUnlocalizedName(themodid + "TheStickGood").setTextureName("apple").setCreativeTab(modCreativeTab);
        stickSuper = (new Item()).setUnlocalizedName(themodid + "TheStickSuper").setTextureName("apple").setCreativeTab(modCreativeTab);
        magicApple = (new ItemMagicApple(5.0)).setUnlocalizedName(themodid + "MagicApple").setTextureName("apple").setCreativeTab(modCreativeTab);
        magicGoodApple = (new ItemMagicApple(100.0)).setUnlocalizedName(themodid + "MagicGoodApple").setTextureName("apple_golden").setCreativeTab(modCreativeTab);
        magicSuperApple = (new ItemMagicApple(2000.0)).setUnlocalizedName(themodid + "MagicSuperApple").setTextureName("apple_golden").setCreativeTab(modCreativeTab);
        magicCookie = (new ItemMagicCookie(20.0)).setUnlocalizedName(themodid + "MagicCookie").setTextureName("cookie").setCreativeTab(modCreativeTab);
        magicGoodCookie = (new ItemMagicCookie(120.0)).setUnlocalizedName(themodid + "MagicGoodCookie").setTextureName("cookie").setCreativeTab(modCreativeTab);
        magicSuperCookie = (new ItemMagicCookie(960.0)).setUnlocalizedName(themodid + "MagicSuperCookie").setTextureName("cookie").setCreativeTab(modCreativeTab);
        essenceArcane = (new ItemEssence(Element.Arcane)).setUnlocalizedName(themodid + "ArcaneEssence").setCreativeTab(modCreativeTab);
        essenceCold = (new ItemEssence(Element.Cold)).setUnlocalizedName(themodid + "ColdEssence").setCreativeTab(modCreativeTab);
        essenceEarth = (new ItemEssence(Element.Earth)).setUnlocalizedName(themodid + "EarthEssence").setCreativeTab(modCreativeTab);
        essenceFire = (new ItemEssence(Element.Fire)).setUnlocalizedName(themodid + "FireEssence").setCreativeTab(modCreativeTab);
        essenceIce = (new ItemEssence(Element.Ice)).setUnlocalizedName(themodid + "IceEssence").setCreativeTab(modCreativeTab);
        essenceLife = (new ItemEssence(Element.Life)).setUnlocalizedName(themodid + "LifeEssence").setCreativeTab(modCreativeTab);
        essenceLightning = (new ItemEssence(Element.Lightning)).setUnlocalizedName(themodid + "LightningEssence").setCreativeTab(modCreativeTab);
        essenceShield = (new ItemEssence(Element.Shield)).setUnlocalizedName(themodid + "ShieldEssence").setCreativeTab(modCreativeTab);
        essenceSteam = (new ItemEssence(Element.Steam)).setUnlocalizedName(themodid + "SteamEssence").setCreativeTab(modCreativeTab);
        essenceWater = (new ItemEssence(Element.Water)).setUnlocalizedName(themodid + "WaterEssence").setCreativeTab(modCreativeTab);
        magickPedia = (new ItemMagickPedia()).setUnlocalizedName(themodid + "MagickPedia").setCreativeTab(modCreativeTab).setTextureName("book_normal");
        matResistance = (new Item()).setUnlocalizedName(themodid + "MatResistance").setTextureName("apple").setCreativeTab(modCreativeTab);
        staff = (new Staff()).setUnlocalizedName(themodid + "Staff");
        staffGrand = (new Staff()).setBaseStats(2.0, 2.0, 0.5, 1.5).setUnlocalizedName(themodid + "StaffGrand");
        staffSuper = (new Staff()).setBaseStats(4.0, 4.0, 0.25, 2.0).setUnlocalizedName(themodid + "StaffSuper");
        hemmyStaff = (new StaffHemmy()).setUnlocalizedName(themodid + "HemmyStaff");
        staffBlessing = (new StaffBlessing()).setUnlocalizedName(themodid + "StaffBlessing");
        staffDestruction = (new StaffDestruction()).setUnlocalizedName(themodid + "StaffDestruction");
        staffTelekinesis = (new StaffTelekinesis()).setUnlocalizedName(themodid + "StaffTelekinesis");
        staffManipulation = (new StaffManipulation()).setUnlocalizedName(themodid + "StaffManipulation");
        hat = (new Hat()).setUnlocalizedName(themodid + "Hat");
        hatImmunity = (new HatOfImmunity()).setUnlocalizedName(themodid + "HatOfImmunity");
        hatRisk = (new Hat((new SpellDamageModifier(2.0)).setModifiers("3l"))).setUnlocalizedName(themodid + "HatOfRisk");
        hatResistance = (new Hat(Entity888.spellResistance.copy())).setUnlocalizedName(themodid + "HatOfResistance");
    }
}
