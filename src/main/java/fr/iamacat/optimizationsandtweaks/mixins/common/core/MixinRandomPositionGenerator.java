package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(RandomPositionGenerator.class)
public class MixinRandomPositionGenerator {
    @Shadow
    private static Vec3 staticVector = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);


    /**
     * finds a random target within par1(x,z) and par2 (y) blocks
     */
    @Overwrite
    public static Vec3 findRandomTarget(EntityCreature p_75463_0_, int p_75463_1_, int p_75463_2_)
    {
        return findRandomTargetBlock(p_75463_0_, p_75463_1_, p_75463_2_, (Vec3)null);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the direction of the point par3
     */
    @Overwrite

    public static Vec3 findRandomTargetBlockTowards(EntityCreature p_75464_0_, int p_75464_1_, int p_75464_2_, Vec3 p_75464_3_)
    {
        staticVector.xCoord = p_75464_3_.xCoord - p_75464_0_.posX;
        staticVector.yCoord = p_75464_3_.yCoord - p_75464_0_.posY;
        staticVector.zCoord = p_75464_3_.zCoord - p_75464_0_.posZ;
        return findRandomTargetBlock(p_75464_0_, p_75464_1_, p_75464_2_, staticVector);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the reverse direction of the point par3
     */
    @Overwrite
    public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature p_75461_0_, int p_75461_1_, int p_75461_2_, Vec3 p_75461_3_)
    {
        staticVector.xCoord = p_75461_0_.posX - p_75461_3_.xCoord;
        staticVector.yCoord = p_75461_0_.posY - p_75461_3_.yCoord;
        staticVector.zCoord = p_75461_0_.posZ - p_75461_3_.zCoord;
        return findRandomTargetBlock(p_75461_0_, p_75461_1_, p_75461_2_, staticVector);
    }

    /**
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
     * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
     */
    @Overwrite
    private static Vec3 findRandomTargetBlock(EntityCreature entity, int maxHorizontalDistance, int maxVerticalDistance, Vec3 preferredDirection) {
        Random random = entity.getRNG();
        boolean hasHome = entity.hasHome();
        int posX = MathHelper.floor_double(entity.posX);
        int posY = MathHelper.floor_double(entity.posY);
        int posZ = MathHelper.floor_double(entity.posZ);

        double maxDistanceSquared = hasHome
            ? entity.getHomePosition().getDistanceSquared(posX, posY, posZ) + 4.0F
            : Double.MAX_VALUE;

        double maxDistance = Math.min(maxDistanceSquared, entity.func_110174_bM() + maxHorizontalDistance);

        int bestX = 0;
        int bestY = 0;
        int bestZ = 0;
        float bestWeight = -99999.0F;

        for (int i = 0; i < 10; ++i) {
            int randomX = random.nextInt(2 * maxHorizontalDistance) - maxHorizontalDistance;
            int randomY = random.nextInt(2 * maxVerticalDistance) - maxVerticalDistance;
            int randomZ = random.nextInt(2 * maxHorizontalDistance) - maxHorizontalDistance;

            if (preferredDirection == null || randomX * preferredDirection.xCoord + randomZ * preferredDirection.zCoord >= 0.0D) {
                int targetX = randomX + posX;
                int targetY = randomY + posY;
                int targetZ = randomZ + posZ;

                if (!hasHome || entity.isWithinHomeDistance(targetX, targetY, targetZ)) {
                    float weight = entity.getBlockPathWeight(targetX, targetY, targetZ);

                    if (weight > bestWeight) {
                        bestWeight = weight;
                        bestX = targetX;
                        bestY = targetY;
                        bestZ = targetZ;
                    }
                }
            }
        }

        return bestWeight > -99999.0F ? Vec3.createVectorHelper(bestX, bestY, bestZ) : null;
    }
}
