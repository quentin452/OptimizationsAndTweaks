package fr.iamacat.multithreading.mixins.common.core;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.common.eventhandler.Event;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(SpawnerAnimals.class)
public class MixinPatchSpawnerAnimals {

    /**
     * Returns whether or not the specified creature type can spawn at the specified location.
     */
    @Unique
    private HashMap<ChunkCoordIntPair, Boolean> multithreadingandtweaks$eligibleChunksForSpawning = new HashMap<>();
    @Unique
    private static ExecutorService multithreadingandtweaks$threadPool = Executors
        .newFixedThreadPool(MultithreadingandtweaksConfig.numberofcpus);

    /**
     * @author iamacatfr
     * @reason greatly reduce tps lags on VoidWorld and add multithreading to reduce overral lag
     */
    @Overwrite
    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType p_77190_0_, World p_77190_1_, int p_77190_2_,
        int p_77190_3_, int p_77190_4_) {
        Future<Boolean> isEmptyFuture = multithreadingandtweaks$threadPool.submit(() -> {
            Chunk chunk = p_77190_1_.getChunkFromChunkCoords(p_77190_2_ >> 4, p_77190_4_ >> 4);
            return chunk.isEmpty();
        });

        boolean isCreatureMaterialWater = p_77190_0_.getCreatureMaterial() == Material.water;
        boolean doesBlockHaveSolidTopSurface = World
            .doesBlockHaveSolidTopSurface(p_77190_1_, p_77190_2_, p_77190_3_ - 1, p_77190_4_);

        try {
            boolean isChunkEmpty = isEmptyFuture.get();

            if (isChunkEmpty) {
                return false;
            }

            if (isCreatureMaterialWater) {
                return p_77190_1_.getBlock(p_77190_2_, p_77190_3_, p_77190_4_)
                    .getMaterial()
                    .isLiquid()
                    && p_77190_1_.getBlock(p_77190_2_, p_77190_3_ - 1, p_77190_4_)
                        .getMaterial()
                        .isLiquid()
                    && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_ + 1, p_77190_4_)
                        .isNormalCube();
            } else if (!doesBlockHaveSolidTopSurface) {
                return false;
            } else {
                Block block = p_77190_1_.getBlock(p_77190_2_, p_77190_3_ - 1, p_77190_4_);
                boolean spawnBlock = block
                    .canCreatureSpawn(p_77190_0_, p_77190_1_, p_77190_2_, p_77190_3_ - 1, p_77190_4_);
                return spawnBlock && block != Blocks.bedrock
                    && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_, p_77190_4_)
                        .isNormalCube()
                    && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_, p_77190_4_)
                        .getMaterial()
                        .isLiquid()
                    && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_ + 1, p_77190_4_)
                        .isNormalCube();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
