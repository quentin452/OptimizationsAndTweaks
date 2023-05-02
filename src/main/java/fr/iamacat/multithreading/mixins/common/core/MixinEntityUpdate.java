package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = WorldServer.class, priority = 997)
public abstract class MixinEntityUpdate {
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Entity-Update-%d").build());

    private int numberOfCPUs = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final AtomicReference<WorldServer> world = new AtomicReference<>((WorldServer) (Object) this);
    private final ConcurrentHashMap<Integer, Entity> loadedEntities = new ConcurrentHashMap<>();

    protected MixinEntityUpdate() {}

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        WorldServer worldServer = (WorldServer) (Object) this;
        world.set(worldServer);
        addEntitiesToUpdateQueue(worldServer.loadedEntityList);
    }

    private synchronized void addEntitiesToUpdateQueue(Collection<Entity> entities) {
        loadedEntities.putAll(
            entities.stream()
                .collect(Collectors.toConcurrentMap(Entity::getEntityId, Function.identity(), (e1, e2) -> e1)));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            ConcurrentHashMap<Integer, Entity> entitiesToUpdate = new ConcurrentHashMap<>(loadedEntities);
            loadedEntities.clear();

            int numBatches = (entitiesToUpdate.size() + MAX_ENTITIES_PER_TICK - 1) / MAX_ENTITIES_PER_TICK; // round up
                                                                                                            // division
            List<List<Entity>> batches = new ArrayList<>(numBatches);
            Iterator<Entity> iter = entitiesToUpdate.values()
                .iterator();
            for (int i = 0; i < numBatches; i++) {
                List<Entity> batch = new ArrayList<>(MAX_ENTITIES_PER_TICK);
                for (int j = 0; j < MAX_ENTITIES_PER_TICK && iter.hasNext(); j++) {
                    batch.add(iter.next());
                }
                batches.add(batch);
            }

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfCPUs);
            for (List<Entity> batch : batches) {
                executorService.execute(() -> {
                    for (Entity entity : batch) {
                        try {
                            if (entity != null) {
                                entity.onEntityUpdate();
                            }
                        } catch (Exception e) {
                            // Handle the exception in a specific way, such as logging the error and continuing with the
                            // program.
                            e.printStackTrace();
                        }
                    }
                });
            }
            executorService.shutdown();
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
