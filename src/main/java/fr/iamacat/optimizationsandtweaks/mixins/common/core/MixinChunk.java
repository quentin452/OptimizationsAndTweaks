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
    public void getEntitiesWithinAABBForEntity(Entity p_76588_1_, AxisAlignedBB p_76588_2_, List p_76588_3_, IEntitySelector p_76588_4_) {
        double minY = (p_76588_2_.minY - World.MAX_ENTITY_RADIUS) / 16.0D;
        double maxY = (p_76588_2_.maxY + World.MAX_ENTITY_RADIUS) / 16.0D;

        int minChunkY = Math.max(0, (int) Math.floor(minY));
        int maxChunkY = Math.min(this.entityLists.length - 1, (int) Math.floor(maxY));

        for (int chunkY = minChunkY; chunkY <= maxChunkY; ++chunkY) {
            List<Entity> entityList = this.entityLists[chunkY];
            optimizationsAndTweaks$processEntitiesWithinAABBForEntity(p_76588_1_, p_76588_2_, p_76588_3_, p_76588_4_, entityList);
        }
    }

    @Unique
    private void optimizationsAndTweaks$processEntitiesWithinAABBForEntity(Entity entity, AxisAlignedBB boundingBox, List<Entity> result, IEntitySelector entitySelector, List<Entity> entityList) {
        for (Entity entityInList : entityList) {
            if (optimizationsAndTweaks$shouldAddEntityToList(entity, entityInList, boundingBox, entitySelector)) {
                result.add(entityInList);
            }
        }
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

        int minChunkY = (int) Math.floor(Math.max(0, minY));
        int maxChunkY = (int) Math.floor(Math.min(this.entityLists.length - 1, maxY));

        for (int chunkY = minChunkY; chunkY <= maxChunkY; ++chunkY) {
            List<Entity> entityList = this.entityLists[chunkY];
            optimizationsAndTweaks$processEntityList(p_76618_1, p_76618_2, p_76618_3, p_76618_4, entityList);
        }
    }


    @Unique
    private void optimizationsAndTweaks$processEntityList(Class<?> entityClass, AxisAlignedBB boundingBox, List<Entity> result, IEntitySelector entitySelector, List<Entity> entityList) {
        for (Entity entityInList : entityList) {
            if (optimizationsAndTweaks$isEntityOfTypeAndIntersects(entityClass, entityInList, boundingBox, entitySelector)) {
                result.add(entityInList);
            }
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$isEntityOfTypeAndIntersects(Class p_76618_1, Entity entity, AxisAlignedBB boundingBox, IEntitySelector entitySelector) {
        return p_76618_1.isAssignableFrom(entity.getClass()) && entity.boundingBox.intersectsWith(boundingBox)
            && (entitySelector == null || entitySelector.isEntityApplicable(entity));
    }
}
