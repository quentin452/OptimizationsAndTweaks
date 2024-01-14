package fr.iamacat.optimizationsandtweaks.mixins.common.cofhcore;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cofh.asmhooks.HooksCore;
import cofh.lib.util.helpers.MathHelper;

@Mixin(HooksCore.class)
public class MixinHooksCore {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static List<AxisAlignedBB> getEntityCollisionBoxes(World world, Entity entity, AxisAlignedBB boundingBox) {
        if (entity.canBePushed()) {
            return world.getCollidingBoundingBoxes(entity, boundingBox);
        }

        List<AxisAlignedBB> collidingBoxes = new ArrayList<>();
        int minX = MathHelper.floor(boundingBox.minX);
        int maxX = MathHelper.floor(boundingBox.maxX + 1.0);
        int minY = MathHelper.floor(boundingBox.minY);
        int maxY = MathHelper.floor(boundingBox.maxY + 1.0);
        int minZ = MathHelper.floor(boundingBox.minZ);
        int maxZ = MathHelper.floor(boundingBox.maxZ + 1.0);

        for (int x = minX; x < maxX; ++x) {
            boolean validX = x >= -30000000 && x < 30000000;

            for (int z = minZ; z < maxZ; ++z) {
                boolean validZ = validX && z >= -30000000 && z < 30000000;

                if (world.blockExists(x, 64, z)) {
                    int minYLoop = minY - 1;

                    for (int y = minYLoop; y < maxY; ++y) {
                        Block block = validZ ? world.getBlock(x, y, z) : Blocks.bedrock;
                        block.addCollisionBoxesToList(world, x, y, z, boundingBox, collidingBoxes, entity);
                    }
                }
            }
        }

        return collidingBoxes;
    }
}
