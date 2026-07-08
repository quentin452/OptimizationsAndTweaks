package fr.iamacat.optimizationsandtweaks.mixins.common.diseasecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import mc.Mitchellbrine.diseaseCraft.modules.med.util.MedUtils;

/**
 * {@code medTimedown} was a byte-for-byte behavioral copy of DiseaseCraft's original method (same
 * world-isRemote guard, same per-disease loop, same getInteger/setInteger/attackEntityFrom calls,
 * identical StringBuilder-driven key concatenation in both versions) -- no actual delta found against
 * decompiled DiseaseCraft 223623, despite the "fix lag caused by StringBuilder" doc. Deleted as a dead
 * dupe; nothing left in this mixin actually overrides/injects behavior (dead shadows only).
 *
 * @author OptimizationsAndTweaks
 * @reason dead whole-method @Overwrite dupe, no behavioral delta vs original DiseaseCraft MedUtils
 */
@Mixin(MedUtils.class)
public class MixinMedUtils {

    @Shadow
    public static DamageSource medication = (new DamageSource("medication")).setDamageBypassesArmor();

    @Shadow
    public static boolean areMedsActive(EntityLivingBase entity, String id) {
        return entity.getEntityData()
            .hasKey("block" + id)
            && entity.getEntityData()
                .getInteger("block" + id) > 0;
    }
}
