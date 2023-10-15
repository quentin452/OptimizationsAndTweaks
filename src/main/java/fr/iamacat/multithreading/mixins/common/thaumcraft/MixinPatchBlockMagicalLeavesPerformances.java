package fr.iamacat.multithreading.mixins.common.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.common.blocks.BlockMagicalLeaves;

import java.util.Random;

@Mixin(BlockMagicalLeaves.class)
public class MixinPatchBlockMagicalLeavesPerformances {
    @Unique
    int[] multithreadingandtweaks$adjacentTreeBlocks;
    /**
     * @author imacatfr
     * @reason fix tps lags caused by updateTick method from BlockMagicalLeaves class
     */

    @Overwrite
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            int metadata = world.getBlockMetadata(x, y, z);
            if ((metadata & 8) != 0 && (metadata & 4) == 0) {
                int radius = 6;
                int diameter = radius * 2 + 1;
                int center = diameter / 2;
                int volume = diameter * diameter * diameter;

                if (this.multithreadingandtweaks$adjacentTreeBlocks == null) {
                    this.multithreadingandtweaks$adjacentTreeBlocks = new int[volume];
                }

                int offX, offY, offZ;
                for (offX = -radius; offX <= radius; ++offX) {
                    for (offY = -radius; offY <= radius; ++offY) {
                        for (offZ = -radius; offZ <= radius; ++offZ) {
                            Block block = world.getBlock(x + offX, y + offY, z + offZ);
                            if (block != null && block.canSustainLeaves(world, x + offX, y + offY, z + offZ)) {
                                this.multithreadingandtweaks$adjacentTreeBlocks[(offX + center) * diameter * diameter + (offY + center) * diameter + (offZ + center)] = 0;
                            } else if (block != null && block.isLeaves(world, x + offX, y + offY, z + offZ)) {
                                this.multithreadingandtweaks$adjacentTreeBlocks[(offX + center) * diameter * diameter + (offY + center) * diameter + (offZ + center)] = -2;
                            } else {
                                this.multithreadingandtweaks$adjacentTreeBlocks[(offX + center) * diameter * diameter + (offY + center) * diameter + (offZ + center)] = -1;
                            }
                        }
                    }
                }

                int centerBlockValue = this.multithreadingandtweaks$adjacentTreeBlocks[center * diameter * diameter + center * diameter + center];

                int delay = 20;

                if (centerBlockValue >= 0 && world.getTotalWorldTime() % delay == 0) {
                    world.setBlockMetadataWithNotify(x, y, z, metadata & -9, 3);
                } else if (centerBlockValue < 0) {
                    this.multithreadingandtweaks$removeLeaves(world, x, y, z);
                }
            }
        }
    }

    @Unique
    public void multithreadingandtweaks$removeLeaves(World world, int x, int y, int z) {
        world.setBlockToAir(x, y, z);
    }
}
