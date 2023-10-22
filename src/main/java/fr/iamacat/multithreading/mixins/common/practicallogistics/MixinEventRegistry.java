package fr.iamacat.multithreading.mixins.common.practicallogistics;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.utils.trove.map.hash.THashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.registries.CableRegistry;
import sonar.logistics.registries.CacheRegistry;
import sonar.logistics.registries.EventRegistry;

import java.util.HashMap;
import java.util.Map;

@Mixin(EventRegistry.class)
public class MixinEventRegistry {
    /**
     * @author
     * @reason
     */
    @Inject(method = "onServerTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void
    onServerTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (event.phase == TickEvent.Phase.START) {
            if (MultithreadingandtweaksConfig.enableMixinEventRegistry){
            THashMap<Integer, INetworkCache> networks = (THashMap)CacheRegistry.getNetworkCache().clone();
            if (networks.isEmpty()) {
                return;
            }

            for (Map.Entry<Integer, INetworkCache> integerINetworkCacheEntry : networks.entrySet()) {
                INetworkCache cache = integerINetworkCacheEntry.getValue();
                if (cache instanceof IRefreshCache) {
                    ((IRefreshCache) cache).updateNetwork(cache.getNetworkID());
                }

                if (CableRegistry.getCables(cache.getNetworkID()).size() == 0) {
                    CacheRegistry.getNetworkCache().remove(cache.getNetworkID());
                }
            }
        }
            ci.cancel();
        }
    }
}
