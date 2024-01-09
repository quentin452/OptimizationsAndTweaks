package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.minefactoryreloaded;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenRubberTree2 {
    public void generateLeaves(World world, Random rand, int x, int y, int z, int treeHeight) {
        List<ChunkCoordinates> leavesToPlace = new ArrayList<>();
        for (int yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
            int var12 = yOffset - (y + treeHeight);
            int center = 1 - var12 / 2;
            for (int xOffset = x - center; xOffset <= x + center; ++xOffset) {
                for (int zOffset = z - center; zOffset <= z + center; ++zOffset) {
                    int xPos = xOffset - x;
                    int zPos = zOffset - z;
                    if ((xPos != center || zPos != center || rand.nextInt(2) != 0 && var12 != 0) && (canPlaceLeaf(rand))) {
                        leavesToPlace.add(new ChunkCoordinates(xOffset, yOffset, zOffset));
                    }
                }
            }
        }
        placeLeaves(world, leavesToPlace);
    }

    private void placeLeaves(World world, List<ChunkCoordinates> leavesToPlace) {
        for (ChunkCoordinates pos : leavesToPlace) {
            world.setBlock(pos.posX, pos.posY, pos.posZ, MFRThings.rubberLeavesBlock);
            notifyBlockUpdate(world, pos.posX, pos.posY, pos.posZ, 0);
        }
    }

    private boolean canPlaceLeaf(Random rand) {
        return rand.nextFloat() < 0.9f;
    }

    private void notifyBlockUpdate(World world, int x, int y, int z, int meta) {
        world.markBlockForUpdate(x, y, z);
        if (!world.isRemote) {
            world.notifyBlockChange(x, y, z, Block.getBlockById(meta));
        }
    }
}
