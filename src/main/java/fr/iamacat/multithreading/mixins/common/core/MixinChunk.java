package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Chunk.class)
public class MixinChunk {
    @Shadow
    public List[] entityLists;
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void getEntitiesWithinAABBForEntity(Entity p_76588_1_, AxisAlignedBB p_76588_2_, List<Entity> p_76588_3_, IEntitySelector p_76588_4_)
    {
        if (MultithreadingandtweaksConfig.enableMixinChunk){
            int i = MathHelper.floor_double((p_76588_2_.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
            int j = MathHelper.floor_double((p_76588_2_.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
            i = MathHelper.clamp_int(i, 0, this.entityLists.length - 1);
            j = MathHelper.clamp_int(j, 0, this.entityLists.length - 1);

            for (int k = i; k <= j; ++k)
            {
                List list1 = this.entityLists[k];

                for (Object o : list1) {
                    Entity entity1 = (Entity) o;

                    if (entity1 != p_76588_1_ && entity1.boundingBox.intersectsWith(p_76588_2_) && (p_76588_4_ == null || p_76588_4_.isEntityApplicable(entity1))) {
                        p_76588_3_.add(entity1);
                    }
                }
            }
        }
    }
}
