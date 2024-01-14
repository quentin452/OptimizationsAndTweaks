package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.mixins.Classers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

@Mixin(BlockFluidClassic.class)
public abstract class MixinBlockFluidClassic extends BlockFluidBase {
    @Shadow
    protected boolean[] isOptimalFlowDirection = new boolean[4];
    @Shadow
    protected int[] flowCost = new int[4];
    @Shadow

    protected FluidStack stack;

    public MixinBlockFluidClassic(Fluid fluid, Material material) {
        super(fluid, material);
    }

    /**
     * @author
     * @reason
     */
    @Shadow
    protected boolean[] getOptimalFlowDirections(World world, int x, int y, int z)
    {
        for (int side = 0; side < 4; side++)
        {
            flowCost[side] = 1000;

            int x2 = x;
            int y2 = y;
            int z2 = z;

            switch (side)
            {
                case 0: --x2; break;
                case 1: ++x2; break;
                case 2: --z2; break;
                case 3: ++z2; break;
            }

            if (!canFlowInto(world, x2, y2, z2) || isSourceBlock(world, x2, y2, z2))
            {
                continue;
            }

            if (canFlowInto(world, x2, y2 + densityDir, z2))
            {
                flowCost[side] = 0;
            }
            else
            {
                flowCost[side] = calculateFlowCost(world, x2, y2, z2, 1, side);
            }
        }

        int min = flowCost[0];
        for (int side = 1; side < 4; side++)
        {
            if (flowCost[side] < min)
            {
                min = flowCost[side];
            }
        }
        for (int side = 0; side < 4; side++)
        {
            isOptimalFlowDirection[side] = flowCost[side] == min;
        }
        return isOptimalFlowDirection;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected int calculateFlowCost(World world, int x, int y, int z, int initialRecurseDepth, int side) {
        int cost = 1000;

        Deque<Classers.FlowCostContext> stack = new ArrayDeque<>();
        stack.push(new Classers.FlowCostContext(x, y, z, initialRecurseDepth, side));

        while (!stack.isEmpty()) {
            Classers.FlowCostContext context = stack.pop();
            int x2 = context.x;
            int y2 = context.y;
            int z2 = context.z;
            int recurseDepth = context.recurseDepth;
            int adjSide = context.adjSide;

            if (!canFlowInto(world, x2, y2, z2) || isSourceBlock(world, x2, y2, z2)) {
                continue;
            }

            if (recurseDepth >= 4 || canFlowInto(world, x2, y2 + densityDir, z2)) {
                return recurseDepth;
            }

            for (int nextAdjSide = 0; nextAdjSide < 4; nextAdjSide++) {
                if ((nextAdjSide == 0 && adjSide == 1) ||
                    (nextAdjSide == 1 && adjSide == 0) ||
                    (nextAdjSide == 2 && adjSide == 3) ||
                    (nextAdjSide == 3 && adjSide == 2)) {
                    continue;
                }

                int nextX = x2;
                int nextZ = z2;

                switch (nextAdjSide) {
                    case 0:
                        --nextX;
                        break;
                    case 1:
                        ++nextX;
                        break;
                    case 2:
                        --nextZ;
                        break;
                    case 3:
                        ++nextZ;
                        break;
                }

                stack.push(new Classers.FlowCostContext(nextX, y2, nextZ, recurseDepth + 1, nextAdjSide));
            }
        }

        return cost;
    }

    @Shadow
    public boolean isSourceBlock(IBlockAccess world, int x, int y, int z)
    {
        return world.getBlock(x, y, z) == this && world.getBlockMetadata(x, y, z) == 0;
    }
    @Shadow
    protected boolean canFlowInto(IBlockAccess world, int x, int y, int z)
    {
        if (world.getBlock(x, y, z).isAir(world, x, y, z)) return true;

        Block block = world.getBlock(x, y, z);
        if (block == this)
        {
            return true;
        }

        if (displacements.containsKey(block))
        {
            return displacements.get(block);
        }

        Material material = block.getMaterial();
        if (material.blocksMovement()  ||
            material == Material.water ||
            material == Material.lava  ||
            material == Material.portal)
        {
            return false;
        }

        int density = getDensity(world, x, y, z);
        if (density == Integer.MAX_VALUE)
        {
            return true;
        }

        if (this.density > density)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
