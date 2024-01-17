package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityMob.class)
public abstract class MixinEntityMob extends EntityCreature implements IMob {
    public MixinEntityMob(World p_i1602_1_) {
        super(p_i1602_1_);
    }

    /**
     * @author
     * @reason : try to fix silent cascading worldgens caused by isValidLightLevel
     * @image : see https://mega.nz/file/P0FTEC4Z#TZoy-l2XaPIDY3PwxRgp_E1CfRtBo4w2E1nuoA0Dz7U
     */
    @Overwrite
    protected boolean isValidLightLevel() {
        if (!this.worldObj.getChunkProvider().chunkExists(MathHelper.floor_double(this.posX) >> 4, MathHelper.floor_double(this.posZ) >> 4)) {
            return false;
        }
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > this.rand.nextInt(32)) {
            return false;
        } else {
            int l = this.worldObj.getBlockLightValue(i, j, k);
            if (this.worldObj.isThundering()) {
                int i1 = this.worldObj.skylightSubtracted;
                this.worldObj.skylightSubtracted = 10;
                l = this.worldObj.getBlockLightValue(i, j, k);
                this.worldObj.skylightSubtracted = i1;
            }
            return l <= this.rand.nextInt(8);
        }
    }
}
