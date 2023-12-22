package fr.iamacat.optimizationsandtweaks.mixins.common.minenautica;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.minenautica.Minenautica.Blocks.TechneRenderings.CanBlockStay;
import com.minenautica.Minenautica.CustomRegistry.BlocksAndItems;

@Mixin(CanBlockStay.class)
public class MixinCanBlockStay {

    @Shadow
    private static Block[] minenauticaMachinesList = new Block[20];
    @Shadow
    private static Block[] illegalBlockList = new Block[8];

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static boolean canBelowGroundBlockStay(World world, int x, int y, int z) {
        Block blockBelow = world.getBlock(x, y - 1, z);

        if (blockBelow != Blocks.air && blockBelow != Blocks.water && blockBelow != BlocksAndItems.saltWater) {
            if (world.getBlockMetadata(x, y, z) != 6 && world.getBlock(x, y, z) == BlocksAndItems.lightStick) {
                Block blockAbove = world.getBlock(x, y + 1, z);
                if (blockAbove != BlocksAndItems.lightStick) {
                    return false;
                }
            }

            for (Block illegalBlock : illegalBlockList) {
                if (blockBelow == illegalBlock) {
                    return false;
                }
            }

            for (int i = 0; i < minenauticaMachinesList.length; ++i) {
                if (i != 5 && i != 6 && blockBelow == minenauticaMachinesList[i]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
