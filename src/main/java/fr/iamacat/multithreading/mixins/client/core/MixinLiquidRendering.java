package fr.iamacat.multithreading.mixins.client.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.falsepattern.lib.compat.BlockPos;

import cpw.mods.fml.client.FMLClientHandler;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(BlockLiquid.class)
public abstract class MixinLiquidRendering {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private static final ExecutorService THREAD_POOL = Executors
        .newFixedThreadPool(MultithreadingandtweaksMultithreadingConfig.numberofcpus);

    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true)
    private void onTesselate(IBlockAccess world, BlockPos pos, Tessellator tessellator, int metadata,
        CallbackInfoReturnable<Boolean> ci) {
        if (MultithreadingandtweaksMultithreadingConfig.enableMixinLiquidRendering) {
            ci.cancel();
            THREAD_POOL.submit(() -> tesselateFluid(world, pos, tessellator, metadata));
        }
    }

    private void tesselateFluid(IBlockAccess world, BlockPos pos, Tessellator tessellator, int state) {
        Queue<BlockPos> fluidBlocks = new ArrayDeque<>();
        Set<BlockPos> visitedBlocks = ConcurrentHashMap.newKeySet();
        fluidBlocks.add(pos);
        visitedBlocks.add(pos);

        while (!fluidBlocks.isEmpty()) {
            List<BlockPos> batchedPositions = new ArrayList<>();
            for (int i = 0; i < BATCH_SIZE && !fluidBlocks.isEmpty(); i++) {
                batchedPositions.add(fluidBlocks.remove());
            }
            tesselateBatch((World) world, batchedPositions, tessellator, visitedBlocks, state);
        }
    }

    private void tesselateBatch(World world, List<BlockPos> positions, Tessellator tessellator,
        Set<BlockPos> visitedBlocks, int state) {
        Block block = world.getBlock(
            positions.get(0)
                .getX(),
            positions.get(0)
                .getY(),
            positions.get(0)
                .getZ());
        List<BlockPos> fluidBlocks = new ArrayList<>();

        Minecraft minecraft = FMLClientHandler.instance()
            .getClient();
        TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();

        for (BlockPos blockPos : positions) {
            Block currentBlock = world.getBlock(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (currentBlock.getMaterial()
                .isLiquid() && currentBlock == block) {
                for (EnumFacing direction : EnumFacing.values()) {
                    BlockPos offset = blockPos.offset(direction);
                    if (!visitedBlocks.add(offset)) {
                        continue;
                    }

                    Block offsetBlock = world.getBlock(offset.getX(), offset.getY(), offset.getZ());
                    int offsetMetadata = world.getBlockMetadata(offset.getX(), offset.getY(), offset.getZ());
                    if (offsetBlock.getMaterial()
                        .isReplaceable()
                        || (offsetBlock.getMaterial()
                            .isLiquid() && offsetBlock == block
                            && offsetMetadata == state)) {
                        putFluidVertex(
                            tessellator,
                            blockPos.getX(),
                            blockPos.getY(),
                            blockPos.getZ(),
                            textureMapBlocks.getTextureExtry(
                                offsetBlock.getIcon(0, offsetMetadata)
                                    .getIconName()),
                            direction);
                    } else if (offsetBlock.getMaterial()
                        .isLiquid() && offsetBlock == block) {
                            fluidBlocks.add(offset);
                        }
                }
            }
        }

        if (!fluidBlocks.isEmpty()) {
            tesselateBatch(world, fluidBlocks, tessellator, visitedBlocks, state);
        }
    }

    private void putFluidVertex(Tessellator tessellator, double x, double y, double z, TextureAtlasSprite sprite,
        EnumFacing facing) {
        float minU, maxU, minV, maxV;
        if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
            minU = sprite.getInterpolatedU(x * 8);
            maxU = sprite.getInterpolatedU((x + 1) * 8);
            minV = sprite.getInterpolatedV(z * 8);
            maxV = sprite.getInterpolatedV((z + 1) * 8);
        } else {
            minU = sprite.getInterpolatedU(z * 8);
            maxU = sprite.getInterpolatedU((z + 1) * 8);
            minV = sprite.getInterpolatedV(y * 8);
            maxV = sprite.getInterpolatedV((y + 1) * 8);
        }

        tessellator.addVertexWithUV(x, y, z, minU, minV);
        tessellator.addVertexWithUV(x, y, z, minU, maxV);
        tessellator.addVertexWithUV(x, y, z, maxU, maxV);
        tessellator.addVertexWithUV(x, y, z, maxU, minV);
    }

}
