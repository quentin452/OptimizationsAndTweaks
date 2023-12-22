package fr.iamacat.optimizationsandtweaks.mixins.common.netherlicious;

import DelirusCrux.Netherlicious.World.Features.Terrain.Crystal.WorldGeneratorAdv;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(WorldGeneratorAdv.class)
public abstract class MixinWorldGeneratorAdv  extends WorldGenerator {
    @Shadow
    private boolean profiling = false;
    @Shadow
    private int blockcount;
    @Shadow
    private int blockcounttotal = 0;
    @Shadow
    private int callcount = 0;
    @Shadow
    private int fillcount = 0;

    public MixinWorldGeneratorAdv() {
    }
    @Shadow

    public void setProfiling(boolean profiling) {
        this.profiling = profiling;
    }
    @Shadow
    public final void noGen() {
        ++this.callcount;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public final boolean func_76484_a(World worldObj, Random rand, int x, int y, int z) {
        this.blockcount = 0;
        ++this.callcount;
        boolean flag = this.doGeneration(worldObj, rand, x, y, z);
        if (this.profiling && this.blockcount > 0) {
            ++this.fillcount;
        }

        return flag;
    }

    @Shadow
    public abstract boolean doGeneration(World var1, Random var2, int var3, int var4, int var5);


    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected final boolean placeBlock(World worldObj, int x, int y, int z, Block block, int metadata, int mask) {
        optimizationsAndTweaks$incrementBlockCounters();
        worldObj.setBlock(x, y, z, block, metadata, mask);
        return true;
    }

    @Unique
    private void optimizationsAndTweaks$incrementBlockCounters() {
        ++this.blockcounttotal;
        ++this.blockcount;
    }
}
