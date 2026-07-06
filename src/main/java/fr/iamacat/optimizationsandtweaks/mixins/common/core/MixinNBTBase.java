package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.nbt.NBTBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NBTBase.class)
public abstract class MixinNBTBase {

    @Shadow
    public abstract byte getId();

    @Overwrite
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }

        if (!(p_equals_1_ instanceof NBTBase)) {
            return false;
        }
        NBTBase nbtbase = (NBTBase) p_equals_1_;
        return this.getId() == nbtbase.getId();
    }
}
