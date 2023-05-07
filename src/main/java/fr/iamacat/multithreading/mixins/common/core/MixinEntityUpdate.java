package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import fr.iamacat.multithreading.Multithreaded;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;
@Mixin(World.class)
public abstract class MixinEntityUpdate {
    private static final int MAX_ENTITIES_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final World world = (World) (Object) this;
    private final List<Entity> entitiesToUpdate = new LinkedList<>();
    private final ExecutorService updateExecutor = Executors.newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    public MixinEntityUpdate() {
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        addEntitiesToUpdateQueue(world.loadedEntityList);
    }

    private void addEntitiesToUpdateQueue(Collection<Entity> entities) {
        entitiesToUpdate.addAll(entities);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinEntityUpdate) {
            List<Entity> entitiesToUpdateCopy = new ArrayList<>(entitiesToUpdate);
            int numEntitiesToUpdate = Math.min(entitiesToUpdateCopy.size(), MAX_ENTITIES_PER_TICK);
            for (int i = 0; i < numEntitiesToUpdate; i++) {
                Entity entity = entitiesToUpdateCopy.get(i);
                updateExecutor.execute(() -> {
                    entity.isInWater();
                    if (entity.isEntityAlive()) {
                        entity.onEntityUpdate();
                        if (entity instanceof EntityLivingBase) {
                            EntityLivingBase livingEntity = (EntityLivingBase) entity;
                            livingEntity.getHealth();
                            livingEntity.getDataWatcher().getWatchableObjectFloat(6);
                        }
                    }
                });
            }
        }
    }

    @Inject(method = "addEntity", at = @At("RETURN"))
    private void onAddEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.add(entity);
    }

    @Inject(method = "removeEntity", at = @At("RETURN"))
    private void onRemoveEntity(Entity entity, CallbackInfo ci) {
        entitiesToUpdate.remove(entity);
    }
}
