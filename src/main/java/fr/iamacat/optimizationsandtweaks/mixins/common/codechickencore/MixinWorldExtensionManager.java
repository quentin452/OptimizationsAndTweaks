package fr.iamacat.optimizationsandtweaks.mixins.common.codechickencore;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import codechicken.lib.world.WorldExtension;

/**
 * Null-guard for CodeChickenLib's world-exit crash.
 * {@code WorldExtensionManager$WorldExtensionEventHandler.onChunkUnLoad}
 * does {@code worldMap.get(event.world)} then immediately reads the array length, with NO null check — unlike its
 * sibling handlers ({@code onChunkLoad}/{@code onChunkDataSave}, which guard with {@code containsKey}). When a world
 * has
 * no registered extension array (never registered, or removed during an unload race — e.g. the world-exit chunk-drop
 * storm), the lookup returns null and the game crashes with a {@code ReportedException} "Exception ticking world". This
 * is a longstanding upstream bug (ChickenBones CCL #33/#61, never fixed).
 *
 * <p>
 * Fix: redirect the {@code HashMap.get} so a null lookup yields an EMPTY {@code WorldExtension[]} instead of null. A
 * world with no extensions has nothing to unload, so the empty array (length 0, loop skipped) is the correct, lossless
 * behaviour. Trigger-agnostic: it holds whatever left the world unregistered.
 */
@Mixin(targets = "codechicken.lib.world.WorldExtensionManager$WorldExtensionEventHandler", remap = false)
public class MixinWorldExtensionManager {

    private static final WorldExtension[] optimizationsandtweaks$EMPTY = new WorldExtension[0];

    @Redirect(
        method = "onChunkUnLoad",
        at = @At(value = "INVOKE", target = "Ljava/util/HashMap;get(Ljava/lang/Object;)Ljava/lang/Object;"),
        remap = false)
    private Object optimizationsandtweaks$guardNullExtensions(HashMap<?, ?> worldMap, Object world) {
        Object extensions = worldMap.get(world);
        return extensions != null ? extensions : optimizationsandtweaks$EMPTY;
    }
}
