package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EnumCreatureType.class)
public abstract class MixinEnumCreatureType {

    @Shadow
    private final Class<?> creatureClass;
    @Shadow private final Material creatureMaterial;
    @Shadow private final boolean isPeacefulCreature;
    @Shadow private final boolean isAnimal;
    @Unique
    private Class<?> cachedCreatureClass;
    @Unique
    private Material cachedCreatureMaterial;
    @Unique
    private boolean cachedIsPeacefulCreature;
    @Unique
    private boolean cachedIsAnimal;

    protected MixinEnumCreatureType(Class<?> creatureClass, Material creatureMaterial1, boolean isPeacefulCreature1, boolean isAnimal1, Material creatureMaterial, boolean isPeacefulCreature, boolean isAnimal) {
        this.creatureClass = creatureClass;
        this.cachedCreatureClass = creatureClass;
        this.creatureMaterial = creatureMaterial1;
        this.isPeacefulCreature = isPeacefulCreature1;
        this.isAnimal = isAnimal1;
        this.cachedCreatureMaterial = creatureMaterial;
        this.cachedIsPeacefulCreature = isPeacefulCreature;
        this.cachedIsAnimal = isAnimal;
    }
    @Overwrite
    public Class<?> getCreatureClass() {
        return this.cachedCreatureClass;
    }
    @Overwrite
    public Material getCreatureMaterial() {
        return this.cachedCreatureMaterial;
    }
    @Overwrite
    public boolean getPeacefulCreature() {
        return this.cachedIsPeacefulCreature;
    }
    @Overwrite
    public boolean getAnimal() {
        return this.cachedIsAnimal;
    }
}
