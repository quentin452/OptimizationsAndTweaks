package fr.iamacat.optimizationsandtweaks.mixins.common.easybreeding;

import easyBreeding.EntityAIEatDroppedFood;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(EntityAIEatDroppedFood.class)
public abstract class MixinEntityAIEatDroppedFood extends EntityAIBase {
    @Shadow
    private EntityAnimal animal;
    @Unique
    double multithreadingandtweaks$searchDistance = 7.5;

    @Unique
    private final AxisAlignedBB multithreadingandtweaks$searchBoundingBox;

    public MixinEntityAIEatDroppedFood(EntityAnimal ent) {
        this.animal = ent;
        this.multithreadingandtweaks$searchBoundingBox = AxisAlignedBB.getBoundingBox(
            ent.posX - multithreadingandtweaks$searchDistance, ent.posY - multithreadingandtweaks$searchDistance, ent.posZ - multithreadingandtweaks$searchDistance,
            ent.posX + multithreadingandtweaks$searchDistance, ent.posY + multithreadingandtweaks$searchDistance, ent.posZ + multithreadingandtweaks$searchDistance
        );
    }
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
        if (enta.getNavigator().tryMoveToXYZ(enti.posX, enti.posY, enti.posZ, 1.25) && enta.getDistanceToEntity(enti) < 1.0F) {
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
