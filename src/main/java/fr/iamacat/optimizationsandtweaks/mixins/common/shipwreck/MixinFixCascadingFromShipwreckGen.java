package fr.iamacat.optimizationsandtweaks.mixins.common.shipwreck;

import java.util.*;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.winslow.shipwreckworldgen.RandomWeightedShip;
import com.winslow.shipwreckworldgen.ShipwreckConfig;
import com.winslow.shipwreckworldgen.ShipwreckGen;
import com.winslow.shipwreckworldgen.shipwrecks.*;

import cpw.mods.fml.common.IWorldGenerator;
import fr.iamacat.optimizationsandtweaks.config.MultithreadingandtweaksConfig;

@Mixin(ShipwreckGen.class)
public class MixinFixCascadingFromShipwreckGen implements IWorldGenerator {

    @Unique
    private static List<BiomeGenBase> validBiomes;

    static {
        validBiomes = Arrays.asList(BiomeGenBase.beach, BiomeGenBase.coldBeach, BiomeGenBase.stoneBeach);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (world.provider.dimensionId == 0 && MultithreadingandtweaksConfig.enableMixinFixCascadingFromShipwreckGen) {
            generateSurface(world, chunkX * 16, chunkZ * 16, random);
        }
    }

    @Unique
    private void generateSurface(World world, int posX, int posZ, Random random) {
        BiomeGenBase biome = world.getBiomeGenForCoords(posX, posZ);

        if (isValidBiome(biome) && canSpawnStructureAtCoords(posX, posZ, random)) {
            String structure = getRandomStructure(random);
            generateStructure(world, posX, posZ, random, structure);
        }
    }

    @Unique
    private boolean isValidBiome(BiomeGenBase biome) {
        return validBiomes.contains(biome);
    }

    @Unique
    private boolean canSpawnStructureAtCoords(int posX, int posZ, Random random) {
        int maxDist = ShipwreckConfig.shipwreckMaxDistance;
        int minDist = ShipwreckConfig.shipwreckMinDistance;

        if (minDist < 0) {
            minDist = 0;
        }

        int xVal = posX / 16;
        int zVal = posZ / 16;

        if (xVal < 0) {
            xVal *= -1;
        }

        if (zVal < 0) {
            zVal *= -1;
        }

        int chunkRange = maxDist - minDist;
        int checkX = xVal / maxDist;
        int checkZ = zVal / maxDist;

        checkX *= maxDist;
        checkZ *= maxDist;
        checkX += random.nextInt(chunkRange);
        checkZ += random.nextInt(chunkRange);

        return xVal == checkX && zVal == checkZ;
    }

    @Unique
    private String getRandomStructure(Random random) {
        RandomWeightedShip weightedShip = new RandomWeightedShip();
        weightedShip.add(ShipwreckConfig.waverunnerOceanRarity, "waverunner");
        weightedShip.add(ShipwreckConfig.schoonerOceanRarity, "schooner");
        weightedShip.add(ShipwreckConfig.sloopOceanRarity, "sloop");
        weightedShip.add(ShipwreckConfig.sailboatOceanRarity, "sailboat");
        weightedShip.add(ShipwreckConfig.rowboatOceanRarity, "rowboat");

        if (isValidBiome(BiomeGenBase.stoneBeach)) {
            weightedShip.add(ShipwreckConfig.spireRarity, "spire");
        }

        return (String) weightedShip.next();
    }

    @Unique
    private void generateStructure(World world, int posX, int posZ, Random random, String structure) {
        switch (structure) {
            case "rowboat":
                RowboatGen.generateRowboat(world, random, posX, posZ);
                break;
            case "sailUp":
                SailboatUprightGen.generateSailboatUpright(world, random, posX, posZ);
                break;
            case "sailSide":
                SailboatSideGen.generateSailboatSide(world, random, posX, posZ);
                break;
            case "sloop":
                SloopGen.generateSloop(world, random, posX, posZ);
                break;
            case "schooner":
                SchoonerGen.generateSchooner(world, random, posX, posZ);
                break;
            case "waverunner":
                WaverunnerGen.generateWaverunner(world, random, posX, posZ);
                break;
            case "spire":
                SpireGen.generateSpire(world, random, posX, posZ);
                break;
            default:
                break;
        }
    }
}
