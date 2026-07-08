package fr.iamacat.optimizationsandtweaks.mixins.common.manametalmod;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes cascading worldgen caused by ManaMetalMod's cave decoration.
 * <p>
 * {@code project.studio.manametalmod.cave.EventCave} listens on {@code PopulateChunkEvent.Post} and,
 * for the chunk being populated, sprinkles cave plants and stalactites between y=32 and y=60 (methods
 * {@code caveSand}, {@code cavePlanis}, {@code caveIce}, {@code caveForest}). All of its reads and
 * writes stay inside the current chunk's 16x16 footprint, so the accesses themselves never leave the
 * chunk — but the writes do not: the three-argument {@code World.setBlock(x,y,z,block)}
 * ({@code func_147449_b}) forwards to {@code setBlock(...,3)}, whose flag&1 fires
 * {@code notifyBlockChange}. On a block at the chunk edge (dx/dz 0 or 15) that neighbour notify reaches
 * into an adjacent, not-yet-generated chunk and forces it to generate mid-populate — a cascade. The
 * six-argument call ({@code func_147465_d(...,2)}) leaves a smaller residual through the light update.
 * <p>
 * Both are redirected to a direct chunk-local write ({@code Chunk.func_150807_a}), exactly like the
 * other {@code MixinFixCascading*} fixes in this mod: the block is placed in the current chunk's block
 * storage with no cross-chunk neighbour notify or light propagation, so the decoration is byte-for-byte
 * the same but no neighbour chunk is dragged in. Light for these cave blocks is resolved when the chunk
 * finishes lighting, and the chunk is sent to clients whole (this runs before it is sent), so dropping
 * the gen-time notify/relight changes nothing a player can observe.
 *
 * @author iamacatfr
 */
@Mixin(targets = "project.studio.manametalmod.cave.EventCave", remap = false)
public class MixinFixCascadingFromManaMetalEventCave {

    @Redirect(
        method = { "caveSand", "cavePlanis", "caveIce", "caveForest" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;func_147449_b(IIILnet/minecraft/block/Block;)Z"),
        remap = false)
    private boolean optimizationsAndTweaks$setBlockLocal(World world, int x, int y, int z, Block block) {
        return optimizationsAndTweaks$chunkLocalSet(world, x, y, z, block, 0);
    }

    @Redirect(
        method = { "caveSand", "cavePlanis", "caveIce", "caveForest" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;func_147465_d(IIILnet/minecraft/block/Block;II)Z"),
        remap = false)
    private boolean optimizationsAndTweaks$setBlockMetaLocal(World world, int x, int y, int z, Block block, int meta,
        int flag) {
        return optimizationsAndTweaks$chunkLocalSet(world, x, y, z, block, meta);
    }

    /**
     * Write a block straight into its (already-loaded, currently-populating) chunk, bypassing the
     * neighbour notify and cross-chunk light propagation that {@code World.setBlock} would trigger.
     */
    @Unique
    private static boolean optimizationsAndTweaks$chunkLocalSet(World world, int x, int y, int z, Block block,
        int meta) {
        if (y < 0 || y >= 256) {
            return false;
        }
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        return chunk.func_150807_a(x & 15, y, z & 15, block, meta);
    }
}
