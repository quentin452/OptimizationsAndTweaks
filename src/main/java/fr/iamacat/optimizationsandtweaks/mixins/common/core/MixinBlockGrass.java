package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {

    /**
     * @author
     * @reason
     */
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    public void updateTick(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        if (worldIn.isRemote) {
            return;
        }

        if (optimizationsAndTweaks$isGrassTurningToDirt(worldIn, x, y, z)) {
            worldIn.setBlock(x, y, z, Blocks.dirt);
        } else if (optimizationsAndTweaks$canSpreadGrass(worldIn, x, y, z)) {
            optimizationsAndTweaks$spreadGrass(worldIn, x, y, z, random);
        }
        ci.cancel();
    }

    @Unique
    private boolean optimizationsAndTweaks$isGrassTurningToDirt(World worldIn, int x, int y, int z) {
        return worldIn.getBlockLightValue(x, y + 1, z) < 4 && worldIn.getBlockLightOpacity(x, y + 1, z) > 2;
    }

    @Unique
    private boolean optimizationsAndTweaks$canSpreadGrass(World worldIn, int x, int y, int z) {
        return worldIn.getBlockLightValue(x, y + 1, z) >= 9;
    }

    private void optimizationsAndTweaks$spreadGrass(World worldIn, int x, int y, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk chunk = worldIn.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return;
        }

        if (worldIn.getTotalWorldTime() % 10 == 0) {
            for (int l = 0; l < 4; ++l) {
                int i1 = x + random.nextInt(3) - 1;
                int j1 = y + random.nextInt(5) - 3;
                int k1 = z + random.nextInt(3) - 1;
                Block block = chunk.getBlock(i1 & 15, j1, k1 & 15);
                int metadata = chunk.getBlockMetadata(i1 & 15, j1, k1 & 15);

                int lightValue = worldIn.getBlockLightValue(i1, j1 + 1, k1);
                int lightOpacity = worldIn.getBlockLightOpacity(i1, j1 + 1, k1);

                if (block == Blocks.dirt && metadata == 0 && lightValue >= 4 && lightOpacity <= 2) {
                    worldIn.setBlock(i1, j1, k1, Blocks.grass, 0, 2);
                }
            }
        }
    }
}
