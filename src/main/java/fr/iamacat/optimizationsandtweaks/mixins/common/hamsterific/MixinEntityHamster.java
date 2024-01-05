package fr.iamacat.optimizationsandtweaks.mixins.common.hamsterific;

import es.razzleberri.hamsterrific.EntityHamster;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityHamster.class)
public abstract class MixinEntityHamster  extends EntityTameable {
    protected MixinEntityHamster(World p_i1604_1_) {
        super(p_i1604_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void setInBall(boolean b) {
        this.dataWatcher.updateObject(20,(b ? 1 : 0));
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean isInBall() {
        return this.dataWatcher.getWatchableObjectInt(20) == 1;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int getBallColor() {
        return this.dataWatcher.getWatchableObjectInt(21);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void setBallColor(int b) {
        this.dataWatcher.updateObject(21, b);
    }
}
