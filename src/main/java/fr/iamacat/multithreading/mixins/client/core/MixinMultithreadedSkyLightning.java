package fr.iamacat.multithreading.mixins.client.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falsepattern.lib.compat.BlockPos;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(WorldRenderer.class)
public class MixinMultithreadedSkyLightning {
    // Fixme todo
    private EnumSkyBlock skyBlockType;
    private int posX;
    private int posY;
    private int posZ;

    @Inject(method = "func_147436_a", at = @At("HEAD"), cancellable = true)
    private void preUpdateLightByType(CallbackInfo ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinMultithreadedSkyLightning) {
            ci.cancel();
        }
    }

    @Inject(method = "func_147436_a", at = @At("HEAD"))
    private void onPreUpdateLightByType(CallbackInfo ci) {
        try {
            skyBlockType = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, null, "field_147472_a");
            posX = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, null, "field_147470_b");
            posY = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, null, "field_147471_c");
            posZ = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, null, "field_147468_d");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "func_147436_a", at = @At("RETURN"))
    private void onPostUpdateLightByType(CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinMultithreadedSkyLightning) {
            // Call the batched sky lighting update method on a separate thread
            new Thread(() -> batchUpdateSkyLighting()).start();
        }
    }

    private void batchUpdateSkyLighting() {
        try {
            // Get the current world
            WorldClient world = Minecraft.getMinecraft().theWorld;

            // Create a list to hold the block positions that need sky lighting updates
            List<BlockPos> updatePositions = new ArrayList<>();

            // Iterate over all loaded chunks in the world
            IChunkProvider chunkProvider = world.getChunkProvider();
            for (int x = -chunkProvider.getLoadedChunkCount(); x < chunkProvider.getLoadedChunkCount(); x++) {
                for (int z = -chunkProvider.getLoadedChunkCount(); z < chunkProvider.getLoadedChunkCount(); z++) {
                    Chunk chunk = chunkProvider.loadChunk(x, z);
                    if (chunk != null) {
                        // Get the chunk's block storage object
                        ExtendedBlockStorage[] storageArrays = chunk.getBlockStorageArray();
                        for (int i = 0; i < storageArrays.length; i++) {
                            ExtendedBlockStorage storage = storageArrays[i];
                            if (storage != null && storage.getBlocklightArray() == null) {
                                // Iterate over all non-air blocks in the chunk section
                                for (int bx = 0; bx < 16; bx++) {
                                    for (int by = 0; by < 16; by++) {
                                        for (int bz = 0; bz < 16; bz++) {
                                            int blockX = (chunk.xPosition << 4) + bx;
                                            int blockY = (i << 4) + by;
                                            int blockZ = (chunk.zPosition << 4) + bz;
                                            Block block = world.getBlock(blockX, blockY, blockZ);
                                            if (block.getMaterial() != Material.air && block.getLightOpacity() == 0) {
                                                updatePositions.add(new BlockPos(blockX, blockY, blockZ));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Batch the update positions into groups of 1000
            List<List<BlockPos>> batches = Lists.partition(updatePositions, 1000);

            // Iterate over each chunk batch and update the sky lighting for the blocks in the corresponding position
            for (List<BlockPos> batch : batches) {
                Chunk chunk = world.getChunkFromBlockCoords(
                    batch.get(0)
                        .getX(),
                    batch.get(0)
                        .getZ());
                if (chunk != null) {
                    int[] xArr = new int[batch.size()];
                    int[] yArr = new int[batch.size()];
                    int[] zArr = new int[batch.size()];
                    for (int i = 0; i < batch.size(); i++) {
                        BlockPos pos = batch.get(i);
                        xArr[i] = pos.getX();
                        yArr[i] = pos.getY();
                        zArr[i] = pos.getZ();
                    }
                    world.theProfiler.startSection("lightChecks");
                    chunk.enqueueRelightChecks();
                    world.theProfiler.endSection();
                    world.markBlockRangeForRenderUpdate(
                        chunk.xPosition * 16,
                        0,
                        chunk.zPosition * 16,
                        (chunk.xPosition * 16) + 15,
                        world.getHeight(),
                        (chunk.zPosition * 16) + 15);
                    for (int i = 0; i < 16; i++) {
                        ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);
                        Chunk chunk1 = world.getChunkProvider()
                            .provideChunk(chunkPos.chunkXPos, chunkPos.chunkZPos);
                        int lightValue = 15; // set the light value to maximum
                        chunk1.setLightValue(
                            EnumSkyBlock.Sky,
                            batch.get(i)
                                .getX(),
                            batch.get(i)
                                .getY(),
                            batch.get(i)
                                .getZ(),
                            lightValue);
                        world.markBlockRangeForRenderUpdate(
                            chunkPos.chunkXPos * 16,
                            i * 16,
                            chunkPos.chunkZPos * 16,
                            chunkPos.chunkXPos * 16 + 15,
                            i * 16 + 15,
                            chunkPos.chunkZPos * 16 + 15);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
