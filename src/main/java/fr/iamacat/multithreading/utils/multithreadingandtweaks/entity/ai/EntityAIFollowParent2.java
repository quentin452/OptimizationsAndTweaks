package fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.ai;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EntityAIFollowParent2 extends EntityAIBase {
    EntityAnimal childAnimal;
    EntityAnimal parentAnimal;
    double field_75347_c;
    private int field_75345_d;
    private final ExecutorService executorService;
    private Future<?> followTask;

    public EntityAIFollowParent2(EntityAnimal p_i1626_1_, double p_i1626_2_) {
        this.childAnimal = p_i1626_1_;
        this.field_75347_c = p_i1626_2_;
        this.executorService = Executors.newFixedThreadPool(MultithreadingandtweaksConfig.numberofcpus);
    }

    public boolean shouldExecute() {
        if (!this.childAnimal.isChild()) {
            return false;
        } else {
            List<EntityAnimal> list = getCachedEntitiesWithinAABB();
            EntityAnimal entityAnimal = null;
            double minDistance = Double.MAX_VALUE;

            for (EntityAnimal entityAnimal1 : list) {
                if (!entityAnimal1.isChild()) {
                    double dx = entityAnimal1.posX - childAnimal.posX;
                    double dy = entityAnimal1.posY - childAnimal.posY;
                    double dz = entityAnimal1.posZ - childAnimal.posZ;
                    double distance = dx * dx + dy * dy + dz * dz;

                    if (distance <= minDistance) {
                        minDistance = distance;
                        entityAnimal = entityAnimal1;
                    }
                }
            }

            if (entityAnimal == null) {
                return false;
            } else if (minDistance < 9.0D) {
                return false;
            } else {
                this.parentAnimal = entityAnimal;
                return true;
            }
        }
    }

    private List cachedEntitiesWithinAABB = null;

    private List getCachedEntitiesWithinAABB() {
        if (cachedEntitiesWithinAABB == null) {
            cachedEntitiesWithinAABB = this.childAnimal.worldObj.getEntitiesWithinAABB(
                this.childAnimal.getClass(), this.childAnimal.boundingBox.expand(8.0D, 4.0D, 8.0D)
            );
        }
        return cachedEntitiesWithinAABB;
    }

    public boolean continueExecuting() {
        if (!this.parentAnimal.isEntityAlive()) {
            return false;
        } else {
            double d0 = this.childAnimal.getDistanceSqToEntity(this.parentAnimal);
            return d0 >= 9.0D && d0 <= 256.0D;
        }
    }

    public void startExecuting() {
        this.field_75345_d = 0;
        this.followTask = executorService.submit(() -> {
            while (continueExecuting()) {
                if (--this.field_75345_d <= 0) {
                    this.field_75345_d = 10;
                    this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.field_75347_c);
                }
            }
        });
    }

    public void resetTask() {
        this.parentAnimal = null;
        if (followTask != null) {
            followTask.cancel(true);
        }
    }
}
