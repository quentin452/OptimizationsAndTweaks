package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.enchantment.Enchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mirror OaT's entity id-conflict resolution (see MixinEntityRegistry) for enchantments.
 *
 * Vanilla throws {@code IllegalArgumentException("Duplicate enchantment id!")} in the Enchantment
 * constructor when two mods claim the same slot in {@code enchantmentsList} — a hard boot crash
 * (BUG-015: WitchingGadgets WGEnchantBackstab vs ManaMetal EnchantmentMagic, both id 89). The
 * assignment is load-order dependent, so it strikes non-deterministically on fresh installs.
 *
 * Instead of crashing, move the LATER registrant to a free slot. {@code effectId} is final and is
 * set to this very parameter, so redirecting the parameter keeps {@code effectId} and the array slot
 * consistent (a lookup by effectId still finds the enchant). The first registrant keeps its id, so
 * items already enchanted with it stay valid; only the colliding enchant (which was crashing anyway)
 * moves. Non-conflicting constructions (the vast majority) return the id untouched.
 *
 * Export the resulting map with {@code /bq_export enchants} to baseline the ids and watch for drift.
 */
@Mixin(Enchantment.class)
public class MixinEnchantment {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static int optimizationsandtweaks$resolveDuplicateEnchantmentId(int id) {
        Enchantment[] list = Enchantment.enchantmentsList;
        if (id >= 0 && id < list.length && list[id] != null) {
            // Scan from the top so vanilla/low ids stay put; keep the first registrant at `id`.
            for (int free = list.length - 1; free >= 0; free--) {
                if (list[free] == null) {
                    return free;
                }
            }
        }
        return id;
    }
}
