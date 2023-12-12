package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import fr.iamacat.optimizationsandtweaks.OptimizationsLogger;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public class MixinWorld {
    @Shadow
    protected IChunkProvider chunkProvider;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Block getBlock(int x, int y, int z) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 && y >= 0 && y < 256) {
            try {
                Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                if (chunk != null) {
                    Block block = chunk.getBlock(x & 15, y, z & 15);
                    if (block != null) {
                        return block;
                    } else {
                        Block blockAtCoordinates = chunk.getBlock(x & 15, y, z & 15);
                        System.out.println("Block is null at coordinates - x: " + x + ", y: " + y + ", z: " + z);
                        System.out.println("Block type: " + (blockAtCoordinates != null ? blockAtCoordinates.getUnlocalizedName() : "Unknown"));
                        System.out.println("Chunk: " + chunk);
                    }
                } else {
                    System.out.println("Chunk is null at coordinates - x: " + (x >> 4) + ", z: " + (z >> 4));
                }
                return Blocks.air;
            } catch (Throwable throwable) {
                System.out.println("Exception getting block type in world at coordinates - x: " + x + ", y: " + y + ", z: " + z);
                System.out.println("Exception details: " + throwable);
            }
        } else {
            System.out.println("Coordinates out of bounds - x: " + x + ", y: " + y + ", z: " + z);
        }
        return Blocks.air;
    }

    /**
     * Returns back a chunk looked up by chunk coordinates Args: x, y
     */
    @Shadow
    public Chunk getChunkFromChunkCoords(int p_72964_1_, int p_72964_2_)
    {
        return this.chunkProvider.provideChunk(p_72964_1_, p_72964_2_);
    }
}
