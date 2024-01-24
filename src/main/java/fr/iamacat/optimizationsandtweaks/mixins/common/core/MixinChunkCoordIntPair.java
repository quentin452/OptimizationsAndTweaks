package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkCoordIntPair.class)
public class MixinChunkCoordIntPair implements Comparable<ChunkCoordIntPair> {

    @Shadow
    public final int chunkXPos;
    @Shadow
    public final int chunkZPos;
    public MixinChunkCoordIntPair(int p_i1947_1_, int p_i1947_2_) {
        this.chunkXPos = p_i1947_1_;
        this.chunkZPos = p_i1947_2_;
    }
    @Overwrite
    public static long chunkXZ2Int(int p_77272_0_, int p_77272_1_) {
        return p_77272_0_ & 4294967295L | (p_77272_1_ & 4294967295L) << 32;
    }

    @Overwrite
    public int hashCode() {
        int i = 1664525 * this.chunkXPos + 1013904223;
        int j = 1664525 * (this.chunkZPos ^ -559038737) + 1013904223;
        return i ^ j;
    }

    @Overwrite
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof ChunkCoordIntPair)) {
            return false;
        } else {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) p_equals_1_;
            return this.chunkXPos == chunkcoordintpair.chunkXPos && this.chunkZPos == chunkcoordintpair.chunkZPos;
        }
    }
    @Overwrite
    public int getCenterXPos() {
        return (this.chunkXPos << 4) + 8;
    }
    @Overwrite
    public int getCenterZPosition() {
        return (this.chunkZPos << 4) + 8;
    }
    @Overwrite
    public ChunkPosition func_151349_a(int p_151349_1_) {
        return new ChunkPosition(this.getCenterXPos(), p_151349_1_, this.getCenterZPosition());
    }

    @Override
    public int compareTo(ChunkCoordIntPair other) {
        int result = Integer.compare(this.chunkXPos, other.chunkXPos);
        if (result == 0) {
            result = Integer.compare(this.chunkZPos, other.chunkZPos);
        }
        return result;
    }

    @Overwrite
    public String toString() {
        return "[" + this.chunkXPos + ", " + this.chunkZPos + "]";
    }
}
