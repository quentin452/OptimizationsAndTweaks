package fr.iamacat.optimizationsandtweaks.mixins.common.manametalmod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Silences ManaMetalMod's missing-schematic boot/worldgen log spam.
 * <p>
 * {@code project.studio.manametalmod.blueprint.Schematic#loadSchematicFromJar} and
 * {@code #loanIDungeonFromJar} fetch a gzipped-NBT resource from the mod jar and feed the stream
 * straight into {@code CompressedStreamTools} with no null check. ManaMetalMod 7.4.5 ships several
 * registries that request resources it does not actually bundle: for example 8 of the 37
 * {@code InstanceDungeonType} schematics (AncientCity, SkyGarden, WindTemple, ThunderTemple,
 * IllusoryTower, PurgatoryDragonPalace, DistortSpaceTime, SteelFortress) loaded eagerly by
 * {@code BlockInstanceDungeonPortal}'s static initializer, plus assorted village/model names
 * resolved lazily by worldgen. For every missing resource {@code getResourceAsStream} returns
 * {@code null}, {@code CompressedStreamTools} throws a {@code NullPointerException}, and the mod
 * catches it and prints the full stack trace — hundreds of lines of console spam and needless
 * exception churn during the already-slow init.
 * <p>
 * Both loaders already resolve "resource absent" to {@code return null} (that is exactly what the
 * caught NPE produces). This mixin makes that outcome explicit and cheap: when the resource does
 * not exist, return {@code null} up front instead of provoking the exception. The result is
 * identical to the current behaviour — a present schematic still loads normally, and any other
 * read/parse failure is still caught by the mod's own handler — only the NPE construction and its
 * stack-trace print are removed. The resource paths mirror the originals exactly
 * ({@code assets/manametalmod/schematic/<name>} and
 * {@code assets/manametalmod/IDungeon/<name>.IDungeonGZIP}) and {@code getResource} is used so the
 * present-resource path opens no extra stream.
 *
 * @author iamacatfr
 */
@Mixin(targets = "project.studio.manametalmod.blueprint.Schematic", remap = false)
public class MixinSchematic {

    @Inject(method = "loadSchematicFromJar", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$skipMissingJarSchematic(String name, CallbackInfoReturnable<Object> cir) {
        if (this.getClass()
            .getClassLoader()
            .getResource("assets/manametalmod/schematic/" + name) == null) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "loanIDungeonFromJar", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsandtweaks$skipMissingIDungeon(String name, CallbackInfoReturnable<Object> cir) {
        if (this.getClass()
            .getClassLoader()
            .getResource("assets/manametalmod/IDungeon/" + name + ".IDungeonGZIP") == null) {
            cir.setReturnValue(null);
        }
    }
}
