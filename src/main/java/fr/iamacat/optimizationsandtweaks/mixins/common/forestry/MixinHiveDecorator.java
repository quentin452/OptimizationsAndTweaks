package fr.iamacat.optimizationsandtweaks.mixins.common.forestry;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.worldgen.Hive;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.core.config.Config;
import forestry.plugins.PluginApiculture;

@Mixin(HiveDecorator.class)
public class MixinHiveDecorator {

    @Shadow
    private static final PopulateChunkEvent.Populate.EventType EVENT_TYPE = (PopulateChunkEvent.Populate.EventType) EnumHelper
        .addEnum(PopulateChunkEvent.Populate.EventType.class, "FORESTRY_HIVES", new Class[0], new Object[0]);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void decorateHives(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ,
        boolean hasVillageGenerated) {
        chunkX = chunkX >> 4;
        chunkZ = chunkZ >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return;
        }
        if (TerrainGen.populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, EVENT_TYPE)) {
            decorateHives(world, rand, chunkX, chunkZ);
        }
    }

    @Shadow
    public static void decorateHives(World world, Random rand, int chunkX, int chunkZ) {
        List<Hive> hives = PluginApiculture.hiveRegistry.getHives();
        if (Config.generateBeehivesDebug) {
            decorateHivesDebug(world, chunkX, chunkZ, hives);
        } else {
            Collections.shuffle(hives, rand);
            Iterator var5 = hives.iterator();

            Hive hive;
            do {
                if (!var5.hasNext()) {
                    return;
                }

                hive = (Hive) var5.next();
            } while (!genHive(world, rand, chunkX, chunkZ, hive));

        }
    }

    @Shadow
    private static void decorateHivesDebug(World world, int chunkX, int chunkZ, List<Hive> hives) {
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        BiomeGenBase biome = world.getBiomeGenForCoords(worldX, worldZ);
        EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Collections.shuffle(hives, world.rand);
                Iterator var10 = hives.iterator();

                while (var10.hasNext()) {
                    Hive hive = (Hive) var10.next();
                    if (hive.isGoodBiome(biome) && hive.isGoodHumidity(humidity)) {
                        tryGenHive(world, worldX + x, worldZ + z, hive);
                    }
                }
            }
        }

    }

    @Shadow
    public static boolean genHive(World world, Random rand, int chunkX, int chunkZ, Hive hive) {
        if ((double) hive.genChance() * Config.getBeehivesAmount() < (double) (rand.nextFloat() * 100.0F)) {
            return false;
        } else {
            int worldX = chunkX * 16;
            int worldZ = chunkZ * 16;
            BiomeGenBase biome = world.getBiomeGenForCoords(worldX, worldZ);
            EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);
            if (hive.isGoodBiome(biome) && hive.isGoodHumidity(humidity)) {
                for (int tries = 0; tries < 4; ++tries) {
                    int x = worldX + rand.nextInt(16);
                    int z = worldZ + rand.nextInt(16);
                    if (tryGenHive(world, x, z, hive)) {
                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        }
    }

    @Shadow
    private static boolean tryGenHive(World world, int x, int z, Hive hive) {
        int y = hive.getYForHive(world, x, z);
        if (y < 0) {
            return false;
        } else if (!hive.canReplace(world, x, y, z)) {
            return false;
        } else {
            BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
            EnumTemperature temperature = EnumTemperature.getFromValue(biome.getFloatTemperature(x, y, z));
            if (!hive.isGoodTemperature(temperature)) {
                return false;
            } else {
                return !hive.isValidLocation(world, x, y, z) ? false : setHive(world, x, y, z, hive);
            }
        }
    }

    @Shadow
    private static boolean setHive(World world, int x, int y, int z, Hive hive) {
        Block hiveBlock = hive.getHiveBlock();
        boolean placed = world.setBlock(x, y, z, hiveBlock, hive.getHiveMeta(), 2);
        if (!placed) {
            return false;
        } else {
            Block placedBlock = world.getBlock(x, y, z);
            if (!Block.isEqualTo(hiveBlock, placedBlock)) {
                return false;
            } else {
                hiveBlock.onBlockAdded(world, x, y, z);
                world.markBlockForUpdate(x, y, z);
                if (!Config.generateBeehivesDebug) {
                    hive.postGen(world, x, y, z);
                }

                return true;
            }
        }
    }
}
