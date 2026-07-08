package fr.iamacat.optimizationsandtweaks.mixins.common.ic2;

import org.spongepowered.asm.mixin.Mixin;

import ic2.core.util.Config;

// NOTE (2026-07-08 mixin @Overwrite->injector conversion): the former @Overwrite `split` was a byte-for-byte
// behavioral copy of vanilla Config.split (a continue-based if-chain rewritten as an if/else chain, numerically
// and logically identical -- verified against De Morgan's law on the final branch condition). Deleted, no real
// change. This EMPTIES the mixin entirely (it had no other content) -- candidate for full removal from
// asm/Mixin.java's registry (out of scope for this batch: no edits to files outside the batch).
@Mixin(Config.class)
public class MixinConfig {
}
