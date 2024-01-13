package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(Stitcher.class)
public class MixinStitcher {
    @Shadow
    private final int mipmapLevelStitcher;
    @Shadow
    private final Set setStitchHolders = new HashSet(256);
    @Shadow
    private final List stitchSlots = new ArrayList(256);
    @Shadow
    private int currentWidth;
    @Shadow
    private int currentHeight;
    @Shadow
    private final int maxWidth;
    @Shadow
    private final int maxHeight;
    @Shadow
    private final boolean forcePowerOf2;
    /** Max size (width or height) of a single tile */
    @Shadow
    private final int maxTileDimension;
    public MixinStitcher(int p_i45095_1_, int p_i45095_2_, boolean p_i45095_3_, int p_i45095_4_, int p_i45095_5_)
    {
        this.mipmapLevelStitcher = p_i45095_5_;
        this.maxWidth = p_i45095_1_;
        this.maxHeight = p_i45095_2_;
        this.forcePowerOf2 = p_i45095_3_;
        this.maxTileDimension = p_i45095_4_;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void doStitch()
    {
        Stitcher.Holder[] aholder = (Stitcher.Holder[])this.setStitchHolders.toArray(new Stitcher.Holder[this.setStitchHolders.size()]);
        cpw.mods.fml.common.ProgressManager.ProgressBar bar = cpw.mods.fml.common.ProgressManager.push("Texture stitching", aholder.length);
        Arrays.sort(aholder);
        Stitcher.Holder[] aholder1 = aholder;
        int i = aholder.length;

        for (int j = 0; j < i; ++j)
        {
            Stitcher.Holder holder = aholder1[j];
            bar.step(holder.getAtlasSprite().getIconName());

            if (!this.allocateSlot(holder))
            {
                String s = String.format("Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution resourcepack?", holder.getAtlasSprite().getIconName(), holder.getAtlasSprite().getIconWidth(), holder.getAtlasSprite().getIconHeight());
                throw new StitcherException(holder, s);
            }
        }

        if (this.forcePowerOf2)
        {
            this.currentWidth = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            this.currentHeight = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
        }
        cpw.mods.fml.common.ProgressManager.pop(bar);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private boolean allocateSlot(Stitcher.Holder p_94310_1_)
    {
        for (Object stitchSlot : this.stitchSlots) {
            if (((Stitcher.Slot) stitchSlot).addSlot(p_94310_1_)) {
                return true;
            }

            p_94310_1_.rotate();

            if (((Stitcher.Slot) stitchSlot).addSlot(p_94310_1_)) {
                return true;
            }

            p_94310_1_.rotate();
        }

        return this.expandAndAllocateSlot(p_94310_1_);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private boolean expandAndAllocateSlot(Stitcher.Holder p_94311_1_)
    {
        int i = Math.min(p_94311_1_.getWidth(), p_94311_1_.getHeight());
        boolean flag = this.currentWidth == 0 && this.currentHeight == 0;
        boolean flag1;
        int j;

        if (this.forcePowerOf2)
        {
            j = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            int k = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
            int l = MathHelper.roundUpToPowerOfTwo(this.currentWidth + i);
            int i1 = MathHelper.roundUpToPowerOfTwo(this.currentHeight + i);
            boolean flag2 = l <= this.maxWidth;
            boolean flag3 = i1 <= this.maxHeight;

            if (!flag2 && !flag3)
            {
                return false;
            }

            boolean flag4 = j != l;
            boolean flag5 = k != i1;

            if (flag4 ^ flag5)
            {
                flag1 = !flag4;
            }
            else
            {
                flag1 = flag2 && j <= k;
            }
        }
        else
        {
            boolean flag6 = this.currentWidth + i <= this.maxWidth;
            boolean flag7 = this.currentHeight + i <= this.maxHeight;

            if (!flag6 && !flag7)
            {
                return false;
            }

            flag1 = flag6 && (flag || this.currentWidth <= this.currentHeight);
        }

        j = Math.max(p_94311_1_.getWidth(), p_94311_1_.getHeight());

        if (MathHelper.roundUpToPowerOfTwo((flag1 ? this.currentHeight : this.currentWidth) + j) > (flag1 ? this.maxHeight : this.maxWidth))
        {
            return false;
        }
        else
        {
            Stitcher.Slot slot;

            if (flag1)
            {
                if (p_94311_1_.getWidth() > p_94311_1_.getHeight())
                {
                    p_94311_1_.rotate();
                }

                if (this.currentHeight == 0)
                {
                    this.currentHeight = p_94311_1_.getHeight();
                }

                slot = new Stitcher.Slot(this.currentWidth, 0, p_94311_1_.getWidth(), this.currentHeight);
                this.currentWidth += p_94311_1_.getWidth();
            }
            else
            {
                slot = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, p_94311_1_.getHeight());
                this.currentHeight += p_94311_1_.getHeight();
            }

            slot.addSlot(p_94311_1_);
            this.stitchSlots.add(slot);
            return true;
        }
    }
}
