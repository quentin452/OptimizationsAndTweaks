package fr.iamacat.optimizationsandtweaks.mixins.common.biomesoplenty;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import biomesoplenty.api.biome.BOPBiomeDecorator;

/**
 * Removes the {@code Double.parseDouble(number.toString())} string round-trip in BoP's weighted grass/flower picker.
 * <p>
 * {@code BOPBiomeDecorator#getRandomWeightedWorldGenerator} converts every map weight with
 * {@code Double.parseDouble(number.toString())} — a String allocation plus a full decimal parse — once per entry in
 * the accumulate loop and once per entry in the select loop. The weights are boxed {@code Double} (grass) or
 * {@code Integer} (flower); for both types {@code Number#doubleValue()} returns exactly the same {@code double} as
 * the round-trip (the {@code toString}/{@code parseDouble} round-trip is lossless for these), so this is
 * behaviour-preserving and generates identical worlds.
 * <p>
 * The two sliced {@link WrapOperation}s replace each {@code parseDouble} call with {@code doubleValue()},
 * recovering the {@link Number} from the accumulate loop's {@code Number} local and, in the select loop, from the
 * {@link Map.Entry} local (its value is the same {@code Number}). The call sites are separated with {@link Slice}s
 * anchored on the stable {@code HashMap.values}/{@code Math.random}/{@code HashMap.entrySet} landmarks. A third
 * {@link WrapOperation} then skips the paired {@code Number#toString()} calls: once the {@code parseDouble} that
 * consumed each String is bypassed, the String is never read, so building it is pure waste. Together they turn the
 * whole {@code Double.parseDouble(number.toString())} expression into a direct {@code number.doubleValue()}.
 * <p>
 * BoP is a compile-only dependency (the pack provides it at runtime); every injected member is JDK, so the
 * injectors use {@code remap = false}.
 *
 * @author OptimizationsAndTweaks
 * @reason Behaviour-preserving worldgen speedup: collapse BoP's per-weight {@code double -> String -> double}
 *         round-trip to a direct {@code Number#doubleValue()} in the hot weighted-generator picker.
 */
@Mixin(BOPBiomeDecorator.class)
public class MixinBOPBiomeDecorator {

    @WrapOperation(
        method = "getRandomWeightedWorldGenerator",
        at = @At(value = "INVOKE", target = "Ljava/lang/Double;parseDouble(Ljava/lang/String;)D"),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Ljava/util/HashMap;values()Ljava/util/Collection;"),
            to = @At(value = "INVOKE", target = "Ljava/lang/Math;random()D")),
        remap = false)
    private static double optimizationsandtweaks$accumulateWeight(String unused, Operation<Double> original,
        @Local Number weight) {
        return weight.doubleValue();
    }

    @WrapOperation(
        method = "getRandomWeightedWorldGenerator",
        at = @At(value = "INVOKE", target = "Ljava/lang/Double;parseDouble(Ljava/lang/String;)D"),
        slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/util/HashMap;entrySet()Ljava/util/Set;")),
        remap = false)
    private static double optimizationsandtweaks$selectWeight(String unused, Operation<Double> original,
        @Local Map.Entry entry) {
        return ((Number) entry.getValue()).doubleValue();
    }

    /**
     * Skips both weight {@code toString()} calls: their results feed only the {@code parseDouble}s bypassed above,
     * so they are dead work. Returning {@code ""} is safe -- it is never parsed; if a future BoP change left a
     * {@code parseDouble} un-bypassed the mixin would fail to apply (loud) rather than parse {@code ""} silently.
     */
    @WrapOperation(
        method = "getRandomWeightedWorldGenerator",
        at = @At(value = "INVOKE", target = "Ljava/lang/Object;toString()Ljava/lang/String;"),
        remap = false)
    private static String optimizationsandtweaks$skipWeightToString(Object weight, Operation<String> original) {
        return "";
    }
}
