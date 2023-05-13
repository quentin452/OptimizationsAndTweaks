package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.falsepattern.lib.compat.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(World.class)
public abstract class MixinTileEntitiesTick {
    private final Queue<TileEntity> tileEntitiesToTick = new ConcurrentLinkedQueue<>();
    private int tickIndex = 0;
    private int tickPerBatch = MultithreadingandtweaksMultithreadingConfig.batchsize;

    @Inject(method = "func_147448_a", at = @At("TAIL"))
    private void onAddTileEntity(Collection<TileEntity> tileEntityCollection, CallbackInfo ci) {
        tileEntitiesToTick.addAll(tileEntityCollection);
    }

    @Inject(method = "removeTileEntity", at = @At("TAIL"))
    private void onRemoveTileEntity(int x, int y, int z, CallbackInfo ci) {
        // Remove tile entity from the ticking queue if it exists
        tileEntitiesToTick.removeIf(tileEntity -> tileEntity.xCoord == x && tileEntity.yCoord == y && tileEntity.zCoord == z);
    }

    @Inject(method = "updateEntities", at = @At("TAIL"))
    private void onUpdateTileEntities(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinTileEntitiesTick) {
            int endIndex = Math.min(tickIndex + tickPerBatch, tileEntitiesToTick.size());
            Iterator<TileEntity> iterator = tileEntitiesToTick.iterator();
            for (int i = 0; i < endIndex; i++) {
                TileEntity tileEntity = iterator.next();
                try {
                    tileEntity.updateEntity();
                } catch (Throwable t) {
                    // Handle exceptions as appropriate
                }
            }

            tickIndex += tickPerBatch;
            if (tickIndex >= tileEntitiesToTick.size()) {
                tickIndex = 0;
            }
        }
    }
}
