package fr.iamacat.multithreading.mixins.common.core.pathfinding;

import net.minecraft.pathfinding.Path;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.pathfinding.PathPoint2;

@Mixin(Path.class)
public class MixinPath {

    // todo WIP
    /** Contains the points in this path */
    @Unique
    private PathPoint2[] pathPoints = new PathPoint2[1024];
    @Shadow
    /** The number of points in this path */
    private int count;

    /**
     * Adds a point to the path
     */
    // @Inject(method = "addPoint", at = @At("HEAD"), cancellable = true)
    public PathPoint2 addPoint(PathPoint2 point, CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinPathFinding) {
            if (point.index >= 0) {
                throw new IllegalStateException("OW KNOWS!");
            } else {
                if (this.count == this.pathPoints.length) {
                    PathPoint2[] apathpoint = new PathPoint2[this.count << 1];
                    System.arraycopy(this.pathPoints, 0, apathpoint, 0, this.count);
                    this.pathPoints = apathpoint;
                }

                this.pathPoints[this.count] = point;
                point.index = this.count;
                this.sortBack(this.count++);
                return point;
            }

        }
        ci.cancel();
        return point;
    }

    /**
     * Clears the path
     */
    public void clearPath() {
        this.count = 0;
    }

    /**
     * Returns and removes the first point in the path
     */
    public PathPoint2 dequeue() {
        PathPoint2 pathpoint = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[--this.count];
        this.pathPoints[this.count] = null;

        if (this.count > 0) {
            this.sortForward(0);
        }

        pathpoint.index = -1;
        return pathpoint;
    }

    /**
     * Changes the provided point's distance to target
     */
    public void changeDistance(PathPoint2 p_75850_1_, float p_75850_2_) {
        float f1 = p_75850_1_.distanceToTarget;
        p_75850_1_.distanceToTarget = p_75850_2_;

        if (p_75850_2_ < f1) {
            this.sortBack(p_75850_1_.index);
        } else {
            this.sortForward(p_75850_1_.index);
        }
    }

    /**
     * Sorts a point to the left
     */

    private void sortBack(int p_75847_1_) {
        PathPoint2 pathpoint = this.pathPoints[p_75847_1_];
        int j;

        for (float f = pathpoint.distanceToTarget; p_75847_1_ > 0; p_75847_1_ = j) {
            j = p_75847_1_ - 1 >> 1;
            PathPoint2 pathpoint1 = this.pathPoints[j];

            if (f >= pathpoint1.distanceToTarget) {
                break;
            }

            this.pathPoints[p_75847_1_] = pathpoint1;
            pathpoint1.index = p_75847_1_;
        }

        this.pathPoints[p_75847_1_] = pathpoint;
        pathpoint.index = p_75847_1_;
    }

    /**
     * Sorts a point to the right
     */
    private void sortForward(int p_75846_1_) {
        PathPoint2 pathpoint = this.pathPoints[p_75846_1_];
        float f = pathpoint.distanceToTarget;

        while (true) {
            int j = 1 + (p_75846_1_ << 1);
            int k = j + 1;

            if (j >= this.count) {
                break;
            }

            PathPoint2 pathpoint1 = this.pathPoints[j];
            float f1 = pathpoint1.distanceToTarget;
            PathPoint2 pathpoint2;
            float f2;

            if (k >= this.count) {
                pathpoint2 = null;
                f2 = Float.POSITIVE_INFINITY;
            } else {
                pathpoint2 = this.pathPoints[k];
                f2 = pathpoint2.distanceToTarget;
            }

            if (f1 < f2) {
                if (f1 >= f) {
                    break;
                }

                this.pathPoints[p_75846_1_] = pathpoint1;
                pathpoint1.index = p_75846_1_;
                p_75846_1_ = j;
            } else {
                if (f2 >= f) {
                    break;
                }

                this.pathPoints[p_75846_1_] = pathpoint2;
                pathpoint2.index = p_75846_1_;
                p_75846_1_ = k;
            }
        }

        this.pathPoints[p_75846_1_] = pathpoint;
        pathpoint.index = p_75846_1_;
    }

    /**
     * Returns true if this path contains no points
     */
    public boolean isPathEmpty() {
        return this.count == 0;
    }
}
