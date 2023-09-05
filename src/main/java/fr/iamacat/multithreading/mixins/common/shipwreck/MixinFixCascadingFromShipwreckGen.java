package fr.iamacat.multithreading.mixins.common.shipwreck;

import java.util.*;

import com.winslow.shipwreckworldgen.RandomWeightedShip;
import com.winslow.shipwreckworldgen.ShipwreckConfig;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;

import com.winslow.shipwreckworldgen.ShipwreckGen;
import com.winslow.shipwreckworldgen.shipwrecks.*;

import cpw.mods.fml.common.IWorldGenerator;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShipwreckGen.class)
public class MixinFixCascadingFromShipwreckGen implements IWorldGenerator {

    @Unique
    private static List biomelist;
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        switch(world.provider.dimensionId) {
            case 0: // Overworld
                this.generateSurface(world, chunkX * 16, chunkZ * 16);
            default:
        }
    }

    @Unique
    private void generateSurface(World world, int posX, int posZ) {
        BiomeGenBase b = world.getBiomeGenForCoords(posX, posZ);
        Random random = new Random();
        if (this.canSpawnStructureAtCoords(world, posX, posZ)) {
            posX += random.nextInt(8) - 16;
            posZ += random.nextInt(8) - 16;
            RandomWeightedShip beachList;
            String structure;
            if (!b.biomeName.equals("Ocean") && !b.biomeName.equals("Deep Ocean") && !b.biomeName.equals("FrozenOcean")) {
                if (b.biomeName.equals("Beach") || b.biomeName.equals("Cold Beach") || b.biomeName.equals("Stone Beach")) {
                    beachList = new RandomWeightedShip();
                    beachList.add((double) ShipwreckConfig.waverunnerOceanRarity, "waverunner");
                    beachList.add((double)ShipwreckConfig.schoonerOceanRarity, "schooner");
                    beachList.add((double)ShipwreckConfig.sloopOceanRarity, "sloop");
                    beachList.add((double)ShipwreckConfig.sailboatOceanRarity, "sailboat");
                    beachList.add((double)ShipwreckConfig.rowboatOceanRarity, "rowboat");
                    structure = (String)beachList.next();
                    if (structure == "rowboat") {
                        RowboatGen.generateRowboat(world, random, posX, posZ);
                    } else if (structure == "sailUp") {
                        SailboatUprightGen.generateSailboatUpright(world, random, posX, posZ);
                    } else if (structure == "sailSide") {
                        SailboatSideGen.generateSailboatSide(world, random, posX, posZ);
                    } else if (structure == "sloop") {
                        SloopGen.generateSloop(world, random, posX, posZ);
                    } else if (structure == "schooner") {
                        SchoonerGen.generateSchooner(world, random, posX, posZ);
                    } else if (structure == "waverunner") {
                        WaverunnerGen.generateWaverunner(world, random, posX, posZ);
                    }
                }
            } else {
                beachList = new RandomWeightedShip();
                beachList.add((double)ShipwreckConfig.waverunnerOceanRarity, "waverunner");
                beachList.add((double)ShipwreckConfig.schoonerOceanRarity, "schooner");
                beachList.add((double)ShipwreckConfig.sloopOceanRarity, "sloop");
                beachList.add((double)(ShipwreckConfig.sailboatOceanRarity / 2), "sailUp");
                beachList.add((double)(ShipwreckConfig.sailboatOceanRarity / 2), "sailSide");
                beachList.add((double)ShipwreckConfig.rowboatOceanRarity, "rowboat");
                beachList.add((double)ShipwreckConfig.spireRarity, "spire");
                structure = (String)beachList.next();
                if (structure == "rowboat") {
                    RowboatGen.generateRowboat(world, random, posX, posZ);
                } else if (structure == "sailUp") {
                    SailboatUprightGen.generateSailboatUpright(world, random, posX, posZ);
                } else if (structure == "sailSide") {
                    SailboatSideGen.generateSailboatSide(world, random, posX, posZ);
                } else if (structure == "sloop") {
                    SloopGen.generateSloop(world, random, posX, posZ);
                } else if (structure == "spire") {
                    SpireGen.generateSpire(world, random, posX, posZ);
                } else if (structure == "schooner") {
                    SchoonerGen.generateSchooner(world, random, posX, posZ);
                } else if (structure == "waverunner") {
                    WaverunnerGen.generateWaverunner(world, random, posX, posZ);
                }
            }
        }

    }

    @Unique
    protected boolean canSpawnStructureAtCoords(World world, int posX, int posZ) {
        int maxDist = ShipwreckConfig.shipwreckMaxDistance;
        int minDist = ShipwreckConfig.shipwreckMinDistance;
        if (minDist < 0) {
            minDist = 0;
        }

        Random random;
        for(random = new Random(); maxDist <= minDist; ++maxDist) {
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

    static {
        biomelist = Arrays.asList(BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.megaTaiga, BiomeGenBase.megaTaigaHills);
    }
}
