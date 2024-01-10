package fr.iamacat.optimizationsandtweaks.mixins.common.obsgreenery;

import com.jim.obsgreenery.ObsGreenery;
import com.jim.obsgreenery.world.WorldGenTreeBase;
import com.jim.obsgreenery.world.WorldGenTreeBlackWattle;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.Classers;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.obsgreenery.WorldGenTreeBase2;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(WorldGenTreeBlackWattle.class)
public class MixinWorldGenTreeBlackWattle extends WorldGenTreeBase {
    @Shadow
    protected Block logBlock;
    @Shadow
    protected int logBlockMeta;
    @Shadow
    protected Block leavesBlock;
    @Shadow
    protected int leavesBlockMeta;
    @Unique
    WorldGenTreeBase2 worldGenTreeBase2 = new WorldGenTreeBase2() {
        @Override
        public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
            return false;
        }
    };

    public MixinWorldGenTreeBlackWattle(boolean notifyBlocks) {
        super(notifyBlocks);
        this.logBlock = ObsGreenery.blockLogA;
        this.logBlockMeta = 1;
        this.leavesBlock = ObsGreenery.blockLeavesA;
        this.leavesBlockMeta = 1;
    }
    @Shadow
    public boolean func_76484_a(World world, Random rand, int x, int y, int z) {
        int posY = this.getGroundYPosition(world, x, z) + 1;
        return posY < 100 && posY > 62 && this.grow(world, rand, x, posY, z);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean grow(World world, Random rand, int x, int y, int z) {
        int minTrunkHeight = 8;
        int extraHeight = rand.nextInt(12);
        int trunkHeight = minTrunkHeight + extraHeight;
        int halfExtraHeight = extraHeight / 2;
        int thirdTrunkHeight = trunkHeight / 3;
        int worldHeight = world.getHeight();
        if (y >= 1 && y + trunkHeight + 1 <= worldHeight && this.isBaseBlockValid(world, x, y - 1, z)) {
            this.thinTrunk(world, x, y, z, trunkHeight, this.logBlock, this.logBlockMeta);
            Classers.Quadrant[] var14 = Classers.Quadrant.values();

            for (Classers.Quadrant q : var14) {
                int bY = y + thirdTrunkHeight + rand.nextInt(3) - 1;
                int bLen = thirdTrunkHeight + rand.nextInt(3) - 1;
                worldGenTreeBase2.optimizationsAndTweaks$recursiveBranch45(world, x, bY, z, bLen, q, this.logBlock, this.logBlockMeta, this.leavesBlock, this.leavesBlockMeta);
                if (extraHeight > 4) {
                    bLen = (int) ( trunkHeight * 0.3) + rand.nextInt(3) - 1;
                    worldGenTreeBase2.optimizationsAndTweaks$recursiveBranch45(world, x, y + minTrunkHeight + halfExtraHeight + rand.nextInt(2) - 1, z, bLen, q, this.logBlock, this.logBlockMeta, this.leavesBlock, this.leavesBlockMeta);
                }

                bLen = (int) ( trunkHeight * 0.15) + rand.nextInt(3) - 1;
                worldGenTreeBase2.optimizationsAndTweaks$recursiveBranch45(world, x, y + trunkHeight - 1, z, bLen, q, this.logBlock, this.logBlockMeta, this.leavesBlock, this.leavesBlockMeta);
            }

            return true;
        } else {
            return false;
        }
    }
}
