package fr.iamacat.optimizationsandtweaks.mixins.common.lotr;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import lotr.common.LOTRDimension;
import lotr.common.world.LOTRWorldProvider;

@Mixin(LOTRWorldProvider.class)
public abstract class MixinLOTRWorldProvider extends WorldProvider {

    @Shadow
    public abstract LOTRDimension getLOTRDimension();

    /*
     * Require Endlessids
     * Fix crash when teleporting to LOTR dimension with Endlessids
     */
    @Override
    public BiomeGenBase getBiomeGenForCoords(int i, int k) {
        if (worldObj.blockExists(i, 0, k)) {
            BiomeGenBase biome = worldChunkMgr.getBiomeGenAt(i, k);
            LOTRDimension dim = getLOTRDimension();
            int biomeID = biome.biomeID;
            return dim.biomeList[biomeID] == null ? dim.biomeList[0] : dim.biomeList[biomeID];
        }
        return worldChunkMgr.getBiomeGenAt(i, k);
    }
}
