package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.goblins;

import goblin.mod_Goblins;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ChestGenHooks;

import java.util.Random;

public class GOBLINWorldGenHutsTwo {
    public static boolean canGenerateHuts(World world, int i, int j, int k) {
        if (!world.blockExists(i, j, k) || !world.blockExists(i + 12, j, k + 12) ||
            !world.blockExists(i + 12, j, k) || !world.blockExists(i, j, k + 12)) {
            return false;
        }

        Block block1 = world.getBlock(i, j, k);
        Block block2 = world.getBlock(i + 12, j, k + 12);
        Block block3 = world.getBlock(i + 12, j, k);
        Block block4 = world.getBlock(i, j, k + 12);

        return block1 == Blocks.grass &&
            block2 == Blocks.grass &&
            block3 == Blocks.grass &&
            block4 == Blocks.grass;
    }
    public static final WeightedRandomChestContent[] goblinStandardChest;
    public static final WeightedRandomChestContent[] goblinRiderChest;
    public static final WeightedRandomChestContent[] goblinMinerChest;
    static {
        goblinStandardChest = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10), new WeightedRandomChestContent(Items.cooked_beef, 0, 1, 3, 10), new WeightedRandomChestContent(Items.gunpowder, 0, 1, 4, 10), new WeightedRandomChestContent(Items.string, 0, 1, 4, 10), new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10), new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1), new WeightedRandomChestContent(mod_Goblins.bomb, 0, 1, 4, 10), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 5, 5), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log), 0, 4, 10, 10)};
        goblinRiderChest = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.bread, 0, 3, 6, 9), new WeightedRandomChestContent(Items.cooked_beef, 0, 3, 6, 9), new WeightedRandomChestContent(Items.cooked_porkchop, 0, 3, 6, 9), new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 9), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 9), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 5, 4), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log), 0, 4, 10, 9), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.skull), 0, 1, 2, 9), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.wool), 0, 10, 20, 10)};
        goblinMinerChest = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10), new WeightedRandomChestContent(Items.cooked_beef, 0, 1, 3, 10), new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10), new WeightedRandomChestContent(Items.iron_ingot, 0, 3, 8, 10), new WeightedRandomChestContent(Items.redstone, 0, 3, 8, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 3, 8, 5), new WeightedRandomChestContent(Items.diamond, 0, 1, 1, 1), new WeightedRandomChestContent(Items.coal, 0, 7, 10, 5), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.stone), 0, 15, 50, 10)};
    }
    public static void func_76484_a(World world, Random rand, int i, int j, int k) {
            for(int a = 0; a <= 12; ++a) {
                for(int b = 0; b <= 12; ++b) {
                    if (world.getBlock(i + a, j + 2, k + b) == Blocks.planks) {
                        return;
                    }
                }
            }

            if (rand.nextInt(2) == 0) {
                generateHuts1(world, rand, i, j, k);
            } else {
                generateHuts2(world, rand, i, j, k);
            }
    }
    private static void generateHuts1(World world, Random rand, int i, int j, int k) {
        generateFireplace(world, rand, i + 8, j, k);
        generateRightHouse(world, rand, i, j, k);
        generateTopHouse(world, rand, i + 6, j, k + 7);
        world.setBlock(i + 9, j, k + 6, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 9, j, k + 5, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 8, j, k + 5, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 8, j, k + 4, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 7, j, k + 4, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 7, j, k + 3, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 6, j, k + 3, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 9, j + 1, k + 6, Blocks.air, 0, 2);
        world.setBlock(i + 9, j + 1, k + 5, Blocks.air, 0, 2);
        world.setBlock(i + 8, j + 1, k + 5, Blocks.air, 0, 2);
        world.setBlock(i + 8, j + 1, k + 4, Blocks.air, 0, 2);
        world.setBlock(i + 7, j + 1, k + 4, Blocks.air, 0, 2);
        world.setBlock(i + 7, j + 1, k + 3, Blocks.air, 0, 2);
        world.setBlock(i + 6, j + 1, k + 3, Blocks.air, 0, 2);

        for(int height = 1; height <= 4; ++height) {
            if (height == 4) {
                world.setBlock(i + 7, j + height, k + 5, chooseTotem(rand), 0, 2);
            } else {
                world.setBlock(i + 7, j + height, k + 5, Blocks.log, 0, 2);
            }
        }

    }
    private static void generateHuts2(World world, Random rand, int i, int j, int k) {
        generateFireplace(world, rand, i, j, k);
        generateTopHouse(world, rand, i, j, k + 7);
        generateLeftHouse(world, rand, i + 7, j, k);
        world.setBlock(i + 6, j, k + 3, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 5, j, k + 3, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 5, j, k + 4, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 4, j, k + 4, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 4, j, k + 5, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 3, j, k + 5, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 3, j, k + 6, Blocks.cobblestone, 0, 2);
        world.setBlock(i + 6, j + 1, k + 3, Blocks.air, 0, 2);
        world.setBlock(i + 5, j + 1, k + 3, Blocks.air, 0, 2);
        world.setBlock(i + 5, j + 1, k + 4, Blocks.air, 0, 2);
        world.setBlock(i + 4, j + 1, k + 4, Blocks.air, 0, 2);
        world.setBlock(i + 4, j + 1, k + 5, Blocks.air, 0, 2);
        world.setBlock(i + 3, j + 1, k + 5, Blocks.air, 0, 2);
        world.setBlock(i + 3, j + 1, k + 6, Blocks.air, 0, 2);

        for(int height = 1; height <= 4; ++height) {
            if (height == 4) {
                world.setBlock(i + 5, j + height, k + 5, chooseTotem(rand), 0, 2);
            } else {
                world.setBlock(i + 5, j + height, k + 5, Blocks.log, 0, 2);
            }
        }
    }
    private static void generateFireplace(World world, Random rand, int i, int j, int k) {
        for(int x = 0; x <= 4; ++x) {
            for(int y = 0; y <= 4; ++y) {
                for(int height = 0; height <= 6; ++height) {
                    if ((x != 0 || y != 0 && y != 4) && (x != 4 || y != 0 && y != 4)) {
                        if (height == 0) {
                            world.setBlock(i + x, j + height, k + y, Blocks.cobblestone, 0, 2);
                        } else {
                            world.setBlock(i + x, j + height, k + y, Blocks.air, 0, 2);
                        }
                    }
                }
            }
        }

        world.setBlock(i + 2, j, k + 2, Blocks.netherrack, 0, 2);
        world.setBlock(i + 2, j + 1, k + 2, Blocks.fire, 0, 2);
        world.setBlock(i + 2, j + 1, k + 1, Blocks.double_stone_slab, 0, 2);
        world.setBlock(i + 1, j + 1, k + 2, Blocks.double_stone_slab, 0, 2);
        world.setBlock(i + 2, j + 1, k + 3, Blocks.double_stone_slab, 0, 2);
        world.setBlock(i + 3, j + 1, k + 2, Blocks.double_stone_slab, 0, 2);
    }

    public static Block chooseTotem(Random rand) {
        switch (rand.nextInt(4)) {
            case 0:
                return mod_Goblins.totemY;
            case 1:
                return mod_Goblins.totemR;
            case 2:
                return mod_Goblins.totemG;
            case 3:
                return mod_Goblins.totemB;
            default:
                return mod_Goblins.totemR;
        }
    }
    protected static void generateRightHouse(World world, Random rand, int i, int j, int k) {
        int height;
        int width2;
            for (height = 0; height <= 7; ++height) {
                for (width2 = 0; width2 <= 9; ++width2) {
                    for (int h = 0; h <= 4; ++h) {
                        if (h == 0) {
                            world.setBlock(i + h, j - height, k + width2, Blocks.grass, 0, 2);
                        } else if (h > 0 && h < 3) {
                            world.setBlock(i + h, j - height, k + width2, Blocks.dirt, 0, 2);
                        } else {
                            world.setBlock(i + h, j - height, k + width2, Blocks.stone, 0, 2);
                        }
                    }
                }
            }

            for (height = -1; height <= 7; ++height) {
                for (width2 = 1; width2 <= 5; ++width2) {
                    for (int h = 1; h <= 4; ++h) {
                        world.setBlock(i + h, j + h, k + width2, Blocks.air, 0, 2);
                    }
                }
            }


        for(height = 1; height <= 2; ++height) {
            world.setBlock(i + 1, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 0, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 0, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 6, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 6, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 0, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 0, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 0, j + height, k + 2, Blocks.planks, 0, 2);
        }

        for(height = 3; height <= 3; ++height) {
            world.setBlock(i + 2, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 3, Blocks.planks, 0, 2);
        }

        for(height = 0; height <= 0; ++height) {
            world.setBlock(i + 2, j + height, k + 1, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 1, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 3, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 5, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 5, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 1, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 1, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 1, j + height, k + 3, Blocks.cobblestone, 0, 2);
        }

        for(height = 4; height <= 4; ++height) {
            world.setBlock(i + 3, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 3, Blocks.planks, 0, 2);
        }

        for(height = 0; height <= 0; ++height) {
            world.setBlock(i + 3, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 3, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 3, Blocks.cobblestone, 0, 2);
        }

        world.setBlock(i + 3, j + 5, k + 3, Blocks.wooden_slab, 0, 2);
        world.setBlock(i + 2, j, k + 3, Blocks.cobblestone, 0, 2);

        for(height = 1; height <= 4; ++height) {
            world.setBlock(i + 3, j + height, k + 3, Blocks.fence, 0, 2);
        }

        world.setBlock(i + 6, j + 2, k + 4, Blocks.torch, 0, 2);
        world.setBlock(i + 6, j + 2, k + 2, Blocks.torch, 0, 2);
        world.setBlock(i + 1, j + 2, k + 3, Blocks.torch, 0, 2);
        world.setBlock(i + 4, j, k + 3, mod_Goblins.MobGSpawner, 0, 2);
        world.setBlock(i + 5, j + 1, k + 3, Blocks.air, 0, 2);
        world.setBlock(i + 1, j + 1, k + 3, Blocks.chest, 0, 2);
        TileEntityChest tileentitychest = (TileEntityChest)world.getTileEntity(i + 1, j + 1, k + 3);
        if (tileentitychest != null) {
            ChestGenHooks info = ChestGenHooks.getInfo("dungeonChest");
            WeightedRandomChestContent.generateChestContents(rand, goblinStandardChest, tileentitychest, rand.nextInt(3) + 7);
        }

    }
    protected static void generateLeftHouse(World world, Random rand, int i, int j, int k) {
        int height;
        int width2;
        for (height = -2; height <= 5; ++height) {
            for (width2 = -2; width2 <= 6; ++width2) {
                for (int h = 0; h <= 4; ++h) {
                    if (h == 0) {
                        world.setBlock(i + h, j - height, k + width2, Blocks.grass, 0, 2);
                    } else if (h > 0 && h < 3) {
                        world.setBlock(i + h, j - height, k + width2, Blocks.dirt, 0, 2);
                    } else {
                        world.setBlock(i + h, j - height, k + width2, Blocks.stone, 0, 2);
                    }
                }
            }
        }

        for (height = -2; height <= 6; ++height) {
            for (width2 = 1; width2 <= 5; ++width2) {
                for (int h = 1; h <= 4; ++h) {
                    world.setBlock(i + h, j + h, k + width2, Blocks.air, 0, 2);
                }
            }
        }

        for(height = 1; height <= 2; ++height) {
            world.setBlock(i + 1, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 0, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 0, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 6, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 6, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 0, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 0, j + height, k + 2, Blocks.planks, 0, 2);
        }

        for(height = 3; height <= 3; ++height) {
            world.setBlock(i + 2, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 0, j + height, k + 3, Blocks.planks, 0, 2);
        }

        for(height = 0; height <= 0; ++height) {
            world.setBlock(i + 2, j + height, k + 1, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 1, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 3, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 5, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 5, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 1, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 1, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 0, j + height, k + 3, Blocks.cobblestone, 0, 2);
        }

        for(height = 4; height <= 4; ++height) {
            world.setBlock(i + 3, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 3, Blocks.planks, 0, 2);
        }

        for(height = 0; height <= 0; ++height) {
            world.setBlock(i + 3, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 3, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 1, j + height, k + 3, Blocks.cobblestone, 0, 2);
        }

        world.setBlock(i + 2, j + 5, k + 3, Blocks.wooden_slab, 0, 2);
        world.setBlock(i + 2, j, k + 3, Blocks.cobblestone, 0, 2);

        for(height = 1; height <= 4; ++height) {
            world.setBlock(i + 2, j + height, k + 3, Blocks.fence, 0, 2);
        }

        world.setBlock(i - 1, j + 2, k + 4, Blocks.torch, 0, 2);
        world.setBlock(i - 1, j + 2, k + 2, Blocks.torch, 0, 2);
        world.setBlock(i + 4, j + 2, k + 3, Blocks.torch, 0, 2);
        world.setBlock(i + 1, j, k + 3, mod_Goblins.MobGSpawner, 0, 2);
        world.setBlock(i + 0, j + 1, k + 3, Blocks.air, 0, 2);
        if (rand.nextInt(2) == 0) {
            world.setBlock(i + 4, j + 1, k + 3, Blocks.chest, 0, 2);
            TileEntityChest tileentitychest = (TileEntityChest)world.getTileEntity(i + 4, j + 1, k + 3);
            if (tileentitychest != null) {
                ChestGenHooks info = ChestGenHooks.getInfo("dungeonChest");
                WeightedRandomChestContent.generateChestContents(rand, goblinStandardChest, tileentitychest, rand.nextInt(3) + 7);
            }
        }

    }
    protected static void generateTopHouse(World world, Random rand, int i, int j, int k) {
        int height;
        int width2;

        for (height = 0; height <= 6; ++height) {
            for (width2 = 0; width2 <= 5; ++width2) {
                for (int h = 0; h <= 4; ++h) {
                    if (h == 0) {
                        world.setBlock(i + h, j - height, k + width2, Blocks.grass, 0, 2);
                    } else if (h < 3) {
                        world.setBlock(i + h, j - height, k + width2, Blocks.dirt, 0, 2);
                    } else {
                        world.setBlock(i + h, j - height, k + width2, Blocks.stone, 0, 2);
                    }
                }
            }
        }

        for (height = 1; height <= 5; ++height) {
            for (width2 = 1; width2 <= 4; ++width2) {
                for (int h = 1; h <= 4; ++h) {
                    world.setBlock(i + h, j + height, k + width2, Blocks.air, 0, 2);
                }
            }
        }

        for(height = 1; height <= 2; ++height) {
            world.setBlock(i + 4, j + height, k + 0, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 0, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 0, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 0, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 5, Blocks.planks, 0, 2);
            world.setBlock(i + 6, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 6, j + height, k + 2, Blocks.planks, 0, 2);
        }

        for(height = 3; height <= 3; ++height) {
            world.setBlock(i + 4, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 1, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 0, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 4, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 1, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 5, j + height, k + 2, Blocks.planks, 0, 2);
        }

        for(height = 4; height <= 4; ++height) {
            world.setBlock(i + 2, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 2, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 4, j + height, k + 2, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 3, Blocks.planks, 0, 2);
            world.setBlock(i + 3, j + height, k + 1, Blocks.planks, 0, 2);
        }

        for(height = 5; height <= 5; ++height) {
            world.setBlock(i + 3, j + height, k + 2, Blocks.wooden_slab, 0, 2);
        }

        for(height = 0; height <= 0; ++height) {
            world.setBlock(i + 4, j + height, k + 1, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 1, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + 1, k, Blocks.air, 0, 2);
            world.setBlock(i + 4, j + height, k + 5, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 5, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 5, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 1, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 1, j + height, k + 3, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 1, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 5, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 5, j + height, k + 3, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 5, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 3, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 2, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 3, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 4, j + height, k + 2, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 4, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 1, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 3, Blocks.cobblestone, 0, 2);
            world.setBlock(i + 3, j + height, k + 2, Blocks.cobblestone, 0, 2);
        }

        for(height = 1; height <= 4; ++height) {
            world.setBlock(i + 3, j + height, k + 2, Blocks.fence, 0, 2);
        }

        for(height = 2; height <= 2; ++height) {
            world.setBlock(i + 2, j + height, k - 1, Blocks.torch, 0, 2);
            world.setBlock(i + 4, j + height, k - 1, Blocks.torch, 0, 2);
            world.setBlock(i + 3, j + height, k + 4, Blocks.torch, 0, 2);
        }

        world.setBlock(i + 3, j, k + 1, mod_Goblins.MobGSpawner, 0, 2);
        world.setBlock(i + 3, j + 1, k + 4, Blocks.chest, 0, 2);
        TileEntityChest tileentitychest = (TileEntityChest)world.getTileEntity(i + 3, j + 1, k + 4);
        if (tileentitychest != null) {
            ChestGenHooks info = ChestGenHooks.getInfo("dungeonChest");
            WeightedRandomChestContent.generateChestContents(rand, goblinStandardChest, tileentitychest, rand.nextInt(3) + 7);
        }

    }
}
