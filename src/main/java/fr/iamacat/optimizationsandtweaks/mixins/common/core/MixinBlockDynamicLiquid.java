package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Optimizes BlockDynamicLiquid class.
 */
@Mixin(BlockDynamicLiquid.class)
public abstract class MixinBlockDynamicLiquid extends BlockLiquid {

    @Shadow
    int field_149815_a;
    @Shadow
    boolean[] field_149814_b = new boolean[4];
    @Shadow
    int[] field_149816_M = new int[4];

    protected MixinBlockDynamicLiquid(Material p_i45413_1_) {
        super(p_i45413_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        int l = this.func_149804_e(worldIn, x, y, z);
        byte b0 = 1;

        if (this.blockMaterial == Material.lava && !worldIn.provider.isHellWorld) {
            b0 = 2;
        }

        boolean flag = true;
        int i1 = this.tickRate(worldIn);
        int j1;

        if (l > 0) {
            byte b1 = -100;
            this.field_149815_a = 0;
            int l1 = this.func_149810_a(worldIn, x - 1, y, z, b1);
            l1 = this.func_149810_a(worldIn, x + 1, y, z, l1);
            l1 = this.func_149810_a(worldIn, x, y, z - 1, l1);
            l1 = this.func_149810_a(worldIn, x, y, z + 1, l1);
            j1 = l1 + b0;

            if (j1 >= 8 || l1 < 0) {
                j1 = -1;
            }

            // Dedupe: vanilla calls func_149804_e(x, y+1, z) twice in a row (world unchanged between).
            int aboveDecay = this.func_149804_e(worldIn, x, y + 1, z);

            if (aboveDecay >= 0) {
                if (aboveDecay >= 8) {
                    j1 = aboveDecay;
                } else {
                    j1 = aboveDecay + 8;
                }
            }

            if (this.field_149815_a >= 2 && this.blockMaterial == Material.water) {
                // Dedupe: vanilla reads getBlock(x, y-1, z) twice here (world unchanged between).
                Material belowMaterial = worldIn.getBlock(x, y - 1, z)
                    .getMaterial();

                if (belowMaterial.isSolid()) {
                    j1 = 0;
                } else if (belowMaterial == this.blockMaterial && worldIn.getBlockMetadata(x, y - 1, z) == 0) {
                    j1 = 0;
                }
            }

            if (this.blockMaterial == Material.lava && l < 8 && j1 < 8 && j1 > l && random.nextInt(4) != 0) {
                i1 *= 4;
            }

            if (j1 == l) {
                if (flag) {
                    this.func_149811_n(worldIn, x, y, z);
                }
            } else {
                l = j1;

                if (j1 < 0) {
                    worldIn.setBlockToAir(x, y, z);
                } else {
                    worldIn.setBlockMetadataWithNotify(x, y, z, j1, 2);
                    worldIn.scheduleBlockUpdate(x, y, z, this, i1);
                    worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
                }
            }
        } else {
            this.func_149811_n(worldIn, x, y, z);
        }

        if (this.func_149809_q(worldIn, x, y - 1, z)) {
            if (this.blockMaterial == Material.lava && worldIn.getBlock(x, y - 1, z)
                .getMaterial() == Material.water) {
                worldIn.setBlock(x, y - 1, z, Blocks.stone);
                this.func_149799_m(worldIn, x, y - 1, z);
                return;
            }

            if (l >= 8) {
                this.func_149813_h(worldIn, x, y - 1, z, l);
            } else {
                this.func_149813_h(worldIn, x, y - 1, z, l + 8);
            }
        } else if (l >= 0 && (l == 0 || this.func_149807_p(worldIn, x, y - 1, z))) {
            boolean[] aboolean = this.func_149808_o(worldIn, x, y, z);
            j1 = l + b0;

            if (l >= 8) {
                j1 = 1;
            }

            if (j1 >= 8) {
                return;
            }

            if (aboolean[0]) {
                this.func_149813_h(worldIn, x - 1, y, z, j1);
            }

            if (aboolean[1]) {
                this.func_149813_h(worldIn, x + 1, y, z, j1);
            }

            if (aboolean[2]) {
                this.func_149813_h(worldIn, x, y, z - 1, j1);
            }

            if (aboolean[3]) {
                this.func_149813_h(worldIn, x, y, z + 1, j1);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void func_149813_h(World p_149813_1_, int p_149813_2_, int p_149813_3_, int p_149813_4_, int p_149813_5_) {
        if (this.func_149809_q(p_149813_1_, p_149813_2_, p_149813_3_, p_149813_4_)) {
            Block block = p_149813_1_.getBlock(p_149813_2_, p_149813_3_, p_149813_4_);

            if (this.blockMaterial == Material.lava) {
                this.func_149799_m(p_149813_1_, p_149813_2_, p_149813_3_, p_149813_4_);
            } else {
                block.dropBlockAsItem(
                    p_149813_1_,
                    p_149813_2_,
                    p_149813_3_,
                    p_149813_4_,
                    p_149813_1_.getBlockMetadata(p_149813_2_, p_149813_3_, p_149813_4_),
                    0);
            }

            p_149813_1_.setBlock(p_149813_2_, p_149813_3_, p_149813_4_, this, p_149813_5_, 3);
        }
    }

    @Shadow
    private boolean func_149809_q(World p_149809_1_, int p_149809_2_, int p_149809_3_, int p_149809_4_) {
        Material material = p_149809_1_.getBlock(p_149809_2_, p_149809_3_, p_149809_4_)
            .getMaterial();
        return material == this.blockMaterial ? false
            : (material == Material.lava ? false
                : !this.func_149807_p(p_149809_1_, p_149809_2_, p_149809_3_, p_149809_4_));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private boolean func_149807_p(World p_149807_1_, int p_149807_2_, int p_149807_3_, int p_149807_4_) {
        Block block = p_149807_1_.getBlock(p_149807_2_, p_149807_3_, p_149807_4_);
        return block == Blocks.wooden_door || block == Blocks.iron_door
            || block == Blocks.standing_sign
            || block == Blocks.ladder
            || block == Blocks.reeds
            || (block.getMaterial() == Material.portal || block.getMaterial()
                .blocksMovement());
    }

    @Shadow
    private void func_149811_n(World p_149811_1_, int p_149811_2_, int p_149811_3_, int p_149811_4_) {
        int l = p_149811_1_.getBlockMetadata(p_149811_2_, p_149811_3_, p_149811_4_);
        p_149811_1_
            .setBlock(p_149811_2_, p_149811_3_, p_149811_4_, Block.getBlockById(Block.getIdFromBlock(this) + 1), l, 2);
    }

    /**
     * @author quentin452
     * @reason Replace vanilla's recursive, visited-set-less flood-fill (func_149812_c: depth 4, branch 3, no
     *         memoization -> up to ~640 world reads per flat block) with a breadth-first search that carries a
     *         visited-set and memoizes per-tick neighbour reads. Returns the IDENTICAL optimal flow directions as
     *         vanilla (see equivalence note below) with each world cell read at most once.
     */
    @Overwrite
    private boolean[] func_149808_o(World world, int x, int y, int z) {
        // The world is not mutated during this computation (all block placements happen after this returns), so
        // reads can be cached for the whole call. Both caches are keyed by column (x,z); y is constant here.
        HashMap<Long, Boolean> traversableCache = new HashMap<>();
        HashMap<Long, Boolean> holeCache = new HashMap<>();
        HashSet<Long> visited = new HashSet<>();
        ArrayDeque<int[]> frontier = new ArrayDeque<>();

        for (int side = 0; side < 4; ++side) {
            int nx = x;
            int nz = z;

            if (side == 0) {
                nx = x - 1;
            }

            if (side == 1) {
                ++nx;
            }

            if (side == 2) {
                nz = z - 1;
            }

            if (side == 3) {
                ++nz;
            }

            this.field_149816_M[side] = this
                .ot_flowCost(world, nx, y, nz, visited, frontier, traversableCache, holeCache);
        }

        int min = this.field_149816_M[0];

        for (int side = 1; side < 4; ++side) {
            if (this.field_149816_M[side] < min) {
                min = this.field_149816_M[side];
            }
        }

        for (int side = 0; side < 4; ++side) {
            this.field_149814_b[side] = this.field_149816_M[side] == min;
        }

        return this.field_149814_b;
    }

    /**
     * Behaviour-preserving replacement for vanilla's per-direction seed check (func_149808_o) + recursive flood-fill
     * (func_149812_c). Returns the minimum number of horizontal steps (0..4) from the direct neighbour (nx,y,nz) to a
     * cell that can drain downwards, or 1000 if none is reachable within 4 steps.
     *
     * Equivalence: vanilla explores this with a depth-limited DFS that has no visited-set (re-reading the same cells
     * exponentially) and only forbids immediate U-turns. Because a shortest path is a simple path (no U-turn, no
     * revisit), that shortest path is always among the walks vanilla explores, so vanilla's returned cost equals the
     * true shortest distance to the nearest reachable hole, capped at depth 4; the extra (longer) walks vanilla
     * explores never lower the minimum. A BFS with a visited-set computes exactly that shortest distance while reading
     * each cell once. The traversable/hole predicates and the depth-4 cap are byte-identical to vanilla, so the cost
     * per direction - and therefore the chosen flow directions - are identical.
     */
    private int ot_flowCost(World world, int nx, int y, int nz, HashSet<Long> visited, ArrayDeque<int[]> frontier,
        HashMap<Long, Boolean> traversableCache, HashMap<Long, Boolean> holeCache) {
        // Direct neighbour unusable -> this direction is blocked (cost stays 1000, matching func_149808_o).
        if (!this.ot_isTraversable(world, nx, y, nz, traversableCache)) {
            return 1000;
        }

        // Neighbour itself can drain downwards -> cost 0 (matching func_149808_o's else branch).
        if (this.ot_isHoleBelow(world, nx, y, nz, holeCache)) {
            return 0;
        }

        visited.clear();
        frontier.clear();
        visited.add(ot_pack(nx, nz));
        frontier.add(new int[] { nx, nz, 0 });

        while (!frontier.isEmpty()) {
            int[] cell = frontier.poll();
            int cx = cell[0];
            int cz = cell[1];
            int dist = cell[2];

            // Vanilla only recurses while depth < 4, so distance-4 cells are inspected for a hole but never expanded.
            if (dist >= 4) {
                continue;
            }

            for (int side = 0; side < 4; ++side) {
                int ax = cx;
                int az = cz;

                if (side == 0) {
                    ax = cx - 1;
                }

                if (side == 1) {
                    ++ax;
                }

                if (side == 2) {
                    az = cz - 1;
                }

                if (side == 3) {
                    ++az;
                }

                long key = ot_pack(ax, az);

                if (!visited.add(key)) {
                    continue;
                }

                if (!this.ot_isTraversable(world, ax, y, az, traversableCache)) {
                    continue;
                }

                int nextDist = dist + 1;

                if (this.ot_isHoleBelow(world, ax, y, az, holeCache)) {
                    // FIFO order guarantees this is the nearest hole for this direction.
                    return nextDist;
                }

                frontier.add(new int[] { ax, az, nextDist });
            }
        }

        return 1000;
    }

    /**
     * True if liquid can flow through (nx,y,nz): not a flow-blocker and not a source block of this liquid. Mirrors
     * vanilla's inline test {@code !func_149807_p && (material != blockMaterial || meta != 0)} exactly, memoized.
     */
    private boolean ot_isTraversable(World world, int x, int y, int z, HashMap<Long, Boolean> cache) {
        long key = ot_pack(x, z);
        Boolean cached = cache.get(key);

        if (cached != null) {
            return cached;
        }

        boolean result;

        if (this.func_149807_p(world, x, y, z)) {
            result = false;
        } else {
            Block block = world.getBlock(x, y, z);
            result = block.getMaterial() != this.blockMaterial || world.getBlockMetadata(x, y, z) != 0;
        }

        cache.put(key, result);
        return result;
    }

    /**
     * True if the cell directly below (x,y,z) is not a flow-blocker (i.e. the liquid could fall there). Mirrors
     * vanilla's {@code !func_149807_p(x, y - 1, z)}, memoized.
     */
    private boolean ot_isHoleBelow(World world, int x, int y, int z, HashMap<Long, Boolean> cache) {
        long key = ot_pack(x, z);
        Boolean cached = cache.get(key);

        if (cached != null) {
            return cached;
        }

        boolean result = !this.func_149807_p(world, x, y - 1, z);
        cache.put(key, result);
        return result;
    }

    /** Packs a column (x,z) into a unique long key; y is constant within a single func_149808_o call. */
    private static long ot_pack(int x, int z) {
        return ((long) x & 0xFFFFFFFFL) | ((long) z << 32);
    }

    @Shadow
    protected int func_149810_a(World p_149810_1_, int p_149810_2_, int p_149810_3_, int p_149810_4_, int p_149810_5_) {
        int i1 = this.func_149804_e(p_149810_1_, p_149810_2_, p_149810_3_, p_149810_4_);

        if (i1 < 0) {
            return p_149810_5_;
        } else {
            if (i1 == 0) {
                ++this.field_149815_a;
            }

            if (i1 >= 8) {
                i1 = 0;
            }

            return p_149810_5_ >= 0 && i1 >= p_149810_5_ ? p_149810_5_ : i1;
        }
    }

    @Overwrite
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        if (worldIn.getBlock(x, y, z) != this) {
            return;
        }
        worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
    }
}
