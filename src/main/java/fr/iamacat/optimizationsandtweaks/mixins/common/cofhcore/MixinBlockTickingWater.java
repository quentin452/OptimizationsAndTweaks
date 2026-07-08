package fr.iamacat.optimizationsandtweaks.mixins.common.cofhcore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cofh.asmhooks.block.BlockTickingWater;

/**
 * Fixes stackoverflow from cofhcore.
 */
@Mixin(BlockTickingWater.class)
public class MixinBlockTickingWater extends BlockDynamicLiquid {

    protected MixinBlockTickingWater(Material p_i45403_1_) {
        super(p_i45403_1_);
    }

    /**
     * @author iamacatfr
     * @reason reclassify COMPLEX (not mechanically convertible): two interleaved changes vs vanilla --
     *         (a) a stackoverflow guard wraps the ENTIRE body (including the {@code super.onBlockAdded} call)
     *         behind "current block != water", where vanilla calls super unconditionally; (b) the hellworld
     *         conversion condition gains an extra "current block != air" clause guarding the SAME setBlock +
     *         2 playAuxSFX calls. (a) alone would be a clean HEAD-cancellable guard, but (b) needs the extra
     *         condition evaluated ONCE before the first mutation (setBlock to air) and then reused for the two
     *         playAuxSFX calls after -- a per-call recheck (as plain @WrapWithCondition would do) would read
     *         "air" after the first setBlock has already run, wrongly skipping the SFX calls. Not safely
     *         expressible without a shared/captured local across 3 call sites; left as @Overwrite.
     */
    @Overwrite
    public void onBlockAdded(World var1, int var2, int var3, int var4) {
        if (var1.getBlock(var2, var3, var4) != Blocks.water) {
            super.onBlockAdded(var1, var2, var3, var4);
            if (this.blockMaterial == Material.water && (var1.provider.isHellWorld)
                && (var1.getBlock(var2, var3, var4) != Blocks.air)) {
                var1.setBlock(var2, var3, var4, Blocks.air, 0, 2);
                var1.playAuxSFX(1004, var2, var3, var4, 0);
                var1.playAuxSFX(2000, var2, var3, var4, 4);
            }
        }
    }

    @Shadow
    public boolean isAssociatedBlock(Block var1) {
        return super.isAssociatedBlock(var1) || var1 == Blocks.water;
    }
}
