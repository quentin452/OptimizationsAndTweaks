package fr.iamacat.optimizationsandtweaks.mixins.common.atum;

import com.teammetallurgy.atum.blocks.AtumBlocks;
import com.teammetallurgy.atum.items.AtumLoot;
import com.teammetallurgy.atum.world.decorators.WorldGenRuins;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(WorldGenRuins.class)
public abstract class MixinWorldGenRuins extends WorldGenerator {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int chunkX = i >> 4;
        int chunkZ = k >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if(!chunk.isChunkLoaded) {
            return false;
        }
        int width = random.nextInt(4) + 6;
        int depth = random.nextInt(2) + 5;
        int height = world.getHeightValue(i, k);
        int x2;
        int z2;
        if (world.getHeightValue(i + width, k + depth) >= height) {
            x2 = i + width;
            z2 = k + depth;
        } else if (world.getHeightValue(i - width, k + depth) >= height) {
            x2 = i - width;
            z2 = k + depth;
        } else if (world.getHeightValue(i + width, k - depth) >= height) {
            x2 = i + width;
            z2 = k - depth;
        } else {
            if (world.getHeightValue(i - width, k - depth) < height) {
                return false;
            }

            x2 = i - width;
            z2 = k - depth;
        }

        int chestX;
        int chestZ;
        int chestY;
        for(chestX = Math.min(x2, i); chestX <= Math.max(x2, i); ++chestX) {
            for(chestZ = Math.min(z2, k); chestZ <= Math.max(z2, k); ++chestZ) {
                int meta = random.nextInt(4);

                for(chestY = -1; chestY < 15; ++chestY) {
                    if (chestX != x2 && chestZ != z2 && chestX != i && chestZ != k && chestY != -1) {
                        world.setBlockToAir(chestX, chestY + height, chestZ);
                    } else if (chestY < meta) {
                        if ((double)random.nextFloat() > 0.1) {
                            world.setBlock(chestX, chestY + height, chestZ, AtumBlocks.BLOCK_LARGEBRICK);
                        } else {
                            world.setBlock(chestX, chestY + height, chestZ, AtumBlocks.BLOCK_SMALLBRICK);
                        }
                    } else if (chestY == meta && (double)random.nextFloat() > 0.7) {
                        if ((double)random.nextFloat() > 0.1) {
                            world.setBlock(chestX, chestY + height, chestZ, AtumBlocks.BLOCK_SLABS, 2, 0);
                        } else {
                            world.setBlock(chestX, chestY + height, chestZ, AtumBlocks.BLOCK_SLABS, 3, 0);
                        }
                    }
                }
            }
        }

        chestX = width / 2 + i;
        chestZ = Math.max(z2, k) - 1;
        boolean var16 = false;
        if ((double)random.nextFloat() > 0.5) {
            chestX = random.nextInt(width - 1) + 1 + Math.min(i, x2);
            if ((double)random.nextFloat() > 0.5) {
                chestZ = Math.max(z2, k) - 1;
                var16 = true;
            } else {
                chestZ = Math.min(z2, k) + 1;
                var16 = true;
            }
        } else {
            chestZ = random.nextInt(depth - 1) + 1 + Math.min(k, z2);
            if ((double)random.nextFloat() > 0.5) {
                chestX = Math.max(x2, i) - 1;
                var16 = true;
            } else {
                chestX = Math.min(x2, i) + 1;
                var16 = true;
            }
        }

        chestY = world.getHeightValue(chestX, chestZ);
        world.setBlock(chestX, chestY, chestZ, AtumBlocks.BLOCK_CURSEDCHEST, 0, 2);
        IInventory chest = (IInventory)world.getTileEntity(chestX, chestY, chestZ);
        AtumLoot.fillChest(chest, 5, 0.5F);
        return false;
    }
}
