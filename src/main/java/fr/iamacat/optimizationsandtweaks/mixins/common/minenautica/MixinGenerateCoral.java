package fr.iamacat.optimizationsandtweaks.mixins.common.minenautica;

import com.minenautica.Minenautica.Biomes.GenerateCoral;
import com.minenautica.Minenautica.Blocks.TechneRenderings.CanBlockStay;
import com.minenautica.Minenautica.CustomRegistry.BlocksAndItems;
import com.minenautica.Minenautica.Schematics.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(GenerateCoral.class)
public class MixinGenerateCoral {
    @Shadow
    private Block blockToSet;
    @Shadow
    private int metadata;
    @Shadow
    private static final Block[] sandSubArray;
    @Shadow
    private static final Block[][] sandArray;
    @Shadow
    private static final Block[] bloodMossSubArray;
    @Shadow
    private static final Block[][] bloodMossArray;
    @Shadow
    private static final Block[] royalBrittleSubArray;
    @Shadow
    private static final Block[][] royalBrittleArray;
    @Shadow
    private static final Block[] commonGrassyPlateausCoralTypes1;
    @Shadow
    private static final Block[] uncommonGrassyPlateausCoralTypes1;
    @Shadow
    private static final Block[][] grassyPlateausCoralTypes1;
    @Shadow
    private static final Block[] commonGrassyPlateausCoralTypes2;
    @Shadow
    private static final Block[] uncommonGrassyPlateausCoralTypes2;
    @Shadow
    private static final Block[][] grassyPlateausCoralTypes2;
    @Shadow
    private static final Block[] commonGrassyPlateausCoralTypes3;
    @Shadow
    private static final Block[][] grassyPlateausCoralTypes3;
    @Shadow
    private static final Block[] commonGrassyPlateausCoralTypes4;
    @Shadow
    private static final Block[][] grassyPlateausCoralTypes4;
    @Shadow
    private static final Block[] safeShallowsCoralSubArray;
    @Shadow
    private static final Block[][] safeShallowsCoralArray;
    @Shadow
    private static final Block[] commonSafeShallowsTypes1;
    @Shadow
    private static final Block[] uncommonSafeShallowsTypes1;
    @Shadow
    private static final Block[] rareSafeShallowsTypes1;
    @Shadow
    private static final Block[] extraRareSafeShallowsTypes1;
    @Shadow
    private static final Block[][] safeShallowsTypes1;
    @Shadow
    private static final Block[] commonSafeShallowsTypes2;
    @Shadow
    private static final Block[] common2SafeShallowsTypes2;
    @Shadow
    private static final Block[][] safeShallowsTypes2;
    @Shadow
    private static final Block[] commonSafeShallowsTypes3;
    @Shadow
    private static final Block[][] safeShallowsTypes3;
    @Shadow
    private static final Block[] kelpForestSandSubArray;
    @Shadow
    private static final Block[][] kelpForestSandArray;
    @Shadow
    private static final Block[] commonKelpForestTypes1;
    @Shadow
    private static final Block[][] kelpForestTypes1;
    @Unique
    private Random random = new Random();
    @Unique
    int x1;
    @Unique
    int y1;
    @Unique
    int z1;
    @Unique
    int random2;
    @Unique
    int type;
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generateGrassyPlateausCoral(World world, Random random, int x1, int y1, int z1) {
        for (int l = 0; l < 1110; ++l) {
            optimizationsAndTweaks$generateCoral(world, random);
            optimizationsAndTweaks$generateLithiumOutcrop(world, random, x1, y1, z1);
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateCoral(World world, Random random) {
        int i1 = x1 + random.nextInt(8) - random.nextInt(8);
        int j1 = y1 + random.nextInt(24) - random.nextInt(24);
        int k1 = z1 + random.nextInt(8) - random.nextInt(8);
        this.blockToSet = BlocksAndItems.bloodgrass;
        if ((!world.provider.hasNoSky || j1 < 255) && this.blockToSet.canBlockStay(world, i1, j1, k1)) {
            random2 = random.nextInt(60);
            if (random2 == 0) {
                type = random.nextInt(20);
                if (type == 0 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire1.generate(world, random, i1, j1, k1);
                }

                if (type == 1 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire2.generate(world, random, i1, j1, k1);
                }

                if (type == 2 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire3.generate(world, random, i1, j1, k1);
                }

                if (type == 3 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire4.generate(world, random, i1, j1, k1);
                }

                if (type == 4 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire5.generate(world, random, i1, j1, k1);
                }

                if (type == 5 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire6.generate(world, random, i1, j1, k1);
                }

                if (type == 6 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire7.generate(world, random, i1, j1, k1);
                }

                if (type == 7 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire8.generate(world, random, i1, j1, k1);
                }

                if (type == 8 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire10.generate(world, random, i1, j1, k1);
                }

                if (type == 9 && this.canStructureSpawn(world, i1, j1, k1, 15, 15, 5.0F)) {
                    Spire11.generate(world, random, i1, j1, k1);
                }

                if (type == 10 && this.canStructureSpawn(world, i1, j1, k1, 15, 15, 5.0F)) {
                    Spire12.generate(world, random, i1, j1, k1);
                }

                if (type == 11 && this.canStructureSpawn(world, i1, j1, k1, 15, 15, 5.0F)) {
                    Spire13.generate(world, random, i1, j1, k1);
                }

                if (type == 12 && this.canStructureSpawn(world, i1, j1, k1, 15, 15, 5.0F)) {
                    Spire14.generate(world, random, i1, j1, k1);
                }

                if (type == 13 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire15.generate(world, random, i1, j1, k1);
                }

                if (type == 14 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire16.generate(world, random, i1, j1, k1);
                }

                if (type == 15 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F)) {
                    Spire20.generate(world, random, i1, j1, k1);
                }

                if (type == 16 && this.canStructureSpawn(world, i1, j1, k1, 15, 15, 5.0F)) {
                    Spire21.generate(world, random, i1, j1, k1);
                }

                if (type == 17 && this.canStructureSpawn(world, i1, j1, k1, 15, 15, 5.0F)) {
                    Spire22.generate(world, random, i1, j1, k1);
                }

                if (type == 18 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F) && this.canStructureSpawn(world, i1 + 15, j1, k1, 10, 10, 5.0F)) {
                    Arch1.generate(world, random, i1, j1, k1);
                }

                if (type == 19 && this.canStructureSpawn(world, i1, j1, k1, 10, 10, 5.0F) && this.canStructureSpawn(world, i1, j1, k1 + 15, 10, 10, 5.0F)) {
                    Arch2.generate(world, random, i1, j1, k1);
                }
            } else if (random2 == 2) {
                world.setBlock(i1, j1, k1, BlocksAndItems.furledPapyrus);
            } else if (random2 <= 6) {
                if (world.getBlock(i1, j1, k1).getMaterial() == Material.water && world.getBlock(i1, j1 - 1, k1).getMaterial() != Material.water) {
                    type = random.nextInt(4) + 2;
                    world.setBlock(i1, j1, k1, BlocksAndItems.saltDeposit, type, 2);
                }
            } else if (world.getBlock(i1, j1, k1) == BlocksAndItems.saltWater) {
                type = random.nextInt(10);
                if (type == 0) {
                    int royalBrittleChance = random.nextInt(5);
                    if (royalBrittleChance <= 3) {
                        this.createCoralCluster(world, random, i1, j1 - 1, k1, royalBrittleArray, 0.6F, true, true);
                    }

                    int varient = random.nextInt(3);
                    if (varient == 0) {
                        this.createCoralCluster(world, random, i1, j1, k1, grassyPlateausCoralTypes2, 0.9F, false, true);
                    }

                    if (varient == 1) {
                        this.createCoralCluster(world, random, i1, j1, k1, grassyPlateausCoralTypes3, 1.0F, false, true);
                    }

                    if (varient == 2) {
                        this.createCoralCluster(world, random, i1, j1, k1, grassyPlateausCoralTypes4, 0.7F, false, true);
                    }
                } else {
                    if (type >= 3) {
                        this.createCoralCluster(world, random, i1, j1 - 1, k1, bloodMossArray, 0.6F, true, true);
                    }

                    this.createCoralCluster(world, random, i1, j1, k1, grassyPlateausCoralTypes1, 1.2F, false, false);
                }
            }
        }

    }

    @Unique
    private void optimizationsAndTweaks$generateLithiumOutcrop(World world, Random random, int x1, int y1, int z1) {
        int i1 = x1 + random.nextInt(8) - random.nextInt(8);
        int j1 = y1 + random.nextInt(24) - random.nextInt(24);
        int k1 = z1 + random.nextInt(8) - random.nextInt(8);
        if (world.getBlock(i1, j1 - 1, k1).getMaterial() != Material.water && world.getBlock(i1, j1, k1).getMaterial() == Material.water) {
            random2 = random.nextInt(30);
            if (random2 == 0) {
                world.setBlock(i1, j1, k1, BlocksAndItems.seagrass);
            }
        }

        i1 = x1 + random.nextInt(8) - random.nextInt(8);
        j1 = y1 + random.nextInt(24) - random.nextInt(24);
        k1 = z1 + random.nextInt(8) - random.nextInt(8);
        if (world.getBlock(i1, j1 - 1, k1).getMaterial() != Material.water && world.getBlock(i1, j1, k1).getMaterial() == Material.water) {
            random2 = random.nextInt(40);
            if (random2 == 0) {
                type = random.nextInt(4) + 2;
                world.setBlock(i1, j1, k1, BlocksAndItems.lithiumOutcrop, type, 2);
            }
        }

        i1 = x1 + random.nextInt(8) - random.nextInt(8);
        j1 = y1 + random.nextInt(24) - random.nextInt(24);
        k1 = z1 + random.nextInt(8) - random.nextInt(8);
        if (world.getBlock(i1, j1, k1) == BlocksAndItems.saltWater && random.nextInt(110) == 0 && (world.getBlock(i1, j1 - 1, k1).getMaterial() != Material.water || world.getBlock(i1 - 1, j1, k1).getMaterial() != Material.water || world.getBlock(i1 + 1, j1, k1).getMaterial() != Material.water || world.getBlock(i1, j1, k1 - 1).getMaterial() != Material.water || world.getBlock(i1, j1, k1 + 1).getMaterial() != Material.water)) {
            if (world.getBlock(i1 + 1, j1, k1).getMaterial() != Material.water) {
                world.setBlock(i1, j1, k1, BlocksAndItems.lithiumOutcrop);
                random2 = (int)Math.floor(Math.random() * 2.0 + 1.0);
                if (random2 == 1) {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 6, 2);
                } else {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 10, 2);
                }
            } else if (world.getBlock(i1 - 1, j1, k1).getMaterial() != Material.water) {
                world.setBlock(i1, j1, k1, BlocksAndItems.lithiumOutcrop);
                random2 = (int)Math.floor(Math.random() * 2.0 + 1.0);
                if (random2 == 1) {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 7, 2);
                } else {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 11, 2);
                }
            } else if (world.getBlock(i1, j1, k1 + 1).getMaterial() != Material.water) {
                world.setBlock(i1, j1, k1, BlocksAndItems.lithiumOutcrop);
                random2 = (int)Math.floor(Math.random() * 2.0 + 1.0);
                if (random2 == 1) {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 8, 2);
                } else {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 12, 2);
                }
            } else if (world.getBlock(i1, j1, k1 - 1).getMaterial() != Material.water) {
                world.setBlock(i1, j1, k1, BlocksAndItems.lithiumOutcrop);
                random2 = (int)Math.floor(Math.random() * 2.0 + 1.0);
                if (random2 == 1) {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 9, 2);
                } else {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 13, 2);
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean canStructureSpawn(World world, int x, int y, int z, int length, int width, float maxInvalidBlocksIndex) {
        int invalidBlocks = 0;
        Block waterBlock = Blocks.water;

        for (int i = 0; i < length; ++i) {
            for (int j = 0; j < width; ++j) {
                Block block = world.getBlock(x + i, y - 1, z + j);
                if (block == waterBlock) {
                    ++invalidBlocks;
                }
            }
        }

        float maxInvalidBlocks = (length * width) * (1.0F / maxInvalidBlocksIndex);
        return invalidBlocks <= maxInvalidBlocks;
    }
    @Shadow
    private void createCoralCluster(World world, Random random, int x, int y, int z, Block[][] coralTypes, float size, boolean doesBlockAboveHaveToBeWater, boolean shouldReplaceBlocks) {
        int[][] storedBlocks = new int[500][3];
        int storedBlocksIndex = 0;
        int blockToSetIndex1 = random.nextInt(coralTypes.length);
        Block[] blockToSetSpawn1 = coralTypes[blockToSetIndex1];
        int blockToSetInsideIndex1 = random.nextInt(blockToSetSpawn1.length);
        Block blockToSetInsideSpawn1 = blockToSetSpawn1[blockToSetInsideIndex1];
        world.setBlock(x, y, z, blockToSetInsideSpawn1);
        int[] positionArray1 = new int[]{x, y, z};
        storedBlocks[storedBlocksIndex] = positionArray1;
        ++storedBlocksIndex;

        for(int i = 0; i < storedBlocks.length && (storedBlocks[i][0] != 0 || storedBlocks[i][1] != 0 || storedBlocks[i][2] != 0) && storedBlocksIndex < 490; ++i) {
            int[] positionArray;
            boolean flag;
            int distanceFromCoral;
            float chanceOfSpawning;
            float percentage;
            boolean flag2;
            int blockToSetIndex;
            Block[] blockToSetSpawn;
            int blockToSetInsideIndex;
            Block blockToSetSpawnInside;
            int canSpawnProbibility;
            if (world.getBlock(storedBlocks[i][0] - 1, storedBlocks[i][1] - 1, storedBlocks[i][2]) != null && world.getBlock(storedBlocks[i][0] - 1, storedBlocks[i][1] - 1, storedBlocks[i][2]).getMaterial() != Material.water) {
                positionArray = new int[]{storedBlocks[i][0] - 1, storedBlocks[i][1], storedBlocks[i][2]};
                flag = false;

                for(distanceFromCoral = 0; distanceFromCoral < storedBlocks.length; ++distanceFromCoral) {
                    if (positionArray[0] == storedBlocks[distanceFromCoral][0] && positionArray[1] == storedBlocks[distanceFromCoral][1] && positionArray[2] == storedBlocks[distanceFromCoral][2]) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    distanceFromCoral = (int)Math.sqrt(Math.pow((double)(positionArray[0] - storedBlocks[0][0]), 2.0) + Math.pow((double)(positionArray[2] - storedBlocks[0][2]), 2.0));
                    chanceOfSpawning = 100.0F;
                    if (distanceFromCoral != 1 || distanceFromCoral != 0) {
                        percentage = (float)distanceFromCoral / size;
                        chanceOfSpawning = 100.0F * (1.0F / percentage);
                    }

                    canSpawnProbibility = random.nextInt(100);
                    if ((float)canSpawnProbibility <= chanceOfSpawning) {
                        flag2 = false;

                        for(blockToSetIndex = 0; blockToSetIndex < storedBlocks.length; ++blockToSetIndex) {
                            if (storedBlocks[blockToSetIndex] == positionArray) {
                                flag2 = true;
                            }
                        }

                        if (!flag2) {
                            blockToSetIndex = random.nextInt(coralTypes.length);
                            blockToSetSpawn = coralTypes[blockToSetIndex];
                            blockToSetInsideIndex = random.nextInt(blockToSetSpawn.length);
                            blockToSetSpawnInside = blockToSetSpawn[blockToSetInsideIndex];
                            if (doesBlockAboveHaveToBeWater) {
                                if (world.getBlock(positionArray[0], positionArray[1] + 1, positionArray[2]) != null && world.getBlock(positionArray[0], positionArray[1] + 1, positionArray[2]) == BlocksAndItems.saltWater && world.getBlock(positionArray[0], positionArray[1] - 1, positionArray[2]) != null && world.getBlock(positionArray[0], positionArray[1] - 1, positionArray[2]) != blockToSetSpawnInside) {
                                    world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                    storedBlocks[storedBlocksIndex] = positionArray;
                                    ++storedBlocksIndex;
                                }
                            } else if (shouldReplaceBlocks) {
                                world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                storedBlocks[storedBlocksIndex] = positionArray;
                                ++storedBlocksIndex;
                            } else if (world.getBlock(positionArray[0], positionArray[1], positionArray[2]) == BlocksAndItems.saltWater) {
                                world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                storedBlocks[storedBlocksIndex] = positionArray;
                                ++storedBlocksIndex;
                            }
                        }
                    }
                }
            }

            if (world.getBlock(storedBlocks[i][0] + 1, storedBlocks[i][1] - 1, storedBlocks[i][2]) != null && world.getBlock(storedBlocks[i][0] + 1, storedBlocks[i][1] - 1, storedBlocks[i][2]).getMaterial() != Material.water) {
                positionArray = new int[]{storedBlocks[i][0] + 1, storedBlocks[i][1], storedBlocks[i][2]};
                flag = false;

                for(distanceFromCoral = 0; distanceFromCoral < storedBlocks.length; ++distanceFromCoral) {
                    if (positionArray[0] == storedBlocks[distanceFromCoral][0] && positionArray[1] == storedBlocks[distanceFromCoral][1] && positionArray[2] == storedBlocks[distanceFromCoral][2]) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    distanceFromCoral = (int)Math.sqrt(Math.pow((double)(positionArray[0] - storedBlocks[0][0]), 2.0) + Math.pow((double)(positionArray[2] - storedBlocks[0][2]), 2.0));
                    chanceOfSpawning = 100.0F;
                    if (distanceFromCoral != 1 || distanceFromCoral != 0) {
                        percentage = (float)distanceFromCoral / size;
                        chanceOfSpawning = 100.0F * (1.0F / percentage);
                    }

                    canSpawnProbibility = random.nextInt(100);
                    if ((float)canSpawnProbibility <= chanceOfSpawning) {
                        flag2 = false;

                        for(blockToSetIndex = 0; blockToSetIndex < storedBlocks.length; ++blockToSetIndex) {
                            if (storedBlocks[blockToSetIndex] == positionArray) {
                                flag2 = true;
                            }
                        }

                        if (!flag2) {
                            blockToSetIndex = random.nextInt(coralTypes.length);
                            blockToSetSpawn = coralTypes[blockToSetIndex];
                            blockToSetInsideIndex = random.nextInt(blockToSetSpawn.length);
                            blockToSetSpawnInside = blockToSetSpawn[blockToSetInsideIndex];
                            if (doesBlockAboveHaveToBeWater) {
                                if (world.getBlock(positionArray[0], positionArray[1] + 1, positionArray[2]) != null && world.getBlock(positionArray[0], positionArray[1] + 1, positionArray[2]) == BlocksAndItems.saltWater && world.getBlock(positionArray[0], positionArray[1] - 1, positionArray[2]) != null && world.getBlock(positionArray[0], positionArray[1] - 1, positionArray[2]) != blockToSetSpawnInside) {
                                    world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                    storedBlocks[storedBlocksIndex] = positionArray;
                                    ++storedBlocksIndex;
                                }
                            } else if (shouldReplaceBlocks) {
                                world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                storedBlocks[storedBlocksIndex] = positionArray;
                                ++storedBlocksIndex;
                            } else if (world.getBlock(positionArray[0], positionArray[1], positionArray[2]) == BlocksAndItems.saltWater) {
                                world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                storedBlocks[storedBlocksIndex] = positionArray;
                                ++storedBlocksIndex;
                            }
                        }
                    }
                }
            }

            if (world.getBlock(storedBlocks[i][0], storedBlocks[i][1] - 1, storedBlocks[i][2] - 1) != null && world.getBlock(storedBlocks[i][0], storedBlocks[i][1] - 1, storedBlocks[i][2] - 1).getMaterial() != Material.water) {
                positionArray = new int[]{storedBlocks[i][0], storedBlocks[i][1], storedBlocks[i][2] - 1};
                flag = false;

                for(distanceFromCoral = 0; distanceFromCoral < storedBlocks.length; ++distanceFromCoral) {
                    if (positionArray[0] == storedBlocks[distanceFromCoral][0] && positionArray[1] == storedBlocks[distanceFromCoral][1] && positionArray[2] == storedBlocks[distanceFromCoral][2]) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    distanceFromCoral = (int)Math.sqrt(Math.pow((double)(positionArray[0] - storedBlocks[0][0]), 2.0) + Math.pow((double)(positionArray[2] - storedBlocks[0][2]), 2.0));
                    chanceOfSpawning = 100.0F;
                    if (distanceFromCoral != 1 || distanceFromCoral != 0) {
                        percentage = (float)distanceFromCoral / size;
                        chanceOfSpawning = 100.0F * (1.0F / percentage);
                    }

                    canSpawnProbibility = random.nextInt(100);
                    if ((float)canSpawnProbibility <= chanceOfSpawning) {
                        flag2 = false;

                        for(blockToSetIndex = 0; blockToSetIndex < storedBlocks.length; ++blockToSetIndex) {
                            if (storedBlocks[blockToSetIndex] == positionArray) {
                                flag2 = true;
                            }
                        }

                        if (!flag2) {
                            blockToSetIndex = random.nextInt(coralTypes.length);
                            blockToSetSpawn = coralTypes[blockToSetIndex];
                            blockToSetInsideIndex = random.nextInt(blockToSetSpawn.length);
                            blockToSetSpawnInside = blockToSetSpawn[blockToSetInsideIndex];
                            if (doesBlockAboveHaveToBeWater) {
                                if (world.getBlock(positionArray[0], positionArray[1] + 1, positionArray[2]) != null && world.getBlock(positionArray[0], positionArray[1] + 1, positionArray[2]) == BlocksAndItems.saltWater && world.getBlock(positionArray[0], positionArray[1] - 1, positionArray[2]) != null && world.getBlock(positionArray[0], positionArray[1] - 1, positionArray[2]) != blockToSetSpawnInside) {
                                    world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                    storedBlocks[storedBlocksIndex] = positionArray;
                                    ++storedBlocksIndex;
                                }
                            } else if (shouldReplaceBlocks) {
                                world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                storedBlocks[storedBlocksIndex] = positionArray;
                                ++storedBlocksIndex;
                            } else if (world.getBlock(positionArray[0], positionArray[1], positionArray[2]) == BlocksAndItems.saltWater) {
                                world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                storedBlocks[storedBlocksIndex] = positionArray;
                                ++storedBlocksIndex;
                            }
                        }
                    }
                }
            }

            if (world.getBlock(storedBlocks[i][0], storedBlocks[i][1] - 1, storedBlocks[i][2] + 1) != null && world.getBlock(storedBlocks[i][0], storedBlocks[i][1] - 1, storedBlocks[i][2] + 1).getMaterial() != Material.water) {
                positionArray = new int[]{storedBlocks[i][0], storedBlocks[i][1], storedBlocks[i][2] + 1};
                flag = false;

                for(distanceFromCoral = 0; distanceFromCoral < storedBlocks.length; ++distanceFromCoral) {
                    if (positionArray[0] == storedBlocks[distanceFromCoral][0] && positionArray[1] == storedBlocks[distanceFromCoral][1] && positionArray[2] == storedBlocks[distanceFromCoral][2]) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    distanceFromCoral = (int)Math.sqrt(Math.pow((double)(positionArray[0] - storedBlocks[0][0]), 2.0) + Math.pow((double)(positionArray[2] - storedBlocks[0][2]), 2.0));
                    chanceOfSpawning = 100.0F;
                    if (distanceFromCoral != 0 && distanceFromCoral != 1) {
                        percentage = (float)distanceFromCoral / size;
                        chanceOfSpawning = 100.0F * (1.0F / percentage);
                    }

                    canSpawnProbibility = random.nextInt(100);
                    if ((float)canSpawnProbibility <= chanceOfSpawning) {
                        flag2 = false;

                        for(blockToSetIndex = 0; blockToSetIndex < storedBlocks.length; ++blockToSetIndex) {
                            if (storedBlocks[blockToSetIndex] == positionArray) {
                                flag2 = true;
                            }
                        }

                        if (!flag2) {
                            blockToSetIndex = random.nextInt(coralTypes.length);
                            blockToSetSpawn = coralTypes[blockToSetIndex];
                            blockToSetInsideIndex = random.nextInt(blockToSetSpawn.length);
                            blockToSetSpawnInside = blockToSetSpawn[blockToSetInsideIndex];
                            if (doesBlockAboveHaveToBeWater) {
                                if (world.getBlock(positionArray[0], positionArray[1] + 1, positionArray[2]) != null && world.getBlock(positionArray[0], positionArray[1] + 1, positionArray[2]) == BlocksAndItems.saltWater && world.getBlock(positionArray[0], positionArray[1] - 1, positionArray[2]) != null && world.getBlock(positionArray[0], positionArray[1] - 1, positionArray[2]) != blockToSetSpawnInside) {
                                    world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                    storedBlocks[storedBlocksIndex] = positionArray;
                                    ++storedBlocksIndex;
                                }
                            } else if (shouldReplaceBlocks) {
                                world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                storedBlocks[storedBlocksIndex] = positionArray;
                                ++storedBlocksIndex;
                            } else if (world.getBlock(positionArray[0], positionArray[1], positionArray[2]) == BlocksAndItems.saltWater) {
                                world.setBlock(positionArray[0], positionArray[1], positionArray[2], blockToSetSpawnInside);
                                storedBlocks[storedBlocksIndex] = positionArray;
                                ++storedBlocksIndex;
                            }
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
    public void generateKelpForestCoral(World world, Random random, int x1, int y1, int z1) {
        boolean flag = false;
        int[] possiblePos = null;

        for (int i = 0; i < 1110; ++i) {
            int[] coordinates = optimizationsAndTweaks$generateRandomCoordinates(random, x1, y1, z1);

            int i1 = coordinates[0];
            int j1 = coordinates[1];
            int k1 = coordinates[2];

            if (optimizationsAndTweaks$isValidPosition(world, random, i1, j1, k1)) {
                optimizationsAndTweaks$generateFormationsBasedOnRandom(world, random, i1, j1, k1, flag);
                optimizationsAndTweaks$generateCreepvines(world, random, i1, j1, k1);
                optimizationsAndTweaks$generateCoralClusters(world, random, i1, j1, k1, possiblePos);
                optimizationsAndTweaks$generateSandstoneOutcrop(world, random, i1, j1, k1);
                optimizationsAndTweaks$generateGreenTableCoral(world, random, i1, j1, k1);
            }
        }

        if (!flag && random.nextInt(2) == 0 && possiblePos != null) {
            int i1 = possiblePos[0];
            int j1 = possiblePos[1];
            int k1 = possiblePos[2];
            generateFormations(world, random, i1, i1 - 1, j1);
        }
    }

    @Unique
    private int[] optimizationsAndTweaks$generateRandomCoordinates(Random random, int x1, int y1, int z1) {
        int i1 = x1 + random.nextInt(8) - random.nextInt(8);
        int j1 = y1 + random.nextInt(24) - random.nextInt(24);
        int k1 = z1 + random.nextInt(8) - random.nextInt(8);
        return new int[]{i1, j1, k1};
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidPosition(World world, Random random, int i1, int j1, int k1) {
        return (world.provider.hasNoSky || j1 < 255)
            && CanBlockStay.canBelowGroundBlockStay(world, i1, j1, k1)
            && world.getBlock(i1, j1 + 1, k1) == BlocksAndItems.saltWater;
    }

    @Unique
    private void optimizationsAndTweaks$generateFormationsBasedOnRandom(World world, Random random, int i1, int j1, int k1, boolean flag) {
        int random2 = random.nextInt(52);
        if (random2 == 0) {
            generateFormations(world, random, i1, j1, k1);
            flag = true;
        }
        if (random2 <= 2 && world.getBlock(i1, j1, k1).getMaterial() == Material.water && world.getBlock(i1, j1 - 1, k1).getMaterial() != Material.water) {
            type = random.nextInt(4) + 2;
            world.setBlock(i1, j1, k1, BlocksAndItems.saltDeposit, type, 2);
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateCreepvines(World world, Random random, int i1, int j1, int k1) {
        i1 = x1 + random.nextInt(8) - random.nextInt(8);
        j1 = y1 + random.nextInt(24) - random.nextInt(24);
        k1 = z1 + random.nextInt(8) - random.nextInt(8);
        if (random.nextInt(3) == 0 && world.getBlock(i1, j1 - 1, k1).getMaterial() != Material.water && world.getBlock(i1, j1, k1) == BlocksAndItems.saltWater) {
            random2 = random.nextInt(3);
            if (random2 == 0) {
                this.generateCreepvine(world, random, i1, j1, k1, BlocksAndItems.creepvine, BlocksAndItems.creepvineTop, false);
            }

            if (random2 == 1) {
                this.generateCreepvine(world, random, i1, j1, k1, BlocksAndItems.creepvineThin, BlocksAndItems.creepvineThinTop, false);
            }

            if (random2 == 2) {
                this.generateCreepvine(world, random, i1, j1, k1, BlocksAndItems.seedClusterCreepvineBottom, BlocksAndItems.seedClusterCreepvineTop, true);
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateCoralClusters(World world, Random random, int i1, int j1, int k1, int[] possiblePos) {
        i1 = x1 + random.nextInt(8) - random.nextInt(8);
        j1 = y1 + random.nextInt(24) - random.nextInt(24);
        k1 = z1 + random.nextInt(8) - random.nextInt(8);
        if (world.getBlock(i1, j1, k1) == BlocksAndItems.saltWater && world.getBlock(i1, j1 - 1, k1).getMaterial() != Material.water) {
            if (possiblePos == null) {
                int[] array = new int[]{i1, j1, k1};
                possiblePos = array;
            }

            random2 = random.nextInt(5);
            if (random2 == 0) {
                this.createCoralCluster(world, random, i1, j1 - 1, k1, sandArray, 0.6F, false, true);
            } else {
                type = random.nextInt(10);
                if (type >= 5) {
                    this.createCoralCluster(world, random, i1, j1 - 1, k1, kelpForestSandArray, 0.6F, true, true);
                }

                this.createCoralCluster(world, random, i1, j1, k1, kelpForestTypes1, 1.2F, false, false);
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateSandstoneOutcrop(World world, Random random, int i1, int j1, int k1) {
        i1 = x1 + random.nextInt(8) - random.nextInt(8);
        j1 = y1 + random.nextInt(24) - random.nextInt(24);
        k1 = z1 + random.nextInt(8) - random.nextInt(8);
        if (world.getBlock(i1, j1 - 1, k1).getMaterial() != Material.water && world.getBlock(i1, j1, k1).getMaterial() == Material.water && random.nextInt(60) == 0) {
            world.setBlock(i1, j1, k1, BlocksAndItems.sandstoneOutcrop);
            world.setBlockMetadataWithNotify(i1, j1, k1, random.nextInt(4) + 2, 2);
        }
        i1 = x1 + random.nextInt(8) - random.nextInt(8);
        j1 = y1 + random.nextInt(24) - random.nextInt(24);
        k1 = z1 + random.nextInt(8) - random.nextInt(8);
        if (world.getBlock(i1, j1, k1) == BlocksAndItems.saltWater && world.getBlock(i1, j1 + 1, k1) == BlocksAndItems.saltWater && random.nextInt(10) == 0 && (world.getBlock(i1, j1 - 1, k1).getMaterial() != Material.water || world.getBlock(i1 - 1, j1, k1).getMaterial() != Material.water || world.getBlock(i1 + 1, j1, k1).getMaterial() != Material.water || world.getBlock(i1, j1, k1 - 1).getMaterial() != Material.water || world.getBlock(i1, j1, k1 + 1).getMaterial() != Material.water)) {
            if (world.getBlock(i1 + 1, j1, k1).getMaterial() != Material.water) {
                world.setBlock(i1, j1, k1, BlocksAndItems.sandstoneOutcrop);
                random2 = (int)Math.floor(Math.random() * 2.0 + 1.0);
                if (random2 == 1) {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 6, 2);
                } else {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 10, 2);
                }
            } else if (world.getBlock(i1 - 1, j1, k1).getMaterial() != Material.water) {
                world.setBlock(i1, j1, k1, BlocksAndItems.sandstoneOutcrop);
                random2 = (int)Math.floor(Math.random() * 2.0 + 1.0);
                if (random2 == 1) {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 7, 2);
                } else {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 11, 2);
                }
            } else if (world.getBlock(i1, j1, k1 + 1).getMaterial() != Material.water) {
                world.setBlock(i1, j1, k1, BlocksAndItems.sandstoneOutcrop);
                random2 = (int)Math.floor(Math.random() * 2.0 + 1.0);
                if (random2 == 1) {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 8, 2);
                } else {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 12, 2);
                }
            } else if (world.getBlock(i1, j1, k1 - 1).getMaterial() != Material.water) {
                world.setBlock(i1, j1, k1, BlocksAndItems.sandstoneOutcrop);
                random2 = (int)Math.floor(Math.random() * 2.0 + 1.0);
                if (random2 == 1) {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 9, 2);
                } else {
                    world.setBlockMetadataWithNotify(i1, j1, k1, 13, 2);
                }
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateGreenTableCoral(World world, Random random, int x1, int y1, int z1) {
        if (x1 < 0 || x1 > 16 || z1 < 0 || z1 > 16) {
            return;
        }
        int i1 = x1 + random.nextInt(8) - random.nextInt(8);
        int j1 = y1 + random.nextInt(24) - random.nextInt(24);
        int k1 = z1 + random.nextInt(8) - random.nextInt(8);

        if (world.getBlock(i1, j1, k1) == BlocksAndItems.saltWater && world.getBlock(i1, j1 + 1, k1) == BlocksAndItems.saltWater && random.nextInt(10) == 0) {
            Block nextBlock1 = world.getBlock(i1 + 1, j1, k1);
            Block nextBlock2 = world.getBlock(i1 - 1, j1, k1);
            Block nextBlock3 = world.getBlock(i1, j1, k1 + 1);
            Block nextBlock4 = world.getBlock(i1, j1, k1 - 1);

            if (nextBlock1 == BlocksAndItems.kelpForestRock || nextBlock2 == BlocksAndItems.kelpForestRock ||
                nextBlock3 == BlocksAndItems.kelpForestRock || nextBlock4 == BlocksAndItems.kelpForestRock ||
                nextBlock1 == BlocksAndItems.kelpForestRockMossy || nextBlock2 == BlocksAndItems.kelpForestRockMossy ||
                nextBlock3 == BlocksAndItems.kelpForestRockMossy || nextBlock4 == BlocksAndItems.kelpForestRockMossy) {

                world.setBlock(i1, j1, k1, BlocksAndItems.greenTableCoral, this.metadata, 2);

                Block coverBlock;
                int metadata;
                boolean cancel;
                int ypos;

                if (nextBlock1 != BlocksAndItems.kelpForestRock && nextBlock1 != BlocksAndItems.kelpForestRockMossy) {
                    coverBlock = BlocksAndItems.greenCover;
                    metadata = 3;
                } else if (nextBlock2 != BlocksAndItems.kelpForestRock && nextBlock2 != BlocksAndItems.kelpForestRockMossy) {
                    coverBlock = BlocksAndItems.greenCover;
                    metadata = 2;
                } else if (nextBlock3 != BlocksAndItems.kelpForestRock && nextBlock3 != BlocksAndItems.kelpForestRockMossy) {
                    coverBlock = BlocksAndItems.greenCover;
                    metadata = 5;
                } else {
                    coverBlock = BlocksAndItems.greenCover;
                    metadata = 4;
                }

                world.setBlockMetadataWithNotify(i1, j1, k1, metadata, 2);
                world.setBlock(i1, j1, k1 - 1, coverBlock, metadata, 2);
                cancel = true;
                ypos = 1;

                while (cancel && world.getBlock(i1, j1 + ypos, k1 - 1) == BlocksAndItems.kelpForestRock || world.getBlock(i1, j1 + ypos, k1 - 1) == BlocksAndItems.kelpForestRockMossy) {
                    if ((world.getBlock(i1, j1 + ypos, k1 - 1) == Blocks.sand || world.getBlock(i1, j1 + ypos, k1 - 1) == BlocksAndItems.kelpForestRock) && Math.floor(Math.random() * 100.0 + 1.0) <= 85.0) {
                        world.setBlock(i1, j1 + ypos, k1, BlocksAndItems.greenTableCoral, this.metadata, 2);
                        world.setBlockMetadataWithNotify(i1, j1 + ypos, k1, metadata, 2);
                        world.setBlock(i1, j1 + ypos, k1 - 1, coverBlock, metadata, 2);
                        ypos++;
                    } else {
                        cancel = false;
                    }
                }
            }
        }
    }

    @Shadow
    private void generateCreepvine(World world, Random random, int x, int y, int z, Block bottomBlocks, Block topBlock, boolean hasCluster) {
        boolean hasPlacedSeedCluster = false;
        boolean startPlacingHigherBlocks = false;
        int heightRange = random.nextInt(9) + 4;
        int highestPoint = 0;

        for(int i = y; i < 100; ++i) {
            if (world.getBlock(x, i + heightRange, z) != BlocksAndItems.saltWater) {
                highestPoint = i;
                break;
            }

            if (!hasCluster) {
                if (world.getBlock(x, i + heightRange + 1, z) != BlocksAndItems.saltWater) {
                    world.setBlock(x, i, z, topBlock);
                } else {
                    world.setBlock(x, i, z, bottomBlocks);
                }
            } else {
                world.setBlock(x, i, z, bottomBlocks);
            }
        }

        if (hasCluster) {
            float difference = (float)(highestPoint - y);
            float percent = (float)Math.floor((double)(difference * 0.7F)) + (float)(random.nextInt(5) - 2);
            world.setBlock(x, (int)((float)y + percent), z, BlocksAndItems.seedClusterCreepvine);

            for(int i = (int)((float)y + percent + 1.0F); i < highestPoint; ++i) {
                world.setBlock(x, i, z, BlocksAndItems.seedClusterCreepvineThick);
            }

            world.setBlock(x, highestPoint - 1, z, topBlock);
        }

    }

    @Shadow
    private boolean generateFormations(World world, Random random, int i1, int j1, int k1) {
        int type = random.nextInt(44) + 1;
        if (type == 1 && this.canStructureSpawn(world, i1, j1, k1, 4, 25, 5.0F)) {
            formation1.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 2 && this.canStructureSpawn(world, i1, j1, k1, 25, 4, 5.0F)) {
            formation2.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 3 && this.canStructureSpawn(world, i1, j1, k1, 4, 25, 5.0F)) {
            formation3.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 4 && this.canStructureSpawn(world, i1, j1, k1, 16, 21, 5.0F)) {
            formation4.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 5 && this.canStructureSpawn(world, i1, j1, k1, 20, 16, 5.0F)) {
            formation5.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 6 && this.canStructureSpawn(world, i1, j1, k1, 20, 19, 5.0F)) {
            formation6.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 7 && this.canStructureSpawn(world, i1, j1, k1, 19, 20, 5.0F)) {
            formation7.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 8 && this.canStructureSpawn(world, i1, j1, k1, 14, 16, 5.0F)) {
            formation8.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 9 && this.canStructureSpawn(world, i1, j1, k1, 16, 14, 5.0F)) {
            formation9.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 10 && this.canStructureSpawn(world, i1, j1, k1, 3, 2, 5.0F)) {
            formation10.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 11 && this.canStructureSpawn(world, i1, j1, k1, 2, 3, 5.0F)) {
            formation11.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 12 && this.canStructureSpawn(world, i1, j1, k1, 14, 16, 5.0F)) {
            formation12.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 13 && this.canStructureSpawn(world, i1, j1, k1, 16, 14, 5.0F)) {
            formation13.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 14 && this.canStructureSpawn(world, i1, j1, k1, 16, 14, 5.0F)) {
            formation14.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 15 && this.canStructureSpawn(world, i1, j1, k1, 14, 16, 5.0F)) {
            formation15.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 16 && this.canStructureSpawn(world, i1, j1, k1, 10, 9, 5.0F)) {
            formation16.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 17 && this.canStructureSpawn(world, i1, j1, k1, 9, 10, 5.0F)) {
            formation17.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 18 && this.canStructureSpawn(world, i1, j1, k1, 13, 11, 5.0F)) {
            formation18.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 19 && this.canStructureSpawn(world, i1, j1, k1, 11, 13, 5.0F)) {
            formation19.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 20 && this.canStructureSpawn(world, i1, j1, k1, 4, 5, 5.0F)) {
            formation20.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 21 && this.canStructureSpawn(world, i1, j1, k1, 5, 4, 5.0F)) {
            formation21.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 22 && this.canStructureSpawn(world, i1, j1, k1, 14, 12, 5.0F)) {
            formation22.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 23 && this.canStructureSpawn(world, i1, j1, k1, 12, 13, 5.0F)) {
            formation23.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 24 && this.canStructureSpawn(world, i1, j1, k1, 5, 6, 5.0F)) {
            formation24.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 25 && this.canStructureSpawn(world, i1, j1, k1, 6, 5, 5.0F)) {
            formation25.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 26 && this.canStructureSpawn(world, i1, j1, k1, 14, 7, 5.0F)) {
            formation26.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 27 && this.canStructureSpawn(world, i1, j1, k1, 7, 14, 5.0F)) {
            formation27.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 28 && this.canStructureSpawn(world, i1, j1, k1, 5, 6, 5.0F)) {
            formation28.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 29 && this.canStructureSpawn(world, i1, j1, k1, 6, 5, 5.0F)) {
            formation29.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 30 && this.canStructureSpawn(world, i1, j1, k1, 20, 19, 5.0F)) {
            formation30.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 31 && this.canStructureSpawn(world, i1, j1, k1, 19, 20, 5.0F)) {
            formation31.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 32 && this.canStructureSpawn(world, i1, j1, k1, 14, 12, 5.0F)) {
            formation32.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 33 && this.canStructureSpawn(world, i1, j1, k1, 12, 14, 5.0F)) {
            formation33.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 34 && this.canStructureSpawn(world, i1, j1, k1, 12, 18, 5.0F)) {
            formation34.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 35 && this.canStructureSpawn(world, i1, j1, k1, 24, 22, 5.0F)) {
            formation35.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 36 && this.canStructureSpawn(world, i1, j1, k1, 22, 24, 5.0F)) {
            formation36.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 37 && this.canStructureSpawn(world, i1, j1, k1, 6, 23, 5.0F)) {
            formation37.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 38 && this.canStructureSpawn(world, i1, j1, k1, 23, 6, 5.0F)) {
            formation38.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 39 && this.canStructureSpawn(world, i1, j1, k1, 9, 19, 5.0F)) {
            formation39.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 40 && this.canStructureSpawn(world, i1, j1, k1, 19, 9, 5.0F)) {
            formation40.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 41 && this.canStructureSpawn(world, i1, j1, k1, 11, 16, 5.0F)) {
            formation41.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 42 && this.canStructureSpawn(world, i1, j1, k1, 16, 11, 5.0F)) {
            formation42.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 43 && this.canStructureSpawn(world, i1, j1, k1, 25, 20, 5.0F)) {
            formation43.generate(world, random, i1, j1, k1);
            return true;
        } else if (type == 44 && this.canStructureSpawn(world, i1, j1, k1, 20, 25, 5.0F)) {
            formation44.generate(world, random, i1, j1, k1);
            return true;
        } else {
            return false;
        }
    }
    static {
        sandSubArray = new Block[]{Blocks.sand};
        sandArray = new Block[][]{sandSubArray};
        bloodMossSubArray = new Block[]{BlocksAndItems.bloodMoss};
        bloodMossArray = new Block[][]{bloodMossSubArray};
        royalBrittleSubArray = new Block[]{BlocksAndItems.bloodMoss, BlocksAndItems.bloodMoss, BlocksAndItems.bloodMoss, BlocksAndItems.bloodMoss, BlocksAndItems.bloodMoss, BlocksAndItems.bloodMoss, BlocksAndItems.bloodMoss, BlocksAndItems.bloodMoss, BlocksAndItems.bloodMoss, BlocksAndItems.royalBrittle};
        royalBrittleArray = new Block[][]{royalBrittleSubArray};
        commonGrassyPlateausCoralTypes1 = new Block[]{BlocksAndItems.bloodgrass};
        uncommonGrassyPlateausCoralTypes1 = new Block[]{BlocksAndItems.doubleBloodgrass};
        grassyPlateausCoralTypes1 = new Block[][]{commonGrassyPlateausCoralTypes1, commonGrassyPlateausCoralTypes1, uncommonGrassyPlateausCoralTypes1};
        commonGrassyPlateausCoralTypes2 = new Block[]{BlocksAndItems.blueTangle, BlocksAndItems.writhingWeed};
        uncommonGrassyPlateausCoralTypes2 = new Block[]{BlocksAndItems.acidMushroom, BlocksAndItems.slantedShellPlate};
        grassyPlateausCoralTypes2 = new Block[][]{commonGrassyPlateausCoralTypes2, commonGrassyPlateausCoralTypes2, uncommonGrassyPlateausCoralTypes2};
        commonGrassyPlateausCoralTypes3 = new Block[]{BlocksAndItems.acidMushroom, BlocksAndItems.blueTangle, BlocksAndItems.violetBeau, BlocksAndItems.redWort};
        grassyPlateausCoralTypes3 = new Block[][]{commonGrassyPlateausCoralTypes3};
        commonGrassyPlateausCoralTypes4 = new Block[]{BlocksAndItems.veinedNettle, BlocksAndItems.magentaRod};
        grassyPlateausCoralTypes4 = new Block[][]{commonGrassyPlateausCoralTypes4};
        safeShallowsCoralSubArray = new Block[]{BlocksAndItems.safeShallowsCoral, BlocksAndItems.royalBrittle, BlocksAndItems.greenCoverBlock, BlocksAndItems.purpleSpongeBlock};
        safeShallowsCoralArray = new Block[][]{safeShallowsCoralSubArray};
        commonSafeShallowsTypes1 = new Block[]{BlocksAndItems.acidMushroom};
        uncommonSafeShallowsTypes1 = new Block[]{BlocksAndItems.writhingWeed, BlocksAndItems.redBranch, BlocksAndItems.redBranch, BlocksAndItems.veinedNettle, BlocksAndItems.doubleWrithingWeed};
        rareSafeShallowsTypes1 = new Block[]{BlocksAndItems.doubleRedBranch, BlocksAndItems.slantedShellPlate, BlocksAndItems.shellPlate, BlocksAndItems.bluePalm};
        extraRareSafeShallowsTypes1 = new Block[]{BlocksAndItems.purpleBrainCoral};
        safeShallowsTypes1 = new Block[][]{commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, commonSafeShallowsTypes1, uncommonSafeShallowsTypes1, uncommonSafeShallowsTypes1, uncommonSafeShallowsTypes1, uncommonSafeShallowsTypes1, uncommonSafeShallowsTypes1, uncommonSafeShallowsTypes1, rareSafeShallowsTypes1, rareSafeShallowsTypes1, extraRareSafeShallowsTypes1};
        commonSafeShallowsTypes2 = new Block[]{BlocksAndItems.acidMushroom};
        common2SafeShallowsTypes2 = new Block[]{BlocksAndItems.writhingWeed};
        safeShallowsTypes2 = new Block[][]{commonSafeShallowsTypes2, common2SafeShallowsTypes2};
        commonSafeShallowsTypes3 = new Block[]{BlocksAndItems.seagrass, BlocksAndItems.greenReeds, BlocksAndItems.yellowCoral};
        safeShallowsTypes3 = new Block[][]{commonSafeShallowsTypes3};
        kelpForestSandSubArray = new Block[]{BlocksAndItems.kelpForestSand, BlocksAndItems.kelpForestSand, BlocksAndItems.kelpForestSand, Blocks.sand};
        kelpForestSandArray = new Block[][]{kelpForestSandSubArray};
        commonKelpForestTypes1 = new Block[]{BlocksAndItems.saltWater, BlocksAndItems.seaMoss, BlocksAndItems.shortGrass, BlocksAndItems.doubleSeagrass};
        kelpForestTypes1 = new Block[][]{commonKelpForestTypes1};
    }
}
