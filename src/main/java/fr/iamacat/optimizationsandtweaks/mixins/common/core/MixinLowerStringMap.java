package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.server.management.LowerStringMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Mixin(LowerStringMap.class)
public class MixinLowerStringMap {
    @Unique
    private final Map multithreadingandtweaks$internalMap = new LinkedHashMap();
    @Unique
    private final Map<String, String> multithreadingandtweaks$lowercaseCache = new HashMap<>();
    /**
     * @author
     * @reason
     */
    @Overwrite
    public int size() {
        return this.multithreadingandtweaks$internalMap.size();
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isEmpty() {
        return this.multithreadingandtweaks$internalMap.isEmpty();
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean containsKey(Object p_containsKey_1_) {
        String lowercaseKey = getLowercaseKey(p_containsKey_1_);
        return this.multithreadingandtweaks$internalMap.containsKey(lowercaseKey);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean containsValue(Object p_containsValue_1_) {
        return this.multithreadingandtweaks$internalMap.containsValue(p_containsValue_1_);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public Object get(Object p_get_1_) {
        String lowercaseKey = getLowercaseKey(p_get_1_);
        return this.multithreadingandtweaks$internalMap.get(lowercaseKey);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public Object put(String p_put_1_, Object p_put_2_) {
        String lowercaseKey = getLowercaseKey(p_put_1_);
        return this.multithreadingandtweaks$internalMap.put(lowercaseKey, p_put_2_);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public Object remove(Object p_remove_1_) {
        String lowercaseKey = getLowercaseKey(p_remove_1_);
        return this.multithreadingandtweaks$internalMap.remove(lowercaseKey);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void putAll(Map p_putAll_1_) {
        for (Object obj : p_putAll_1_.entrySet()) {
            if (obj instanceof Map.Entry entry) {
                String lowercaseKey = getLowercaseKey(entry.getKey());
                this.multithreadingandtweaks$internalMap.put(lowercaseKey, entry.getValue());
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void clear() {
        this.multithreadingandtweaks$internalMap.clear();
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public Set keySet() {
        return this.multithreadingandtweaks$internalMap.keySet();
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public Collection values() {
        return this.multithreadingandtweaks$internalMap.values();
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public Set entrySet() {
        return this.multithreadingandtweaks$internalMap.entrySet();
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
    private String getLowercaseKey(Object key) {
        if (key instanceof String) {
            String strKey = (String) key;
            String lowercaseKey = multithreadingandtweaks$lowercaseCache.get(strKey);
            if (lowercaseKey == null) {
                lowercaseKey = strKey.toLowerCase();
                multithreadingandtweaks$lowercaseCache.put(strKey, lowercaseKey);
            }
            return lowercaseKey;
        } else {
            return key.toString().toLowerCase();
        }
    }
}
