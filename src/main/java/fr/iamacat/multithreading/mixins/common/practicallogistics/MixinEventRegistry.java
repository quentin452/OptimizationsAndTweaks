package fr.iamacat.multithreading.mixins.common.practicallogistics;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.registries.CableRegistry;
import sonar.logistics.registries.CacheRegistry;
import sonar.logistics.registries.EventRegistry;

import java.util.Iterator;
import java.util.Map;

@Mixin(EventRegistry.class)
public class MixinEventRegistry {

    @Inject(method = "onServerTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void onServerTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEventRegistry) {
            if (event.phase == TickEvent.Phase.START) {
                Map<Integer, INetworkCache> networks = CacheRegistry.getNetworkCache();
                if (networks.isEmpty()) {
                    return;
                }

                Iterator<Map.Entry<Integer, INetworkCache>> iterator = networks.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, INetworkCache> entry = iterator.next();
                    INetworkCache cache = entry.getValue();
                    if (cache instanceof IRefreshCache) {
                        ((IRefreshCache) cache).updateNetwork(cache.getNetworkID());
                    }

                    if (CableRegistry.getCables(cache.getNetworkID()).size() == 0) {
                        iterator.remove();
                    }
                }
            }
            ci.cancel();
        }
    }
}
