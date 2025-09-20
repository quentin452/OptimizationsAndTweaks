package fr.iamacat.optimizationsandtweaks.utilsformods.chromaticraft;

import java.util.Objects;

import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class ChromaticraftUtils {

    public static class WorldStructureKey {

        final ReikaWorldHelper.WorldID worldID;
        final ChromaStructures structure;

        public WorldStructureKey(ReikaWorldHelper.WorldID id, ChromaStructures s) {
            worldID = id;
            structure = s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorldStructureKey that = (WorldStructureKey) o;
            return Objects.equals(worldID, that.worldID) && structure == that.structure;
        }

        @Override
        public int hashCode() {
            return Objects.hash(worldID, structure);
        }
    }
}
