package fr.iamacat.optimizationsandtweaks.mixins.common.easybreeding;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import easyBreeding.EntityAIEatDroppedFood;

@Mixin(EntityAIEatDroppedFood.class)
public abstract class MixinEntityAIEatDroppedFood extends EntityAIBase {

    @Shadow
    private EntityAnimal animal;

    /**
     * @author iamcatfr
     * @reason d
     */
    @Overwrite(remap = false)
    public EntityItem whatFoodIsNear() {
        List<EntityItem> items = animal.worldObj.getEntitiesWithinAABB(EntityItem.class, animal.boundingBox);

        for (EntityItem item : items) {
            if (animal.isBreedingItem(item.getEntityItem())) {
                return item;
            }
        }
        return null;
    }

    /**
     * @author iamcatfr
     * @reason v
     */
    @Overwrite(remap = false)
    public boolean func_75250_a() {
        if (!animal.isChild() && animal.getGrowingAge() == 0) {
            EntityItem closeFood = this.whatFoodIsNear();
            if (closeFood != null && !animal.isInLove()) {
                this.execute(animal, closeFood);
                return true;
            }
        }
        return false;
    }

    /**
     * @author iamcatfr
     * @reason d
     */
    @Overwrite(remap = false)
    public boolean execute(EntityAnimal enta, EntityItem enti) {
        if (enta.getNavigator()
            .tryMoveToXYZ(enti.posX, enti.posY, enti.posZ, 1.25) && enta.getDistanceToEntity(enti) < 1.0F) {
            this.eatOne(enti);
            enta.func_146082_f(null);
        }
        return true;
    }

    /**
     * @author iamcatfr
     * @reason d
     */
    @Overwrite(remap = false)
    public void eatOne(EntityItem enti) {
        ItemStack stack = enti.getEntityItem();
        --stack.stackSize;
        if (stack.stackSize == 0) {
            enti.setDead();
        }
    }
}
