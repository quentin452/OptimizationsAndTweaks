package fr.iamacat.optimizationsandtweaks.mixins.common.core.pathfinding;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PathNavigate.class)
public class MixinPathNavigate {

    // todo WIP
    @Shadow
    private EntityLiving theEntity;
    @Shadow
    private World worldObj;
    /** The PathEntity being followed. */
    @Shadow
    private PathEntity currentPath;
    @Shadow
    private double speed;
    /** The number of blocks (extra) +/- in each axis that get pulled out as cache for the pathfinder's search space */
    @Shadow
    private IAttributeInstance pathSearchRange;
    @Shadow
    private boolean noSunPathfind;
    /** Time, in number of ticks, following the current path */
    @Shadow
    private int totalTicks;
    /** The time when the last position check was done (to detect successful movement) */
    @Shadow
    private int ticksAtLastPos;
    /** Coordinates of the entity's position last time a check was done (part of monitoring getting 'stuck') */
    @Shadow
    private Vec3 lastPosCheck = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
    /** Specifically, if a wooden door block is even considered to be passable by the pathfinder */
    @Shadow
    private boolean canPassOpenWoodenDoors = true;
    /** If door blocks are considered passable even when closed */
    @Shadow
    private boolean canPassClosedWoodenDoors;
    /** If water blocks are avoided (at least by the pathfinder) */
    @Shadow
    private boolean avoidsWater;
    /**
     * If the entity can swim. Swimming AI enables this and the pathfinder will also cause the entity to swim straight
     * upwards when underwater
     */
    @Shadow
    private boolean canSwim;

    public MixinPathNavigate(EntityLiving p_i1671_1_, World p_i1671_2_) {
        this.theEntity = p_i1671_1_;
        this.worldObj = p_i1671_2_;
        this.pathSearchRange = p_i1671_1_.getEntityAttribute(SharedMonsterAttributes.followRange);
    }

    public void setAvoidsWater(boolean p_75491_1_) {
        this.avoidsWater = p_75491_1_;
    }

    public boolean getAvoidsWater() {
        return this.avoidsWater;
    }

    public void setBreakDoors(boolean p_75498_1_) {
        this.canPassClosedWoodenDoors = p_75498_1_;
    }

    /**
     * Sets if the entity can enter open doors
     */
    public void setEnterDoors(boolean p_75490_1_) {
        this.canPassOpenWoodenDoors = p_75490_1_;
    }

    /**
     * Returns true if the entity can break doors, false otherwise
     */
    public boolean getCanBreakDoors() {
        return this.canPassClosedWoodenDoors;
    }

    /**
     * Sets if the path should avoid sunlight
     */
    public void setAvoidSun(boolean p_75504_1_) {
        this.noSunPathfind = p_75504_1_;
    }

    /**
     * Sets the speed
     */
    @Overwrite
    public void setSpeed(double p_75489_1_) {
        this.speed = p_75489_1_;
    }

    /**
     * Sets if the entity can swim
     */
    public void setCanSwim(boolean p_75495_1_) {
        this.canSwim = p_75495_1_;
    }

    /**
     * Gets the maximum distance that the path finding will search in.
     */
    @Overwrite
    public float getPathSearchRange() {
        return (float) this.pathSearchRange.getAttributeValue();
    }

    /**
     * Returns the path to the given coordinates
     */
    @Overwrite
    public PathEntity getPathToXYZ(double p_75488_1_, double p_75488_3_, double p_75488_5_) {
        return !this.canNavigate() ? null
            : this.worldObj.getEntityPathToXYZ(
                this.theEntity,
                MathHelper.floor_double(p_75488_1_),
                (int) p_75488_3_,
                MathHelper.floor_double(p_75488_5_),
                this.getPathSearchRange(),
                this.canPassOpenWoodenDoors,
                this.canPassClosedWoodenDoors,
                this.avoidsWater,
                this.canSwim);
    }

    /**
     * Try to find and set a path to XYZ. Returns true if successful.
     */
    public boolean tryMoveToXYZ(double p_75492_1_, double p_75492_3_, double p_75492_5_, double p_75492_7_) {
        PathEntity pathentity = this.getPathToXYZ(
            (double) MathHelper.floor_double(p_75492_1_),
            (double) ((int) p_75492_3_),
            (double) MathHelper.floor_double(p_75492_5_));
        return this.setPath(pathentity, p_75492_7_);
    }

    /**
     * Returns the path to the given EntityLiving
     */
    @Overwrite
    public PathEntity getPathToEntityLiving(Entity p_75494_1_) {
        return !this.canNavigate() ? null
            : this.worldObj.getPathEntityToEntity(
                this.theEntity,
                p_75494_1_,
                this.getPathSearchRange(),
                this.canPassOpenWoodenDoors,
                this.canPassClosedWoodenDoors,
                this.avoidsWater,
                this.canSwim);
    }

    /**
     * Try to find and set a path to EntityLiving. Returns true if successful.
     */
    @Overwrite
    public boolean tryMoveToEntityLiving(Entity p_75497_1_, double p_75497_2_) {
        PathEntity pathentity = this.getPathToEntityLiving(p_75497_1_);
        return pathentity != null ? this.setPath(pathentity, p_75497_2_) : false;
    }

    /**
     * sets the active path data if path is 100% unique compared to old path, checks to adjust path for sun avoiding
     * ents and stores end coords
     */
    @Unique
    private Map<PathEntity, Boolean> multithreadingandtweaks$pathCache = new HashMap<>();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean setPath(PathEntity newPath, double speed) {
        if (newPath == null) {
            this.currentPath = null;
            return false;
        }

        // Check if the path is already in the cache
        if (multithreadingandtweaks$pathCache.containsKey(newPath)) {
            return multithreadingandtweaks$pathCache.get(newPath);
        }

        // Check only if the start/end of the path has changed
        if (this.currentPath == null || !this.currentPath.isSamePath(newPath)) {
            this.currentPath = newPath;
            multithreadingandtweaks$pathCache.put(newPath, true); // Cache the result
        }

        // Calculate position data only once
        Vec3 entityPosition = this.getEntityPosition();
        this.speed = speed;
        this.ticksAtLastPos = this.totalTicks;
        this.lastPosCheck.xCoord = entityPosition.xCoord;
        this.lastPosCheck.yCoord = entityPosition.yCoord;
        this.lastPosCheck.zCoord = entityPosition.zCoord;

        return true;
    }

    /**
     * gets the actively used PathEntity
     */
    @Overwrite
    public PathEntity getPath() {
        if (multithreadingandtweaks$pathCache.containsKey(currentPath)) {
            return currentPath;
        } else {
            return null;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onUpdateNavigation() {
        ++this.totalTicks;

        if (!this.noPath()) {
            if (this.canNavigate()) {
                this.pathFollow();
            }

            if (!this.noPath()) {
                Vec3 vec3 = this.currentPath.getPosition(this.theEntity);

                if (vec3 != null) {
                    this.theEntity.getMoveHelper().setMoveTo(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.speed);
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void pathFollow() {
        Vec3 vec3 = this.getEntityPosition();
        int i = this.currentPath.getCurrentPathLength();

        for (int j = this.currentPath.getCurrentPathIndex(); j < i; ++j) {
            if (this.currentPath.getPathPointFromIndex(j).yCoord != (int)vec3.yCoord) {
                i = j;
                break;
            }
        }

        float entityWidthSquared = this.theEntity.width * this.theEntity.width;
        int ceilingWidth = MathHelper.ceiling_float_int(this.theEntity.width);
        int entityHeightPlusOne = (int)this.theEntity.height + 1;

        for (int k = this.currentPath.getCurrentPathIndex(); k < i; ++k) {
            Vec3 pathVector = this.currentPath.getVectorFromIndex(this.theEntity, k);

            if (pathVector != null && vec3.squareDistanceTo(pathVector) < entityWidthSquared) {
                this.currentPath.setCurrentPathIndex(k + 1);
            }
        }

        for (int k = i - 1; k >= this.currentPath.getCurrentPathIndex(); --k) {
            Vec3 pathVector = this.currentPath.getVectorFromIndex(this.theEntity, k);

            if (pathVector != null && isDirectPathBetweenPoints(vec3, pathVector, ceilingWidth, entityHeightPlusOne, ceilingWidth)) {
                this.currentPath.setCurrentPathIndex(k);
                break;
            }
        }

        if (this.totalTicks - this.ticksAtLastPos > 100) {
            if (vec3.squareDistanceTo(this.lastPosCheck) < 2.25D) {
                // this.clearPathEntity(); null crash when enabled
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = vec3;
        }
    }

    /**
     * If null path or reached the end
     */
    @Overwrite
    public boolean noPath() {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Vec3 getEntityPosition() {
        return Vec3.createVectorHelper(this.theEntity.posX, this.getPathableYPos(), this.theEntity.posZ);
    }

    /**
     * Gets the safe pathing Y position for the entity depending on if it can path swim or not
     */
    @Overwrite
    private int getPathableYPos() {
        if (this.theEntity.isInWater() && this.canSwim) {
            int i = (int) this.theEntity.boundingBox.minY;
            Block block = this.worldObj.getBlock(
                MathHelper.floor_double(this.theEntity.posX),
                i,
                MathHelper.floor_double(this.theEntity.posZ));
            int j = 0;

            do {
                if (block != Blocks.flowing_water && block != Blocks.water) {
                    return i;
                }

                ++i;
                block = this.worldObj.getBlock(
                    MathHelper.floor_double(this.theEntity.posX),
                    i,
                    MathHelper.floor_double(this.theEntity.posZ));
                ++j;
            } while (j <= 16);

            return (int) this.theEntity.boundingBox.minY;
        } else {
            return (int) (this.theEntity.boundingBox.minY + 0.5D);
        }
    }

    /**
     * If on ground or swimming and can swim
     */
    @Overwrite
    public boolean canNavigate() {
        return this.theEntity.onGround || this.canSwim && this.isInLiquid()
            || this.theEntity.isRiding() && this.theEntity instanceof EntityZombie
                && this.theEntity.ridingEntity instanceof EntityChicken;
    }

    /**
     * Returns true if the entity is in water or lava, false otherwise
     */
    @Overwrite
    public boolean isInLiquid() {
        return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    private void removeSunnyPath() {
        if (!this.worldObj.canBlockSeeTheSky(
            MathHelper.floor_double(this.theEntity.posX),
            (int) (this.theEntity.boundingBox.minY + 0.5D),
            MathHelper.floor_double(this.theEntity.posZ))) {
            for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
                PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);

                if (this.worldObj.canBlockSeeTheSky(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord)) {
                    this.currentPath.setCurrentPathLength(i - 1);
                    return;
                }
            }
        }
    }

    /**
     * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
     * pos1, pos2, entityXSize, entityYSize, entityZSize
     */
    @Overwrite
    public boolean isDirectPathBetweenPoints(Vec3 p_75493_1, Vec3 p_75493_2, int p_75493_3, int p_75493_4, int p_75493_5) {
        int l = MathHelper.floor_double(p_75493_1.xCoord);
        int i1 = MathHelper.floor_double(p_75493_1.zCoord);

        double d0 = p_75493_2.xCoord - p_75493_1.xCoord;
        double d1 = p_75493_2.zCoord - p_75493_1.zCoord;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D) {
            return false;
        }

        double d3 = 1.0D / Math.sqrt(d2);
        d0 *= d3;
        d1 *= d3;

        p_75493_3 += 2;
        p_75493_5 += 2;

        if (!this.isSafeToStandAt(l, (int) p_75493_1.yCoord, i1, p_75493_3, p_75493_4, p_75493_5, p_75493_1, d0, d1)) {
            return false;
        }

        p_75493_3 -= 2;
        p_75493_5 -= 2;

        double d4 = 1.0D / Math.abs(d0);
        double d5 = 1.0D / Math.abs(d1);

        double d6 = (l) - p_75493_1.xCoord;
        double d7 = (i1) - p_75493_1.zCoord;

        if (d0 >= 0.0D) {
            ++d6;
        }

        if (d1 >= 0.0D) {
            ++d7;
        }

        d6 /= d0;
        d7 /= d1;

        int j1 = d0 < 0.0D ? -1 : 1;
        int k1 = d1 < 0.0D ? -1 : 1;

        int l1 = MathHelper.floor_double(p_75493_2.xCoord);
        int i2 = MathHelper.floor_double(p_75493_2.zCoord);

        int j2 = l1 - l;
        int k2 = i2 - i1;

        do {
            if (j2 * j1 <= 0 && k2 * k1 <= 0) {
                return true;
            }

            if (d6 < d7) {
                d6 += d4;
                l += j1;
                j2 = l1 - l;
            } else {
                d7 += d5;
                i1 += k1;
                k2 = i2 - i1;
            }
        } while (this.isSafeToStandAt(l, (int) p_75493_1.yCoord, i1, p_75493_3, p_75493_4, p_75493_5, p_75493_1, d0, d1));

        return false;
    }

    /**
     * Returns true when an entity could stand at a position, including solid blocks under the entire entity. Args:
     * xOffset, yOffset, zOffset, entityXSize, entityYSize, entityZSize, originPosition, vecX, vecZ
     */
    @Overwrite
    public boolean isSafeToStandAt(int p_75483_1, int p_75483_2, int p_75483_3, int p_75483_4, int p_75483_5, int p_75483_6, Vec3 p_75483_7, double p_75483_8, double p_75483_10) {
        int k1 = p_75483_1 - p_75483_4 / 2;
        int l1 = p_75483_3 - p_75483_6 / 2;

        if (!this.isPositionClear(k1, p_75483_2, l1, p_75483_4, p_75483_5, p_75483_6, p_75483_7, p_75483_8, p_75483_10)) {
            return false;
        }

        double halfWidth = p_75483_8 * 0.5;
        double halfHeight = p_75483_10 * 0.5;

        double centerX = p_75483_7.xCoord + halfWidth;
        double centerZ = p_75483_7.zCoord + halfHeight;

        for (int i2 = k1; i2 < k1 + p_75483_4; ++i2) {
            double d2 = (double) i2 + 0.5D - centerX;
            double xProjection = d2 * p_75483_8;

            for (int j2 = l1; j2 < l1 + p_75483_6; ++j2) {
                double d3 = (double) j2 + 0.5D - centerZ;
                double zProjection = d3 * p_75483_10;

                double projection = xProjection + zProjection;

                if (projection >= 0.0D) {
                    Block block = this.worldObj.getBlock(i2, p_75483_2 - 1, j2);
                    Material material = block.getMaterial();

                    if (material == Material.air) {
                        return false;
                    }

                    if (material == Material.water && !this.theEntity.isInWater()) {
                        return false;
                    }

                    if (material == Material.lava) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    /**
     * Returns true if an entity does not collide with any solid blocks at the position. Args: xOffset, yOffset,
     * zOffset, entityXSize, entityYSize, entityZSize, originPosition, vecX, vecZ
     */
    @Overwrite
    public boolean isPositionClear(int p_75496_1, int p_75496_2, int p_75496_3, int p_75496_4, int p_75496_5, int p_75496_6, Vec3 p_75496_7, double p_75496_8, double p_75496_10) {
        double centerX = p_75496_7.xCoord + 0.5;
        double centerZ = p_75496_7.zCoord + 0.5;

        double xProjection = p_75496_8 * 0.5;
        double zProjection = p_75496_10 * 0.5;

        for (int k1 = p_75496_1; k1 < p_75496_1 + p_75496_4; ++k1) {
            double d2 = (double) k1 + 0.5 - centerX;

            for (int l1 = p_75496_2; l1 < p_75496_2 + p_75496_5; ++l1) {
                double d3 = (double) l1 + 0.5 - centerZ;

                double projection = d2 * xProjection + d3 * zProjection;

                if (projection >= 0.0D) {
                    Block block = this.worldObj.getBlock(k1, l1, p_75496_3);

                    if (!block.getBlocksMovement(this.worldObj, k1, l1, p_75496_3)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
