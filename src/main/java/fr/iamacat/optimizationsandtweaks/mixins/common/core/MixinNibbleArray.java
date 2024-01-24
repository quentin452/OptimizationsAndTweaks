package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.world.chunk.NibbleArray;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NibbleArray.class)
public class MixinNibbleArray {

    @Shadow
    public final byte[] data;

    @Shadow
    private final int depthBits;

    @Shadow
    private final int depthBitsPlusFour;

    public MixinNibbleArray(int p_i1992_1_, int p_i1992_2_) {
        this.data = new byte[p_i1992_1_ >> 1];
        this.depthBits = p_i1992_2_;
        this.depthBitsPlusFour = p_i1992_2_ + 4;
    }

    public MixinNibbleArray(byte[] p_i1993_1_, int p_i1993_2_) {
        this.data = p_i1993_1_;
        this.depthBits = p_i1993_2_;
        this.depthBitsPlusFour = p_i1993_2_ + 4;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int get(int p_76582_1_, int p_76582_2_, int p_76582_3) {
        int index = (p_76582_2_ << this.depthBitsPlusFour) | (p_76582_3 << this.depthBits) | p_76582_1_;
        return (this.data[index >> 1] >> ((index & 1) << 2)) & 0xF;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void set(int p_76581_1, int p_76581_2, int p_76581_3, int p_76581_4) {
        int index = (p_76581_2 << this.depthBitsPlusFour) | (p_76581_3 << this.depthBits) | p_76581_1;
        int shift = (index & 1) << 2;
        this.data[index >> 1] = (byte) ((this.data[index >> 1] & ~(0xF << shift)) | ((p_76581_4 & 0xF) << shift));
    }
}
