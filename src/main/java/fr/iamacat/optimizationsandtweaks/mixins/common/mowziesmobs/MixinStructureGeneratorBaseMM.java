package fr.iamacat.optimizationsandtweaks.mixins.common.mowziesmobs;

import coolalias.structuregenapi.util.*;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.LinkedList;
import java.util.List;

@Mixin(StructureGeneratorBase.class)
public abstract class MixinStructureGeneratorBaseMM extends WorldGenerator {
    @Shadow
    public static final int SET_NO_BLOCK = Integer.MAX_VALUE;
    @Shadow
    public static final int SOUTH = 0;
    @Shadow
    public static final int WEST = 1;
    @Shadow
    public static final int NORTH = 2;
    @Shadow
    public static final int EAST = 3;
    @Shadow
    private int structureFacing;
    @Shadow
    private int manualRotations;
    @Shadow
    private int facing;
    @Shadow
    private int offsetX;
    @Shadow
    private int offsetY;
    @Shadow
    private int offsetZ;
    @Shadow
    private boolean removeStructure;
    @Shadow
    private int[][][][] blockArray;
    @Shadow
    private final List<int[][][][]> blockArrayList;
    @Shadow
    private final List<BlockData> postGenBlocks;
    public MixinStructureGeneratorBaseMM() {
        super(true);
        this.structureFacing = 3;
        this.manualRotations = 0;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.removeStructure = false;
        this.blockArrayList = new LinkedList<>();
        this.postGenBlocks = new LinkedList<>();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private final void setBlockAt(World world, int fakeID, int realID, int meta, int customData1, int customData2, int x, int y, int z) {
        Block block = Block.getBlockById(realID);
        boolean isRealBlock = realID >= 0;
        boolean isAirBlock = world.isAirBlock(x, y, z);
        boolean canBlockMove = !world.getBlock(x, y, z).getMaterial().blocksMovement();

        if (isRealBlock || isAirBlock || !canBlockMove) {
            BlockRotationData.Rotation rotationType = BlockRotationData.getBlockRotationType(block);

            if (rotationType != null) {
                int rotations = ((this.isOppositeAxis() ? this.structureFacing + 2 : this.structureFacing) + this.facing) % 4;
                meta = GenHelper.getMetadata(rotations, block, meta);
            }

            if ((rotationType == BlockRotationData.Rotation.WALL_MOUNTED || rotationType == BlockRotationData.Rotation.LEVER)) {
                LogHelper.fine("Block " + block + " requires post-processing. Adding to list. Meta = " + meta);
                this.postGenBlocks.add(new BlockData(x, y, z, fakeID, meta, customData1, customData2));
            } else {
                world.setBlock(x, y, z, block, meta, 2);

                if (rotationType != null) {
                    GenHelper.setMetadata(world, x, y, z, meta);
                }

                if (Math.abs(fakeID) > 4095) {
                    this.onCustomBlockAdded(world, x, y, z, fakeID, customData1, customData2);
                }
            }
        }
    }

    @Shadow
    public final boolean isOppositeAxis() {
        return this.getOriginalFacing() % 2 != this.structureFacing % 2;
    }
    @Shadow
    public abstract void onCustomBlockAdded(World var1, int var2, int var3, int var4, int var5, int var6, int var7);
    @Shadow
    public final int getOriginalFacing() {
        return (this.structureFacing + (4 - this.manualRotations)) % 4;
    }
}
