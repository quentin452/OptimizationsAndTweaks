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
        if (MultithreadingandtweaksConfig.enableMixinEventRegistry && event.phase == TickEvent.Phase.START) {
            Map<Integer, INetworkCache> networks = CacheRegistry.getNetworkCache();
            if (networks.isEmpty()) {
                ci.cancel();
                return;
            }

            for (Iterator<Map.Entry<Integer, INetworkCache>> iterator = networks.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<Integer, INetworkCache> entry = iterator.next();
                INetworkCache cache = entry.getValue();
                int networkID = cache.getNetworkID();

                if (cache instanceof IRefreshCache) {
                    ((IRefreshCache) cache).updateNetwork(networkID);
                }

                if (CableRegistry.getCables(networkID).isEmpty()) {
                    iterator.remove();
                }
            }
            ci.cancel();
        }
    }
}
