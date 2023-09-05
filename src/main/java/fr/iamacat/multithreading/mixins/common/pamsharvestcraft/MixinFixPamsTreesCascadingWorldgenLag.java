package fr.iamacat.multithreading.mixins.common.pamsharvestcraft;

// todo fixme
// @Mixin(PamTreeGenerator.class)
public class MixinFixPamsTreesCascadingWorldgenLag// implements IWorldGenerator
{

    /*
     * @Unique
     * public void generateSurface(World world, Random rand, int chunkX, int chunkZ) {
     * BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(chunkX * 16, chunkZ * 16);
     * if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.DEAD)) {
     * int k;
     * int l;
     * int i1;
     * int j1;
     * if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST) && !BiomeDictionary.isBiomeOfType(biome,
     * BiomeDictionary.Type.COLD) && !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SPOOKY) &&
     * !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MOUNTAIN)) {
     * for(k = 0; k < BlockRegistry.temperatefruittreeRarity; ++k) {
     * l = rand.nextInt(256);
     * i1 = chunkX + rand.nextInt(16);
     * j1 = chunkZ + rand.nextInt(16);
     * if (world.getBlock(i1, l - 1, j1) == Blocks.grass ||
     * world.getBlock(i1, l - 1, j1) == Blocks.dirt) {
     * switch (rand.nextInt(8)) {
     * case 0:
     * if (BlockRegistry.appletreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 0, 0, BlockRegistry.pamApple)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 1:
     * if (BlockRegistry.avocadotreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 0, 0, BlockRegistry.pamAvocado)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 2:
     * if (BlockRegistry.cherrytreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 0, 0, BlockRegistry.pamCherry)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 3:
     * if (BlockRegistry.chestnuttreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 0, 0, BlockRegistry.pamChestnut)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 4:
     * if (BlockRegistry.nutmegtreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 0, 0, BlockRegistry.pamNutmeg)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 5:
     * if (BlockRegistry.peartreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 0, 0, BlockRegistry.pamPear)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 6:
     * if (BlockRegistry.walnuttreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 0, 0, BlockRegistry.pamWalnut)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 7:
     * if (BlockRegistry.gooseberrytreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 0, 0, BlockRegistry.pamGooseberry)).func_76484_a(world, rand, i1, l, j1);
     * }
     * }
     * }
     * }
     * }
     * if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.OCEAN) && BlockRegistry.coconuttreeGeneration) {
     * for(k = 0; k < BlockRegistry.tropicalfruittreeRarity; ++k) {
     * l = rand.nextInt(256);
     * i1 = chunkX + rand.nextInt(16);
     * j1 = chunkZ + rand.nextInt(16);
     * if (world.getBlock(i1, l - 1, j1) == Blocks.grass ||
     * world.getBlock(i1, l - 1, j1) == Blocks.dirt) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamCoconut)).func_76484_a(world, rand, i1, l, j1);
     * }
     * }
     * }
     * if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.WET) && !BiomeDictionary.isBiomeOfType(biome,
     * BiomeDictionary.Type.PLAINS) && !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.DRY) ||
     * BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.HOT) && !BiomeDictionary.isBiomeOfType(biome,
     * BiomeDictionary.Type.PLAINS) && !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.DRY)) {
     * for(k = 0; k < BlockRegistry.tropicalfruittreeRarity; ++k) {
     * l = rand.nextInt(256);
     * i1 = chunkX + rand.nextInt(16);
     * j1 = chunkZ + rand.nextInt(16);
     * if (world.getBlock(i1, l - 1, j1) == Blocks.grass ||
     * world.getBlock(i1, l - 1, j1) == Blocks.dirt) {
     * switch (rand.nextInt(24)) {
     * case 0:
     * if (BlockRegistry.bananatreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamBanana)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 1:
     * if (BlockRegistry.cinnamontreeGeneration) {
     * (new WorldGenPamFruitLogTree(true, 5, 3, 3, BlockRegistry.pamCinnamon)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 2:
     * if (BlockRegistry.coconuttreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamCoconut)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 3:
     * if (BlockRegistry.datetreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamDate)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 4:
     * if (BlockRegistry.dragonfruittreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamDragonfruit)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 5:
     * if (BlockRegistry.papayatreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamPapaya)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 6:
     * if (BlockRegistry.almondtreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamAlmond)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 7:
     * if (BlockRegistry.apricottreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamApricot)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 8:
     * if (BlockRegistry.cashewtreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamCashew)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 9:
     * if (BlockRegistry.duriantreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamDurian)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 10:
     * if (BlockRegistry.figtreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamFig)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 11:
     * if (BlockRegistry.grapefruittreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamGrapefruit)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 12:
     * if (BlockRegistry.lemontreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamLemon)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 13:
     * if (BlockRegistry.limetreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamLime)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 14:
     * if (BlockRegistry.mangotreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamMango)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 15:
     * if (BlockRegistry.orangetreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamOrange)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 16:
     * if (BlockRegistry.paperbarktreeGeneration) {
     * (new WorldGenPamFruitLogTree(true, 5, 3, 3, BlockRegistry.pamPaperbark)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 17:
     * if (BlockRegistry.peachtreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamPeach)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 18:
     * if (BlockRegistry.pecantreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamPecan)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 19:
     * if (BlockRegistry.peppercorntreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamPeppercorn)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 20:
     * if (BlockRegistry.persimmontreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamPersimmon)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 21:
     * if (BlockRegistry.pistachiotreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamPistachio)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 22:
     * if (BlockRegistry.pomegranatetreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamPomegranate)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 23:
     * if (BlockRegistry.starfruittreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamStarfruit)).func_76484_a(world, rand, i1, l, j1);
     * }
     * case 24:
     * if (BlockRegistry.vanillabeantreeGeneration) {
     * (new WorldGenPamFruitTree(true, 5, 3, 3, BlockRegistry.pamVanillabean)).func_76484_a(world, rand, i1, l, j1);
     * }
     * }
     * }
     * }
     * if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.CONIFEROUS) && BlockRegistry.mapletreeGeneration) {
     * for(k = 0; k < BlockRegistry.coniferousfruittreeRarity; ++k) {
     * l = rand.nextInt(256);
     * i1 = chunkX + rand.nextInt(16);
     * j1 = chunkZ + rand.nextInt(16);
     * if (world.getBlock(i1, l - 1, j1) == Blocks.grass ||
     * world.getBlock(i1, l - 1, j1) == Blocks.dirt) {
     * (new WorldGenPamFruitLogTree(true, 5, 1, 1, BlockRegistry.pamMaple)).func_76484_a(world, rand, i1, l, j1);
     * }
     * }
     * }
     * }
     * }
     * }
     * @Override
     * public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
     * IChunkProvider chunkProvider) {
     * BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(chunkX * 16, chunkZ * 16);
     * if(!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.DEAD)) {
     * this.generateSurface(world, random, chunkX * 16, chunkZ * 16);
     * }
     * }
     */
}
