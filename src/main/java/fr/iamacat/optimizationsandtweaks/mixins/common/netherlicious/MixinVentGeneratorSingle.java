package fr.iamacat.optimizationsandtweaks.mixins.common.netherlicious;

import DelirusCrux.Netherlicious.World.Features.Terrain.VentGeneratorSingle;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Mixin(VentGeneratorSingle.class)
public abstract class MixinVentGeneratorSingle extends WorldGenerator {
    @Shadow
    private Block Ground;
    @Shadow
    private Block Bottom;
    @Shadow
    private Block Vent;
    @Shadow
    private int metaVent;

    @Unique
    private final Set<Long> optimizationsAndTweaks$generatedChunks = new HashSet<>();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random random, int x, int y, int z) {
        long chunkPos = ((long) x >> 4) & 0xFFFFFFFFL | (((long) z >> 4) & 0xFFFFFFFFL) << 32;
        if (optimizationsAndTweaks$generatedChunks.contains(chunkPos)) {
            return false; // Chunk already generated, skip
        }

        ChunkProviderServer chunkProvider = ((WorldServer) world).theChunkProviderServer;
        if (!chunkProvider.chunkExists(x >> 4, z >> 4)) {
            return false;
        }

        optimizationsAndTweaks$generatedChunks.add(chunkPos);

        for (int l = 0; l < 4; ++l) {
            int i1 = (x >> 4 << 4) + random.nextInt(16);
            int j1 = random.nextInt(4);
            int k1 = (z >> 4 << 4) + random.nextInt(16);

            if (optimizationsAndTweaks$canPlaceVent(world, i1, j1, k1)) {
                int var999 = random.nextInt(4);
                switch (var999) {
                    case 0:
                        if (optimizationsAndTweaks$isAir(world, i1, j1 + 2, k1)) {
                            optimizationsAndTweaks$placeVent(world, i1, j1, k1, this.Bottom, 0, this.Vent, 0, this.Vent, 2);
                        }
                        break;
                    case 1:
                        if (optimizationsAndTweaks$isAir(world, i1, j1 + 2, k1) && optimizationsAndTweaks$isAir(world, i1, j1 + 3, k1)) {
                            optimizationsAndTweaks$placeVent(world, i1, j1, k1, this.Bottom, 0, this.Vent, 1, this.Vent, 2);
                            world.setBlock(i1, j1 + 2, k1, this.Vent, this.metaVent, 2);
                        }
                        break;
                    case 2:
                        if (optimizationsAndTweaks$canPlaceVentAtHeight(world, i1, j1, k1, 4, 12)) {
                            optimizationsAndTweaks$placeVent(world, i1, j1, k1, this.Bottom, 0, this.Vent, 0, this.Vent, 1);
                            world.setBlock(i1, j1 + 3, k1, this.Vent, this.metaVent, 2);
                        }
                        break;
                    case 3:
                        if (optimizationsAndTweaks$canPlaceVentAtHeight(world, i1, j1, k1, 5, 12)) {
                            optimizationsAndTweaks$placeVent(world, i1, j1, k1, this.Bottom, 0, this.Vent, 0, this.Vent, 0);
                            world.setBlock(i1, j1 + 4, k1, this.Vent, this.metaVent, 2);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return true;
    }

    @Unique
    private boolean optimizationsAndTweaks$canPlaceVent(World world, int x, int y, int z) {
        return world.getBlock(x, y - 1, z) == this.Ground &&
            optimizationsAndTweaks$isAir(world, x, y, z) &&
            optimizationsAndTweaks$isAir(world, x, y + 1, z);
    }

    @Unique
    private boolean optimizationsAndTweaks$canPlaceVentAtHeight(World world, int x, int y, int z, int height, int maxDepth) {
        boolean canPlace = true;
        for (int h = 2; h <= height && h <= maxDepth; h++) {
            if (!optimizationsAndTweaks$isAir(world, x, y + h, z)) {
                canPlace = false;
                break;
            }
        }
        return canPlace;
    }

    @Unique
    private void optimizationsAndTweaks$placeVent(World world, int x, int y, int z, Block bottom, int bottomMeta, Block vent, int ventMeta1, Block vent2, int ventMeta2) {
        world.setBlock(x, y - 1, z, bottom, bottomMeta, 2);
        world.setBlock(x, y, z, vent, ventMeta1, 2);
        world.setBlock(x, y + 1, z, vent2, ventMeta2, 2);
    }

    @Unique
    private boolean optimizationsAndTweaks$isAir(World world, int x, int y, int z) {
        return world.isAirBlock(x, y, z);
    }
}
