package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Chunk.class)
public class MixinChunk {
    @Shadow
    public boolean isTerrainPopulated;
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
        int topFilledSegment = -1;

        while (low <= high) {
            int mid = (low + high) >>> 1;

            if (this.storageArrays[mid] != null && this.storageArrays[mid].getYLocation() != -1) {
                topFilledSegment = this.storageArrays[mid].getYLocation();
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return topFilledSegment;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void getEntitiesWithinAABBForEntity(Entity entity, AxisAlignedBB aabb, List<Entity> listToFill,
        IEntitySelector entitySelector) {
        int minY = MathHelper.floor_double((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
        int maxY = MathHelper.floor_double((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
        minY = MathHelper.clamp_int(minY, 0, this.entityLists.length - 1);
        maxY = MathHelper.clamp_int(maxY, 0, this.entityLists.length - 1);

        for (int y = minY; y <= maxY; ++y) {
            for (Object entityObj : this.entityLists[y]) {
                Entity targetEntity = (Entity) entityObj;

                if (optimizationsAndTweaks$isTargetEntityValid(entity, targetEntity, aabb, entitySelector)) {
                    listToFill.add(targetEntity);
                    optimizationsAndTweaks$addPartsIfValid(entity, aabb, entitySelector, listToFill, targetEntity);
                }
            }
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$isTargetEntityValid(Entity sourceEntity, Entity targetEntity,
        AxisAlignedBB aabb, IEntitySelector entitySelector) {
        return targetEntity != sourceEntity && targetEntity.boundingBox.intersectsWith(aabb)
            && (entitySelector == null || entitySelector.isEntityApplicable(targetEntity));
    }

    @Unique
    private void optimizationsAndTweaks$addPartsIfValid(Entity sourceEntity, AxisAlignedBB aabb,
        IEntitySelector entitySelector, List<Entity> listToFill, Entity targetEntity) {
        Entity[] parts = targetEntity.getParts();
        if (parts != null) {
            for (Entity partEntity : parts) {
                if (optimizationsAndTweaks$isTargetEntityValid(sourceEntity, partEntity, aabb, entitySelector)) {
                    listToFill.add(partEntity);
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
    // Remove all EntityItem during initial chunk generation
    // to prevent lags caused by a large amount of EntityItem on mod packs.
    // todo remove all loggings/try and catch when i am sure that this method is work well
    // todo fix seem to be doesn't work
    @Unique
    private void optimizationsAndTweaks$clearEntityItems(Chunk chunk, int x, int z) {
        World world = chunk.worldObj;
        System.out.println("Starting optimizationsAndTweaks$clearEntityItems for chunk (" + x + ", " + z + ")");

        List<EntityItem> entitiesToRemove = new ArrayList<>();

        for (Object obj : world.loadedEntityList) {
            if (obj instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) obj;
                int entityX = MathHelper.floor_double(entityItem.posX);
                int entityZ = MathHelper.floor_double(entityItem.posZ);

                System.out.println("Checking EntityItem: " + entityItem + " at (" + entityX + ", " + entityZ + ")");

                if (entityX >= x * 16 && entityX < (x + 1) * 16 && entityZ >= z * 16 && entityZ < (z + 1) * 16) {
                    System.out.println("EntityItem marked for removal: " + entityItem);
                    entitiesToRemove.add(entityItem);
                }
            }
        }

        for (EntityItem entity : entitiesToRemove) {
            System.out.println("EntityItem removed: " + entity);
            world.removeEntity(entity);
        }

        System.out.println("Finished optimizationsAndTweaks$clearEntityItems for chunk (" + x + ", " + z + ")");
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void populateChunk(IChunkProvider p_76624_1_, IChunkProvider p_76624_2_, int p_76624_3_, int p_76624_4_)
    {
        Chunk chunk = p_76624_1_.provideChunk(p_76624_3_,p_76624_4_);
        if (!this.isTerrainPopulated && p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_ + 1) && p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ + 1) && p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_))
        {
            p_76624_1_.populate(p_76624_2_, p_76624_3_, p_76624_4_);
        }

        if (p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_) && !p_76624_1_.provideChunk(p_76624_3_ - 1, p_76624_4_).isTerrainPopulated && p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_ + 1) && p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ + 1) && p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_ + 1))
        {
            p_76624_1_.populate(p_76624_2_, p_76624_3_ - 1, p_76624_4_);
        }

        if (p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ - 1) && !p_76624_1_.provideChunk(p_76624_3_, p_76624_4_ - 1).isTerrainPopulated && p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_ - 1) && p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_ - 1) && p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_))
        {
            p_76624_1_.populate(p_76624_2_, p_76624_3_, p_76624_4_ - 1);
        }

        if (p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_ - 1) && !p_76624_1_.provideChunk(p_76624_3_ - 1, p_76624_4_ - 1).isTerrainPopulated && p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ - 1) && p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_))
        {
            p_76624_1_.populate(p_76624_2_, p_76624_3_ - 1, p_76624_4_ - 1);
        }
        optimizationsAndTweaks$clearEntityItems(chunk, p_76624_3_, p_76624_4_);
    }
}
