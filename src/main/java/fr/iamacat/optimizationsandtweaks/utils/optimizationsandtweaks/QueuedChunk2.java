package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks;

public class QueuedChunk2 {
    final int x;
    final int z;
    final net.minecraft.world.chunk.storage.AnvilChunkLoader loader;
    final net.minecraft.world.World world;
    final net.minecraft.world.gen.ChunkProviderServer provider;
    net.minecraft.nbt.NBTTagCompound compound;

    public QueuedChunk2(int x, int z, net.minecraft.world.chunk.storage.AnvilChunkLoader loader, net.minecraft.world.World world, net.minecraft.world.gen.ChunkProviderServer provider) {
        this.x = x;
        this.z = z;
        this.loader = loader;
        this.world = world;
        this.provider = provider;
    }

    @Override
    public int hashCode() {
        return (x * 31 + z * 29) ^ world.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof QueuedChunk2) {
            QueuedChunk2 other = (QueuedChunk2) object;
            return x == other.x && z == other.z && world == other.world;
        }

        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " {" + NEW_LINE);
        result.append(" x: " + x + NEW_LINE);
        result.append(" z: " + z + NEW_LINE);
        result.append(" loader: " + loader + NEW_LINE );
        result.append(" world: " + world.getWorldInfo().getWorldName() + NEW_LINE);
        result.append(" dimension: " + world.provider.dimensionId + NEW_LINE);
        result.append(" provider: " + world.provider.getClass().getName() + NEW_LINE);
        result.append("}");

        return result.toString();
    }
}
