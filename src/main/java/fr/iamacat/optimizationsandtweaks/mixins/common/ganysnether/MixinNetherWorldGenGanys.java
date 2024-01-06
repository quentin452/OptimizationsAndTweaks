package fr.iamacat.optimizationsandtweaks.mixins.common.ganysnether;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import ganymedes01.ganysnether.GanysNether;
import ganymedes01.ganysnether.ModBlocks;
import ganymedes01.ganysnether.core.utils.Utils;
import ganymedes01.ganysnether.tileentities.TileEntityUndertaker;
import ganymedes01.ganysnether.world.NetherWorldGen;

@Mixin(NetherWorldGen.class)
public class MixinNetherWorldGenGanys {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        chunkX = chunkX >> 4;
        chunkZ = chunkZ >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return;
        }
        if (world.provider.isHellWorld) {
            for (int x = 0; x < 16; ++x) {
                for (int y = 20; y < 84; ++y) {
                    for (int z = 0; z < 16; ++z) {
                        int blockX = chunkX * 16 + x;
                        int blockY = y + 1;
                        int blockZ = chunkZ * 16 + z;
                        if (GanysNether.shouldGenerateCrops && rand.nextInt(GanysNether.netherCropRate) == 0
                            && this.shouldGenerate(world, blockX, blockY, blockZ)
                            && this.hasLavaNearby(world, blockX, blockY - 1, blockZ)) {
                            switch (rand.nextInt(6)) {
                                case 0:
                                    if (GanysNether.shouldGenerateGlowingReed) {
                                        world.setBlock(blockX, blockY - 1, blockZ, Blocks.netherrack);
                                        world.setBlock(blockX, blockY, blockZ, ModBlocks.glowingReed);
                                        if (rand.nextInt(10) == 5) {
                                            world.setBlock(blockX, blockY + 1, blockZ, ModBlocks.glowingReed);
                                        }
                                    }

                                    return;
                                case 1:
                                    if (GanysNether.shouldGenerateSpectreWheat) {
                                        world.setBlock(blockX, blockY - 1, blockZ, ModBlocks.tilledNetherrack);
                                        world.setBlock(
                                            blockX,
                                            blockY,
                                            blockZ,
                                            ModBlocks.spectreWheat,
                                            rand.nextInt(7),
                                            2);
                                    }

                                    return;
                                case 2:
                                    if (GanysNether.shouldGenerateQuarzBerryBush) {
                                        world.setBlock(blockX, blockY - 1, blockZ, ModBlocks.tilledNetherrack);
                                        world.setBlock(
                                            blockX,
                                            blockY,
                                            blockZ,
                                            ModBlocks.quarzBerryBush,
                                            rand.nextInt(7),
                                            2);
                                    }

                                    return;
                                case 3:
                                    if (GanysNether.shouldGenerateWitherShrub
                                        && rand.nextInt(GanysNether.witherShrubRate) == 0) {
                                        world.setBlock(blockX, blockY - 1, blockZ, ModBlocks.tilledNetherrack);
                                        world.setBlock(
                                            blockX,
                                            blockY,
                                            blockZ,
                                            ModBlocks.witherShrub,
                                            rand.nextInt(6),
                                            2);
                                        world.setBlock(blockX, blockY + 1, blockZ, Blocks.glowstone);
                                    }

                                    return;
                                case 4:
                                    if (GanysNether.shouldGenerateBlazingCactoid) {
                                        world.setBlock(blockX, blockY - 1, blockZ, Blocks.netherrack);
                                        world.setBlock(blockX, blockY, blockZ, ModBlocks.blazingCactoid);
                                        if (rand.nextInt(10) == 5) {
                                            world.setBlock(blockX, blockY + 1, blockZ, ModBlocks.blazingCactoid);
                                        }
                                    }

                                    return;
                                case 5:
                                    if (GanysNether.shouldGenerateHellBush) {
                                        world.setBlock(blockX, blockY - 1, blockZ, ModBlocks.tilledNetherrack);
                                        world.setBlock(blockX, blockY, blockZ, ModBlocks.hellBush, rand.nextInt(7), 2);
                                    }

                                    return;
                            }
                        }

                        if (GanysNether.shouldGenerateUndertakers && rand.nextInt(GanysNether.undertakerRate) == 0
                            && this.shouldGenerate(world, blockX, blockY, blockZ)) {
                            this.generateUndertakerWithRandomContents(world, blockX, blockY, blockZ, rand);
                        }
                    }
                }
            }

        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private boolean hasLavaNearby(World world, int x, int y, int z) {
        Block block;

        block = world.getBlock(x + 1, y, z);
        if (block != null && block.getMaterial() == Material.lava) {
            return true;
        }

        block = world.getBlock(x - 1, y, z);
        if (block != null && block.getMaterial() == Material.lava) {
            return true;
        }

        block = world.getBlock(x, y, z + 1);
        if (block != null && block.getMaterial() == Material.lava) {
            return true;
        }

        block = world.getBlock(x, y, z - 1);
        return block != null && block.getMaterial() == Material.lava;
    }

    @Shadow
    private boolean shouldGenerate(World world, int x, int y, int z) {
        return y >= 25 && !world.isAirBlock(x, y - 1, z)
            && world.getBlock(x, y - 1, z)
                .getMaterial() != Material.lava
            && world.isAirBlock(x, y, z)
            && world.isAirBlock(x, y + 1, z)
            && world.isAirBlock(x, y + 2, z);
    }

    @Shadow
    private void generateUndertakerWithRandomContents(World world, int x, int y, int z, Random rand) {
        world.setBlock(x, y, z, ModBlocks.undertaker);
        TileEntityUndertaker undertaker = Utils.getTileEntity(world, x, y, z, TileEntityUndertaker.class);
        if (undertaker != null) {
            ChestGenHooks info = ChestGenHooks.getInfo("ganysnether.undertaker");
            WeightedRandomChestContent
                .generateChestContents(world.rand, info.getItems(world.rand), undertaker, info.getCount(world.rand));
        }

    }
}
