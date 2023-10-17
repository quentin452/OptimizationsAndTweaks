package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(World.class)
public class MixinWorld {
    @Shadow
    protected IChunkProvider chunkProvider;
    @Shadow
    public static double MAX_ENTITY_RADIUS = 2.0D;
    /**
     * @author iamacatfr
     * @reason optimize selectEntitiesWithinAABB
     */
    @Overwrite
    public List selectEntitiesWithinAABB(Class clazz, AxisAlignedBB bb, IEntitySelector selector) {
        if (MultithreadingandtweaksConfig.enableMixinWorld) {
        int minXChunk = MathHelper.floor_double((bb.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int maxXChunk = MathHelper.floor_double((bb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int minZChunk = MathHelper.floor_double((bb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int maxZChunk = MathHelper.floor_double((bb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

        List<Entity> entityList = new ArrayList<>();

        for (int chunkX = minXChunk; chunkX <= maxXChunk; ++chunkX) {
            for (int chunkZ = minZChunk; chunkZ <= maxZChunk; ++chunkZ) {
                if (this.multithreadingandtweaks$chunkExists(chunkX, chunkZ)) {
                    Chunk chunk = this.multithreadingandtweaks$getChunkFromChunkCoords(chunkX, chunkZ);
                    chunk.getEntitiesOfTypeWithinAAAB(clazz, bb, entityList, selector);
                }
            }
        }

        return entityList;
        }
        return null;
    }


    /**
     * Returns whether a chunk exists at chunk coordinates x, y
     */
    @Unique
    protected boolean multithreadingandtweaks$chunkExists(int p_72916_1_, int p_72916_2_)
    {
        return this.chunkProvider.chunkExists(p_72916_1_, p_72916_2_);
    }
    /**
     * Returns back a chunk looked up by chunk coordinates Args: x, y
     */
    @Unique
    public Chunk multithreadingandtweaks$getChunkFromChunkCoords(int p_72964_1_, int p_72964_2_)
    {
        return this.chunkProvider.provideChunk(p_72964_1_, p_72964_2_);
    }

}
