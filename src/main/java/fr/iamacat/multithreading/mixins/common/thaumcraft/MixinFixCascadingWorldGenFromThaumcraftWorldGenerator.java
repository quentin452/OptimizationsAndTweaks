package fr.iamacat.multithreading.mixins.common.thaumcraft;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraftforge.common.BiomeDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.*;
import thaumcraft.common.lib.world.biomes.BiomeHandler;
import thaumcraft.common.lib.world.dim.MazeHandler;
import thaumcraft.common.lib.world.dim.MazeThread;
import thaumcraft.common.tiles.TileNode;

@Mixin(ThaumcraftWorldGenerator.class)
public class MixinFixCascadingWorldGenFromThaumcraftWorldGenerator {

    @Shadow
    public static BiomeGenBase biomeTaint;
    @Shadow
    public static BiomeGenBase biomeEerie;
    @Shadow
    public static BiomeGenBase biomeMagicalForest;
    @Shadow
    public static BiomeGenBase biomeEldritchLands;
    @Shadow
    static Collection<Aspect> c;
    @Shadow
    static ArrayList<Aspect> basicAspects;
    @Shadow
    static ArrayList<Aspect> complexAspects;
    @Shadow
    public static HashMap<Integer, Integer> dimensionBlacklist;
    @Shadow
    public static HashMap<Integer, Integer> biomeBlacklist;
    @Shadow
    HashMap<Integer, Boolean> structureNode = new HashMap();

    @Inject(method = "generate", at = @At("HEAD"), remap = false, cancellable = true)
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator) {
            this.worldGeneration(random, chunkX, chunkZ, world, true, ci);
            ci.cancel();
        }

    }

    @Inject(method = "worldGeneration", at = @At("HEAD"), remap = false, cancellable = true)
    public void worldGeneration(Random random, int chunkX, int chunkZ, World world, boolean newGen, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator) {
            if (world.provider.dimensionId == Config.dimensionOuterId) {
                MazeHandler.generateEldritch(world, random, chunkX, chunkZ);
                world.getChunkFromChunkCoords(chunkX, chunkZ)
                    .setChunkModified();
            } else {
                switch (world.provider.dimensionId) {
                    case -1:
                        this.generateNether(world, random, chunkX, chunkZ, newGen, ci);
                        break;
                    case 1:
                        break;
                    default:
                        this.generateSurface(world, random, chunkX, chunkZ, newGen, ci);
                        break;
                }

                if (!newGen) {
                    world.getChunkFromChunkCoords(chunkX, chunkZ)
                        .setChunkModified();
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "generateNether", at = @At("HEAD"), remap = false, cancellable = true)
    private void generateNether(World world, Random random, int chunkX, int chunkZ, boolean newGen, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator) {
            boolean auraGen = false;
            if (!world.getWorldInfo()
                .getTerrainType()
                .getWorldTypeName()
                .startsWith("flat") && (newGen || Config.regenStructure)) {
                this.generateTotem(world, random, chunkX, chunkZ, auraGen, newGen, ci);
            }

            if (newGen || Config.regenAura) {
                this.generateWildNodes(world, random, chunkX, chunkZ, auraGen, newGen, ci);
            }
            ci.cancel();
        }

    }

    @Inject(method = "generateSurface", at = @At("HEAD"), remap = false, cancellable = true)
    private void generateSurface(World world, Random random, int chunkX, int chunkZ, boolean newGen, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator) {
            boolean auraGen = false;
            int blacklist = getDimBlacklist(world.provider.dimensionId);
            if (blacklist == -1 && Config.genTrees
                && !world.getWorldInfo()
                    .getTerrainType()
                    .getWorldTypeName()
                    .startsWith("flat")
                && (newGen || Config.regenTrees)) {
                this.generateVegetation(world, random, chunkX, chunkZ, newGen);
            }

            if (blacklist != 0 && blacklist != 2) {
                this.generateOres(world, random, chunkX, chunkZ, newGen);
            }

            if (blacklist != 0 && blacklist != 2 && Config.genAura && (newGen || Config.regenAura)) {
                ChunkPosition var7 = (new MapGenScatteredFeature()).func_151545_a(
                    world,
                    chunkX * 16 + 8,
                    world.getHeightValue(chunkX * 16 + 8, chunkZ * 16 + 8),
                    chunkZ * 16 + 8);
                if (var7 != null && !this.structureNode.containsKey(var7.hashCode())) {
                    auraGen = true;
                    this.structureNode.put(var7.hashCode(), true);
                    createRandomNodeAt(
                        world,
                        var7.chunkPosX,
                        world.getHeightValue(var7.chunkPosX, var7.chunkPosZ) + 3,
                        var7.chunkPosZ,
                        random,
                        false,
                        false,
                        false,
                        ci);
                }

                auraGen = this.generateWildNodes(world, random, chunkX, chunkZ, auraGen, newGen, ci);
            }

            if (blacklist == -1 && Config.genStructure
                && world.provider.dimensionId == 0
                && !world.getWorldInfo()
                    .getTerrainType()
                    .getWorldTypeName()
                    .startsWith("flat")
                && (newGen || Config.regenStructure)) {
                int randPosX = chunkX * 16 + random.nextInt(16);
                int randPosZ = chunkZ * 16 + random.nextInt(16);
                int randPosY = world.getHeightValue(randPosX, randPosZ) - 9;
                if (randPosY < world.getActualHeight()) {
                    world.getChunkFromBlockCoords(
                        MathHelper.floor_double((double) randPosX),
                        MathHelper.floor_double((double) randPosZ));
                    WorldGenerator mound = new WorldGenMound();
                    if (random.nextInt(150) == 0) {
                        if (mound.generate(world, random, randPosX, randPosY, randPosZ)) {
                            auraGen = true;
                            int value = random.nextInt(200) + 400;
                            createRandomNodeAt(
                                world,
                                randPosX + 9,
                                randPosY + 8,
                                randPosZ + 9,
                                random,
                                false,
                                true,
                                false,
                                ci);
                        }
                    } else if (random.nextInt(66) == 0) {
                        WorldGenEldritchRing stonering = new WorldGenEldritchRing();
                        randPosY += 8;
                        int w = 11 + random.nextInt(6) * 2;
                        int h = 11 + random.nextInt(6) * 2;
                        stonering.chunkX = chunkX;
                        stonering.chunkZ = chunkZ;
                        stonering.width = w;
                        stonering.height = h;
                        if (stonering.func_76484_a(world, random, randPosX, randPosY, randPosZ)) {
                            auraGen = true;
                            createRandomNodeAt(world, randPosX, randPosY + 2, randPosZ, random, false, true, false, ci);
                            Thread t = new Thread(new MazeThread(chunkX, chunkZ, w, h, random.nextLong()));
                            t.start();
                        }
                    } else if (random.nextInt(40) == 0) {
                        randPosY += 9;
                        WorldGenerator hilltopStones = new WorldGenHilltopStones();
                        if (hilltopStones.generate(world, random, randPosX, randPosY, randPosZ)) {
                            auraGen = true;
                            createRandomNodeAt(world, randPosX, randPosY + 5, randPosZ, random, false, true, false, ci);
                        }
                    }
                }

                this.generateTotem(world, random, chunkX, chunkZ, auraGen, newGen, ci);
            }
            ci.cancel();
        }
    }

    @Inject(method = "createRandomNodeAt", at = @At("HEAD"), remap = false, cancellable = true)
    private static void createRandomNodeAt(World world, int x, int y, int z, Random random, boolean silverwood,
        boolean eerie, boolean small, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator) {
            if (basicAspects.size() == 0) {

                for (Aspect as : c) {
                    if (as.getComponents() != null) {
                        complexAspects.add(as);
                    } else {
                        basicAspects.add(as);
                    }
                }
            }

            NodeType type = NodeType.NORMAL;
            if (silverwood) {
                type = NodeType.PURE;
            } else if (eerie) {
                type = NodeType.DARK;
            } else if (random.nextInt(Config.specialNodeRarity) == 0) {
                switch (random.nextInt(10)) {
                    case 0:
                    case 1:
                    case 2:
                        type = NodeType.DARK;
                        break;
                    case 3:
                    case 4:
                    case 5:
                        type = NodeType.UNSTABLE;
                        break;
                    case 6:
                    case 7:
                    case 8:
                        type = NodeType.PURE;
                        break;
                    case 9:
                        type = NodeType.HUNGRY;
                }
            }

            NodeModifier modifier = null;
            if (random.nextInt(Config.specialNodeRarity / 2) == 0) {
                switch (random.nextInt(3)) {
                    case 0:
                        modifier = NodeModifier.BRIGHT;
                        break;
                    case 1:
                        modifier = NodeModifier.PALE;
                        break;
                    case 2:
                        modifier = NodeModifier.FADING;
                }
            }

            BiomeGenBase bg = world.getBiomeGenForCoords(x, z);
            int baura = BiomeHandler.getBiomeAura(bg);
            if (type != NodeType.PURE && bg.biomeID == biomeTaint.biomeID) {
                baura = (int) ((float) baura * 1.5F);
                if (random.nextBoolean()) {
                    type = NodeType.TAINTED;
                    baura = (int) ((float) baura * 1.5F);
                }
            }

            if (silverwood || small) {
                baura /= 4;
            }

            int value = random.nextInt(baura / 2) + baura / 2;
            Aspect ra = BiomeHandler.getRandomBiomeTag(bg.biomeID, random);
            AspectList al = new AspectList();
            if (ra != null) {
                al.add(ra, 2);
            } else {
                Aspect aa = (Aspect) complexAspects.get(random.nextInt(complexAspects.size()));
                al.add(aa, 1);
                aa = (Aspect) basicAspects.get(random.nextInt(basicAspects.size()));
                al.add(aa, 1);
            }

            int water;
            for (water = 0; water < 3; ++water) {
                if (random.nextBoolean()) {
                    Aspect aa;
                    if (random.nextInt(Config.specialNodeRarity) == 0) {
                        aa = (Aspect) complexAspects.get(random.nextInt(complexAspects.size()));
                        al.merge(aa, 1);
                    } else {
                        aa = (Aspect) basicAspects.get(random.nextInt(basicAspects.size()));
                        al.merge(aa, 1);
                    }
                }
            }

            if (type == NodeType.HUNGRY) {
                al.merge(Aspect.HUNGER, 2);
                if (random.nextBoolean()) {
                    al.merge(Aspect.GREED, 1);
                }
            } else if (type == NodeType.PURE) {
                if (random.nextBoolean()) {
                    al.merge(Aspect.LIFE, 2);
                } else {
                    al.merge(Aspect.ORDER, 2);
                }
            } else if (type == NodeType.DARK) {
                if (random.nextBoolean()) {
                    al.merge(Aspect.DEATH, 1);
                }

                if (random.nextBoolean()) {
                    al.merge(Aspect.UNDEAD, 1);
                }

                if (random.nextBoolean()) {
                    al.merge(Aspect.ENTROPY, 1);
                }

                if (random.nextBoolean()) {
                    al.merge(Aspect.DARKNESS, 1);
                }
            }

            water = 0;
            int lava = 0;
            int stone = 0;
            int foliage = 0;

            int a;
            try {
                for (int xx = -5; xx <= 5; ++xx) {
                    for (int yy = -5; yy <= 5; ++yy) {
                        for (a = -5; a <= 5; ++a) {
                            try {
                                Block bi = world.getBlock(x + xx, y + yy, z + a);
                                if (bi.getMaterial() == Material.water) {
                                    ++water;
                                } else if (bi.getMaterial() == Material.lava) {
                                    ++lava;
                                } else if (bi == Blocks.stone) {
                                    ++stone;
                                }

                                if (bi.isFoliage(world, x + xx, y + yy, z + a)) {
                                    ++foliage;
                                }
                            } catch (Exception var23) {}
                        }
                    }
                }
            } catch (Exception var24) {}

            if (water > 100) {
                al.merge(Aspect.WATER, 1);
            }

            if (lava > 100) {
                al.merge(Aspect.FIRE, 1);
                al.merge(Aspect.EARTH, 1);
            }

            if (stone > 500) {
                al.merge(Aspect.EARTH, 1);
            }

            if (foliage > 100) {
                al.merge(Aspect.PLANT, 1);
            }

            int[] spread = new int[al.size()];
            float total = 0.0F;

            for (a = 0; a < spread.length; ++a) {
                if (al.getAmount(al.getAspectsSorted()[a]) == 2) {
                    spread[a] = 50 + random.nextInt(25);
                } else {
                    spread[a] = 25 + random.nextInt(50);
                }

                total += (float) spread[a];
            }

            for (a = 0; a < spread.length; ++a) {
                al.merge(al.getAspectsSorted()[a], (int) ((float) spread[a] / total * (float) value));
            }

            createNodeAt(world, x, y, z, type, modifier, al, ci);
            ci.cancel();
        }
    }

    @Inject(method = "createNodeAt", at = @At("HEAD"), remap = false, cancellable = true)
    private static void createNodeAt(World world, int x, int y, int z, NodeType nt, NodeModifier nm, AspectList al,
        CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator) {
            if (world.isAirBlock(x, y, z)) {
                world.setBlock(x, y, z, ConfigBlocks.blockAiry, 0, 0);
            }

            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileNode) {
                ((TileNode) te).setNodeType(nt);
                ((TileNode) te).setNodeModifier(nm);
                ((TileNode) te).setAspects(al);
            }

            world.markBlockForUpdate(x, y, z);
            ci.cancel();
        }
    }

    @Unique
    private boolean generateWildNodes(World world, Random random, int chunkX, int chunkZ, boolean auraGen,
        boolean newGen, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator) {
            if (Config.genAura && random.nextInt(Config.nodeRarity) == 0 && !auraGen) {
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int q = Utils.getFirstUncoveredY(world, x, z);
                if (q < 2) {
                    q = world.provider.getAverageGroundLevel() + random.nextInt(64)
                        - 32
                        + Utils.getFirstUncoveredY(world, x, z);
                }

                if (q < 2) {
                    q = 32 + random.nextInt(64);
                }

                if (world.isAirBlock(x, q + 1, z)) {
                    ++q;
                }

                int p = random.nextInt(4);
                Block b = world.getBlock(x, q + p, z);
                if (world.isAirBlock(x, q + p, z) || b.isReplaceable(world, x, q + p, z)) {
                    q += p;
                }

                if (q > world.getActualHeight()) {
                    return false;
                } else {
                    createRandomNodeAt(world, x, q, z, random, false, false, false, ci);
                    return true;
                }
            } else {
                return false;
            }
        }
        return auraGen;
    }

    private boolean generateTotem(World world, Random random, int chunkX, int chunkZ, boolean auraGen, boolean newGen,
        CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator) {
            if (Config.genStructure && (world.provider.dimensionId == 0 || world.provider.dimensionId == 1)
                && newGen
                && !auraGen
                && random.nextInt(Config.nodeRarity * 10) == 0) {
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int topy = world.provider.dimensionId == -1 ? Utils.getFirstUncoveredY(world, x, z) - 1
                    : world.getHeightValue(x, z) - 1;
                if (topy > world.getActualHeight()) {
                    return false;
                }

                if (world.getBlock(x, topy, z) != null && world.getBlock(x, topy, z)
                    .isLeaves(world, x, topy, z)) {
                    do {
                        --topy;
                    } while (world.getBlock(x, topy, z) != Blocks.grass && topy > 40);
                }

                if (world.getBlock(x, topy, z) == Blocks.snow_layer || world.getBlock(x, topy, z) == Blocks.tallgrass) {
                    --topy;
                }

                if (world.getBlock(x, topy, z) == Blocks.grass || world.getBlock(x, topy, z) == Blocks.sand
                    || world.getBlock(x, topy, z) == Blocks.dirt
                    || world.getBlock(x, topy, z) == Blocks.stone
                    || world.getBlock(x, topy, z) == Blocks.netherrack) {
                    int count;
                    for (count = 1; (world.isAirBlock(x, topy + count, z)
                        || world.getBlock(x, topy + count, z) == Blocks.snow_layer
                        || world.getBlock(x, topy + count, z) == Blocks.tallgrass) && count < 3; ++count) {}

                    if (count >= 2) {
                        world.setBlock(x, topy, z, ConfigBlocks.blockCosmeticSolid, 1, 3);
                        count = 1;

                        while ((world.isAirBlock(x, topy + count, z)
                            || world.getBlock(x, topy + count, z) == Blocks.snow_layer
                            || world.getBlock(x, topy + count, z) == Blocks.tallgrass) && count < 5) {
                            world.setBlock(x, topy + count, z, ConfigBlocks.blockCosmeticSolid, 0, 3);
                            if (count > 1 && random.nextInt(4) == 0) {
                                world.setBlock(x, topy + count, z, ConfigBlocks.blockCosmeticSolid, 8, 3);
                                createRandomNodeAt(world, x, topy + count, z, random, false, true, false, ci);
                                count = 5;
                                auraGen = true;
                            }

                            ++count;
                            if (count >= 5 && !auraGen) {
                                world.setBlock(x, topy + 5, z, ConfigBlocks.blockCosmeticSolid, 8, 3);
                                createRandomNodeAt(world, x, topy + 5, z, random, false, true, false, ci);
                            }
                        }
                    }
                }
            }

            return false;
        }
        return auraGen;
    }

    @Unique
    private static int getDimBlacklist(int dim) {
        return !dimensionBlacklist.containsKey(dim) ? -1 : dimensionBlacklist.get(dim);
    }

    @Unique
    private void generateVegetation(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        BiomeGenBase bgb = world.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);
        if (getBiomeBlacklist(bgb.biomeID) == -1) {
            if (random.nextInt(60) == 3) {
                generateSilverwood(world, random, chunkX, chunkZ);
            }

            if (random.nextInt(25) == 7) {
                generateGreatwood(world, random, chunkX, chunkZ);
            }

            int randPosX = chunkX * 16 + random.nextInt(16);
            int randPosZ = chunkZ * 16 + random.nextInt(16);
            int randPosY = world.getHeightValue(randPosX, randPosZ);
            if (randPosY <= world.getActualHeight()) {
                if (world.getBiomeGenForCoords(randPosX, randPosZ).topBlock == Blocks.sand
                    && world.getBiomeGenForCoords(randPosX, randPosZ).temperature > 1.0F
                    && random.nextInt(30) == 0) {
                    generateFlowers(world, random, randPosX, randPosY, randPosZ, 3);
                }

            }
        }
    }

    @Unique
    private static boolean generateGreatwood(World world, Random random, int chunkX, int chunkZ) {
        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);
        int y = world.getHeightValue(x, z);
        int bio = world.getBiomeGenForCoords(x, z).biomeID;
        if (BiomeHandler.getBiomeSupportsGreatwood(bio) > random.nextFloat()) {
            boolean t = (new WorldGenGreatwoodTrees(false)).generate(world, random, x, y, z, random.nextInt(8) == 0);
            return t;
        } else {
            return false;
        }
    }

    @Unique
    private static boolean generateFlowers(World world, Random random, int x, int y, int z, int flower) {
        WorldGenerator flowers = new WorldGenCustomFlowers(ConfigBlocks.blockCustomPlant, flower);
        return flowers.generate(world, random, x, y, z);
    }

    @Unique
    private void generateOres(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        BiomeGenBase bgb = world.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);
        if (getBiomeBlacklist(bgb.biomeID) != 0 && getBiomeBlacklist(bgb.biomeID) != 2) {
            int i;
            int randPosX;
            int randPosZ;
            int randPosY;
            Block block;
            if (Config.genCinnibar && (newGen || Config.regenCinnibar)) {
                for (i = 0; i < 18; ++i) {
                    randPosX = chunkX * 16 + random.nextInt(16);
                    randPosZ = random.nextInt(world.getHeight() / 5);
                    randPosY = chunkZ * 16 + random.nextInt(16);
                    block = world.getBlock(randPosX, randPosZ, randPosY);
                    if (block != null && block.isReplaceableOreGen(world, randPosX, randPosZ, randPosY, Blocks.stone)) {
                        world.setBlock(randPosX, randPosZ, randPosY, ConfigBlocks.blockCustomOre, 0, 0);
                    }
                }
            }

            if (Config.genAmber && (newGen || Config.regenAmber)) {
                for (i = 0; i < 20; ++i) {
                    randPosX = chunkX * 16 + random.nextInt(16);
                    randPosZ = chunkZ * 16 + random.nextInt(16);
                    randPosY = world.getHeightValue(randPosX, randPosZ) - random.nextInt(25);
                    block = world.getBlock(randPosX, randPosY, randPosZ);
                    if (block != null && block.isReplaceableOreGen(world, randPosX, randPosY, randPosZ, Blocks.stone)) {
                        world.setBlock(randPosX, randPosY, randPosZ, ConfigBlocks.blockCustomOre, 7, 2);
                    }
                }
            }

            if (Config.genInfusedStone && (newGen || Config.regenInfusedStone)) {
                for (i = 0; i < 8; ++i) {
                    randPosX = chunkX * 16 + random.nextInt(16);
                    randPosZ = chunkZ * 16 + random.nextInt(16);
                    randPosY = random.nextInt(Math.max(5, world.getHeightValue(randPosX, randPosZ) - 5));
                    int md = random.nextInt(6) + 1;
                    if (random.nextInt(3) == 0) {
                        Aspect tag = BiomeHandler
                            .getRandomBiomeTag(world.getBiomeGenForCoords(randPosX, randPosZ).biomeID, random);
                        if (tag == null) {
                            md = 1 + random.nextInt(6);
                        } else if (tag == Aspect.AIR) {
                            md = 1;
                        } else if (tag == Aspect.FIRE) {
                            md = 2;
                        } else if (tag == Aspect.WATER) {
                            md = 3;
                        } else if (tag == Aspect.EARTH) {
                            md = 4;
                        } else if (tag == Aspect.ORDER) {
                            md = 5;
                        } else if (tag == Aspect.ENTROPY) {
                            md = 6;
                        }
                    }

                    try {
                        (new WorldGenMinable(ConfigBlocks.blockCustomOre, md, 6, Blocks.stone))
                            .generate(world, random, randPosX, randPosY, randPosZ);
                    } catch (Exception var13) {
                        var13.printStackTrace();
                    }
                }
            }

        }
    }

    @Unique
    private static int getBiomeBlacklist(int biome) {
        return !biomeBlacklist.containsKey(biome) ? -1 : biomeBlacklist.get(biome);
    }

    @Unique
    private static boolean generateSilverwood(World world, Random random, int chunkX, int chunkZ) {
        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);
        int y = world.getHeightValue(x, z);
        BiomeGenBase bio = world.getBiomeGenForCoords(x, z);
        if (bio.equals(biomeMagicalForest) || bio.equals(biomeTaint)
            || !BiomeDictionary.isBiomeOfType(bio, BiomeDictionary.Type.MAGICAL)
                && bio.biomeID != BiomeGenBase.forestHills.biomeID
                && bio.biomeID != BiomeGenBase.birchForestHills.biomeID) {
            return false;
        } else {
            boolean t = (new WorldGenSilverwoodTrees(false, 7, 4)).func_76484_a(world, random, x, y, z);
            return t;
        }
    }
}
