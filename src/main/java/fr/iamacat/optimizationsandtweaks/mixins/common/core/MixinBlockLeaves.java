package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockLeaves.class)
public abstract class MixinBlockLeaves extends BlockLeavesBase implements IShearable {

    protected MixinBlockLeaves(Material p_i45433_1_, boolean p_i45433_2_) {
        super(p_i45433_1_, p_i45433_2_);
    }
    @Unique
    private final int AREA_SIZE = 32;
    @Unique
    private final int HALF_AREA = AREA_SIZE / 2;
    @Unique
    private int[] optimizationsAndTweaks$blockArray = new int[AREA_SIZE * AREA_SIZE * AREA_SIZE];

    /**
     * @author
     * @reason
     */
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    public void updateTick(World world, int posX, int posY, int posZ, Random random, CallbackInfo ci) {
        if (!world.isRemote) {
            int currentMetadata = world.getBlockMetadata(posX, posY, posZ);

            if ((currentMetadata & 8) != 0 && (currentMetadata & 4) == 0) {
                final int SEARCH_RADIUS = 4;

                // Initialize a block array
                Arrays.fill(optimizationsAndTweaks$blockArray, 0);

                for (int xOffset = -SEARCH_RADIUS; xOffset <= SEARCH_RADIUS; ++xOffset) {
                    for (int yOffset = -SEARCH_RADIUS; yOffset <= SEARCH_RADIUS; ++yOffset) {
                        for (int zOffset = -SEARCH_RADIUS; zOffset <= SEARCH_RADIUS; ++zOffset) {
                            int blockX = posX + xOffset;
                            int blockY = posY + yOffset;
                            int blockZ = posZ + zOffset;

                            Block currentBlock = world.getBlock(blockX, blockY, blockZ);
                            int index = (xOffset + HALF_AREA) * AREA_SIZE * AREA_SIZE
                                + (yOffset + HALF_AREA) * AREA_SIZE
                                + zOffset
                                + HALF_AREA;

                            if (!currentBlock.canSustainLeaves(world, blockX, blockY, blockZ)) {
                                if (currentBlock.isLeaves(world, blockX, blockY, blockZ)) {
                                    optimizationsAndTweaks$blockArray[index] = -2;
                                } else {
                                    optimizationsAndTweaks$blockArray[index] = -1;
                                }
                            } else {
                                optimizationsAndTweaks$blockArray[index] = 0;
                            }
                        }
                    }
                }

                // Update block status based on neighbor blocks and propagate leaves status
                for (int iteration = 1; iteration <= 4; ++iteration) {
                    for (int xOffset = -SEARCH_RADIUS; xOffset <= SEARCH_RADIUS; ++xOffset) {
                        for (int yOffset = -SEARCH_RADIUS; yOffset <= SEARCH_RADIUS; ++yOffset) {
                            for (int zOffset = -SEARCH_RADIUS; zOffset <= SEARCH_RADIUS; ++zOffset) {
                                int index = (xOffset + HALF_AREA) * AREA_SIZE * AREA_SIZE
                                    + (yOffset + HALF_AREA) * AREA_SIZE
                                    + zOffset
                                    + HALF_AREA;

                                if (optimizationsAndTweaks$blockArray[index] == iteration - 1) {
                                    optimizationsAndTweaks$propagateLeavesStatus(optimizationsAndTweaks$blockArray, index - 1, iteration);
                                    optimizationsAndTweaks$propagateLeavesStatus(optimizationsAndTweaks$blockArray, index + 1, iteration);
                                    optimizationsAndTweaks$propagateLeavesStatus(optimizationsAndTweaks$blockArray, index - AREA_SIZE, iteration);
                                    optimizationsAndTweaks$propagateLeavesStatus(optimizationsAndTweaks$blockArray, index + AREA_SIZE, iteration);
                                    optimizationsAndTweaks$propagateLeavesStatus(optimizationsAndTweaks$blockArray, index - AREA_SIZE * AREA_SIZE, iteration);
                                    optimizationsAndTweaks$propagateLeavesStatus(optimizationsAndTweaks$blockArray, index + AREA_SIZE * AREA_SIZE, iteration);
                                }
                            }
                        }
                    }
                }

                // Check central block and update world
                int centralBlockStatus = optimizationsAndTweaks$blockArray[HALF_AREA * AREA_SIZE * AREA_SIZE + HALF_AREA * AREA_SIZE
                    + HALF_AREA];

                if (centralBlockStatus >= 0) {
                    world.setBlockMetadataWithNotify(posX, posY, posZ, currentMetadata & -9, 4);
                } else {
                    removeLeaves(world, posX, posY, posZ);
                }
            }
        }
        ci.cancel();
    }

    @Unique
    private void optimizationsAndTweaks$propagateLeavesStatus(int[] blockArray, int index, int iteration) {
        if (blockArray[index] == -2) {
            blockArray[index] = iteration;
        }
    }

    @Shadow
    private void removeLeaves(World p_150126_1_, int p_150126_2_, int p_150126_3_, int p_150126_4_) {
        this.dropBlockAsItem(
            p_150126_1_,
            p_150126_2_,
            p_150126_3_,
            p_150126_4_,
            p_150126_1_.getBlockMetadata(p_150126_2_, p_150126_3_, p_150126_4_),
            0);
        p_150126_1_.setBlockToAir(p_150126_2_, p_150126_3_, p_150126_4_);
    }

    @Shadow
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) // do not drop items while restoring blockstates,
                                                                   // prevents item dupe
        {
            ArrayList<ItemStack> items = getDrops(worldIn, x, y, z, meta, fortune);
            chance = ForgeEventFactory
                .fireBlockHarvesting(items, worldIn, this, x, y, z, meta, fortune, chance, false, harvesters.get());

            for (ItemStack item : items) {
                if (worldIn.rand.nextFloat() <= chance) {
                    this.dropBlockAsItem(worldIn, x, y, z, item);
                }
            }
        }
    }

    @Shadow
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        int chance = this.func_150123_b(metadata);

        if (fortune > 0) {
            chance -= 2 << fortune;
            if (chance < 10) chance = 10;
        }

        if (world.rand.nextInt(chance) == 0)
            ret.add(new ItemStack(this.getItemDropped(metadata, world.rand, fortune), 1, this.damageDropped(metadata)));

        chance = 200;
        if (fortune > 0) {
            chance -= 10 << fortune;
            if (chance < 40) chance = 40;
        }

        this.captureDrops(true);
        this.func_150124_c(world, x, y, z, metadata, chance); // Dammet mojang
        ret.addAll(this.captureDrops(false));
        return ret;
    }

    @Shadow
    protected int func_150123_b(int p_150123_1_) {
        return 20;
    }

    @Shadow
    protected void func_150124_c(World p_150124_1_, int p_150124_2_, int p_150124_3_, int p_150124_4_, int p_150124_5_,
        int p_150124_6_) {}

}
