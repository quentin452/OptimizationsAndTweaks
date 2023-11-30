package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.ChunkIOProvider2;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.QueuedChunk2;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.chunkio.ChunkIOExecutor;
import net.minecraftforge.common.util.AsynchronousExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkIOExecutor.class)
public class MixinChunkIOExecutor {

    @Shadow
    static final int BASE_THREADS = 1;
    @Shadow
    static final int PLAYERS_PER_THREAD = 50;
    @Unique
    private static final AsynchronousExecutor<QueuedChunk2, Chunk, Runnable, RuntimeException> instance = new AsynchronousExecutor<QueuedChunk2, net.minecraft.world.chunk.Chunk, Runnable, RuntimeException>(new ChunkIOProvider2(), BASE_THREADS);
    @Overwrite
    public static net.minecraft.world.chunk.Chunk syncChunkLoad(net.minecraft.world.World world, net.minecraft.world.chunk.storage.AnvilChunkLoader loader, net.minecraft.world.gen.ChunkProviderServer provider, int x, int z) {
        return instance.getSkipQueue(new QueuedChunk2(x, z, loader, world, provider));
    }
    @Overwrite
    public static void queueChunkLoad(net.minecraft.world.World world, net.minecraft.world.chunk.storage.AnvilChunkLoader loader, net.minecraft.world.gen.ChunkProviderServer provider, int x, int z, Runnable runnable) {
        instance.add(new QueuedChunk2(x, z, loader, world, provider), runnable);
    }
    @Overwrite
    // Abuses the fact that hashCode and equals for QueuedChunk only use world and coords
    public static void dropQueuedChunkLoad(net.minecraft.world.World world, int x, int z, Runnable runnable) {
        instance.drop(new QueuedChunk2(x, z, null, world, null), runnable);
    }
    @Overwrite
    public static void adjustPoolSize(int players) {
        int size = Math.max(BASE_THREADS, (int) Math.ceil(players / PLAYERS_PER_THREAD));
        instance.setActiveThreads(size);
    }
    @Overwrite
    public static void tick() {
        instance.finishActive();
    }
}
