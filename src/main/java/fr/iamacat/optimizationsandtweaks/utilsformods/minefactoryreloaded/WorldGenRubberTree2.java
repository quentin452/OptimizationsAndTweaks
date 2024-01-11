package fr.iamacat.optimizationsandtweaks.utilsformods.minefactoryreloaded;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenRubberTree2 {
    public void generateLeaves(World world, Random rand, int x, int y, int z, int treeHeight) {
        List<ChunkCoordinates> leavesToPlace = new ArrayList<>();
        Chunk chunk = world.getChunkFromBlockCoords(x, z);

        for (int yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
            int var12 = yOffset - (y + treeHeight);
            int center = 1 - var12 / 2;
            for (int xOffset = x - center; xOffset <= x + center; ++xOffset) {
                for (int zOffset = z - center; zOffset <= z + center; ++zOffset) {
                    int xPos = xOffset - x;
                    int zPos = zOffset - z;
                    if ((xPos != center || zPos != center || rand.nextInt(2) != 0 && var12 != 0) && canPlaceLeaf(rand) && chunk.isChunkLoaded) {
                        leavesToPlace.add(new ChunkCoordinates(xOffset, yOffset, zOffset));
                    }
                }
            }
        }
        placeLeaves(world, leavesToPlace);
    }

    private void placeLeaves(World world, List<ChunkCoordinates> leavesToPlace) {
        if (leavesToPlace.isEmpty()) {
            return;
        }

        List<ChunkCoordinates> validLeavesToPlace = new ArrayList<>();
        for (ChunkCoordinates pos : leavesToPlace) {
            if (world.blockExists(pos.posX, pos.posY, pos.posZ) && world.getBlock(pos.posX, pos.posY, pos.posZ).isAir(world, pos.posX, pos.posY, pos.posZ) && world.getChunkProvider().chunkExists(pos.posX >> 4, pos.posZ >> 4)) {
                validLeavesToPlace.add(pos);
            }
        }

        if (!validLeavesToPlace.isEmpty()) {
            for (ChunkCoordinates pos : validLeavesToPlace) {
                world.setBlock(pos.posX, pos.posY, pos.posZ, MFRThings.rubberLeavesBlock,0,2);
            }
        }
    }

    private boolean canPlaceLeaf(Random rand) {
        return rand.nextFloat() < 0.9f;
    }
}
