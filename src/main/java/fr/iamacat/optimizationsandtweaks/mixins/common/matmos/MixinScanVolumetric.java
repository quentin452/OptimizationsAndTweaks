package fr.iamacat.optimizationsandtweaks.mixins.common.matmos;

import eu.ha3.matmos.data.scanners.Scan;
import eu.ha3.matmos.data.scanners.ScanVolumetric;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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

    /**
     * @reason
     */
    @Overwrite(remap = false)
    protected void initScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn) {
        int worldHeight = Minecraft.getMinecraft().theWorld.getHeight();
        this.ysize = Math.min(worldHeight, ysizeIn);
        this.ystart = Math.max(0, Math.min(worldHeight - this.ysize, y - this.ysize / 2));
        this.xsize = xsizeIn;
        this.zsize = zsizeIn;
        this.xstart = x - this.xsize / 2;
        this.zstart = z - this.zsize / 2;
        this.finalProgress = this.xsize * this.ysize * this.zsize;
        this.xx = 0;
        this.yy = 0;
        this.zz = 0;
    }

    /**
     * @reason
     */
    @Overwrite(remap = false)
    protected boolean doRoutine() {
        int ops = 0;
        while (ops < this.opspercall && this.progress < this.finalProgress) {
            this.pipeline.input(this.xstart + this.xx, this.ystart + this.yy, this.zstart + this.zz);
            this.xx = (this.xx + 1) % this.xsize;
            if (this.xx == 0) {
                this.zz = (this.zz + 1) % this.zsize;
                if (this.zz == 0) {
                    this.yy++;
                    if (this.yy >= this.ysize && this.progress != this.finalProgress - 1) {
                        System.err.println("LOGIC ERROR");
                    }
                }
            }
            ops++;
            this.progress++;
        }
        return true;
    }
}
