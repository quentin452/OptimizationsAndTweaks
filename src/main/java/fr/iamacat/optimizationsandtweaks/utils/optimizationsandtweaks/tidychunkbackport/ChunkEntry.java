package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.tidychunkbackport;

import com.falsepattern.lib.compat.ChunkPos;

public class ChunkEntry {

    private final ChunkPos chunkPos;
    private final long time;

    public ChunkEntry(ChunkPos chunkPos, long time) {
        this.chunkPos = chunkPos;
        this.time = time;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public long getTime() {
        return time;
    }
}
