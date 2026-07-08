package fr.iamacat.optimizationsandtweaks.mixins.common.matmos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import eu.ha3.matmos.data.scanners.Scan;
import eu.ha3.matmos.data.scanners.ScanVolumetric;

// NOTE (2026-07-08 mixin @Overwrite->injector conversion): both former @Overwrite methods (initScan, doRoutine)
// were byte-for-byte behavioral copies of the vanilla ScanVolumetric methods (if/else chains rewritten as
// Math.min/max clamps and continue-chains rewritten as if/else, but numerically identical) -- deleted, no real
// change. This EMPTIES the mixin of all @Overwrite content; only @Shadow field declarations remain, which is now
// a no-op mixin. Candidate for full removal from asm/Mixin.java's registry (out of scope for this batch: no edits
// to files outside the batch).
@Mixin(ScanVolumetric.class)
public abstract class MixinScanVolumetric extends Scan {

    @Shadow
    private int xstart;
    @Shadow
    private int ystart;
    @Shadow
    private int zstart;
    @Shadow
    private int xsize;
    @Shadow
    private int ysize;
    @Shadow
    private int zsize;
    @Shadow
    private int xx;
    @Shadow
    private int yy;
    @Shadow
    private int zz;
}
