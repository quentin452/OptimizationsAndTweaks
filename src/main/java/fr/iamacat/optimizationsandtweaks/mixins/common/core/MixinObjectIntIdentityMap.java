package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import com.google.common.collect.Lists;
import net.minecraft.util.ObjectIntIdentityMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ObjectIntIdentityMap.class)
public class MixinObjectIntIdentityMap {
    @Shadow
    protected List field_148748_b = Lists.newArrayList();

    @Overwrite
    public Object func_148745_a(int p_148745_1_) {
        Map<Integer, Object> map = new HashMap<>(this.field_148748_b.size());
        for (int i = 0; i < this.field_148748_b.size(); i++) {
            map.put(i, this.field_148748_b.get(i));
        }
        return map.getOrDefault(p_148745_1_, null);
    }
}
