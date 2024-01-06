package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.WorldGenEldritchRing;
import thaumcraft.common.lib.world.dim.MazeHandler;
import thaumcraft.common.tiles.TileBanner;
import thaumcraft.common.tiles.TileEldritchAltar;

@Mixin(WorldGenEldritchRing.class)
public abstract class MixinWorldGenEldritchRing extends WorldGenerator {

    @Shadow
    public int chunkX;
    @Shadow
    public int chunkZ;
    @Shadow
    public int width;
    @Shadow
    public int height = 0;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected Block[] GetValidSpawnBlocks() {
        return new Block[] { Blocks.stone, Blocks.sand, Blocks.packed_ice, Blocks.grass, Blocks.gravel, Blocks.dirt };
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean LocationIsValidSpawn(World world, int i, int j, int k) {
        int distanceToAir = 0;

        while (distanceToAir <= 2) {
            Block checkID = world.getBlock(i, j + distanceToAir, k);

            if (checkID == Blocks.air) {
                break;
            }

            ++distanceToAir;
        }

        if (distanceToAir <= 2) {
            j += distanceToAir - 1;
            Block blockID = world.getBlock(i, j, k);
            Block blockIDAbove = world.getBlock(i, j + 1, k);
            Block blockIDBelow = world.getBlock(i, j - 1, k);
            Block[] validSpawnBlocks = this.GetValidSpawnBlocks();

            for (Block x : validSpawnBlocks) {
                if (blockIDAbove != Blocks.air) {
                    return false;
                }

                if (blockID == x
                    || ((blockID == Blocks.snow_layer || blockID == Blocks.tallgrass) && blockIDBelow == x)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean generate(World world, Random rand, int i, int j, int k) {
        if (this.LocationIsValidSpawn(world, i - 3, j, k - 3) && this.LocationIsValidSpawn(world, i, j, k)
            && this.LocationIsValidSpawn(world, i + 3, j, k)
            && this.LocationIsValidSpawn(world, i + 3, j, k + 3)
            && this.LocationIsValidSpawn(world, i, j, k + 3)
            && !MazeHandler.mazesInRange(this.chunkX, this.chunkZ, this.width, this.height)) {
            Block replaceBlock = world.getBiomeGenForCoords(i, k).topBlock;

            for (int x = i - 3; x <= i + 3; ++x) {
                for (int z = k - 3; z <= k + 3; ++z) {
                    if (x != i - 3 && x != i + 3 || z != k - 3 && z != k + 3) {
                        int r;
                        for (r = -4; r < 5; ++r) {
                            Block bb = world.getBlock(x, j + r, z);
                            if (r <= 0 || bb.isReplaceable(world, x, j + r, z)
                                || !bb.getMaterial()
                                    .blocksMovement()
                                || bb.isFoliage(world, x, j + r, z)) {
                                if (rand.nextInt(4) == 0) {
                                    world.setBlock(x, j + r, z, Blocks.obsidian);
                                } else {
                                    world.setBlock(x, j + r, z, ConfigBlocks.blockCosmeticSolid, 1, 3);
                                }
                            }

                            if (r > 0) {
                                world.setBlockToAir(x, j + r, z);
                            }
                        }

                        if (x == i && z == k) {
                            world.setBlock(x, j + 1, z, ConfigBlocks.blockEldritch, 0, 3);
                            world.setBlock(x, j, z, ConfigBlocks.blockCosmeticSolid, 1, 3);
                            r = rand.nextInt(10);
                            TileEntity te = world.getTileEntity(x, j + 1, z);
                            if (te != null && te instanceof TileEldritchAltar) {
                                switch (r) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                        ((TileEldritchAltar) te).setSpawner(true);
                                        ((TileEldritchAltar) te).setSpawnType((byte) 0);

                                        for (int a = 2; a < 6; ++a) {
                                            ForgeDirection dir = ForgeDirection.getOrientation(a);
                                            world.setBlock(
                                                x - dir.offsetX * 3,
                                                j + 1,
                                                z + dir.offsetZ * 3,
                                                ConfigBlocks.blockWoodenDevice,
                                                8,
                                                3);
                                            te = world.getTileEntity(x - dir.offsetX * 3, j + 1, z + dir.offsetZ * 3);
                                            if (te != null && te instanceof TileBanner) {
                                                int face = 0;
                                                switch (a) {
                                                    case 2:
                                                        face = 8;
                                                        break;
                                                    case 3:
                                                        face = 0;
                                                        break;
                                                    case 4:
                                                        face = 12;
                                                        break;
                                                    case 5:
                                                        face = 4;
                                                }

                                                ((TileBanner) te).setFacing((byte) face);
                                            }
                                        }
                                    case 5:
                                    default:
                                        break;
                                    case 6:
                                    case 7:
                                        ((TileEldritchAltar) te).setSpawner(true);
                                        ((TileEldritchAltar) te).setSpawnType((byte) 1);
                                }
                            }

                            world.setBlock(x, j + 3, z, ConfigBlocks.blockEldritch, 1, 3);
                            world.setBlock(x, j + 4, z, ConfigBlocks.blockEldritch, 2, 3);
                            world.setBlock(x, j + 5, z, ConfigBlocks.blockEldritch, 2, 3);
                            world.setBlock(x, j + 6, z, ConfigBlocks.blockEldritch, 2, 3);
                            world.setBlock(x, j + 7, z, ConfigBlocks.blockEldritch, 2, 3);
                        } else if (((x == i - 3 || x == i + 3) && Math.abs((z - k) % 2) == 1
                            || (z == k - 3 || z == k + 3) && Math.abs((x - i) % 2) == 1)
                            && Math.abs(x - i) != Math.abs(z - k)) {
                                world.setBlock(x, j, z, ConfigBlocks.blockCosmeticSolid, 1, 3);
                                world.setBlock(x, j + 1, z, ConfigBlocks.blockEldritch, 3, 3);
                            }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
