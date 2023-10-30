package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import org.spongepowered.asm.mixin.Unique;

@Mixin(Chunk.class)
public class MixinChunk {

    @Shadow
    public List[] entityLists;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void getEntitiesWithinAABBForEntity(Entity entity, AxisAlignedBB boundingBox, List<Entity> result, IEntitySelector entitySelector) {
        double minY = (boundingBox.minY - World.MAX_ENTITY_RADIUS) / 16.0D;
        double maxY = (boundingBox.maxY + World.MAX_ENTITY_RADIUS) / 16.0D;

        int minChunkY = MathHelper.floor_double(minY);
        int maxChunkY = MathHelper.floor_double(maxY);

        minChunkY = MathHelper.clamp_int(minChunkY, 0, this.entityLists.length - 1);
        maxChunkY = MathHelper.clamp_int(maxChunkY, 0, this.entityLists.length - 1);

        for (int chunkY = minChunkY; chunkY <= maxChunkY; ++chunkY) {
            List<Entity> entityList = this.entityLists[chunkY];

            optimizationsAndTweaks$processEntitiesWithinAABBForEntity(entity, boundingBox, result, entitySelector, entityList);
        }
    }

    @Unique
    private void optimizationsAndTweaks$processEntitiesWithinAABBForEntity(Entity entity, AxisAlignedBB boundingBox, List<Entity> result, IEntitySelector entitySelector, List<Entity> entityList) {
        entityList.stream()
            .filter(entityInList -> optimizationsAndTweaks$shouldAddEntityToList(entity, entityInList, boundingBox, entitySelector))
            .forEach(result::add);
    }

    @Unique
    private boolean optimizationsAndTweaks$shouldAddEntityToList(Entity entity, Entity entityInList, AxisAlignedBB boundingBox, IEntitySelector entitySelector) {
        return entityInList != entity && entityInList.boundingBox.intersectsWith(boundingBox)
            && (entitySelector == null || entitySelector.isEntityApplicable(entityInList));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void getEntitiesOfTypeWithinAAAB(Class p_76618_1, AxisAlignedBB p_76618_2, List<Entity> p_76618_3, IEntitySelector p_76618_4) {
        double minY = (p_76618_2.minY - World.MAX_ENTITY_RADIUS) / 16.0D;
        double maxY = (p_76618_2.maxY + World.MAX_ENTITY_RADIUS) / 16.0D;

        int minChunkY = MathHelper.floor_double(minY);
        int maxChunkY = MathHelper.floor_double(maxY);

        minChunkY = MathHelper.clamp_int(minChunkY, 0, this.entityLists.length - 1);
        maxChunkY = MathHelper.clamp_int(maxChunkY, 0, this.entityLists.length - 1);

        for (int chunkY = minChunkY; chunkY <= maxChunkY; ++chunkY) {
            List<Entity> entityList = this.entityLists[chunkY];

            optimizationsAndTweaks$processEntityList(p_76618_1, p_76618_2, p_76618_3, p_76618_4, entityList);
        }
    }

    @Unique
    private void optimizationsAndTweaks$processEntityList(Class<?> entityClass, AxisAlignedBB boundingBox, List<Entity> result, IEntitySelector entitySelector, List<Entity> entityList) {
        entityList.stream()
            .filter(entityInList -> optimizationsAndTweaks$isEntityOfTypeAndIntersects(entityClass, entityInList, boundingBox, entitySelector))
            .forEach(result::add);
    }

    @Unique
    private boolean optimizationsAndTweaks$isEntityOfTypeAndIntersects(Class p_76618_1, Entity entity, AxisAlignedBB boundingBox, IEntitySelector entitySelector) {
        return p_76618_1.isAssignableFrom(entity.getClass()) && entity.boundingBox.intersectsWith(boundingBox)
            && (entitySelector == null || entitySelector.isEntityApplicable(entity));
    }
}
