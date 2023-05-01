package fr.iamacat.multithreading.mixins.common.core;

import com.google.common.collect.Lists;
import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Mixin(World.class)
public abstract class MixinEntities {

    @Shadow
    public abstract List<Entity> getEntitiesWithinAABB(Class<? extends Entity> clazz, AxisAlignedBB aabb);
    public CompletableFuture<List<Entity>> getEntitiesWithinAABBMultithreaded(Class<? extends Entity> clazz, AxisAlignedBB aabb) {
        int batchSize = MultithreadingandtweaksMultithreadingConfig.batchsize;;
        Executor executor = SharedThreadPool.getExecutorService();

        List<CompletableFuture<List<Entity>>> futures = Lists.partition(getEntitiesWithinAABB(clazz, aabb), batchSize)
            .stream()
            .map(entities -> CompletableFuture.supplyAsync(() -> entities.stream().filter(entity -> entity.getClass() == clazz).collect(Collectors.toList()), executor))
            .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
            .thenApply(ignored -> futures.stream().map(CompletableFuture::join).flatMap(List::stream).collect(Collectors.toList()));
    }
}
