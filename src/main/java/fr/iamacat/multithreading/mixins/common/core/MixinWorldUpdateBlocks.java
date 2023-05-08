package fr.iamacat.multithreading.mixins.common.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinWorldUpdateBlocks {

    private final ExecutorService executorService = Executors.newFixedThreadPool(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        new ThreadFactoryBuilder().setNameFormat("MixinWorldUpdateBlocks-worker-%d")
            .build());

    private World world;

    public MixinWorldUpdateBlocks(World world) {
        this.world = world;
    }

    @Inject(method = "updateBlocks", at = @At("HEAD"))
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.world == world && MultithreadingandtweaksMultithreadingConfig.enableMixinWorldUpdateBlocks) {
            int minX = -30000000;
            int minY = 0;
            int minZ = -30000000;
            int maxX = 30000000;
            int maxY = 255;
            int maxZ = 30000000;

            int chunkMinX = minX >> 4;
            int chunkMinZ = minZ >> 4;
            int chunkMaxX = maxX >> 4;
            int chunkMaxZ = maxZ >> 4;

            for (int chunkX = chunkMinX; chunkX <= chunkMaxX; chunkX++) {
                for (int chunkZ = chunkMinZ; chunkZ <= chunkMaxZ; chunkZ++) {
                    final Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                    if (chunk != null) {
                        final int finalChunkX = chunkX;
                        final int finalChunkZ = chunkZ;
                        executorService.submit(
                            () -> updateChunk(chunk, minX, minY, minZ, maxX, maxY, maxZ, finalChunkX, finalChunkZ));
                    }
                }
            }
        }
    }

    private void updateChunk(Chunk chunk, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int chunkX,
        int chunkZ) {
        int chunkMinX = chunkX << 4;
        int chunkMinY = minY < 0 ? 0 : minY;
        int chunkMinZ = chunkZ << 4;
        int chunkMaxX = chunkMinX | 15;
        int chunkMaxY = maxY > 255 ? 255 : maxY;
        int chunkMaxZ = chunkMinZ | 15;

        int xMin = Math.max(chunkMinX, minX);
        int yMin = Math.max(chunkMinY, minY);
        int zMin = Math.max(chunkMinZ, minZ);
        int xMax = Math.min(chunkMaxX, maxX);
        int yMax = Math.min(chunkMaxY, maxY);
        int zMax = Math.min(chunkMaxZ, maxZ);

        for (int i = xMin; i <= xMax; i++) {
            int x = i & 15;
            for (int j = yMin; j <= yMax; j++) {
                for (int k = zMin; k <= zMax; k++) {
                    chunk.func_150807_a(x, j, k, world.getBlock(i, j, k), world.getBlockMetadata(i, j, k));
                }
            }
        }
    }
}
