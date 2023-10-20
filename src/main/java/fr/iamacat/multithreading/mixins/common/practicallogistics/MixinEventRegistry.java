package fr.iamacat.multithreading.mixins.common.practicallogistics;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(EventRegistry.class)
public class MixinEventRegistry {

    @Inject(method = "onServerTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void onServerTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinEventRegistry){
        if (event.phase == TickEvent.Phase.START) {
            LinkedHashMap<Integer, INetworkCache> networks = (LinkedHashMap) CacheRegistry.getNetworkCache().clone();
            if (networks.isEmpty()) {
                return;
            }

            Iterator var3 = networks.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<Integer, INetworkCache> set = (Map.Entry)var3.next();
                INetworkCache cache = set.getValue();
                if (cache instanceof IRefreshCache) {
                    ((IRefreshCache)cache).updateNetwork(cache.getNetworkID());
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
