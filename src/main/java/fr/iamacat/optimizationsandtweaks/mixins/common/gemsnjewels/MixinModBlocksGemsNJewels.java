package fr.iamacat.optimizationsandtweaks.mixins.common.gemsnjewels;

import com.chrisgli.gemsnjewels.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModBlocks.class)
public class MixinModBlocksGemsNJewels {
    /**
     * @reason1 add oredics to ores
     */
    @Shadow
    public static Block opalOreBlock;
    @Shadow
    public static Block opalBlock;
    @Shadow
    public static Block amethystOreBlock;
    @Shadow
    public static Block amethystBlock;
    @Shadow
    public static Block sapphireOreBlock;
    @Shadow
    public static Block sapphireBlock;
    @Shadow
    public static Block rubyOreBlock;
    @Shadow
    public static Block rubyBlock;
    @Shadow
    public static Block garnetOreBlock;
    @Shadow
    public static Block topazOreBlock;
    @Shadow
    public static Block peridotOreBlock;
    @Shadow
    public static Block aquamarineOreBlock;
    @Shadow
    public static Block zirconOreBlock;
    @Shadow
    public static Block alexandriteOreBlock;
    @Shadow
    public static Block tanzaniteOreBlock;
    @Shadow
    public static Block tourmalineOreBlock;
    @Shadow
    public static Block spinelOreBlock;
    @Shadow
    public static Block blackOpalOreBlock;
    @Shadow
    public static Block jasperOreBlock;
    @Shadow
    public static Block citrineOreBlock;
    @Shadow
    public static Block amberOreBlock;
    @Shadow
    public static Block jadeOreBlock;
    @Shadow
    public static Block malachiteOreBlock;
    @Shadow
    public static Block turquoiseOreBlock;
    @Shadow
    public static Block sugiliteOreBlock;
    @Shadow
    public static Block roseQuartzOreBlock;
    @Shadow
    public static Block onyxOreBlock;
    @Shadow
    public static Block garnetBlock;
    @Shadow
    public static Block topazBlock;
    @Shadow
    public static Block peridotBlock;
    @Shadow
    public static Block aquamarineBlock;
    @Shadow
    public static Block zirconBlock;
    @Shadow
    public static Block alexandriteBlock;
    @Shadow
    public static Block tanzaniteBlock;
    @Shadow
    public static Block tourmalineBlock;
    @Shadow
    public static Block spinelBlock;
    @Shadow
    public static Block blackOpalBlock;
    @Shadow
    public static Block jasperBlock;
    @Shadow
    public static Block citrineBlock;
    @Shadow
    public static Block amberBlock;
    @Shadow
    public static Block jadeBlock;
    @Shadow
    public static Block malachiteBlock;
    @Shadow
    public static Block turquoiseBlock;
    @Shadow
    public static Block sugiliteBlock;
    @Shadow
    public static Block roseQuartzBlock;
    @Shadow
    public static Block onyxBlock;
    @Shadow
    public static Block jasperStairBlock;
    @Shadow
    public static Block citrineStairBlock;
    @Shadow
    public static Block amberStairBlock;
    @Shadow
    public static Block jadeStairBlock;
    @Shadow
    public static Block malachiteStairBlock;
    @Shadow
    public static Block turquoiseStairBlock;
    @Shadow
    public static Block sugiliteStairBlock;
    @Shadow
    public static Block roseQuartzStairBlock;
    @Shadow
    public static Block onyxStairBlock;
    @Shadow
    public static Block opalOreNetherBlock;
    @Shadow
    public static Block amethystOreNetherBlock;
    @Shadow
    public static Block sapphireOreNetherBlock;
    @Shadow
    public static Block rubyOreNetherBlock;
    @Shadow
    public static Block emeraldOreNetherBlock;
    @Shadow
    public static Block diamondOreNetherBlock;
    @Shadow
    public static Block garnetOreNetherBlock;
    @Shadow
    public static Block topazOreNetherBlock;
    @Shadow
    public static Block peridotOreNetherBlock;
    @Shadow
    public static Block aquamarineOreNetherBlock;
    @Shadow
    public static Block zirconOreNetherBlock;
    @Shadow
    public static Block alexandriteOreNetherBlock;
    @Shadow
    public static Block tanzaniteOreNetherBlock;
    @Shadow
    public static Block tourmalineOreNetherBlock;
    @Shadow
    public static Block spinelOreNetherBlock;
    @Shadow
    public static Block blackOpalOreNetherBlock;
    @Shadow
    public static Block jasperOreNetherBlock;
    @Shadow
    public static Block citrineOreNetherBlock;
    @Shadow
    public static Block amberOreNetherBlock;
    @Shadow
    public static Block jadeOreNetherBlock;
    @Shadow
    public static Block malachiteOreNetherBlock;
    @Shadow
    public static Block turquoiseOreNetherBlock;
    @Shadow
    public static Block sugiliteOreNetherBlock;
    @Shadow
    public static Block roseQuartzOreNetherBlock;
    @Shadow
    public static Block onyxOreNetherBlock;
    @Shadow
    public static Block lapisOreNetherBlock;
    @Shadow
    public static Block silverOreBlock;
    @Shadow
    public static Block silverBlock;
    @Shadow
    public static Block platinumOreBlock;
    @Shadow
    public static Block platinumBlock;
    @Shadow
    public static Block emeraldOreBlock;
    @Shadow
    public static Block emeraldEnchantBlock;
    @Shadow
    public static Block diamondEnchantBlock;
    @Shadow
    public static Block lapisEnchantBlock;
    @Shadow
    public static Block quartzEnchantBlock;
    @Shadow
    public static Block diamondGlimmer;
    @Shadow
    public static Block stoneRail;
    @Shadow
    public static Block cobblestoneRail;
    @Shadow
    public static Block brickRail;
    @Shadow
    public static Block netherBrickRail;
    @Shadow
    public static Block sandstoneRail;
    @Shadow
    public static Block stoneBrickRail;
    @Shadow
    public static Block quartzRail;

    @Inject(method = "init", at = @At("RETURN"),remap = false)
    private static final void init(CallbackInfo ci) {
        registerOreDict();
    }

    @Unique
    private static void registerOreDict() {
        OreDictionary.registerOre("oreOpal", opalOreNetherBlock);
        OreDictionary.registerOre("oreOpal", opalOreBlock);
        OreDictionary.registerOre("oreBlackOpal", blackOpalOreBlock);
        OreDictionary.registerOre("oreBlackOpal", blackOpalOreNetherBlock);
        OreDictionary.registerOre("oreAmethyst", amethystOreBlock);
        OreDictionary.registerOre("oreAmethyst", amethystOreNetherBlock);
        OreDictionary.registerOre("oreLapis", lapisOreNetherBlock);
        OreDictionary.registerOre("oreOnyx", onyxOreNetherBlock);
        OreDictionary.registerOre("oreOnyx", onyxOreBlock);
        OreDictionary.registerOre("oreRoseQuartz", roseQuartzOreNetherBlock);
        OreDictionary.registerOre("oreRoseQuartz", roseQuartzOreBlock);
        OreDictionary.registerOre("oreSugilite", sugiliteOreNetherBlock);
        OreDictionary.registerOre("oreSugilite", sugiliteOreBlock);
        OreDictionary.registerOre("oreTurquoise", turquoiseOreNetherBlock);
        OreDictionary.registerOre("oreTurquoise", turquoiseOreBlock);
        OreDictionary.registerOre("oreMalachite", malachiteOreNetherBlock);
        OreDictionary.registerOre("oreMalachite", malachiteOreBlock);
        OreDictionary.registerOre("oreJade", jadeOreBlock);
        OreDictionary.registerOre("oreJade", jadeOreNetherBlock);
        OreDictionary.registerOre("oreAmber", amberOreNetherBlock);
        OreDictionary.registerOre("oreAmber", amberOreBlock);
        OreDictionary.registerOre("oreCitrine", citrineOreNetherBlock);
        OreDictionary.registerOre("oreCitrine", citrineOreBlock);
        OreDictionary.registerOre("oreJasper", jasperOreBlock);
        OreDictionary.registerOre("oreJasper", jasperOreNetherBlock);
        OreDictionary.registerOre("oreSpinel", spinelOreNetherBlock);
        OreDictionary.registerOre("oreSpinel", spinelOreBlock);
        OreDictionary.registerOre("oreTourmaline", tourmalineOreBlock);
        OreDictionary.registerOre("oreTourmaline", tourmalineOreNetherBlock);
        OreDictionary.registerOre("oreTanzanite", tanzaniteOreNetherBlock);
        OreDictionary.registerOre("oreTanzanite", tanzaniteOreBlock);
        OreDictionary.registerOre("oreAlexandrite", alexandriteOreNetherBlock);
        OreDictionary.registerOre("oreAlexandrite", alexandriteOreBlock);
        OreDictionary.registerOre("oreZircon", zirconOreNetherBlock);
        OreDictionary.registerOre("oreZircon", zirconOreBlock);
        OreDictionary.registerOre("oreAquamarine", aquamarineOreNetherBlock);
        OreDictionary.registerOre("oreAquamarine", aquamarineOreBlock);
        OreDictionary.registerOre("orePeridot", peridotOreNetherBlock);
        OreDictionary.registerOre("orePeridot", peridotOreBlock);
        OreDictionary.registerOre("oreTopaz", topazOreNetherBlock);
        OreDictionary.registerOre("oreTopaz", topazOreBlock);
        OreDictionary.registerOre("oreGarnet", garnetOreBlock);
        OreDictionary.registerOre("oreGarnet", garnetOreNetherBlock);
        OreDictionary.registerOre("oreDiamond", diamondOreNetherBlock);
        OreDictionary.registerOre("oreEmerald", emeraldOreNetherBlock);
        OreDictionary.registerOre("oreRuby", rubyOreBlock);
        OreDictionary.registerOre("oreRuby", rubyOreNetherBlock);
        OreDictionary.registerOre("oreSapphire", sapphireOreNetherBlock);
        OreDictionary.registerOre("oreSapphire", sapphireOreBlock);
        OreDictionary.registerOre("oreSapphire", sapphireOreBlock);
        OreDictionary.registerOre("oreSapphire", sapphireOreBlock);
        OreDictionary.registerOre("oreSilver", silverOreBlock);
        OreDictionary.registerOre("orePlatinum", platinumOreBlock);
    }
}
