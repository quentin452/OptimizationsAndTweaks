package fr.iamacat.optimizationsandtweaks.mixins.common.manametalmod;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes cascading worldgen caused by ManaMetalMod's dungeon door structure generator.
 * <p>
 * {@code project.studio.manametalmod.world.generate.WorldGenDungeonDoor} is registered as an
 * {@code IWorldGenerator} and, per populating chunk, rolls one candidate surface column strictly inside
 * the chunk (dx/dz + {@code rand.nextInt(15)}). Unlike the other {@code MixinFixCascadingFromManaMetal*}
 * fixes in this package, this generator is NOT a single in-chunk block decoration: on a successful roll
 * ({@code trySpawn}) it (1) calls {@code getAverageHeight}, which samples a 25x25 column grid
 * ({@code World.func_72976_f}, the heightmap read) centred {@code +12/+12} blocks off the roll position —
 * i.e. up to ~24 blocks away on each axis — and (2), if that succeeds, stamps a schematic
 * ({@code base}/{@code four_times}) whose blocks ({@code World.func_147465_d}, 16 call sites across
 * {@code base}, {@code four_times} and {@code setBlockFromStyle}) and matching dungeon-portal tile entity
 * ({@code World.func_147455_a}) are written at the schematic's own absolute coordinates, offset from the
 * roll position by the schematic's width/length — a fixed 32-block clear cube
 * ({@code AreaClear.clearArea(..., 32)}) is carved first, so the structure routinely reaches into
 * neighbour chunks by design (a dungeon door/temple is tens of blocks wide). This is the #1 per-mod
 * {@code IWorldGenerator} time offender in the pack (~6.8 ms/chunk): every one of those far reads/writes
 * that lands on a chunk which has not generated yet goes through
 * {@code World.getChunkFromChunkCoords}/{@code getBlock}, which forces that neighbour to generate (and
 * populate) as a nested call mid-populate of the current chunk — the cascade.
 * <p>
 * This is archetype B (a structure that deliberately spans multiple chunks), not archetype A: the
 * chunk-local {@code Chunk.func_150807_a} redirect used by the sibling fixes in this package would
 * silently relocate any out-of-chunk coordinate into the WRONG chunk (position corruption of the
 * schematic) instead of skipping it, so that approach is not used here. Instead, exactly like
 * {@code themistsofriov.MixinWorldGenBalance}, every far-reaching call is redirected to first check
 * whether its target chunk already exists ({@code World.getChunkProvider().chunkExists}) and, if not, to
 * skip it without loading anything:
 * <ul>
 * <li>{@code func_72976_f} (heightmap read) inside {@code getAverageHeight}: returns
 * {@code Integer.MIN_VALUE} for an unloaded column instead of reading it. That sentinel always fails the
 * existing {@code height > minY && height < maxY} band filter, so unloaded columns are simply excluded
 * from the average exactly like out-of-band ones already are; if the centre column itself is unloaded,
 * every comparison in the loop fails, {@code count} stays 0 and the method returns -1, which
 * {@code trySpawn} already treats as "do not spawn here" — a clean, side-effect-free abort.</li>
 * <li>{@code func_147465_d} (the block+meta+flag setter used by all 16 call sites): skipped (returns
 * {@code false}, matching the vanilla no-op-on-failure contract) for any coordinate whose chunk is not
 * loaded yet.</li>
 * <li>{@code func_147455_a} (dungeon-portal tile entity placement, 2 call sites): skipped for the same
 * reason, 1:1 with the block write it always follows — no tile entity is ever attached to a block that
 * was never placed.</li>
 * </ul>
 * Content-safety: any coordinate inside an already-loaded chunk is written byte-for-byte identically to
 * vanilla (same absolute coordinate, same block/meta/flag/tile entity, no behaviour change at all) — only
 * the rare case where this structure's own footprint pokes into a chunk that genuinely has not generated
 * yet is affected, and there it degrades to "that sliver of the structure (or height sample) is skipped"
 * instead of forcing a nested populate. That is the same trade-off already accepted and shipped in
 * {@code MixinWorldGenBalance} for RioV's ore generator.
 * <p>
 * Known scope gap: {@code AreaClear.clearArea(...)}, called by {@code base}/{@code four_times} right
 * before the schematic loop, is a separate ManaMetalMod class ({@code project.studio.manametalmod.core.
 * AreaClear}) not covered by this mixin — its own block-clearing writes are out of scope here and may
 * still contribute a residual cascade; candidate for a follow-up mixin on that class if it shows up in
 * further profiling.
 *
 * @author iamacatfr
 */
@Mixin(targets = "project.studio.manametalmod.world.generate.WorldGenDungeonDoor", remap = false)
public class MixinFixCascadingFromManaMetalWorldGenDungeonDoor {

    @Redirect(
        method = "getAverageHeight",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_72976_f(II)I"),
        remap = false)
    private static int optimizationsAndTweaks$getHeightGuarded(World world, int x, int z) {
        if (!optimizationsAndTweaks$chunkLoaded(world, x, z)) {
            return Integer.MIN_VALUE;
        }
        return world.getHeightValue(x, z);
    }

    @Redirect(
        method = { "base", "four_times", "setBlockFromStyle" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;func_147465_d(IIILnet/minecraft/block/Block;II)Z"),
        remap = false)
    private static boolean optimizationsAndTweaks$setBlockGuarded(World world, int x, int y, int z, Block block,
        int meta, int flag) {
        if (!optimizationsAndTweaks$chunkLoaded(world, x, z)) {
            return false;
        }
        return world.setBlock(x, y, z, block, meta, flag);
    }

    @Redirect(
        method = { "base", "four_times" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;func_147455_a(IIILnet/minecraft/tileentity/TileEntity;)V"),
        remap = false)
    private static void optimizationsAndTweaks$setTileEntityGuarded(World world, int x, int y, int z,
        TileEntity tile) {
        if (!optimizationsAndTweaks$chunkLoaded(world, x, z)) {
            return;
        }
        world.setTileEntity(x, y, z, tile);
    }

    /**
     * Whether the chunk containing (x, z) already exists, i.e. whether reading/writing it is safe
     * without forcing it to generate.
     */
    @Unique
    private static boolean optimizationsAndTweaks$chunkLoaded(World world, int x, int z) {
        return world.getChunkProvider()
            .chunkExists(x >> 4, z >> 4);
    }
}
