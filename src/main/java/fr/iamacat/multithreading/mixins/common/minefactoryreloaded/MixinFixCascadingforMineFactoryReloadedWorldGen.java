package fr.iamacat.multithreading.mixins.common.minefactoryreloaded;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.*;

import com.google.common.primitives.Ints;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;
import powercrystals.minefactoryreloaded.world.WorldGenLakesMeta;
import powercrystals.minefactoryreloaded.world.WorldGenMassiveTree;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

@Mixin(MineFactoryReloadedWorldGen.class)
public class MixinFixCascadingforMineFactoryReloadedWorldGen {

    @Shadow
    private static List<Integer> _blacklistedDimensions;
    @Shadow
    private static List<String> _sludgeBiomeList;
    @Shadow
    private static List<String> _sewageBiomeList;
    @Shadow
    private static List<String> _rubberTreeBiomeList;
    @Shadow
    private static boolean _sludgeLakeMode;
    @Shadow
    private static boolean _sewageLakeMode;
    @Shadow
    private static boolean _rubberTreesEnabled;
    @Shadow
    private static boolean _lakesEnabled;
    @Shadow
    private static boolean _regenSewage;
    @Shadow
    private static boolean _regenSludge;
    @Shadow
    private static boolean _regenTrees;
    @Shadow
    private static int _sludgeLakeRarity;
    @Shadow
    private static int _sewageLakeRarity;
    @Final
    @Shadow
    private final String name = "MFR:WorldGen";

    public MixinFixCascadingforMineFactoryReloadedWorldGen() {}

    @Unique
    private static boolean multithreadingandtweaks$generateMegaRubberTree(World var0, Random var1, int var2, int var3,
        int var4, boolean var5) {
        return (new WorldGenMassiveTree(false)).setTreeScale((float) (4 + var1.nextInt(3)), 0.8F, 0.7F)
            .setLeafAttenuation(0.6F)
            .setSloped(true)
            .setSafe(var5)
            .func_76484_a(var0, var1, var2, var3, var4);
    }

    @Unique
    private static boolean multithreadingandtweaks$generateSacredSpringRubberTree(World var0, Random var1, int var2,
        int var3, int var4) {
        return (new WorldGenMassiveTree(false)).setTreeScale((float) (6 + var1.nextInt(4)), 1.0F, 0.9F)
            .setLeafAttenuation(0.35F)
            .setSloped(false)
            .setMinTrunkSize(4)
            .func_76484_a(var0, var1, var2, var3, var4);
    }

    @Shadow
    public String getFeatureName() {
        return "MFR:WorldGen";
    }

    /**
     * @author f
     * @reason f
     */

    @Overwrite(remap = false)
    public boolean generateFeature(Random var1, int var2, int var3, World var4, boolean var5) {
        if (_blacklistedDimensions == null) {
            multithreadingandtweaks$buildBlacklistedDimensions();
        }

        if (_blacklistedDimensions.contains(var4.provider.dimensionId)) {
            return false;
        }

        int var6 = var2 * 16 + var1.nextInt(16);
        int var7 = var3 * 16 + var1.nextInt(16);
        BiomeGenBase var8 = var4.getBiomeGenForCoords(var6, var7);

        if (var8 == null) {
            return false;
        }

        String var9 = var8.biomeName;

        // Generate rubber trees
        if (_rubberTreesEnabled && (var5 || _regenTrees)
            && _rubberTreeBiomeList.contains(var9)
            && var1.nextInt(100) < 40) {
            if (var1.nextInt(30) == 0) {
                String var10 = var9.toLowerCase(Locale.US);
                if (var10.contains("mega")) {
                    multithreadingandtweaks$generateMegaRubberTree(
                        var4,
                        var1,
                        var6,
                        var4.getTopSolidOrLiquidBlock(var6, var7),
                        var7,
                        false);
                } else if (var10.contains("sacred") && var1.nextInt(20) == 0) {
                    multithreadingandtweaks$generateSacredSpringRubberTree(
                        var4,
                        var1,
                        var6,
                        var4.getTopSolidOrLiquidBlock(var6, var7),
                        var7);
                }
            }

            (new WorldGenRubberTree(false)).func_76484_a(var4, var1, var6, var1.nextInt(3) + 4, var7);
        }

        // Generate lakes
        if (_lakesEnabled && var4.getWorldInfo()
            .isMapFeaturesEnabled()) {
            int var15;

            // Sludge lakes
            var15 = _sludgeLakeRarity;
            if (var15 > 0 && (var5 || _regenSludge)
                && _sludgeBiomeList.contains(var9) == _sludgeLakeMode
                && var1.nextInt(var15) == 0) {
                int var11 = var6 - 8 + var1.nextInt(16);
                int var12 = var1.nextInt(var4.getHeight());
                int var13 = var7 - 8 + var1.nextInt(16);
                (new WorldGenLakesMeta(MFRThings.sludgeLiquid, 0)).func_76484_a(var4, var1, var11, var12, var13);
            }

            // Sewage lakes
            var15 = _sewageLakeRarity;
            if (var15 > 0 && (var5 || _regenSewage)
                && _sewageBiomeList.contains(var9) == _sewageLakeMode
                && var1.nextInt(var15) == 0) {
                int var11 = var6 - 8 + var1.nextInt(16);
                int var12 = var1.nextInt(var4.getHeight());
                int var13 = var7 - 8 + var1.nextInt(16);
                String var14 = var9.toLowerCase(Locale.US);
                if (var14.contains("mushroom")) {
                    (new WorldGenLakesMeta(MFRThings.mushroomSoupLiquid, 0))
                        .func_76484_a(var4, var1, var11, var12, var13);
                } else {
                    (new WorldGenLakesMeta(MFRThings.sewageLiquid, 0)).func_76484_a(var4, var1, var11, var12, var13);
                }
            }
        }

        return true;
    }

    @Unique
    private static void multithreadingandtweaks$buildBlacklistedDimensions() {
        _blacklistedDimensions = Ints.asList(MFRConfig.worldGenDimensionBlacklist.getIntList());
        _rubberTreeBiomeList = MFRRegistry.getRubberTreeBiomes();
        _rubberTreesEnabled = MFRConfig.rubberTreeWorldGen.getBoolean(true);
        _lakesEnabled = MFRConfig.mfrLakeWorldGen.getBoolean(true);
        _sludgeLakeRarity = MFRConfig.mfrLakeSludgeRarity.getInt();
        _sludgeBiomeList = Arrays.asList(MFRConfig.mfrLakeSludgeBiomeList.getStringList());
        _sludgeLakeMode = MFRConfig.mfrLakeSludgeBiomeListToggle.getBoolean(false);
        _sewageLakeRarity = MFRConfig.mfrLakeSewageRarity.getInt();
        _sewageBiomeList = Arrays.asList(MFRConfig.mfrLakeSewageBiomeList.getStringList());
        _sewageLakeMode = MFRConfig.mfrLakeSewageBiomeListToggle.getBoolean(false);
        _regenSewage = MFRConfig.mfrLakeSewageRetrogen.getBoolean(false);
        _regenSludge = MFRConfig.mfrLakeSludgeRetrogen.getBoolean(false);
        _regenTrees = MFRConfig.rubberTreeRetrogen.getBoolean(false);
    }
}
