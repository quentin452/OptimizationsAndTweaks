package fr.iamacat.optimizationsandtweaks.mixins.common.eternalfrost.mobspawningfix;

import eternalfrost.blocks.BlockMobSpawner3;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockMobSpawner3.class)
public class MixinBlockMobSpawner3 {
    @Overwrite
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityMobSpawner();
    }
}