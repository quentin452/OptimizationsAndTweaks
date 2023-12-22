package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.thaumcraft;

import thaumcraft.api.nodes.NodeType;

public class Thaumcraft {

    public static class BiomeAuraResult {

        private final int baura;
        private final NodeType type;

        public BiomeAuraResult(int baura, NodeType type) {
            this.baura = baura;
            this.type = type;
        }

        public int getBaura() {
            return baura;
        }

        public NodeType getType() {
            return type;
        }
    }
}
