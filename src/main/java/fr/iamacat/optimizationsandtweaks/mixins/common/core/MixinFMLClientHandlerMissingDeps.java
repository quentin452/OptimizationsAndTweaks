package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.MissingModsException;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.fml.MissingModsAggregate;

/**
 * Makes the missing-dependency screen list EVERY mod with an unsatisfied requirement instead of
 * only the first one.
 * <p>
 * Vanilla FML aborts {@code Loader.sortModList()} on the first failing mod and stores that single
 * {@code MissingModsException} in {@code FMLClientHandler.modsMissing}; {@code onInitializationComplete}
 * then shows {@code GuiModsMissing} for that one mod. {@code Loader} itself is loaded far too early to
 * be reachable by a mixin (it triggers {@code MixinTargetAlreadyLoadedException}), so instead this
 * mixin hooks {@code FMLClientHandler.onInitializationComplete} - which is loaded late and already a
 * mixin target for OaT - and, when a missing-mods failure is pending, rescans the full active mod list
 * ({@link Loader#getActiveModList()} still holds every active mod, since the failed sort never cleared
 * it) to build an aggregated exception covering all of them. The per-mod breakdown is recorded in
 * {@link MissingModsAggregate} for the {@code GuiModsMissing} mixin to render (grouped + paginated),
 * and {@code modsMissing} is replaced with the aggregate so even the unmodified screen would list them
 * all.
 */
@Mixin(FMLClientHandler.class)
public abstract class MixinFMLClientHandlerMissingDeps {

    @Shadow(remap = false)
    private MissingModsException modsMissing;

    @Inject(method = "onInitializationComplete", at = @At("HEAD"), remap = false)
    private void optimizationsandtweaks$aggregateMissingMods(CallbackInfo ci) {
        if (this.modsMissing == null) {
            return;
        }
        MissingModsException aggregated = MissingModsAggregate.buildFrom(
            Loader.instance()
                .getActiveModList());
        if (aggregated != null) {
            this.modsMissing = aggregated;
        }
    }
}
