package fr.iamacat.multithreading.asm;

import java.util.*;
import java.util.function.Predicate;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Mixin implements IMixin {

    // BUGFIX MIXINS

    common_minefactoryreloaded_MixinFixCascadingforMineFactoryReloadedWorldGen(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingforMineFactoryReloadedWorldGen,
        "minefactoryreloaded.MixinFixCascadingforMineFactoryReloadedWorldGen"),
    common_pamsharvestcraft_MixinFixWorldGenPamFruitTree(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixWorldGenPamFruitTree,
        "pamsharvestcraft.MixinFixWorldGenPamFruitTree"),
    common_steamcraft2_MixinFixCascadingFromWorldGenBrassTree(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromWorldGenBrassTree,
        "steamcraft2.MixinFixCascadingFromWorldGenBrassTree"),
    common_core_MixinDisableinitialWorldChunkLoad(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinDisableinitialWorldChunkLoad,
        "core.MixinDisableinitialWorldChunkLoad"),
    common_cofhcore_fixoredictcrash_MixinOreDictionaryArbiter(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinOreDictCofhFix,
        "cofhcore.fixoredictcrash.MixinOreDictionaryArbiter"),
    common_slimecarnage_MixinFixCascadingFromWorldGenSlimeCarnage(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromWorldGenSlimeCarnage,
        "slimecarnage.MixinFixCascadingFromWorldGenSlimeCarnage"),
    common_pamsharvestcraft_MixinFixPamsTreesCascadingWorldgenLag(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixPamsTreesCascadingWorldgenLag,
        "pamsharvestcraft.MixinFixPamsTreesCascadingWorldgenLag"),

    common_MixinFixCascadingFromShipwreckGen(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromShipwreckGen,
        "shipwreck.MixinFixCascadingFromShipwreckGen"),
    common_shincolle_MixinFixCascadingFromWorldGenPolyGravel(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromWorldGenPolyGravel,
        "shincolle.MixinFixCascadingFromWorldGenPolyGravel"),
    common_shincolle_MixinFixCascadingFromShinColleWorldGen(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromShinColleWorldGen,
        "shincolle.MixinFixCascadingFromShinColleWorldGen"),
    common_minefactoryreloaded_MixinFixWorldGenLakesMetaCascadingWorldgenLag(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixWorldGenLakesMetaMinefactoryReloadedCascadingWorldgenFix,
        "minefactoryreloaded.MixinFixWorldGenLakesMetaCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixRubberTreesCascadingWorldgenLag(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix,
        "minefactoryreloaded.MixinFixRubberTreesCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixNoSuchMethodException(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixNoSuchMethodException,
        "minefactoryreloaded.MixinFixNoSuchMethodException"),

    common_core_MixinGodZillaFix(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinGodZillaFix,
        "core.MixinGodZillaFix"),
    common_core_MixinStatList(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinStatList,
        "core.MixinStatList"),

    // MULTITHREADING MIXINS
    common_core_MixinLeafDecay(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinLeafDecay,
        "core.MixinLeafDecay"),
    common_core_MixinEntityLivingUpdate(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityLivingUpdate,
        "core.MixinEntityLivingUpdate"),
    common_core_MixinFireTick(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinFireTick,
        "core.MixinFireTick"),
    common_core_MixinGrowthSpreading(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinGrowthSpreading,
        "core.MixinGrowthSpreading"),
    common_core_MixinLiquidTick(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinliquidTick,
        "core.MixinLiquidTick"),
    common_core_MixinEntitySpawning(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntitySpawning,
        "core.MixinEntitySpawning"),
    common_core_MixinChunkPopulating(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinChunkPopulating,
        "core.MixinChunkPopulating"),
    common_core_MixinTileEntitiesTick(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinTileEntitiesTick,
        "core.MixinTileEntitiesTick"),

    common_core_MixinWorldTick(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinWorldTick,
        "core.MixinWorldTick"),
    common_core_MixinExplosions(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinExplosions,
        "core.MixinExplosions"),
    common_core_MixinFallBlocksTick(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinFallBlocksTick,
        "core.MixinFallBlocksTick"),
    common_core_MixinUpdateBlocks(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinUpdateBlocks,
        "core.MixinUpdateBlocks"),
    common_core_MixinGrassSpread(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinGrassSpread,
        "core.MixinGrassSpread"),
    common_core_MixinEntitiesCollision(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntitiesCollision,
        "core.MixinEntitiesCollision"),

    common_core_MixinEntitiesUpdateTimeandLight(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinEntitiesUpdateTimeandLight,
        "core.MixinEntitiesUpdateTimeandLight"),

    common_core_MixinChunkProviderServer(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinChunkProviderServer,
        "core.MixinChunkProviderServer"),
    // CLIENT MIXINS

    client_core_MixinParticle(Side.CLIENT, m -> MultithreadingandtweaksConfig.enableMixinParticle,
        "core.MixinParticleManager"),
    client_core_MixinEntitiesRendering(Side.CLIENT, m -> MultithreadingandtweaksConfig.enableMixinEntitiesRendering,
        "core.MixinEntitiesRendering"),
    client_core_MixinTileEntities(Side.CLIENT, m -> MultithreadingandtweaksConfig.enableMixinTileEntities,
        "core.MixinTileEntities"),
    client_core_MixinGUIHUD(Side.CLIENT, m -> MultithreadingandtweaksConfig.enableMixinGUIHUD, "core.MixinGUIHUD"),
    client_core_MixinMultithreadedSkyLightning(Side.CLIENT,
        m -> MultithreadingandtweaksConfig.enableMixinMultithreadedSkyLightning, "core.MixinMultithreadedSkyLightning"),
    client_core_MixinWorldgen(Side.CLIENT, m -> MultithreadingandtweaksConfig.enableMixinWorldgen,
        "core.MixinWorldgen"),
    client_core_MixinLiquidRendering(Side.CLIENT, m -> MultithreadingandtweaksConfig.enableMixinLiquidRendering,
        "core.MixinLiquidRendering"),
    // MOD-FILTERED MIXINS

    // The modFilter argument is a predicate, so you can also use the .and(), .or(), and .negate() methods to mix and
    // match multiple predicates.
    ;

    @Getter
    public final Side side;
    @Getter
    public final Predicate<List<ITargetedMod>> filter;
    @Getter
    public final String mixin;
}
