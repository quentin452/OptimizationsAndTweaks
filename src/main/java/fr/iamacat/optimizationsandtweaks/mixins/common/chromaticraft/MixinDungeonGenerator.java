package fr.iamacat.optimizationsandtweaks.mixins.common.chromaticraft;

import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.World.IWG.DungeonGenerator;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Math.Noise.VoronoiNoiseGenerator;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import fr.iamacat.optimizationsandtweaks.utilsformods.chromaticraft.ChromaticraftUtils;

@Mixin(DungeonGenerator.class)
public class MixinDungeonGenerator {

    @Shadow
    private EnumMap<ChromaStructures, VoronoiNoiseGenerator> structs = new EnumMap(ChromaStructures.class);

    @Unique
    private final Map<ChromaticraftUtils.WorldStructureKey, VoronoiNoiseGenerator> optimizationsAndTweaks$noiseCache = new WeakHashMap<>();

    /**
     * @author quentin452
     * @reason Add a cache to DungeonGenerator from Chromaticraft to avoid reloading the dungeon every time from file
     */

    @Overwrite(remap = false)
    private void updateNoisemaps(World world) {
        ReikaWorldHelper.WorldID id = ReikaWorldHelper.getCurrentWorldID(world);

        for (ChromaStructures s : structs.keySet()) {
            ChromaticraftUtils.WorldStructureKey key = new ChromaticraftUtils.WorldStructureKey(id, s);
            VoronoiNoiseGenerator v = optimizationsAndTweaks$noiseCache.get(key);

            if (v == null) {
                long seed = calculateStructureSeed(world, id, s);
                v = createNewGenerator(s, seed);
                optimizationsAndTweaks$noiseCache.put(key, v);
            }

            structs.put(s, v);
        }
    }

    private long calculateStructureSeed(World world, ReikaWorldHelper.WorldID id, ChromaStructures s) {
        return world.getSeed() ^ (s.ordinal() * 41381L)
            ^ ~id.worldCreationTime
            ^ ReikaFileReader.getRealPath(
                world.getSaveHandler()
                    .getWorldDirectory())
                .hashCode();
    }

    private VoronoiNoiseGenerator createNewGenerator(ChromaStructures s, long seed) {
        VoronoiNoiseGenerator gen = (VoronoiNoiseGenerator) new VoronoiNoiseGenerator(seed)
            .setFrequency(0.75D / getNoiseScale(s));
        gen.randomFactor = 0.55;
        return gen;
    }

    @Shadow
    private int getNoiseScale(ChromaStructures s) {
        switch (s) {
            case DESERT:
                return 440;
            case OCEAN:
                return 640;
            case CAVERN:
                return 144;
            case BURROW:
                return 240;
            case SNOWSTRUCT:
                return 480;
            case BIOMEFRAG:
                return 640;
            default:
                return 1;
        }
    }
}
