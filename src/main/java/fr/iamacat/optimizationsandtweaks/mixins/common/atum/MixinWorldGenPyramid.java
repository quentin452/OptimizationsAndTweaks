package fr.iamacat.optimizationsandtweaks.mixins.common.atum;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.teammetallurgy.atum.blocks.AtumBlocks;
import com.teammetallurgy.atum.blocks.tileentity.chests.TileEntityPharaohChest;
import com.teammetallurgy.atum.items.AtumLoot;
import com.teammetallurgy.atum.world.decorators.WorldGenPyramid;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.mixins.Classers;

@Mixin(WorldGenPyramid.class)
public abstract class MixinWorldGenPyramid extends WorldGenerator {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int chunkX = i >> 4;
        int chunkZ = k >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return false;
        }
        if (random.nextFloat() > 0.3) {
            j -= 8;
        }

        int width = 17;
        int depth = 17;
        boolean[][] maze = new boolean[17][17];
        new ArrayList();
        int zIn = 9;
        maze[0][zIn] = true;
        this.generateMaze(maze, random, 1, zIn);

        int x;
        int dx;
        int dz;
        for (x = -6; x < 10; ++x) {
            for (dx = x; dx <= width - x; ++dx) {
                for (dz = x; dz <= depth - x; ++dz) {
                    Block id = world.getBlock(dx + i, x + j + 3, dz + k);
                    if (id == null || id == AtumBlocks.BLOCK_SAND) {
                        world.setBlockToAir(dx + i, x + j + 3, dz + k);
                    }

                    world.setBlock(dx + i, x + j + 3, dz + k, AtumBlocks.BLOCK_LARGEBRICK);
                }
            }
        }

        for (x = -3; x < width + 3; ++x) {
            for (dx = -3; dx < depth + 3; ++dx) {
                if (x >= 0 && x < width && dx >= 0 && dx < depth) {
                    world.setBlockToAir(x + i, j, dx + k);
                    world.setBlock(x + i, j - 1, dx + k, AtumBlocks.BLOCK_STONE);
                    Block temp;
                    if (!maze[x][dx]) {
                        if (random.nextFloat() > 0.1F) {
                            world.setBlock(x + i, j, dx + k, AtumBlocks.BLOCK_LARGEBRICK);
                            temp = world.getBlock(x + i, j + 1, dx + k);
                            if (temp != null) {
                                temp.setBlockUnbreakable();
                            }
                        } else {
                            this.placeTrap(world, x + i, j, dx + k);
                        }

                        world.setBlock(x + i, j + 1, dx + k, AtumBlocks.BLOCK_LARGEBRICK);
                        temp = world.getBlock(x + i, j + 1, dx + k);
                        if (temp != null) {
                            temp.setBlockUnbreakable();
                        }

                        world.setBlock(x + i, j + 2, dx + k, AtumBlocks.BLOCK_LARGEBRICK);
                        temp = world.getBlock(x + i, j + 2, dx + k);
                        if (temp != null) {
                            temp.setBlockUnbreakable();
                        }
                    } else {
                        dz = random.nextInt(5);
                        world.setBlock(x + i, j, dx + k, AtumBlocks.BLOCK_SANDLAYERED, dz, 0);
                        world.setBlockToAir(x + i, j + 1, dx + k);
                        world.setBlockToAir(x + i, j + 2, dx + k);
                    }

                    world.setBlock(x + i, j + 3, dx + k, AtumBlocks.BLOCK_LARGEBRICK);
                    temp = world.getBlock(x + i, j + 3, dx + k);
                    if (temp != null) {
                        temp.setBlockUnbreakable();
                    }
                }
            }
        }

        world.setBlockToAir(i - 1, j, k + zIn);
        world.setBlockToAir(i - 1, j + 1, k + zIn);
        world.setBlockToAir(i - 2, j, k + zIn);
        world.setBlockToAir(i - 2, j + 1, k + zIn);
        world.setBlockToAir(i - 3, j, k + zIn);
        world.setBlockToAir(i - 3, j + 1, k + zIn);
        world.setBlockToAir(i - 4, j, k + zIn);
        world.setBlockToAir(i - 4, j + 1, k + zIn);

        for (x = 4; x < 8; ++x) {
            for (dx = 6; dx < 12; ++dx) {
                for (dz = 6; dz < 12; ++dz) {
                    world.setBlockToAir(i + dx, j + x, k + dz);
                }
            }
        }

        world.setBlock(i + 11, j + 6, k + 7, Blocks.torch, 2, 0);
        world.setBlock(i + 11, j + 6, k + 10, Blocks.torch, 2, 0);
        world.setBlock(i + 10, j + 4, k + 8, AtumBlocks.BLOCK_PHARAOHCHEST, 0, 2);

        try {
            TileEntityPharaohChest te = (TileEntityPharaohChest) world.getTileEntity(i + 10, j + 4, k + 8);
            AtumLoot.fillChest(te, 15, 0.9F);
        } catch (ClassCastException ignored) {}

        if (world.isAirBlock(i + 7, j + 1, k + 7)) {
            this.placeLadders(world, i + 7, j, k + 7, 4);
        } else {
            boolean found = false;

            for (dx = -1; dx <= 1 && !found; ++dx) {
                for (dz = -1; dz <= 1; ++dz) {
                    if (world.isAirBlock(i + 7 + dx, j + 1, k + 7 + dz)) {
                        this.placeLadders(world, i + 7 + dx, j, k + 7 + dz, 3);
                        found = true;
                        break;
                    }
                }
            }
        }

        return false;
    }

    @Shadow
    public void placeTrap(World world, int x, int y, int z) {
        int meta = 0;
        if (world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH)) {
            meta = 3;
        }

        if (world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH)) {
            meta = 4;
        }

        if (world.isSideSolid(x + 1, y, z, ForgeDirection.WEST)) {
            meta = 5;
        }

        if (world.isSideSolid(x - 1, y, z, ForgeDirection.EAST)) {
            meta = 2;
        }

        world.setBlock(x, y, z, AtumBlocks.BLOCK_TRAPARROW, meta, 0);
    }

    @Shadow
    public void placeLadders(World world, int x, int y, int z, int height) {
        int meta = 0;
        if (world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH)) {
            meta = 2;
        }

        if (world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH)) {
            meta = 3;
        }

        if (world.isSideSolid(x + 1, y, z, ForgeDirection.WEST)) {
            meta = 4;
        }

        if (world.isSideSolid(x - 1, y, z, ForgeDirection.EAST)) {
            meta = 5;
        }

        for (int i = 0; i < height; ++i) {
            world.setBlock(x, y + i, z, Blocks.ladder, meta, 0);
        }

    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generateMaze(boolean[][] array, Random random, int x, int y) {
        ArrayList<Classers.Pair> choices = new ArrayList<>();

        do {
            choices.clear();
            if (x + 2 < 16 && !array[x + 2][y]) {
                choices.add(new Classers.Pair(2, 0));
            }

            if (x - 2 >= 0 && !array[x - 2][y]) {
                choices.add(new Classers.Pair(-2, 0));
            }

            if (y + 2 < 16 && !array[x][y + 2]) {
                choices.add(new Classers.Pair(0, 2));
            }

            if (y - 2 >= 0 && !array[x][y - 2]) {
                choices.add(new Classers.Pair(0, -2));
            }

            if (!choices.isEmpty()) {
                int i = random.nextInt(choices.size());
                Classers.Pair choice = choices.get(i);
                choices.remove(i);
                array[choice.x + x][choice.y + y] = true;
                array[x + choice.x / 2][y + choice.y / 2] = true;
                this.generateMaze(array, random, x + choice.x, y + choice.y);
            }
        } while (!choices.isEmpty());
    }
}
