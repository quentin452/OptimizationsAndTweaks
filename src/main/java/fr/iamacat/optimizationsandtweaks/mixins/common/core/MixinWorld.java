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
    public Block getBlock(int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        if (p_147439_1_ >= -30000000 && p_147439_3_ >= -30000000 && p_147439_1_ < 30000000 && p_147439_3_ < 30000000 && p_147439_2_ >= 0 && p_147439_2_ < 256) {
            try {
                Chunk chunk = this.getChunkFromChunkCoords(p_147439_1_ >> 4, p_147439_3_ >> 4);
                if (chunk != null) {
                    Block block = chunk.getBlock(p_147439_1_ & 15, p_147439_2_, p_147439_3_ & 15);
                    if (block != null) {
                        return block;
                    } else {
                        Block blockAtCoordinates = chunk.getBlock(p_147439_1_ & 15, p_147439_2_, p_147439_3_ & 15);
                        OptimizationsLogger.LOGGER.error("Block is null at coordinates - x: {}, y: {}, z: {}", p_147439_1_, p_147439_2_, p_147439_3_);
                        OptimizationsLogger.LOGGER.error("Block type: {}", blockAtCoordinates.getUnlocalizedName());
                        OptimizationsLogger.LOGGER.error("Chunk: {}", chunk);
                    }
                } else {
                    OptimizationsLogger.LOGGER.error("Chunk is null at coordinates - x: {}, z: {}", p_147439_1_ >> 4, p_147439_3_ >> 4);
                }
                return Blocks.air;
            } catch (Throwable throwable) {
                OptimizationsLogger.LOGGER.error("Exception getting block type in world at coordinates - x: {}, y: {}, z: {}", p_147439_1_, p_147439_2_, p_147439_3_);
               OptimizationsLogger.LOGGER.error("Exception details: ", throwable);
            }
        } else {
            return Blocks.air;
        }
        return null;
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
