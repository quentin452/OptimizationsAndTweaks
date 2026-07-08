package fr.iamacat.optimizationsandtweaks.mixins.common.manametalmod;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes cascading worldgen caused by ManaMetalMod's surface rock decoration.
 * <p>
 * {@code project.studio.manametalmod.world.generate.WorldGenRock} is registered as an
 * {@code IWorldGenerator} and, for the chunk being populated, rolls up to 64 candidate columns whose
 * x/z are derived as {@code chunkX*16 + rand.nextInt(16)} / {@code chunkZ*16 + rand.nextInt(16)} — i.e.
 * strictly inside the populating chunk's own 16x16 footprint (this is a single-block-per-roll
 * decoration, not a spilling blob/radius shape, so there is no legitimate cross-chunk content to
 * preserve). All reads ({@code World.getBlock}, {@code func_147439_a}) and the placement itself stay
 * inside that footprint, but the placement uses the three-argument
 * {@code World.setBlock(x,y,z,block)} ({@code func_147449_b}), which forwards to
 * {@code setBlock(...,3)}: flag&1 fires {@code notifyBlockChange}. On a roll that lands at the chunk
 * edge (dx/dz 0 or 15) that neighbour notify reaches into an adjacent, possibly not-yet-generated
 * chunk and forces it to generate mid-populate — a cascade.
 * <p>
 * Redirected to a direct chunk-local write ({@code Chunk.func_150807_a}), exactly like the other
 * {@code MixinFixCascading*} fixes in this mod: the rock is placed in the current chunk's block storage
 * with no cross-chunk neighbour notify or light propagation, so the decoration is byte-for-byte the
 * same but no neighbour chunk is dragged in. Light for these blocks is resolved when the chunk finishes
 * lighting, and the chunk is sent to clients whole (this runs before it is sent), so dropping the
 * gen-time notify/relight changes nothing a player can observe.
 *
 * @author iamacatfr
 */
@Mixin(targets = "project.studio.manametalmod.world.generate.WorldGenRock", remap = false)
public class MixinFixCascadingFromManaMetalWorldGenRock {

    @Redirect(
        method = "generateSurface",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_147449_b(IIILnet/minecraft/block/Block;)Z"),
        remap = false)
    private boolean optimizationsAndTweaks$setBlockLocal(World world, int x, int y, int z, Block block) {
        return optimizationsAndTweaks$chunkLocalSet(world, x, y, z, block);
    }

    /**
     * Write a block straight into its (already-loaded, currently-populating) chunk, bypassing the
     * neighbour notify and cross-chunk light propagation that {@code World.setBlock} would trigger.
     */
    @Unique
    private static boolean optimizationsAndTweaks$chunkLocalSet(World world, int x, int y, int z, Block block) {
        if (y < 0 || y >= 256) {
            return false;
        }
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        return chunk.func_150807_a(x & 15, y, z & 15, block, 0);
    }
}
