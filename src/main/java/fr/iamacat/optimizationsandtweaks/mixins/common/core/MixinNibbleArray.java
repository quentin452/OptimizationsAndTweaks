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
    @Shadow
    public int get(int p_76582_1_, int p_76582_2_, int p_76582_3) {
        int l = (p_76582_2_ << this.depthBitsPlusFour) | (p_76582_3 << this.depthBits) | p_76582_1_;
        int i1 = l >> 1;
        int j1 = l & 1;
        return (j1 == 0) ? (this.data[i1] & 0xF) : (this.data[i1] >> 4 & 0xF);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void set(int p_76581_1, int p_76581_2, int p_76581_3, int p_76581_4) {
        int i1 = (p_76581_2 << this.depthBitsPlusFour) | (p_76581_3 << this.depthBits) | p_76581_1;
        int j1 = i1 >> 1;
        int k1 = i1 & 1;
        byte currentData = this.data[j1];

        if (k1 == 0) {
            this.data[j1] = (byte) ((currentData & 0xF0) | (p_76581_4 & 0xF));
        } else {
            this.data[j1] = (byte) ((currentData & 0xF) | ((p_76581_4 & 0xF) << 4));
        }
    }
}
