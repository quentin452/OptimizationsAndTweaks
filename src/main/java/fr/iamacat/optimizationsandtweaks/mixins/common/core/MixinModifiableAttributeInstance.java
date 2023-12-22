package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.google.common.collect.Maps;

@Mixin(ModifiableAttributeInstance.class)
public class MixinModifiableAttributeInstance {

    @Unique
    private ModifiableAttributeInstance optimizationsAndTweaks$modifiableAttributeInstance;
    @Shadow
    private final Map mapByUUID = Maps.newHashMap();

    @Shadow
    private final Map mapByOperation = Maps.newHashMap();

    @Shadow
    private final Map mapByName = Maps.newHashMap();

    @Shadow
    private boolean needsUpdate = true;

    @Shadow
    private final BaseAttributeMap attributeMap;

    public MixinModifiableAttributeInstance(BaseAttributeMap attributeMap) {
        this.attributeMap = attributeMap;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public AttributeModifier getModifier(UUID id) {
        return (AttributeModifier) this.mapByUUID.get(id);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void applyModifier(AttributeModifier modifier) {
        UUID id = modifier.getID();
        if (this.mapByUUID.containsKey(id)) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }

        Set<AttributeModifier> operationSet = (Set<AttributeModifier>) this.mapByOperation
            .computeIfAbsent(modifier.getOperation(), k -> new HashSet<>());
        Set<AttributeModifier> nameSet = (Set<AttributeModifier>) this.mapByName
            .computeIfAbsent(modifier.getName(), k -> new HashSet<>());

        operationSet.add(modifier);
        nameSet.add(modifier);
        this.mapByUUID.put(id, modifier);
        this.flagForUpdate();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void removeModifier(AttributeModifier modifier) {
        int operation = modifier.getOperation();
        Set<AttributeModifier> operationSet = (Set<AttributeModifier>) this.mapByOperation.get(operation);

        if (operationSet != null) {
            operationSet.remove(modifier);
        }

        Set<AttributeModifier> nameSet = (Set<AttributeModifier>) this.mapByName.get(modifier.getName());

        if (nameSet != null) {
            nameSet.remove(modifier);
            if (nameSet.isEmpty()) {
                this.mapByName.remove(modifier.getName());
            }
        }

        this.mapByUUID.remove(modifier.getID());
        this.flagForUpdate();
    }

    @Shadow
    private void flagForUpdate() {
        this.needsUpdate = true;
        this.attributeMap.addAttributeInstance(optimizationsAndTweaks$modifiableAttributeInstance);
    }
}
