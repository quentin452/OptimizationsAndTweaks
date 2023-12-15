package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Chunk.class)
public class MixinChunk {

    @Shadow
    public List[] entityLists;

    @Shadow
    private ExtendedBlockStorage[] storageArrays;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getTopFilledSegment() {
        int low = 0;
        int high = this.storageArrays.length - 1;
        int topFilledSegment = 0;

        while (low <= high) {
            int mid = (low + high) >>> 1; // Equivalent to (low + high) / 2 but more efficient

            if (this.storageArrays[mid] != null) {
                topFilledSegment = this.storageArrays[mid].getYLocation();
                low = mid + 1; // Continue search in the upper half
            } else {
                high = mid - 1; // Continue search in the lower half
            }
        }

        return topFilledSegment;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void getEntitiesWithinAABBForEntity(Entity entity, AxisAlignedBB aabb, List listToFill,
        IEntitySelector entitySelector) {
        int minY = MathHelper.floor_double((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
        int maxY = MathHelper.floor_double((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
        minY = MathHelper.clamp_int(minY, 0, this.entityLists.length - 1);
        maxY = MathHelper.clamp_int(maxY, 0, this.entityLists.length - 1);

        for (int y = minY; y <= maxY; ++y) {
            for (Object entityObj : this.entityLists[y]) {
                Entity targetEntity = (Entity) entityObj;

                if (targetEntity != entity && targetEntity.boundingBox.intersectsWith(aabb)
                    && (entitySelector == null || entitySelector.isEntityApplicable(targetEntity))) {
                    listToFill.add(targetEntity);
                    Entity[] parts = targetEntity.getParts();

                    if (parts != null) {
                        for (Entity partEntity : parts) {
                            if (partEntity != entity && partEntity.boundingBox.intersectsWith(aabb)
                                && (entitySelector == null || entitySelector.isEntityApplicable(partEntity))) {
                                listToFill.add(partEntity);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void getEntitiesOfTypeWithinAAAB(Class entityClass, AxisAlignedBB aabb, List listToFill,
        IEntitySelector entitySelector) {
        int minY = MathHelper.floor_double((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
        int maxY = MathHelper.floor_double((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
        minY = MathHelper.clamp_int(minY, 0, this.entityLists.length - 1);
        maxY = MathHelper.clamp_int(maxY, 0, this.entityLists.length - 1);

        for (int y = minY; y <= maxY; ++y) {
            for (Object entityObj : this.entityLists[y]) {
                Entity entity = (Entity) entityObj;

                if (entityClass.isAssignableFrom(entity.getClass()) && entity.boundingBox.intersectsWith(aabb)
                    && (entitySelector == null || entitySelector.isEntityApplicable(entity))) {
                    listToFill.add(entity);
                }
            }
        }
    }
}
