package fr.iamacat.optimizationsandtweaks.mixins.common.slimecarnage;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import supremopete.SlimeCarnage.commom.SlimeCarnage;
import supremopete.SlimeCarnage.mobs.EntityDonatelloSlime;
import supremopete.SlimeCarnage.mobs.EntityLeonardoSlime;
import supremopete.SlimeCarnage.mobs.EntityMichelangeloSlime;
import supremopete.SlimeCarnage.mobs.EntityRaphaelSlime;
import supremopete.SlimeCarnage.worldgen.WorldGenSewers;

@Mixin(WorldGenSewers.class)
public abstract class MixinWorldGenSewers{

    @Shadow
    protected Block[] GetValidSpawnBlocks() {
        return new Block[] { Blocks.grass };
    }

    /**
     * @author iamacatfr
     * @reason tried fixing cascadings , but some cascading remains caused by LocationIsValidSpawn and can't fix it
     */
    @Overwrite(remap = false)
    public boolean LocationIsValidSpawn(World world, int posX, int posY, int posZ) {
        int distanceToAir = optimizationsAndTweaks$calculateDistanceToAir(world, posX, posY, posZ);

        if (distanceToAir > 3) {
            return false;
        }

        posY += distanceToAir - 1;
        if (posY >= world.getHeight()) {
            return false;
        }

        Block block = world.getBlock(posX, posY, posZ);
        Block blockBelow = world.getBlock(posX, posY - 1, posZ);
        Block[] validSpawnBlocks = GetValidSpawnBlocks();

        return optimizationsAndTweaks$isValidSpawnBlock(block, blockBelow, validSpawnBlocks);
    }

    @Unique
    private int optimizationsAndTweaks$calculateDistanceToAir(World world, int posX, int posY, int posZ) {
        int maxDistance = 3;
        int distance = 0;
        int chunkX = posX >> 4;
        int chunkZ = posZ >> 4;
        Chunk chunk = world.getChunkFromBlockCoords(chunkX, chunkZ);

        int chunkMinY = Math.max(0, posY);
        int chunkMaxY = Math.min(world.getHeight(), posY + maxDistance);

        for (int y = chunkMinY; y < chunkMaxY; y++) {
            Block currentBlock = chunk.getBlock(posX & 15, y, posZ & 15);

            if (currentBlock == Blocks.air) {
                break;
            }
            distance++;
        }
        return distance;
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidSpawnBlock(Block block, Block blockBelow, Block[] validSpawnBlocks) {
        for (Block validBlock : validSpawnBlocks) {
            if (block == validBlock || (block == Blocks.snow && blockBelow == validBlock)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random rand, int i, int j, int k) {
        if (this.LocationIsValidSpawn(world, i, j, k) && this.LocationIsValidSpawn(world, i + 8, j, k)
            && this.LocationIsValidSpawn(world, i + 8, j, k + 4)
            && this.LocationIsValidSpawn(world, i, j, k + 4)) {
            optimizationsAndTweaks$generate1(world,rand,i,j,k);
            optimizationsAndTweaks$generate2(world,rand,i,j,k);
            optimizationsAndTweaks$generate3(world,rand,i,j,k);
            optimizationsAndTweaks$generate4(world,rand,i,j,k);
            if (!world.isRemote) {
                optimizationsAndTweaks$generate5(world,rand,i,j,k);
            }
            return true;
        } else {
            return false;
        }
    }
    @Unique
    public void optimizationsAndTweaks$generate1(World world, Random rand, int i, int j, int k) {
        int j4;
        int j3;
        int i3;
        int k3;
        for (j4 = -15; j4 < 1; ++j4) {
            for (j3 = 0; j3 < 5; ++j3) {
                for (i3 = 0; i3 < 5; ++i3) {
                    k3 = rand.nextInt(5);
                    if (k3 >= 0 || k3 <= 2) {
                        world.setBlock(i + j3, j + j4, k + i3, Blocks.stonebrick);
                    }

                    if (k3 == 3) {
                        world.setBlock(i + j3, j + j4, k + i3, Blocks.stonebrick, 1, 2);
                    }

                    if (k3 == 4) {
                        world.setBlock(i + j3, j + j4, k + i3, Blocks.stonebrick, 2, 2);
                    }
                }
            }
        }

        for (j4 = -12; j4 < 0; ++j4) {
            for (j3 = 1; j3 < 4; ++j3) {
                for (i3 = 1; i3 < 4; ++i3) {
                    world.setBlock(i + j3, j + j4, k + i3, Blocks.air);
                }
            }
        }

        for (j4 = -15; j4 < -8; ++j4) {
            for (j3 = 13; j3 < 25; ++j3) {
                for (i3 = -4; i3 < 32; ++i3) {
                    k3 = rand.nextInt(5);
                    if (k3 >= 0 || k3 <= 2) {
                        world.setBlock(i + j3, j + j4, k + i3, Blocks.stonebrick);
                    }

                    if (k3 == 3) {
                        world.setBlock(i + j3, j + j4, k + i3, Blocks.stonebrick, 1, 2);
                    }

                    if (k3 == 4) {
                        world.setBlock(i + j3, j + j4, k + i3, Blocks.stonebrick, 2, 2);
                    }
                }
            }
        }

        for (j4 = -15; j4 < -8; ++j4) {
            for (j3 = 0; j3 < 23; ++j3) {
                for (i3 = -4; i3 < 9; ++i3) {
                    k3 = rand.nextInt(5);
                    if (k3 >= 0 || k3 <= 2) {
                        world.setBlock(i + j3, j + j4, k + i3, Blocks.stonebrick);
                    }

                    if (k3 == 3) {
                        world.setBlock(i + j3, j + j4, k + i3, Blocks.stonebrick, 1, 2);
                    }

                    if (k3 == 4) {
                        world.setBlock(i + j3, j + j4, k + i3, Blocks.stonebrick, 2, 2);
                    }
                }
            }
        }

        world.setBlock(i, j + 1, k + 2, Blocks.stonebrick);
        world.setBlock(i + 1, j, k + 2, Blocks.air);
        world.setBlock(i + 1, j - 10, k + 1, Blocks.air);
        world.setBlock(i + 3, j - 10, k + 2, Blocks.air);
        world.setBlock(i + 1, j - 10, k + 3, Blocks.air);
        world.setBlock(i + 2, j - 10, k + 1, Blocks.air);
        world.setBlock(i + 2, j - 10, k + 2, Blocks.air);
        world.setBlock(i + 2, j - 10, k + 3, Blocks.air);
        world.setBlock(i + 3, j - 10, k + 1, Blocks.air);
        world.setBlock(i + 3, j - 10, k + 3, Blocks.air);
        world.setBlock(i + 1, j - 9, k + 1, Blocks.air);
        world.setBlock(i + 3, j - 9, k + 2, Blocks.air);
        world.setBlock(i + 1, j - 9, k + 3, Blocks.air);
        world.setBlock(i + 2, j - 9, k + 1, Blocks.air);
        world.setBlock(i + 2, j - 9, k + 2, Blocks.air);
        world.setBlock(i + 2, j - 9, k + 3, Blocks.air);
        world.setBlock(i + 3, j - 9, k + 1, Blocks.air);
        world.setBlock(i + 3, j - 9, k + 3, Blocks.air);
        world.setBlock(i + 2, j - 14, k, Blocks.air);
        world.setBlock(i + 2, j - 14, k, Blocks.water);
        world.setBlock(i + 2, j - 14, k - 1, Blocks.air);
        world.setBlock(i + 2, j - 14, k - 1, Blocks.water);
        world.setBlock(i + 2, j - 13, k - 2, Blocks.air);
        world.setBlock(i + 2, j - 13, k - 2, Blocks.water);
        world.setBlock(i + 2, j - 13, k - 3, Blocks.air);
        world.setBlock(i + 2, j - 13, k - 3, Blocks.iron_bars);
        world.setBlock(i + 2, j - 13, k - 4, Blocks.air);
    }
    @Unique
    public void optimizationsAndTweaks$generate2(World world, Random rand, int i, int j, int k) {
        int j3;
        int i3;
        int k3;
        world.setBlock(i + 2, j - 13, k - 4, Blocks.chest);
        TileEntityChest tileentitychest = new TileEntityChest();
        world.setTileEntity(i + 2, j - 13, k - 4, tileentitychest);

        for (j3 = 0; j3 < 6; ++j3) {
            ItemStack itemstack2 = this.pickCheckLootItem(rand);
            if (itemstack2 != null) {
                tileentitychest
                    .setInventorySlotContents(rand.nextInt(tileentitychest.getSizeInventory()), itemstack2);
            }
        }

        world.setBlock(i + 19, j - 14, k, Blocks.air);
        world.setBlock(i + 19, j - 14, k, Blocks.water);
        world.setBlock(i + 19, j - 14, k - 1, Blocks.air);
        world.setBlock(i + 19, j - 14, k - 1, Blocks.water);
        world.setBlock(i + 19, j - 13, k - 2, Blocks.air);
        world.setBlock(i + 19, j - 13, k - 2, Blocks.water);
        world.setBlock(i + 19, j - 13, k - 3, Blocks.air);
        world.setBlock(i + 19, j - 13, k - 3, Blocks.iron_bars);
        world.setBlock(i + 19, j - 13, k - 4, Blocks.air);

        int j8;
        for (j3 = -15; j3 < -8; ++j3) {
            for (i3 = 8; i3 < 15; ++i3) {
                for (k3 = -8; k3 < 0; ++k3) {
                    j8 = rand.nextInt(5);
                    if (j8 >= 0 || j8 <= 2) {
                        world.setBlock(i + i3, j + j3, k + k3, Blocks.stonebrick);
                    }

                    if (j8 == 3) {
                        world.setBlock(i + i3, j + j3, k + k3, Blocks.stonebrick, 1, 2);
                    }

                    if (j8 == 4) {
                        world.setBlock(i + i3, j + j3, k + k3, Blocks.stonebrick, 2, 2);
                    }
                }
            }
        }

        for (j3 = -13; j3 < -10; ++j3) {
            for (i3 = 10; i3 < 13; ++i3) {
                for (k3 = -5; k3 < -2; ++k3) {
                    world.setBlock(i + i3, j + j3, k + k3, Blocks.air);
                }
            }
        }

        world.setBlock(i + 10, j - 11, k - 2, Blocks.iron_bars);
        world.setBlock(i + 11, j - 11, k - 2, Blocks.iron_bars);
        world.setBlock(i + 12, j - 11, k - 2, Blocks.iron_bars);
        world.setBlock(i + 10, j - 12, k - 2, Blocks.iron_bars);
        world.setBlock(i + 11, j - 12, k - 2, Blocks.iron_bars);
        world.setBlock(i + 12, j - 12, k - 2, Blocks.iron_bars);
        world.setBlock(i + 10, j - 13, k - 2, Blocks.iron_bars);
        world.setBlock(i + 11, j - 13, k - 2, Blocks.iron_bars);
        world.setBlock(i + 12, j - 13, k - 2, Blocks.iron_bars);
        world.setBlock(i + 11, j - 14, k, Blocks.air);
        world.setBlock(i + 11, j - 14, k, Blocks.water);
        world.setBlock(i + 11, j - 14, k - 1, Blocks.air);
        world.setBlock(i + 11, j - 14, k - 1, Blocks.water);
        world.setBlock(i + 11, j - 14, k - 2, Blocks.air);
        world.setBlock(i + 11, j - 14, k - 2, Blocks.water);
        world.setBlock(i + 11, j - 14, k - 3, Blocks.air);
        world.setBlock(i + 11, j - 14, k - 3, Blocks.water);
        world.setBlock(i + 11, j - 14, k - 4, Blocks.air);
        world.setBlock(i + 11, j - 14, k - 4, Blocks.water);
        world.setBlock(i + 11, j - 14, k - 5, Blocks.air);
        world.setBlock(i + 11, j - 14, k - 5, Blocks.water);
        world.setBlock(i + 11, j - 12, k - 6, Blocks.air);
        world.setBlock(i + 11, j - 12, k - 6, Blocks.water);
        world.setBlock(i + 11, j - 12, k - 7, Blocks.air);
        world.setBlock(i + 11, j - 12, k - 7, Blocks.iron_bars);
        world.setBlock(i + 8, j - 12, k - 3, Blocks.mob_spawner);
        TileEntityMobSpawner tileentitymobspawner1 = (TileEntityMobSpawner) world
            .getTileEntity(i + 8, j - 12, k - 3);
        tileentitymobspawner1.func_145881_a()
            .setEntityName("SlimeCarnage.FootSoldierSlime");
        world.setBlock(i + 14, j - 12, k - 3, Blocks.mob_spawner);
        TileEntityMobSpawner tileentitymobspawner2 = (TileEntityMobSpawner) world
            .getTileEntity(i + 14, j - 12, k - 3);
        tileentitymobspawner2.func_145881_a()
            .setEntityName("SlimeCarnage.FootSoldierSlime");
        world.setBlock(i + 19, j - 13, k - 4, Blocks.chest);
        TileEntityChest tileentitychest2 = new TileEntityChest();
        world.setTileEntity(i + 19, j - 13, k - 4, tileentitychest2);

        for (j8 = 0; j8 < 6; ++j8) {
            ItemStack itemstack2 = this.pickCheckLootItem(rand);
            if (itemstack2 != null) {
                tileentitychest2
                    .setInventorySlotContents(rand.nextInt(tileentitychest.getSizeInventory()), itemstack2);
            }
        }
    }

    @Unique
    public void optimizationsAndTweaks$generate3(World world, Random rand, int i, int j, int k) {
        int j8;
        int i8;
        for (j8 = 16; j8 < 23; ++j8) {
            for (i8 = 23; i8 < 30; ++i8) {
                world.setBlock(i + j8, j - 10, k + i8, Blocks.air);
            }
        }
        int k8;
        int h;
        for (j8 = -20; j8 < -10; ++j8) {
            for (i8 = 21; i8 < 28; ++i8) {
                for (k8 = -3; k8 < 8; ++k8) {
                    h = rand.nextInt(5);
                    if (h >= 0 || h <= 2) {
                        world.setBlock(i + i8, j + j8, k + k8, Blocks.stonebrick);
                    }

                    if (h == 3) {
                        world.setBlock(i + i8, j + j8, k + k8, Blocks.stonebrick, 1, 2);
                    }

                    if (h == 4) {
                        world.setBlock(i + i8, j + j8, k + k8, Blocks.stonebrick, 2, 2);
                    }
                }
            }
        }

        for (j8 = -20; j8 < -13; ++j8) {
            for (i8 = 23; i8 < 46; ++i8) {
                for (k8 = -4; k8 < 9; ++k8) {
                    h = rand.nextInt(5);
                    if (h >= 0 || h <= 2) {
                        world.setBlock(i + i8, j + j8, k + k8, Blocks.stonebrick);
                    }

                    if (h == 3) {
                        world.setBlock(i + i8, j + j8, k + k8, Blocks.stonebrick, 1, 2);
                    }

                    if (h == 4) {
                        world.setBlock(i + i8, j + j8, k + k8, Blocks.stonebrick, 2, 2);
                    }
                }
            }
        }

        for (j8 = -14; j8 < -13; ++j8) {
            for (i8 = 2; i8 < 25; ++i8) {
                for (k8 = 1; k8 < 4; ++k8) {
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.air);
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.water);
                }
            }
        }

        for (j8 = -14; j8 < -13; ++j8) {
            for (i8 = 18; i8 < 21; ++i8) {
                for (k8 = 1; k8 < 21; ++k8) {
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.air);
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.water);
                }
            }
        }

        for (j8 = -19; j8 < -18; ++j8) {
            for (i8 = 23; i8 < 46; ++i8) {
                for (k8 = 1; k8 < 4; ++k8) {
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.air);
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.water);
                }
            }
        }

        for (j8 = -13; j8 < -10; ++j8) {
            for (i8 = 1; i8 < 23; ++i8) {
                for (k8 = -1; k8 < 6; ++k8) {
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.air);
                }
            }
        }

        for (j8 = -13; j8 < -10; ++j8) {
            for (i8 = 16; i8 < 23; ++i8) {
                for (k8 = -1; k8 < 30; ++k8) {
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.air);
                }
            }
        }

        for (j8 = -18; j8 < -15; ++j8) {
            for (i8 = 23; i8 < 46; ++i8) {
                for (k8 = -1; k8 < 6; ++k8) {
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.air);
                }
            }
        }

        for (j8 = -18; j8 < -10; ++j8) {
            for (i8 = 23; i8 < 26; ++i8) {
                for (k8 = -1; k8 < 6; ++k8) {
                    world.setBlock(i + i8, j + j8, k + k8, Blocks.air);
                }
            }
        }

        for (j8 = -13; j8 < 1; ++j8) {
            world.setBlock(i + 1, j + j8, k + 2, Blocks.ladder);
            world.setBlockMetadataWithNotify(i + 1, j + j8, k + 2, 5, 2);
        }
    }

    @Unique
    public void optimizationsAndTweaks$generate4(World world, Random rand, int i, int j, int k) {
        world.setBlock(i + 16, j - 13, k + 22, Blocks.stonebrick, 1, 2);
        world.setBlock(i + 16, j - 12, k + 22, Blocks.stonebrick);
        world.setBlock(i + 16, j - 11, k + 22, Blocks.stonebrick);
        world.setBlock(i + 17, j - 13, k + 22, Blocks.stonebrick);
        world.setBlock(i + 17, j - 12, k + 22, Blocks.stonebrick);
        world.setBlock(i + 17, j - 11, k + 22, Blocks.stonebrick, 1, 2);
        world.setBlock(i + 18, j - 11, k + 22, Blocks.iron_bars);
        world.setBlock(i + 18, j - 12, k + 22, Blocks.iron_bars);
        world.setBlock(i + 18, j - 13, k + 22, Blocks.iron_bars);
        world.setBlock(i + 19, j - 11, k + 22, Blocks.iron_bars);
        world.setBlock(i + 19, j - 12, k + 22, Blocks.iron_bars);
        world.setBlock(i + 19, j - 13, k + 22, Blocks.iron_bars);
        world.setBlock(i + 20, j - 11, k + 22, Blocks.iron_bars);
        world.setBlock(i + 20, j - 12, k + 22, Blocks.iron_bars);
        world.setBlock(i + 20, j - 13, k + 22, Blocks.iron_bars);
        world.setBlock(i + 21, j - 13, k + 22, Blocks.stonebrick);
        world.setBlock(i + 21, j - 12, k + 22, Blocks.stonebrick);
        world.setBlock(i + 21, j - 11, k + 22, Blocks.stonebrick, 1, 2);
        world.setBlock(i + 22, j - 13, k + 22, Blocks.stonebrick);
        world.setBlock(i + 22, j - 12, k + 22, Blocks.stonebrick);
        world.setBlock(i + 22, j - 11, k + 22, Blocks.stonebrick, 2, 2);
    }

    @Unique
    public void optimizationsAndTweaks$generate5(World world, Random rand, int i, int j, int k) {
        EntityLeonardoSlime leoslime = new EntityLeonardoSlime(world);
        leoslime.setLocationAndAngles((i + 17), j - 12, k + 24, 0.0F, 0.0F);
        world.spawnEntityInWorld(leoslime);
        EntityDonatelloSlime donslime = new EntityDonatelloSlime(world);
        donslime.setLocationAndAngles(i + 21, j - 12, k + 24, 0.0F, 0.0F);
        world.spawnEntityInWorld(donslime);
        EntityRaphaelSlime raphslime = new EntityRaphaelSlime(world);
        raphslime.setLocationAndAngles(i + 17, j - 12, k + 26, 0.0F, 0.0F);
        world.spawnEntityInWorld(raphslime);
        EntityMichelangeloSlime micslime = new EntityMichelangeloSlime(world);
        micslime.setLocationAndAngles(i + 21, j - 12, k + 26, 0.0F, 0.0F);
        world.spawnEntityInWorld(micslime);
    }
    @Shadow
    private ItemStack pickCheckLootItem(Random random) {
        int i = random.nextInt(31);
        if (i == 0) {
            return new ItemStack(SlimeCarnage.PizzaSlice, random.nextInt(2) + 1);
        } else if (i == 1) {
            return new ItemStack(SlimeCarnage.Banana, random.nextInt(2) + 1);
        } else if (i == 2 && random.nextInt(200) == 0) {
            return null;
        } else if (i == 3) {
            return null;
        } else if (i == 4) {
            return new ItemStack(Items.bucket, 1);
        } else if (i == 5) {
            return new ItemStack(SlimeCarnage.LimeJam, random.nextInt(4) + 1);
        } else if (i == 6) {
            return new ItemStack(Items.experience_bottle, random.nextInt(20) + 1);
        } else if (i == 7 && random.nextInt(5) == 0) {
            return new ItemStack(Items.name_tag, 1);
        } else if (i == 8) {
            return new ItemStack(Items.gold_ingot, random.nextInt(4) + 1);
        } else if (i == 9 && random.nextInt(10) == 0) {
            return new ItemStack(Items.record_cat, 1);
        } else if (i == 10) {
            return new ItemStack(Items.iron_ingot, random.nextInt(4) + 1);
        } else if (i == 11 && random.nextInt(20) == 0) {
            return new ItemStack(SlimeCarnage.GreenGelBoots, 1);
        } else if (i == 12 && random.nextInt(20) == 0) {
            return new ItemStack(SlimeCarnage.GreenGelLeggings, 1);
        } else if (i == 13 && random.nextInt(20) == 0) {
            return new ItemStack(SlimeCarnage.GreenGelChestplate, 1);
        } else if (i == 14 && random.nextInt(20) == 0) {
            return new ItemStack(SlimeCarnage.GreenGelHelmet, 1);
        } else if (i == 15 && random.nextInt(3) == 0) {
            return new ItemStack(SlimeCarnage.BlueGel, random.nextInt(12) + 1);
        } else if (i == 16 && random.nextInt(3) == 0) {
            return new ItemStack(SlimeCarnage.RedGel, random.nextInt(12) + 1);
        } else if (i == 17 && random.nextInt(3) == 0) {
            return new ItemStack(SlimeCarnage.YellowGel, random.nextInt(12) + 1);
        } else if (i == 18 && random.nextInt(3) == 0) {
            return new ItemStack(SlimeCarnage.GreenGel, random.nextInt(12) + 1);
        } else if (i == 19) {
            return new ItemStack(SlimeCarnage.OrangeGel, random.nextInt(12) + 1);
        } else if (i == 20) {
            return new ItemStack(SlimeCarnage.ScrollField, 1);
        } else if (i == 21) {
            return new ItemStack(SlimeCarnage.ScrollChurch, 1);
        } else if (i == 22) {
            return new ItemStack(SlimeCarnage.ScrollWell, 1);
        } else if (i == 23) {
            return new ItemStack(SlimeCarnage.ScrollBlacksmith, 1);
        } else if (i == 24) {
            return new ItemStack(SlimeCarnage.ScrollHouse1, 1);
        } else if (i == 25) {
            return new ItemStack(SlimeCarnage.ScrollHouse2, 1);
        } else if (i == 26 && random.nextInt(10) == 0) {
            return new ItemStack(SlimeCarnage.ScrollHouse3, 1);
        } else if (i == 27 && random.nextInt(10) == 0) {
            return new ItemStack(SlimeCarnage.ScrollHouse4, 1);
        } else if (i == 28) {
            return new ItemStack(Items.arrow, 16);
        } else if (i == 29) {
            return new ItemStack(Items.golden_apple, 1);
        } else {
            return i == 30 ? new ItemStack(Items.slime_ball, 4) : null;
        }
    }
}
