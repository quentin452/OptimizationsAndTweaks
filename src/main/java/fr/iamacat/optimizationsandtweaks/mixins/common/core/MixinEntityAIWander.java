package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityAIWander.class)
public class MixinEntityAIWander extends EntityAIBase {

    @Shadow
    private EntityCreature entity;
    @Shadow
    private double xPosition;
    @Shadow
    private double yPosition;
    @Shadow
    private double zPosition;
    @Shadow
    private double speed;

    @Unique
    private PathEntity optimizationsAndTweaks$cachedPath;

    public MixinEntityAIWander(EntityCreature p_i1648_1_, double p_i1648_2_) {
        this.entity = p_i1648_1_;
        this.speed = p_i1648_2_;
        this.setMutexBits(1);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean shouldExecute() {
        if (this.entity.getAge() >= 100) {
            return false;
        } else if (this.entity.getRNG()
            .nextInt(120) != 0) {
                return false;
            } else {
                if (optimizationsAndTweaks$cachedPath != null) {
                    return true;
                }

                Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);

                if (vec3 == null) {
                    return false;
                } else {
                    this.xPosition = vec3.xCoord;
                    this.yPosition = vec3.yCoord;
                    this.zPosition = vec3.zCoord;
                    return true;
                }
            }
    }

    public boolean continueExecuting() {
        return !this.entity.getNavigator()
            .noPath();
    }

    public void startExecuting() {
        if (optimizationsAndTweaks$cachedPath != null) {
            this.entity.getNavigator()
                .setPath(optimizationsAndTweaks$cachedPath, this.speed);
        } else {
            PathEntity path = this.entity.getNavigator()
                .getPathToXYZ(this.xPosition, this.yPosition, this.zPosition);

            if (path != null) {
                optimizationsAndTweaks$cachedPath = path;
                this.entity.getNavigator()
                    .setPath(path, this.speed);
            }
        }
    }
}
