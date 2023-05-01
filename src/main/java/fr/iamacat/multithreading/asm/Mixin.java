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

    // MULTITHREADING MIXINS
    common_core_MixinLeafDecay(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinLeafDecay,
        "core.MixinLeafDecay"),
    common_core_MixinEntityAITask(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinEntityAITask,
        "core.MixinEntityAITask"),
    common_core_MixinMixinEntityUpdate(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate, "core.MixinEntityUpdate"),
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

    common_core_MixinEntityLightningBolt(Side.COMMON,
        m -> MultithreadingandtweaksMultithreadingConfig.enableMixinEntityLightningBolt,
        "core.MixinEntityLightningBolt"),
    common_core_MixinTileEntities(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinTileEntities,
        "core.MixinTileEntities"),
    common_core_MixinParticle(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinParticle,
        "core.MixinParticleManager"),
    common_core_MixinEntities(Side.COMMON, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinEntities,
        "core.MixinEntities"),

    // OPTIMIZATIONS MIXINS

    // TWEAKS MIXINS

    // CLIENT MIXINS
    client_core_MixinGUIHUD(Side.CLIENT, m -> MultithreadingandtweaksMultithreadingConfig.enableMixinGUIHUD,
        "core.MixinGUIHUD"),

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
