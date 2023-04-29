package fr.iamacat.multithreading.asm;

import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.*;

import java.util.*;
import java.util.function.Predicate;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Mixin implements IMixin {

    // SERVER MIXINS
    common_core_MixinLeafDecay(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinLeafDecay,
        "core.MixinLeafDecay"),
    common_core_MixinEntityAITask(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityAITask,
        "core.MixinEntityAITask"),
    common_core_MixinMixinEntityUpdate(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityUpdate,
        "core.MixinEntityUpdate"),
    common_core_MixinFireTick(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinFireTick,
        "core.MixinFireTick"),
    common_core_MixinGrowthSpreading(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinGrowthSpreading,
        "core.MixinGrowthSpreading"),
    common_core_MixinLiquidTick(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinliquidTick,
        "core.MixinLiquidTick"),
    common_core_MixinEntitySpawning(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntitySpawning,
        "core.MixinEntitySpawning"),
    common_core_MixinChunkPopulating(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinChunkPopulating,
        "core.MixinEntitySpawning"),

    // CLIENT MIXINS

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
