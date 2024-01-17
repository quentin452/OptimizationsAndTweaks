package fr.iamacat.optimizationsandtweaks.utilsformods.goblins;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import goblin.*;

public class GOBLINWorldGenFireplaceTwo {

    public static boolean canGenerateFireplace(World world, int i, int j, int k) {
        if (!world.blockExists(i, j, k) || !world.blockExists(i + 6, j, k + 6)
            || !world.blockExists(i + 6, j, k)
            || !world.blockExists(i, j, k + 6)) {
            return false;
        }

        Block block1 = world.getBlock(i, j, k);
        Block block2 = world.getBlock(i + 6, j, k + 6);
        Block block3 = world.getBlock(i + 6, j, k);
        Block block4 = world.getBlock(i, j, k + 6);

        return block1 == Blocks.grass && block2 == Blocks.grass && block3 == Blocks.grass && block4 == Blocks.grass;
    }

    public static void func_76484_a(World world, Random rand, int i, int j, int k) {
        int width1;
        int width2;
        int goblinPick;
        for (width1 = -1; width1 <= 6; ++width1) {
            for (width2 = -1; width2 <= 6; ++width2) {
                for (goblinPick = 1; goblinPick <= 10; ++goblinPick) {
                    if (width1 >= 1 && width1 <= 5
                        && width2 >= 1
                        && width2 <= 5
                        && world.getBlock(i + width1, j + goblinPick, k + width2) == Blocks.log) {
                        world.setBlock(i + width1, j + goblinPick, k + width2, Blocks.air, 0, 2);
                    }

                    if (world.getBlock(i + width1, j + goblinPick, k + width2) == Blocks.leaves
                        || world.getBlock(i + width1, j + goblinPick, k + width2) == Blocks.tallgrass
                        || world.getBlock(i + width1, j + goblinPick, k + width2) == Blocks.vine) {
                        world.setBlock(i + width1, j + goblinPick, k + width2, Blocks.air, 0, 2);
                    }
                }
            }
        }

        int c1;
        int c;
        world.setBlock(i + 3, j + 1, k + 2, Blocks.double_stone_slab, 0, 2);
        world.setBlock(i + 2, j + 1, k + 3, Blocks.double_stone_slab, 0, 2);
        world.setBlock(i + 3, j + 1, k + 3, Blocks.iron_bars, 0, 2);
        world.setBlock(i + 3, j, k + 3, Blocks.fire, 0, 2);
        world.setBlock(i + 3, j - 1, k + 3, Blocks.netherrack, 0, 2);
        world.setBlock(i + 4, j + 1, k + 3, Blocks.double_stone_slab, 0, 2);
        world.setBlock(i + 3, j + 1, k + 4, Blocks.double_stone_slab, 0, 2);
        width1 = rand.nextInt(4);
        label115: switch (width1) {
            case 0:
                world.setBlock(i + 3, j + 1, k + 0, Blocks.chest, 0, 2);
                TileEntityChest tileentitychest = (TileEntityChest) world.getTileEntity(i + 3, j + 1, k + 0);
                goblinPick = 0;

                while (true) {
                    if (goblinPick > 4) {
                        break label115;
                    }

                    ItemStack itemstack = pickCheckLootItem(rand);
                    if (itemstack != null) {
                        tileentitychest
                            .setInventorySlotContents(rand.nextInt(tileentitychest.getSizeInventory()), itemstack);
                    }

                    ++goblinPick;
                }
            case 1:
                world.setBlock(i + 0, j + 1, k + 3, Blocks.chest, 0, 2);
                TileEntityChest tileentitychest2 = (TileEntityChest) world.getTileEntity(i + 0, j + 1, k + 3);
                int r1 = 0;

                while (true) {
                    if (r1 > 4) {
                        break label115;
                    }

                    ItemStack itemstack = pickCheckLootItem(rand);
                    if (itemstack != null) {
                        tileentitychest2
                            .setInventorySlotContents(rand.nextInt(tileentitychest2.getSizeInventory()), itemstack);
                    }

                    ++r1;
                }
            case 2:
                world.setBlock(i + 6, j + 1, k + 3, Blocks.chest, 0, 2);
                TileEntityChest tileentitychest3 = (TileEntityChest) world.getTileEntity(i + 6, j + 1, k + 3);
                c = 0;

                while (true) {
                    if (c > 4) {
                        break label115;
                    }

                    ItemStack itemstack = pickCheckLootItem(rand);
                    if (itemstack != null) {
                        tileentitychest3
                            .setInventorySlotContents(rand.nextInt(tileentitychest3.getSizeInventory()), itemstack);
                    }

                    ++c;
                }
            case 3:
                world.setBlock(i + 3, j + 1, k + 6, Blocks.chest, 0, 2);
                TileEntityChest tileentitychest4 = (TileEntityChest) world.getTileEntity(i + 3, j + 1, k + 6);

                for (c1 = 0; c1 <= 4; ++c1) {
                    ItemStack itemstack = pickCheckLootItem(rand);
                    if (itemstack != null) {
                        tileentitychest4
                            .setInventorySlotContents(rand.nextInt(tileentitychest4.getSizeInventory()), itemstack);
                    }
                }
        }

        for (width2 = rand.nextInt(3) + 3; width2 >= 0; --width2) {
            goblinPick = rand.nextInt(20);
            if (goblinPick >= 0 && goblinPick <= 6) {
                GOBLINEntityGoblin goblin = new GOBLINEntityGoblin(world);
                c = -1 + rand.nextInt(9);
                c1 = -1 + rand.nextInt(9);
                goblin.setLocationAndAngles(i + c, j + 1, k + c1, world.rand.nextFloat() * 360.0F, 0.0F);
                goblin.setPosition((i + c), (j + 1), (k + c1));
                world.spawnEntityInWorld(goblin);
            }

            if (goblinPick >= 7 && goblinPick <= 12) {
                GOBLINEntityGoblinRanger goblin = new GOBLINEntityGoblinRanger(world);
                c = -1 + rand.nextInt(9);
                c1 = -1 + rand.nextInt(9);
                goblin.setLocationAndAngles((i + c), (j + 1), (k + c1), world.rand.nextFloat() * 360.0F, 0.0F);
                goblin.setPosition((i + c), (j + 1), (k + c1));
                world.spawnEntityInWorld(goblin);
            }

            if (goblinPick >= 13 && goblinPick <= 18) {
                GOBLINEntityGoblinSoldier goblin = new GOBLINEntityGoblinSoldier(world);
                c = -1 + rand.nextInt(9);
                c1 = -1 + rand.nextInt(9);
                goblin.setLocationAndAngles((i + c), (j + 1), (k + c1), world.rand.nextFloat() * 360.0F, 0.0F);
                goblin.setPosition((i + c), (j + 1), (k + c1));
                world.spawnEntityInWorld(goblin);
            }

            if (goblinPick == 19) {
                GOBLINEntityGoblinBomber goblin = new GOBLINEntityGoblinBomber(world);
                c = -1 + rand.nextInt(9);
                c1 = -1 + rand.nextInt(9);
                goblin.setLocationAndAngles((i + c), (j + 1), k + c1, world.rand.nextFloat() * 360.0F, 0.0F);
                goblin.setPosition((i + c), (j + 1), (k + c1));
                world.spawnEntityInWorld(goblin);
            }
        }
    }

    private static ItemStack pickCheckLootItem(Random random) {
        int i = random.nextInt(7);
        if (i == 0) {
            return new ItemStack(Items.apple, random.nextInt(2) + 1);
        } else if (i == 1) {
            return new ItemStack(Item.getItemFromBlock(Blocks.log), 8);
        } else if (i == 2) {
            return new ItemStack(Items.bread, random.nextInt(2) + 1);
        } else if (i == 3) {
            return new ItemStack(Items.cooked_beef, random.nextInt(2) + 1);
        } else if (i == 4) {
            return new ItemStack(Items.iron_ingot, random.nextInt(2) + 2);
        } else if (i == 5 && random.nextInt(20) == 1) {
            return new ItemStack(mod_Goblins.powderR);
        } else {
            return i == 6 ? new ItemStack(Items.coal, random.nextInt(3) + 5) : null;
        }
    }
}
