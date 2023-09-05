package fr.iamacat.multithreading.asm;

import java.util.*;
import java.util.function.Predicate;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Mixin implements IMixin {
    // BUGFIX MIXINS
    common_minefactoryreloaded_MixinFixWorldGenLakesMetaCascadingWorldgenLag(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinFixWorldGenLakesMetaMinefactoryReloadedCascadingWorldgenFix,
        "minefactoryreloaded.MixinFixWorldGenLakesMetaCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixRubberTreesCascadingWorldgenLag(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix,
        "minefactoryreloaded.MixinFixRubberTreesCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixNoSuchMethodException(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinFixNoSuchMethodException,
        "minefactoryreloaded.MixinFixNoSuchMethodException"),

    common_core_MixinGodZillaFix(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinGodZillaFix,
        "core.MixinGodZillaFix"),
    common_core_MixinStatList(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinStatList,
        "core.MixinStatList"),

    // MULTITHREADING MIXINS
    common_core_MixinLeafDecay(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinLeafDecay,
        "core.MixinLeafDecay"),
    common_core_MixinEntityLivingUpdate(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinEntityLivingUpdate, "core.MixinEntityLivingUpdate"),
    common_core_MixinFireTick(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinFireTick,
        "core.MixinFireTick"),
    common_core_MixinGrowthSpreading(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinGrowthSpreading, "core.MixinGrowthSpreading"),
    common_core_MixinLiquidTick(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinliquidTick,
        "core.MixinLiquidTick"),
    common_core_MixinEntitySpawning(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinEntitySpawning, "core.MixinEntitySpawning"),
    common_core_MixinChunkPopulating(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinChunkPopulating, "core.MixinChunkPopulating"),
    common_core_MixinTileEntitiesTick(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinTileEntitiesTick, "core.MixinTileEntitiesTick"),

    common_core_MixinWorldTick(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinWorldTick,
        "core.MixinWorldTick"),
    common_core_MixinExplosions(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinExplosions,
        "core.MixinExplosions"),
    common_core_MixinFallBlocksTick(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinFallBlocksTick, "core.MixinFallBlocksTick"),
    common_core_MixinUpdateBlocks(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinUpdateBlocks,
        "core.MixinUpdateBlocks"),
    common_core_MixinGrassSpread(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinGrassSpread,
        "core.MixinGrassSpread"),
    common_core_MixinEntitiesCollision(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesCollision, "core.MixinEntitiesCollision"),

    common_core_MixinEntitiesUpdateTimeandLight(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesUpdateTimeandLight,
        "core.MixinEntitiesUpdateTimeandLight"),

    common_core_MixinChunkProviderServer(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinChunkProviderServer,
        "core.MixinChunkProviderServer"),
    // CLIENT MIXINS

    client_core_MixinParticle(Side.CLIENT, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinParticle,
        "core.MixinParticleManager"),
    client_core_MixinEntitiesRendering(Side.CLIENT,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinEntitiesRendering, "core.MixinEntitiesRendering"),
    client_core_MixinTileEntities(Side.CLIENT, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinTileEntities,
        "core.MixinTileEntities"),
    client_core_MixinGUIHUD(Side.CLIENT, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinGUIHUD,
        "core.MixinGUIHUD"),
    client_core_MixinMultithreadedSkyLightning(Side.CLIENT,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinMultithreadedSkyLightning,
        "core.MixinMultithreadedSkyLightning"),
    client_core_MixinWorldgen(Side.CLIENT, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinWorldgen,
        "core.MixinWorldgen"),
    client_core_MixinLiquidRendering(Side.CLIENT,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinLiquidRendering, "core.MixinLiquidRendering"),
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
