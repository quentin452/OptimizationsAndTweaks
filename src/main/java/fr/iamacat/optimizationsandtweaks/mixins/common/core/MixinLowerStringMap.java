package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.*;

import net.minecraft.server.management.LowerStringMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;

@Mixin(LowerStringMap.class)
public class MixinLowerStringMap {

    @Unique
    private final Map optimizationsAndTweaks$internalMap = new LinkedHashMap();
    @Unique
    private final Map<String, String> optimizationsAndTweaks$lowercaseCache = new Object2ObjectHashMap<>();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int size() {
        return this.optimizationsAndTweaks$internalMap.size();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isEmpty() {
        return this.optimizationsAndTweaks$internalMap.isEmpty();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean containsKey(Object p_containsKey_1_) {
        String lowercaseKey = optimizationsAndTweaks$getLowercaseKey(p_containsKey_1_);
        return this.optimizationsAndTweaks$internalMap.containsKey(lowercaseKey);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean containsValue(Object p_containsValue_1_) {
        return this.optimizationsAndTweaks$internalMap.containsValue(p_containsValue_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Object get(Object p_get_1_) {
        String lowercaseKey = optimizationsAndTweaks$getLowercaseKey(p_get_1_);
        return this.optimizationsAndTweaks$internalMap.get(lowercaseKey);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Object put(String p_put_1_, Object p_put_2_) {
        String lowercaseKey = optimizationsAndTweaks$getLowercaseKey(p_put_1_);
        return this.optimizationsAndTweaks$internalMap.put(lowercaseKey, p_put_2_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Object remove(Object p_remove_1_) {
        String lowercaseKey = optimizationsAndTweaks$getLowercaseKey(p_remove_1_);
        return this.optimizationsAndTweaks$internalMap.remove(lowercaseKey);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void putAll(Map p_putAll_1_) {
        for (Object obj : p_putAll_1_.entrySet()) {
            if (obj instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) obj;
                String lowercaseKey = optimizationsAndTweaks$getLowercaseKey(entry.getKey());
                this.optimizationsAndTweaks$internalMap.put(lowercaseKey, entry.getValue());
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void clear() {
        this.optimizationsAndTweaks$internalMap.clear();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Set keySet() {
        return this.optimizationsAndTweaks$internalMap.keySet();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Collection values() {
        return this.optimizationsAndTweaks$internalMap.values();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Set entrySet() {
        return this.optimizationsAndTweaks$internalMap.entrySet();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Object put(Object p_put_1_, Object p_put_2_) {
        return this.put((String) p_put_1_, p_put_2_);
    }

    @Unique
    private String optimizationsAndTweaks$getLowercaseKey(Object key) {
        if (key instanceof String) {
            String strKey = (String) key;
            return optimizationsAndTweaks$lowercaseCache
                .computeIfAbsent(strKey, this::optimizationsAndTweaks$toLowerCase);
        } else {
            return optimizationsAndTweaks$toLowerCase(key.toString());
        }
    }

    @Unique
    private String optimizationsAndTweaks$toLowerCase(String str) {
        return str.toLowerCase(Locale.ROOT);
    }
}
