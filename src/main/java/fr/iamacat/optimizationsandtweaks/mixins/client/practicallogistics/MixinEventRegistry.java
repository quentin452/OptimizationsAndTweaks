package fr.iamacat.optimizationsandtweaks.mixins.client.practicallogistics;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.utils.trove.map.hash.THashMap;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.registries.CableRegistry;
import sonar.logistics.registries.CacheRegistry;
import sonar.logistics.registries.EventRegistry;

@Mixin(EventRegistry.class)
public class MixinEventRegistry {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (OptimizationsandTweaksConfig.enableMixinEventRegistry) {
            if (event.phase == TickEvent.Phase.START) {
                THashMap<Integer, INetworkCache> networks = new THashMap<>();
                networks.putAll(CacheRegistry.getNetworkCache());
                if (networks.isEmpty()) {
                    return;
                }

                for (Map.Entry<Integer, INetworkCache> integerINetworkCacheEntry : networks.entrySet()) {
                    INetworkCache cache = integerINetworkCacheEntry.getValue();
                    if (cache instanceof IRefreshCache) {
                        ((IRefreshCache) cache).updateNetwork(cache.getNetworkID());
                    }

                    if (CableRegistry.getCables(cache.getNetworkID())
                        .size() == 0) {
                        CacheRegistry.getNetworkCache()
                            .remove(cache.getNetworkID());
                    }
                }
            }
        }
    }
}
