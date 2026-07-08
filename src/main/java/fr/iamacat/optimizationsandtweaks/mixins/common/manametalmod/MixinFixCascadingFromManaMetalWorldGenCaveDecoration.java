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
 * {@code project.studio.manametalmod.world.generate.WorldGenCaveDecoration} is an
 * {@code IWorldGenerator} that, for the chunk being populated, scatters stalagmites/stalactites
 * ({@code gen1}), glowing mushrooms ({@code gen3}) and old urns ({@code gen4}) between y=5 and y=70.
 * Every candidate coordinate is drawn as {@code chunkX*16 + rand.nextInt(16)} /
 * {@code chunkZ*16 + rand.nextInt(16)}, so all reads/writes stay inside the current chunk's 16x16
 * footprint horizontally, and the y+1/y-1 offsets used to place a decoration above/below a stone block
 * stay in the same chunk column. All writes go through the six-argument
 * {@code World.func_147465_d(x,y,z,block,meta,flag)} with {@code flag=2} (send-to-client only, no
 * {@code notifyBlockChange}) — same call already redirected for this mod's {@code EventCave}
 * (see {@code MixinFixCascadingFromManaMetalEventCave}) — but the light update it triggers still drags a
 * neighbour chunk in when the write lands on the chunk edge (dx/dz 0 or 15), forcing it to generate
 * mid-populate.
 * <p>
 * Redirected to a direct chunk-local write ({@code Chunk.func_150807_a}), exactly like the other
 * {@code MixinFixCascading*} fixes in this mod: the block is placed in the current chunk's block storage
 * with no cross-chunk neighbour notify or light propagation, so the decoration is byte-for-byte the same
 * but no neighbour chunk is dragged in. Light for these cave blocks is resolved when the chunk finishes
 * lighting, and the chunk is sent to clients whole (this runs before it is sent), so dropping the
 * gen-time notify/relight changes nothing a player can observe.
 *
 * @author iamacatfr
 */
@Mixin(targets = "project.studio.manametalmod.world.generate.WorldGenCaveDecoration", remap = false)
public class MixinFixCascadingFromManaMetalWorldGenCaveDecoration {

    @Redirect(
        method = { "gen1", "gen3", "gen4" },
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
