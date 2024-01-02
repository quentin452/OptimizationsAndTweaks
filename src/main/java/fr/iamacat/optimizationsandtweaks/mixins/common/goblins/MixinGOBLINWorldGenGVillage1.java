package fr.iamacat.optimizationsandtweaks.mixins.common.goblins;

import goblin.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenTrees;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(GOBLINWorldGenGVillage1.class)
public class MixinGOBLINWorldGenGVillage1 extends GOBLINWorldGen {
    @Shadow
    int houseLoc1;
    @Shadow
    int houseLoc2;

    @Shadow
    public boolean generateVillage(World world, Random rand, int i, int j, int k) {
        int width;
        int boss;
        for(width = 1; width < 20; ++width) {
            for(boss = 4; boss < 26; ++boss) {
                if (world.getBlock(i + width, j - 1, k + boss) == Blocks.grass) {
                    world.setBlock(i + width, j, k + boss, Blocks.grass);
                    world.setBlock(i + width, j - 1, k + boss, Blocks.dirt);
                }

                if (world.getBlock(i + width, j - 2, k + boss) == Blocks.grass) {
                    world.setBlock(i + width, j - 1, k + boss, Blocks.grass);
                    world.setBlock(i + width, j - 2, k + boss, Blocks.dirt);
                }
            }
        }

        int height;
        for(width = 0; width < 5; ++width) {
            for(boss = 0; boss < 10; ++boss) {
                if (rand.nextInt(6) == 1) {
                    world.setBlock(i + 9 + width, j, k + 9 + boss, Blocks.mossy_cobblestone, 0, 2);
                } else {
                    world.setBlock(i + 9 + width, j, k + 9 + boss, Blocks.cobblestone, 0, 2);
                }

                for(height = 1; height <= 5; ++height) {
                    if (world.getBlock(i + 9 + width, j + height, k + 9 + boss) != Blocks.log) {
                        world.setBlock(i + 9 + width, j + height, k + 9 + boss, Blocks.air, 0, 2);
                    }
                }

                world.setBlock(i + 9 + width, j - 1, k + 9 + boss, Blocks.dirt, 0, 2);
            }
        }

        switch (rand.nextInt(4)) {
            case 0:
            case 1:
            case 2:
            case 3:
            default:
                world.setBlock(i + 11, j + 1, k + 16, mod_Goblins.totemR, 0, 2);
                world.setBlock(i + 11, j + 2, k + 16, mod_Goblins.totemG, 0, 2);
                world.setBlock(i + 11, j + 3, k + 16, mod_Goblins.totemB, 0, 2);
                world.setBlock(i + 11, j + 4, k + 16, mod_Goblins.totemY, 0, 2);
                world.setBlock(i + 11, j - 1, k + 11, Blocks.netherrack, 0, 2);
                world.setBlock(i + 11, j, k + 11, Blocks.fire, 0, 2);
                world.setBlock(i + 11, j + 1, k + 11, Blocks.iron_bars, 0, 2);
                world.setBlock(i + 11, j + 1, k + 12, Blocks.double_stone_slab, 0, 2);
                world.setBlock(i + 11, j + 1, k + 10, Blocks.double_stone_slab, 0, 2);
                world.setBlock(i + 10, j + 1, k + 11, Blocks.double_stone_slab, 0, 2);
                world.setBlock(i + 12, j + 1, k + 11, Blocks.double_stone_slab, 0, 2);
                world.setBlock(i + 12, j + 1, k + 17, Blocks.fence, 0, 2);
                world.setBlock(i + 10, j + 1, k + 17, Blocks.fence, 0, 2);
                world.setBlock(i + 12, j + 1, k + 15, Blocks.fence, 0, 2);
                world.setBlock(i + 10, j + 1, k + 15, Blocks.fence, 0, 2);
                world.setBlock(i + 12, j + 2, k + 17, Blocks.torch, 0, 2);
                world.setBlock(i + 10, j + 2, k + 17, Blocks.torch, 0, 2);
                world.setBlock(i + 12, j + 2, k + 15, Blocks.torch, 0, 2);
                world.setBlock(i + 10, j + 2, k + 15, Blocks.torch, 0, 2);

                for(boss = 0; boss < 3; ++boss) {
                    for(height = 0; height < 3; ++height) {
                        world.setBlock(i + 10 + boss, j, k + 15 + height, Blocks.grass, 0, 2);
                    }
                }

                this.houseLoc1 = 3 - rand.nextInt(2);
                this.houseLoc2 = 3 - rand.nextInt(2);
                this.generateRandomCornerHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2, 0);
                this.houseLoc1 = 14 + rand.nextInt(2);
                this.houseLoc2 = 3 - rand.nextInt(2);
                this.generateRandomCornerHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2, 1);
                this.houseLoc1 = 3 - rand.nextInt(2);
                this.houseLoc2 = 19 + rand.nextInt(2);
                this.generateRandomCornerHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2, 2);
                this.houseLoc1 = 14 + rand.nextInt(2);
                this.houseLoc2 = 19 + rand.nextInt(2);
                this.generateRandomCornerHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2, 3);
                if (rand.nextInt(4) != 0) {
                    this.houseLoc1 = 16 + rand.nextInt(2);
                    this.houseLoc2 = 10 + rand.nextInt(2);
                    if (rand.nextInt(2) == 0) {
                        this.generateLeftWolfPen(world, rand, i + this.houseLoc1, j, k + this.houseLoc2);
                    } else {
                        this.generateLeftHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2);
                    }
                } else {
                    this.houseLoc1 = 19 + rand.nextInt(2);
                    this.houseLoc2 = 15;
                    this.generateTopLeftCornerHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2);

                    for(boss = 0; boss < 1; ++boss) {
                        for(height = 0; height < 1; ++height) {
                            if (world.getBlock(i + this.houseLoc1 + boss + 0, j + 1, k + this.houseLoc2 + height + 0) != Blocks.log) {
                                world.setBlock(i + this.houseLoc1 + boss + 0, j + 1, k + this.houseLoc2 + height + 0, Blocks.air, 0, 2);
                            }
                        }
                    }

                    this.houseLoc1 = 19 + rand.nextInt(2);
                    this.houseLoc2 = 7;
                    this.generateBottomLeftCornerHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2);

                    for(boss = 0; boss < 1; ++boss) {
                        for(height = 0; height < 1; ++height) {
                            if (world.getBlock(i + this.houseLoc1 + boss + 0, j + 1, k + this.houseLoc2 + height + 5) != Blocks.log) {
                                world.setBlock(i + this.houseLoc1 + boss + 0, j + 1, k + this.houseLoc2 + height + 5, Blocks.air, 0, 2);
                            }
                        }
                    }
                }

                if (rand.nextInt(4) != 0) {
                    this.houseLoc1 = 1 - rand.nextInt(2);
                    this.houseLoc2 = 10 + rand.nextInt(2);
                    if (rand.nextInt(2) == 0) {
                        this.generateRightMine(world, rand, i + this.houseLoc1, j, k + this.houseLoc2);
                    } else {
                        this.generateRightHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2);
                    }
                } else {
                    this.houseLoc1 = -3 - rand.nextInt(2);
                    this.houseLoc2 = 15;
                    this.generateTopRightCornerHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2);

                    for(boss = 0; boss < 1; ++boss) {
                        for(height = 0; height < 1; ++height) {
                            if (world.getBlock(i + this.houseLoc1 + boss + 0, j + 1, k + this.houseLoc2 + height + 0) != Blocks.log) {
                                world.setBlock(i + this.houseLoc1 + boss + 0, j + 1, k + this.houseLoc2 + height + 0, Blocks.air, 0, 2);
                            }
                        }
                    }

                    this.houseLoc1 = -3 - rand.nextInt(2);
                    this.houseLoc2 = 7;
                    this.generateBottomRightCornerHouse(world, rand, i + this.houseLoc1, j, k + this.houseLoc2);

                    for(boss = 0; boss < 1; ++boss) {
                        for(height = 0; height < 1; ++height) {
                            if (world.getBlock(i + this.houseLoc1 + boss + 0, j + 1, k + this.houseLoc2 + height + 5) != Blocks.log) {
                                world.setBlock(i + this.houseLoc1 + boss + 0, j + 1, k + this.houseLoc2 + height + 5, Blocks.air, 0, 2);
                            }
                        }
                    }
                }

                this.houseLoc1 = 8;
                this.houseLoc2 = 24;

                for(boss = 0; boss <= 5; ++boss) {
                    for(height = 1; height <= 4; ++height) {
                        for(width = 1; width <= 4; ++width) {
                            world.setBlock(i + boss + this.houseLoc1, j + width, k + height + this.houseLoc2, Blocks.air, 0, 2);
                        }
                    }
                }

                for(boss = 0; boss <= 6; ++boss) {
                    for(height = 0; height <= 5; ++height) {
                        for(width = 0; width <= 4; ++width) {
                            if (width == 0) {
                                world.setBlock(i + boss + this.houseLoc1, j - width, k + height + this.houseLoc2, Blocks.grass, 0, 2);
                            } else if (width > 0 && width < 3) {
                                world.setBlock(i + boss + this.houseLoc1, j - width, k + height + this.houseLoc2, Blocks.dirt, 0, 2);
                            } else {
                                world.setBlock(i + boss + this.houseLoc1, j - width, k + height + this.houseLoc2, Blocks.stone, 0, 2);
                            }
                        }
                    }
                }

                for(boss = 0; boss <= 4; ++boss) {
                    world.setBlock(i + 11, j, k + 19 + boss, Blocks.cobblestone, 0, 2);

                    for(height = 1; height <= 4; ++height) {
                        for(width = -1; width <= 1; ++width) {
                            if (world.getBlock(i + 11 + width, j + height, k + 19 + boss) != Blocks.log) {
                                world.setBlock(i + 11 + width, j + height, k + 19 + boss, Blocks.air, 0, 2);
                            }
                        }
                    }
                }

                for(boss = 1; boss <= 2; ++boss) {
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 0, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 0, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 1, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 1, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 4, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 3, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 2, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 5, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 5, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 6, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 6, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 6, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 6, j + boss, k + this.houseLoc2 + 4, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 6, j + boss, k + this.houseLoc2 + 3, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 6, j + boss, k + this.houseLoc2 + 2, Blocks.cobblestone, 0, 2);
                }

                for(boss = 3; boss <= 3; ++boss) {
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 1, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 0, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Blocks.cobblestone, 0, 2);
                }

                for(boss = 4; boss <= 4; ++boss) {
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 3, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Blocks.cobblestone, 0, 2);
                }

                for(boss = 5; boss <= 5; ++boss) {
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Blocks.cobblestone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Blocks.cobblestone, 0, 2);
                }

                for(boss = 0; boss <= 0; ++boss) {
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 1, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 0, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 3, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Blocks.planks, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Blocks.planks, 0, 2);
                }

                for(boss = 1; boss <= 4; ++boss) {
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Blocks.fence, 0, 2);
                }

                for(boss = 2; boss <= 2; ++boss) {
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 - 1, Blocks.torch, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 - 1, Blocks.torch, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Blocks.torch, 0, 2);
                }

                for(boss = 1; boss <= 1; ++boss) {
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Blocks.stonebrick, 3, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Blocks.stone, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss + 1, k + this.houseLoc2 + 3, Blocks.brewing_stand, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Blocks.stonebrick, 3, 2);
                    world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Blocks.stonebrick, 3, 2);
                    world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Blocks.stone_brick_stairs, 2, 2);
                    world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Blocks.stonebrick, 3, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Blocks.stonebrick, 3, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Blocks.cauldron, 0, 2);
                    world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Blocks.stonebrick, 3, 2);
                }

                boss = rand.nextInt(2);
                if (boss == 0) {
                    GOBLINEntityGoblinMage goblinmage = new GOBLINEntityGoblinMage(world);
                    goblinmage.setLocationAndAngles((double)(i + this.houseLoc1 + 3), (double)(j + 1), (double)(k + this.houseLoc2 - 1), world.rand.nextFloat() * 360.0F, 0.0F);
                    goblinmage.setPosition((double)(i + this.houseLoc1 + 3), (double)(j + 1), (double)(k + this.houseLoc2 - 1));
                    world.spawnEntityInWorld(goblinmage);
                } else {
                    GOBLINEntityGoblinLord goblinlord = new GOBLINEntityGoblinLord(world);
                    goblinlord.setLocationAndAngles((double)(i + this.houseLoc1 + 3), (double)(j + 1), (double)(k + this.houseLoc2 - 1), world.rand.nextFloat() * 360.0F, 0.0F);
                    goblinlord.setPosition((double)(i + this.houseLoc1 + 3), (double)(j + 1), (double)(k + this.houseLoc2 - 1));
                    world.spawnEntityInWorld(goblinlord);
                }

                this.generateTrees(world, rand, i + 11, j, k + 16, 15 + rand.nextInt(15));
                this.generatePoles(world, rand, i + 11, j, k + 16, rand.nextInt(10));
                if (rand.nextInt(2) == 0) {
                    this.generateWalls(world, rand, i + 11, j, k + 16, 4 + rand.nextInt(3));
                }

                return true;
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random rand, int i, int j, int k) {
        int chunkX = i >> 4;
        int chunkZ = k >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if(!chunk.isChunkLoaded) {
            return false;
        }
        if (world.getBlock(i, j, k) == Blocks.grass && world.getBlock(i + 21, j, k + 30) == Blocks.grass && this.canGenerate(world, rand, i, j, k) && world.getBlock(i + 21, j + 10, k + 30) == Blocks.air && world.getBlock(i, j + 10, k) == Blocks.air) {
            int a = -15;

            while(true) {
                if (a > 45) {
                    this.generateVillage(world, rand, i, j, k);
                    break;
                }

                for(int b = -15; b <= 55; ++b) {
                    if (world.getBlock(i + a, j + 3, k + b) == Blocks.planks || world.getBlock(i + a, j + 3, k + b) == Blocks.cobblestone || world.getBlock(i + a, j + 3, k + b) == Blocks.stonebrick) {
                        return false;
                    }
                }

                ++a;
            }
        }

        return false;
    }
    @Shadow
    public void generateRandomCornerHouse(World world, Random rand, int i, int j, int k, int preference) {
        int choice = rand.nextInt(4);
        if (rand.nextInt(3) == 0) {
            choice = preference;
        }

        int width;
        int length;
        switch (choice) {
            case 0:
                this.generateBottomRightCornerHouse(world, rand, i, j, k);

                for(width = 0; width < 1; ++width) {
                    for(length = 0; length < 1; ++length) {
                        if (world.getBlock(i + width + 5, j + 1, k + length + 5) != Blocks.log) {
                            world.setBlock(i + width + 5, j + 1, k + length + 5, Blocks.air, 0, 2);
                        }
                    }
                }

                return;
            case 1:
                this.generateBottomLeftCornerHouse(world, rand, i, j, k);

                for(width = 0; width < 1; ++width) {
                    for(length = 0; length < 1; ++length) {
                        if (world.getBlock(i + width + 0, j + 1, k + length + 5) != Blocks.log) {
                            world.setBlock(i + width + 0, j + 1, k + length + 5, Blocks.air, 0, 2);
                        }
                    }
                }

                return;
            case 2:
                this.generateTopRightCornerHouse(world, rand, i, j, k);

                for(width = 0; width < 1; ++width) {
                    for(length = 0; length < 1; ++length) {
                        if (world.getBlock(i + width + 5, j + 1, k + length + 0) != Blocks.log) {
                            world.setBlock(i + width + 5, j + 1, k + length + 0, Blocks.air, 0, 2);
                        }
                    }
                }

                return;
            case 3:
                this.generateTopLeftCornerHouse(world, rand, i, j, k);

                for(width = 0; width < 1; ++width) {
                    for(length = 0; length < 1; ++length) {
                        if (world.getBlock(i + width + 0, j + 1, k + length + 0) != Blocks.log) {
                            world.setBlock(i + width + 0, j + 1, k + length + 0, Blocks.air, 0, 2);
                        }
                    }
                }
        }

    }
    @Shadow
    public boolean generateWallSouth(World world, Random rand, int i, int j, int k) {
        int limit = 17 - rand.nextInt(7);
        int rangeLeft = 0;
        int rangeRight = 0;

        int x;
        Block block;
        for(x = 0; x < limit / 2; ++x) {
            block = world.getBlock(i + x, j, k);
            if (block != Blocks.air && !block.isReplaceable(world, i + x, j, k) || world.getBlock(i + x, j - 1, k) != Blocks.grass) {
                break;
            }

            block = world.getBlock(i + x, j, k - 1);
            if (block != Blocks.air && !block.isReplaceable(world, i + x, j, k - 1) || world.getBlock(i + x, j - 1, k - 1) != Blocks.grass) {
                break;
            }

            ++rangeRight;
        }

        for(x = 0; x > -limit / 2; --x) {
            block = world.getBlock(i + x, j, k);
            if (block != Blocks.air && !block.isReplaceable(world, i + x, j, k) || world.getBlock(i + x, j - 1, k) != Blocks.grass) {
                break;
            }

            block = world.getBlock(i + x, j, k - 1);
            if (block != Blocks.air && !block.isReplaceable(world, i + x, j, k - 1) || world.getBlock(i + x, j - 1, k - 1) != Blocks.grass) {
                break;
            }

            --rangeLeft;
        }

        for(x = rangeLeft; x < rangeRight; ++x) {
            for(int k1 = rangeLeft; k1 < rangeRight; ++k1) {
                for(int j1 = rangeLeft; j1 < rangeRight; ++j1) {
                    if (world.getBlock(i + x, j + j1, k + k1) == Blocks.planks || world.getBlock(i + x, j + j1, k + k1) == Blocks.oak_stairs) {
                        return false;
                    }
                }
            }
        }

        ++rangeLeft;
        if (rangeRight - rangeLeft > 6) {
            for(x = rangeLeft; x < rangeRight; ++x) {
                if (x == rangeLeft) {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.oak_stairs, 0, 3);
                    world.setBlock(i + x, j, k - 1, Blocks.oak_stairs, 0, 3);
                } else if (x == rangeRight - 1) {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j, k - 1, Blocks.oak_stairs, 1, 3);
                } else if (x == rangeLeft + 1) {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.planks);
                    world.setBlock(i + x, j + 2, k, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j + 1, k - 1, Blocks.oak_stairs, 0, 3);
                    world.setBlock(i + x, j, k - 1, Blocks.planks);
                } else if (x == rangeRight - 2) {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.planks);
                    world.setBlock(i + x, j + 2, k, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j + 1, k - 1, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j, k - 1, Blocks.planks);
                } else {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k - 1, Blocks.oak_stairs, 6, 3);
                    world.setBlock(i + x, j + 2, k, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j + 2, k - 1, Blocks.air);
                    world.setBlock(i + x, j + 3, k - 1, Blocks.air);
                    world.setBlock(i + x, j + 3, k, Blocks.air);
                    if (rand.nextInt(2) == 0 && world.getBlock(i + x + 1, j + 2, k - 1) != Blocks.wooden_slab && world.getBlock(i + x - 1, j + 2, k - 1) != Blocks.wooden_slab) {
                        world.setBlock(i + x, j + 2, k - 1, Blocks.wooden_slab);
                        GOBLINEntityGoblinRangerGuard guard = new GOBLINEntityGoblinRangerGuard(world);
                        guard.setLocationAndAngles((double)(i + x) + 0.5, (double)(j + 3), (double)(k - 1) + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
                        guard.setPosition((double)(i + x) + 0.5, (double)(j + 3), (double)(k - 1) + 0.5);
                        world.spawnEntityInWorld(guard);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
    @Shadow
    public boolean generateWallNorth(World world, Random rand, int i, int j, int k) {
        int limit = 17 - rand.nextInt(7);
        int rangeLeft = 0;
        int rangeRight = 0;

        int x;
        Block block;
        for(x = 0; x < limit / 2; ++x) {
            block = world.getBlock(i + x, j, k);
            if (block != Blocks.air && !block.isReplaceable(world, i + x, j, k) || world.getBlock(i + x, j - 1, k) != Blocks.grass) {
                break;
            }

            block = world.getBlock(i + x, j, k + 1);
            if (block != Blocks.air && !block.isReplaceable(world, i + x, j, k + 1) || world.getBlock(i + x, j - 1, k + 1) != Blocks.grass) {
                break;
            }

            ++rangeRight;
        }

        for(x = 0; x > -limit / 2; --x) {
            block = world.getBlock(i + x, j, k);
            if (block != Blocks.air && !block.isReplaceable(world, i + x, j, k) || world.getBlock(i + x, j - 1, k) != Blocks.grass) {
                break;
            }

            block = world.getBlock(i + x, j, k + 1);
            if (block != Blocks.air && !block.isReplaceable(world, i + x, j, k + 1) || world.getBlock(i + x, j - 1, k + 1) != Blocks.grass) {
                break;
            }

            --rangeLeft;
        }

        for(x = rangeLeft; x < rangeRight; ++x) {
            for(int k1 = rangeLeft; k1 < rangeRight; ++k1) {
                for(int j1 = rangeLeft; j1 < rangeRight; ++j1) {
                    if (world.getBlock(i + x, j + j1, k + k1) == Blocks.planks || world.getBlock(i + x, j + j1, k + k1) == Blocks.oak_stairs) {
                        return false;
                    }
                }
            }
        }

        ++rangeLeft;
        if (rangeRight - rangeLeft > 6) {
            for(x = rangeLeft; x < rangeRight; ++x) {
                if (x == rangeLeft) {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.oak_stairs, 0, 3);
                    world.setBlock(i + x, j, k + 1, Blocks.oak_stairs, 0, 3);
                } else if (x == rangeRight - 1) {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j, k + 1, Blocks.oak_stairs, 1, 3);
                } else if (x == rangeLeft + 1) {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.planks);
                    world.setBlock(i + x, j + 2, k, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j + 1, k + 1, Blocks.oak_stairs, 0, 3);
                    world.setBlock(i + x, j, k + 1, Blocks.planks);
                } else if (x == rangeRight - 2) {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.planks);
                    world.setBlock(i + x, j + 2, k, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j + 1, k + 1, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j, k + 1, Blocks.planks);
                } else {
                    world.setBlock(i + x, j, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k, Blocks.planks);
                    world.setBlock(i + x, j + 1, k + 1, Blocks.oak_stairs, 7, 3);
                    world.setBlock(i + x, j + 2, k, Blocks.oak_stairs, 1, 3);
                    world.setBlock(i + x, j + 2, k + 1, Blocks.air);
                    world.setBlock(i + x, j + 3, k + 1, Blocks.air);
                    world.setBlock(i + x, j + 3, k, Blocks.air);
                    if (rand.nextInt(2) == 0 && world.getBlock(i + x + 1, j + 2, k + 1) != Blocks.wooden_slab && world.getBlock(i + x - 1, j + 2, k + 1) != Blocks.wooden_slab) {
                        world.setBlock(i + x, j + 2, k + 1, Blocks.wooden_slab);
                        GOBLINEntityGoblinRangerGuard guard = new GOBLINEntityGoblinRangerGuard(world);
                        guard.setLocationAndAngles((double)(i + x) + 0.5, (double)(j + 3), (double)(k + 1) + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
                        guard.setPosition((double)(i + x) + 0.5, (double)(j + 3), (double)(k + 1) + 0.5);
                        world.spawnEntityInWorld(guard);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
    @Shadow
    public boolean generateWallWest(World world, Random rand, int i, int j, int k) {
        int limit = 17 - rand.nextInt(7);
        int rangeLeft = 0;
        int rangeRight = 0;

        int x;
        Block block;
        for(x = 0; x < limit / 2; ++x) {
            block = world.getBlock(i, j, k + x);
            if (block != Blocks.air && !block.isReplaceable(world, i, j, k + x) || world.getBlock(i, j - 1, k + x) != Blocks.grass) {
                break;
            }

            block = world.getBlock(i - 1, j, k + x);
            if (block != Blocks.air && !block.isReplaceable(world, i - 1, j, k + x) || world.getBlock(i - 1, j - 1, k + x) != Blocks.grass) {
                break;
            }

            ++rangeRight;
        }

        for(x = 0; x > -limit / 2; --x) {
            block = world.getBlock(i, j, k + x);
            if (block != Blocks.air && !block.isReplaceable(world, i, j, k + x) || world.getBlock(i, j - 1, k + x) != Blocks.grass) {
                break;
            }

            block = world.getBlock(i - 1, j, k + x);
            if (block != Blocks.air && !block.isReplaceable(world, i - 1, j, k + x) || world.getBlock(i - 1, j - 1, k + x) != Blocks.grass) {
                break;
            }

            --rangeLeft;
        }

        for(x = rangeLeft; x < rangeRight; ++x) {
            for(int k1 = rangeLeft; k1 < rangeRight; ++k1) {
                for(int j1 = rangeLeft; j1 < rangeRight; ++j1) {
                    if (world.getBlock(i + x, j + j1, k + k1) == Blocks.planks || world.getBlock(i + x, j + j1, k + k1) == Blocks.oak_stairs) {
                        return false;
                    }
                }
            }
        }

        ++rangeLeft;
        if (rangeRight - rangeLeft > 6) {
            for(x = rangeLeft; x < rangeRight; ++x) {
                if (x == rangeLeft) {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.oak_stairs, 2, 3);
                    world.setBlock(i - 1, j, k + x, Blocks.oak_stairs, 2, 3);
                } else if (x == rangeRight - 1) {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i - 1, j, k + x, Blocks.oak_stairs, 3, 3);
                } else if (x == rangeLeft + 1) {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.planks);
                    world.setBlock(i, j + 2, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i - 1, j + 1, k + x, Blocks.oak_stairs, 2, 3);
                    world.setBlock(i - 1, j, k + x, Blocks.planks);
                } else if (x == rangeRight - 2) {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.planks);
                    world.setBlock(i, j + 2, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i - 1, j + 1, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i - 1, j, k + x, Blocks.planks);
                } else {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.planks);
                    world.setBlock(i - 1, j + 1, k + x, Blocks.oak_stairs, 4, 3);
                    world.setBlock(i, j + 2, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i - 1, j + 2, k + x, Blocks.air);
                    world.setBlock(i - 1, j + 3, k + x, Blocks.air);
                    world.setBlock(i, j + 3, k + x, Blocks.air);
                    if (rand.nextInt(2) == 0 && world.getBlock(i - 1, j + 2, k + x + 1) != Blocks.wooden_slab && world.getBlock(i - 1, j + 2, k + x - 1) != Blocks.wooden_slab) {
                        world.setBlock(i - 1, j + 2, k + x, Blocks.wooden_slab);
                        GOBLINEntityGoblinRangerGuard guard = new GOBLINEntityGoblinRangerGuard(world);
                        guard.setLocationAndAngles((double)(i - 1) + 0.5, (double)(j + 3), (double)(k + x) + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
                        guard.setPosition((double)(i - 1) + 0.5, (double)(j + 3), (double)(k + x) + 0.5);
                        world.spawnEntityInWorld(guard);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
    @Shadow
    public boolean generateWallEast(World world, Random rand, int i, int j, int k) {
        int limit = 17 - rand.nextInt(7);
        int rangeLeft = 0;
        int rangeRight = 0;

        int x;
        Block block;
        for(x = 0; x < limit / 2; ++x) {
            block = world.getBlock(i, j, k + x);
            if (block != Blocks.air && !block.isReplaceable(world, i, j, k + x) || world.getBlock(i, j - 1, k + x) != Blocks.grass) {
                break;
            }

            block = world.getBlock(i + 1, j, k + x);
            if (block != Blocks.air && !block.isReplaceable(world, i + 1, j, k + x) || world.getBlock(i + 1, j - 1, k + x) != Blocks.grass) {
                break;
            }

            ++rangeRight;
        }

        for(x = 0; x > -limit / 2; --x) {
            block = world.getBlock(i, j, k + x);
            if (block != Blocks.air && !block.isReplaceable(world, i, j, k + x) || world.getBlock(i, j - 1, k + x) != Blocks.grass) {
                break;
            }

            block = world.getBlock(i + 1, j, k + x);
            if (block != Blocks.air && !block.isReplaceable(world, i + 1, j, k + x) || world.getBlock(i + 1, j - 1, k + x) != Blocks.grass) {
                break;
            }

            --rangeLeft;
        }

        for(x = rangeLeft; x < rangeRight; ++x) {
            for(int k1 = rangeLeft; k1 < rangeRight; ++k1) {
                for(int j1 = rangeLeft; j1 < rangeRight; ++j1) {
                    if (world.getBlock(i + x, j + j1, k + k1) == Blocks.planks || world.getBlock(i + x, j + j1, k + k1) == Blocks.oak_stairs) {
                        return false;
                    }
                }
            }
        }

        ++rangeLeft;
        if (rangeRight - rangeLeft > 6) {
            for(x = rangeLeft; x < rangeRight; ++x) {
                if (x == rangeLeft) {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.oak_stairs, 2, 3);
                    world.setBlock(i + 1, j, k + x, Blocks.oak_stairs, 2, 3);
                } else if (x == rangeRight - 1) {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i + 1, j, k + x, Blocks.oak_stairs, 3, 3);
                } else if (x == rangeLeft + 1) {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.planks);
                    world.setBlock(i, j + 2, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i + 1, j + 1, k + x, Blocks.oak_stairs, 2, 3);
                    world.setBlock(i + 1, j, k + x, Blocks.planks);
                } else if (x == rangeRight - 2) {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.planks);
                    world.setBlock(i, j + 2, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i + 1, j + 1, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i + 1, j, k + x, Blocks.planks);
                } else {
                    world.setBlock(i, j, k + x, Blocks.planks);
                    world.setBlock(i, j + 1, k + x, Blocks.planks);
                    world.setBlock(i + 1, j + 1, k + x, Blocks.oak_stairs, 5, 3);
                    world.setBlock(i, j + 2, k + x, Blocks.oak_stairs, 3, 3);
                    world.setBlock(i + 1, j + 2, k + x, Blocks.air);
                    world.setBlock(i + 1, j + 3, k + x, Blocks.air);
                    world.setBlock(i, j + 3, k + x, Blocks.air);
                    if (rand.nextInt(2) == 0 && world.getBlock(i + 1, j + 2, k + x + 1) != Blocks.wooden_slab && world.getBlock(i + 1, j + 2, k + x - 1) != Blocks.wooden_slab) {
                        world.setBlock(i + 1, j + 2, k + x, Blocks.wooden_slab);
                        GOBLINEntityGoblinRangerGuard guard = new GOBLINEntityGoblinRangerGuard(world);
                        guard.setLocationAndAngles((double)(i + 1) + 0.5, (double)(j + 3), (double)(k + x) + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
                        guard.setPosition((double)(i + 1) + 0.5, (double)(j + 3), (double)(k + x) + 0.5);
                        world.spawnEntityInWorld(guard);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
    @Shadow
    public void generateTrees(World world, Random rand, int i, int j, int k, int numTrees) {
        for(int x = 0; x < numTrees; ++x) {
            int i1;
            int k1;
            do {
                do {
                    i1 = rand.nextInt(30) - rand.nextInt(30);
                    k1 = rand.nextInt(30) - rand.nextInt(30);
                } while(i1 < 8 && i1 > -8);
            } while(k1 < 8 && k1 > -8);

            for(int j1 = -10; j1 < 10; ++j1) {
                if (world.getBlock(i1 + i, j + j1, k1 + k) == Blocks.grass && world.getBlock(i1 + i, j + j1 + 1, k1 + k) == Blocks.air) {
                    if (rand.nextInt(2) == 0) {
                        (new WorldGenTrees(true)).generate(world, rand, i + i1, j + j1 + 1, k + k1);
                    } else {
                        world.setBlock(i + i1, j + j1 + 1, k + k1, Blocks.log);
                    }
                    break;
                }
            }
        }

    }
    @Shadow
    public void generatePoles(World world, Random rand, int i, int j, int k, int numPoles) {
        for(int x = 0; x < numPoles; ++x) {
            int i1;
            int k1;
            do {
                do {
                    i1 = rand.nextInt(20) - rand.nextInt(20);
                    k1 = rand.nextInt(20) - rand.nextInt(20);
                } while(i1 < 8 && i1 > -8);
            } while(k1 < 8 && k1 > -8);

            for(int j1 = -10; j1 < 10; ++j1) {
                if (world.getBlock(i1 + i, j + j1, k1 + k) == Blocks.grass && world.getBlock(i1 + i, j + j1 + 1, k1 + k) == Blocks.air) {
                    world.setBlock(i + i1, j + j1 + 1, k + k1, Blocks.fence);
                    world.setBlock(i + i1, j + j1 + 2, k + k1, Blocks.skull, 1, 3);
                    break;
                }
            }
        }

    }

    public void generateWalls(World world, Random rand, int i, int j, int k, int numWalls) {
        for(int x = 0; x < numWalls; ++x) {
            int generateCount = 0;
            int i1 = 0;
            int k1 = 0;

            do {
                if (rand.nextInt(2) == 0) {
                    i1 = 14 + rand.nextInt(5);
                    if (rand.nextInt(2) == 0) {
                        i1 *= -1;
                    }

                    k1 = rand.nextInt(13) - rand.nextInt(13);
                }

                if (rand.nextInt(2) == 0) {
                    k1 = 16 + rand.nextInt(5);
                    if (rand.nextInt(2) == 0) {
                        k1 *= -1;
                    }

                    i1 = rand.nextInt(13) - rand.nextInt(13);
                }

                for(int j1 = -10; j1 < 10; ++j1) {
                    Block block = world.getBlock(i1 + i, j + j1 + 1, k1 + k);
                    if ((block == Blocks.air || block.isReplaceable(world, i1 + i, j + j1 + 1, k1 + k)) && world.getBlock(i1 + i, j + j1, k1 + k) == Blocks.grass) {
                        int i2 = Math.abs(i1);
                        int k2 = Math.abs(k1);
                        if (k1 < 0) {
                            if (k2 > i2) {
                                if (this.generateWallNorth(world, rand, i + i1, j + j1 + 1, k + k1)) {
                                    generateCount = 1000;
                                    break;
                                }

                                ++generateCount;
                            } else if (i1 < 0) {
                                if (this.generateWallEast(world, rand, i + i1, j + j1 + 1, k + k1)) {
                                    generateCount = 1000;
                                    break;
                                }

                                ++generateCount;
                            } else {
                                if (this.generateWallWest(world, rand, i + i1, j + j1 + 1, k + k1)) {
                                    generateCount = 1000;
                                    break;
                                }

                                ++generateCount;
                            }
                        } else if (k1 >= 0) {
                            if (k2 > i2) {
                                if (this.generateWallSouth(world, rand, i + i1, j + j1 + 1, k + k1)) {
                                    generateCount = 1000;
                                    break;
                                }

                                ++generateCount;
                            } else if (i1 < 0) {
                                if (this.generateWallEast(world, rand, i + i1, j + j1 + 1, k + k1)) {
                                    generateCount = 1000;
                                    break;
                                }

                                ++generateCount;
                            } else {
                                if (this.generateWallWest(world, rand, i + i1, j + j1 + 1, k + k1)) {
                                    generateCount = 1000;
                                    break;
                                }

                                ++generateCount;
                            }
                        }
                    }
                }
            } while(generateCount < 800);
        }

    }
    @Shadow
    public boolean canGenerate(World world, Random rand, int i, int j, int k) {
        int countGrass = 0;

        for(int i1 = 0; i1 <= 20; ++i1) {
            for(int k1 = 0; k1 <= 30; ++k1) {
                for(int j1 = -1; j1 <= 1; ++j1) {
                    if (world.getBlock(i + i1, j + j1, k + k1) == Blocks.grass) {
                        if (j1 == 1) {
                            ++countGrass;
                        } else {
                            countGrass += 2;
                        }
                    }
                }
            }
        }

        if (countGrass > 1100) {
            return true;
        } else {
            return false;
        }
    }
}
