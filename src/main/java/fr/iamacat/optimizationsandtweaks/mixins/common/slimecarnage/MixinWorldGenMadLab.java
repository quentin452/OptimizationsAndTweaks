package fr.iamacat.optimizationsandtweaks.mixins.common.slimecarnage;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import supremopete.SlimeCarnage.mobs.*;
import supremopete.SlimeCarnage.worldgen.WorldGenMadLab;

import java.util.Random;

@Mixin(WorldGenMadLab.class)
public class MixinWorldGenMadLab {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean func_76484_a(World world, Random rand, int i, int j, int k) {
        if (world.getBlock(i, j, k) == Blocks.grass && world.getBlock(i, j + 1, k) == Blocks.air && world.getBlock(i + 4, j, k) == Blocks.grass && world.getBlock(i + 4, j, k + 4) == Blocks.grass && world.getBlock(i, j, k + 4) == Blocks.grass && world.getBlock(i + 4, j + 1, k) == Blocks.air && world.getBlock(i + 4, j + 1, k + 4) == Blocks.air && world.getBlock(i, j + 1, k + 4) == Blocks.air) {
            int i7;
            int k7;
            int dunno;
            for(i7 = -10; i7 < 5; ++i7) {
                for(k7 = 1; k7 < 4; ++k7) {
                    for(dunno = 1; dunno < 4; ++dunno) {
                        world.setBlock(i + k7, j + i7, k + dunno, Blocks.stone);
                    }
                }
            }

            for(i7 = -10; i7 < -5; ++i7) {
                for(k7 = -4; k7 < 9; ++k7) {
                    for(dunno = 1; dunno < 17; ++dunno) {
                        world.setBlock(i + k7, j + i7, k + dunno, Blocks.stone);
                    }
                }
            }

            for(i7 = 5; i7 < 10; ++i7) {
                for(k7 = 1; k7 < 4; ++k7) {
                    for(dunno = 1; dunno < 4; ++dunno) {
                        world.setBlock(i + k7, j + i7, k + dunno, Blocks.air);
                    }
                }
            }

            for(i7 = -9; i7 < 4; ++i7) {
                world.setBlock(i + 2, j + i7, k + 2, Blocks.air);
            }

            for(i7 = -10; i7 < -5; ++i7) {
                for(k7 = -4; k7 < 9; ++k7) {
                    for(dunno = 1; dunno < 17; ++dunno) {
                        world.setBlock(i + k7, j + i7, k + dunno, Blocks.stone);
                    }
                }
            }

            for(i7 = -9; i7 < -6; ++i7) {
                for(k7 = -3; k7 < 8; ++k7) {
                    for(dunno = 2; dunno < 16; ++dunno) {
                        world.setBlock(i + k7, j + i7, k + dunno, Blocks.air);
                    }
                }
            }

            for(i7 = -4; i7 < 9; ++i7) {
                for(k7 = 1; k7 < 17; ++k7) {
                    world.setBlock(i + i7, j - 10, k + k7, Blocks.air);
                }
            }

            for(i7 = -4; i7 < 9; ++i7) {
                for(k7 = 1; k7 < 17; ++k7) {
                    dunno = rand.nextInt(2);
                    if (dunno == 0) {
                        world.setBlock(i + i7, j - 10, k + k7, Blocks.cobblestone);
                    } else {
                        world.setBlock(i + i7, j - 10, k + k7, Blocks.mossy_cobblestone);
                    }
                }
            }

            world.setBlock(i + 1, j + 1, k + 2, Blocks.air);
            world.setBlock(i + 1, j + 2, k + 2, Blocks.air);
            world.setBlock(i + 1, j + 3, k + 2, Blocks.air);
            world.setBlock(i + 2, j + 1, k + 1, Blocks.air);
            world.setBlock(i + 2, j + 2, k + 1, Blocks.air);
            world.setBlock(i + 2, j + 3, k + 1, Blocks.air);
            world.setBlock(i + 3, j + 1, k + 2, Blocks.air);
            world.setBlock(i + 3, j + 2, k + 2, Blocks.air);
            world.setBlock(i + 3, j + 3, k + 2, Blocks.air);
            world.setBlock(i + 2, j + 1, k + 3, Blocks.air);
            world.setBlock(i + 2, j + 2, k + 3, Blocks.air);
            world.setBlock(i + 2, j + 3, k + 3, Blocks.air);
            world.setBlock(i + 1, j + 1, k + 2, Blocks.iron_bars);
            world.setBlock(i + 1, j + 2, k + 2, Blocks.iron_bars);
            world.setBlock(i + 1, j + 3, k + 2, Blocks.iron_bars);
            world.setBlock(i + 2, j + 3, k + 1, Blocks.iron_bars);
            world.setBlock(i + 3, j + 1, k + 2, Blocks.iron_bars);
            world.setBlock(i + 3, j + 2, k + 2, Blocks.iron_bars);
            world.setBlock(i + 3, j + 3, k + 2, Blocks.iron_bars);
            world.setBlock(i + 2, j + 1, k + 3, Blocks.iron_bars);
            world.setBlock(i + 2, j + 2, k + 3, Blocks.iron_bars);
            world.setBlock(i + 2, j + 3, k + 3, Blocks.iron_bars);
            world.setBlock(i - 3, j - 7, k + 4, Blocks.stone);
            world.setBlock(i - 3, j - 8, k + 4, Blocks.stone);
            world.setBlock(i - 3, j - 9, k + 4, Blocks.stone);
            world.setBlock(i + 0, j - 7, k + 4, Blocks.stone);
            world.setBlock(i + 0, j - 8, k + 4, Blocks.stone);
            world.setBlock(i + 0, j - 9, k + 4, Blocks.stone);
            world.setBlock(i - 3, j - 7, k + 7, Blocks.stone);
            world.setBlock(i - 3, j - 8, k + 7, Blocks.stone);
            world.setBlock(i - 3, j - 9, k + 7, Blocks.stone);
            world.setBlock(i + 0, j - 7, k + 7, Blocks.stone);
            world.setBlock(i + 0, j - 8, k + 7, Blocks.stone);
            world.setBlock(i + 0, j - 9, k + 7, Blocks.stone);
            world.setBlock(i - 2, j - 7, k + 4, Blocks.iron_bars);
            world.setBlock(i - 2, j - 8, k + 4, Blocks.iron_bars);
            world.setBlock(i - 2, j - 9, k + 4, Blocks.iron_bars);
            world.setBlock(i - 1, j - 7, k + 4, Blocks.iron_bars);
            world.setBlock(i - 1, j - 8, k + 4, Blocks.iron_bars);
            world.setBlock(i - 1, j - 9, k + 4, Blocks.iron_bars);
            world.setBlock(i - 2, j - 7, k + 7, Blocks.iron_bars);
            world.setBlock(i - 2, j - 8, k + 7, Blocks.iron_bars);
            world.setBlock(i - 2, j - 9, k + 7, Blocks.iron_bars);
            world.setBlock(i - 1, j - 7, k + 7, Blocks.iron_bars);
            world.setBlock(i - 1, j - 8, k + 7, Blocks.iron_bars);
            world.setBlock(i - 1, j - 9, k + 7, Blocks.iron_bars);
            world.setBlock(i - 3, j - 7, k + 5, Blocks.iron_bars);
            world.setBlock(i - 3, j - 8, k + 5, Blocks.iron_bars);
            world.setBlock(i - 3, j - 9, k + 5, Blocks.iron_bars);
            world.setBlock(i - 0, j - 7, k + 5, Blocks.iron_bars);
            world.setBlock(i - 0, j - 8, k + 5, Blocks.iron_bars);
            world.setBlock(i - 0, j - 9, k + 5, Blocks.iron_bars);
            world.setBlock(i - 3, j - 7, k + 6, Blocks.iron_bars);
            world.setBlock(i - 3, j - 8, k + 6, Blocks.iron_bars);
            world.setBlock(i - 3, j - 9, k + 6, Blocks.iron_bars);
            world.setBlock(i - 0, j - 7, k + 6, Blocks.iron_bars);
            world.setBlock(i - 0, j - 8, k + 6, Blocks.iron_bars);
            world.setBlock(i - 0, j - 9, k + 6, Blocks.iron_bars);
            world.setBlock(i + 1, j - 7, k + 4, Blocks.torch);
            world.setBlock(i + 1, j - 7, k + 7, Blocks.torch);
            world.setBlock(i + 4, j - 7, k + 4, Blocks.stone);
            world.setBlock(i + 4, j - 8, k + 4, Blocks.stone);
            world.setBlock(i + 4, j - 9, k + 4, Blocks.stone);
            world.setBlock(i + 7, j - 7, k + 4, Blocks.stone);
            world.setBlock(i + 7, j - 8, k + 4, Blocks.stone);
            world.setBlock(i + 7, j - 9, k + 4, Blocks.stone);
            world.setBlock(i + 4, j - 7, k + 7, Blocks.stone);
            world.setBlock(i + 4, j - 8, k + 7, Blocks.stone);
            world.setBlock(i + 4, j - 9, k + 7, Blocks.stone);
            world.setBlock(i + 7, j - 7, k + 7, Blocks.stone);
            world.setBlock(i + 7, j - 8, k + 7, Blocks.stone);
            world.setBlock(i + 7, j - 9, k + 7, Blocks.stone);
            world.setBlock(i + 5, j - 7, k + 4, Blocks.iron_bars);
            world.setBlock(i + 5, j - 8, k + 4, Blocks.iron_bars);
            world.setBlock(i + 5, j - 9, k + 4, Blocks.iron_bars);
            world.setBlock(i + 6, j - 7, k + 4, Blocks.iron_bars);
            world.setBlock(i + 6, j - 8, k + 4, Blocks.iron_bars);
            world.setBlock(i + 6, j - 9, k + 4, Blocks.iron_bars);
            world.setBlock(i + 5, j - 7, k + 7, Blocks.iron_bars);
            world.setBlock(i + 5, j - 8, k + 7, Blocks.iron_bars);
            world.setBlock(i + 5, j - 9, k + 7, Blocks.iron_bars);
            world.setBlock(i + 6, j - 7, k + 7, Blocks.iron_bars);
            world.setBlock(i + 6, j - 8, k + 7, Blocks.iron_bars);
            world.setBlock(i + 6, j - 9, k + 7, Blocks.iron_bars);
            world.setBlock(i + 4, j - 7, k + 5, Blocks.iron_bars);
            world.setBlock(i + 4, j - 8, k + 5, Blocks.iron_bars);
            world.setBlock(i + 4, j - 9, k + 5, Blocks.iron_bars);
            world.setBlock(i + 7, j - 7, k + 5, Blocks.iron_bars);
            world.setBlock(i + 7, j - 8, k + 5, Blocks.iron_bars);
            world.setBlock(i + 7, j - 9, k + 5, Blocks.iron_bars);
            world.setBlock(i + 4, j - 7, k + 6, Blocks.iron_bars);
            world.setBlock(i + 4, j - 8, k + 6, Blocks.iron_bars);
            world.setBlock(i + 4, j - 9, k + 6, Blocks.iron_bars);
            world.setBlock(i + 7, j - 7, k + 6, Blocks.iron_bars);
            world.setBlock(i + 7, j - 8, k + 6, Blocks.iron_bars);
            world.setBlock(i + 7, j - 9, k + 6, Blocks.iron_bars);
            world.setBlock(i + 3, j - 7, k + 4, Blocks.torch);
            world.setBlock(i + 3, j - 7, k + 7, Blocks.torch);
            world.setBlock(i - 3, j - 7, k + 10, Blocks.stone);
            world.setBlock(i - 3, j - 8, k + 10, Blocks.stone);
            world.setBlock(i - 3, j - 9, k + 10, Blocks.stone);
            world.setBlock(i + 0, j - 7, k + 10, Blocks.stone);
            world.setBlock(i + 0, j - 8, k + 10, Blocks.stone);
            world.setBlock(i + 0, j - 9, k + 10, Blocks.stone);
            world.setBlock(i - 3, j - 7, k + 13, Blocks.stone);
            world.setBlock(i - 3, j - 8, k + 13, Blocks.stone);
            world.setBlock(i - 3, j - 9, k + 13, Blocks.stone);
            world.setBlock(i + 0, j - 7, k + 13, Blocks.stone);
            world.setBlock(i + 0, j - 8, k + 13, Blocks.stone);
            world.setBlock(i + 0, j - 9, k + 13, Blocks.stone);
            world.setBlock(i - 2, j - 7, k + 10, Blocks.iron_bars);
            world.setBlock(i - 2, j - 8, k + 10, Blocks.iron_bars);
            world.setBlock(i - 2, j - 9, k + 10, Blocks.iron_bars);
            world.setBlock(i - 1, j - 7, k + 10, Blocks.iron_bars);
            world.setBlock(i - 1, j - 8, k + 10, Blocks.iron_bars);
            world.setBlock(i - 1, j - 9, k + 10, Blocks.iron_bars);
            world.setBlock(i - 2, j - 7, k + 13, Blocks.iron_bars);
            world.setBlock(i - 2, j - 8, k + 13, Blocks.iron_bars);
            world.setBlock(i - 2, j - 9, k + 13, Blocks.iron_bars);
            world.setBlock(i - 1, j - 7, k + 13, Blocks.iron_bars);
            world.setBlock(i - 1, j - 8, k + 13, Blocks.iron_bars);
            world.setBlock(i - 1, j - 9, k + 13, Blocks.iron_bars);
            world.setBlock(i - 3, j - 7, k + 11, Blocks.iron_bars);
            world.setBlock(i - 3, j - 8, k + 11, Blocks.iron_bars);
            world.setBlock(i - 3, j - 9, k + 11, Blocks.iron_bars);
            world.setBlock(i - 0, j - 7, k + 11, Blocks.iron_bars);
            world.setBlock(i - 0, j - 8, k + 11, Blocks.iron_bars);
            world.setBlock(i - 0, j - 9, k + 11, Blocks.iron_bars);
            world.setBlock(i - 3, j - 7, k + 12, Blocks.iron_bars);
            world.setBlock(i - 3, j - 8, k + 12, Blocks.iron_bars);
            world.setBlock(i - 3, j - 9, k + 12, Blocks.iron_bars);
            world.setBlock(i - 0, j - 7, k + 12, Blocks.iron_bars);
            world.setBlock(i - 0, j - 8, k + 12, Blocks.iron_bars);
            world.setBlock(i - 0, j - 9, k + 12, Blocks.iron_bars);
            world.setBlock(i + 1, j - 7, k + 10, Blocks.torch);
            world.setBlock(i + 1, j - 7, k + 13, Blocks.torch);
            world.setBlock(i + 4, j - 7, k + 10, Blocks.stone);
            world.setBlock(i + 4, j - 8, k + 10, Blocks.stone);
            world.setBlock(i + 4, j - 9, k + 10, Blocks.stone);
            world.setBlock(i + 7, j - 7, k + 10, Blocks.stone);
            world.setBlock(i + 7, j - 8, k + 10, Blocks.stone);
            world.setBlock(i + 7, j - 9, k + 10, Blocks.stone);
            world.setBlock(i + 4, j - 7, k + 13, Blocks.stone);
            world.setBlock(i + 4, j - 8, k + 13, Blocks.stone);
            world.setBlock(i + 4, j - 9, k + 13, Blocks.stone);
            world.setBlock(i + 7, j - 7, k + 13, Blocks.stone);
            world.setBlock(i + 7, j - 8, k + 13, Blocks.stone);
            world.setBlock(i + 7, j - 9, k + 13, Blocks.stone);
            world.setBlock(i + 5, j - 7, k + 10, Blocks.iron_bars);
            world.setBlock(i + 5, j - 8, k + 10, Blocks.iron_bars);
            world.setBlock(i + 5, j - 9, k + 10, Blocks.iron_bars);
            world.setBlock(i + 6, j - 7, k + 10, Blocks.iron_bars);
            world.setBlock(i + 6, j - 8, k + 10, Blocks.iron_bars);
            world.setBlock(i + 6, j - 9, k + 10, Blocks.iron_bars);
            world.setBlock(i + 5, j - 7, k + 13, Blocks.iron_bars);
            world.setBlock(i + 5, j - 8, k + 13, Blocks.iron_bars);
            world.setBlock(i + 5, j - 9, k + 13, Blocks.iron_bars);
            world.setBlock(i + 6, j - 7, k + 13, Blocks.iron_bars);
            world.setBlock(i + 6, j - 8, k + 13, Blocks.iron_bars);
            world.setBlock(i + 6, j - 9, k + 13, Blocks.iron_bars);
            world.setBlock(i + 4, j - 7, k + 11, Blocks.iron_bars);
            world.setBlock(i + 4, j - 8, k + 11, Blocks.iron_bars);
            world.setBlock(i + 4, j - 9, k + 11, Blocks.iron_bars);
            world.setBlock(i + 7, j - 7, k + 11, Blocks.iron_bars);
            world.setBlock(i + 7, j - 8, k + 11, Blocks.iron_bars);
            world.setBlock(i + 7, j - 9, k + 11, Blocks.iron_bars);
            world.setBlock(i + 4, j - 7, k + 12, Blocks.iron_bars);
            world.setBlock(i + 4, j - 8, k + 12, Blocks.iron_bars);
            world.setBlock(i + 4, j - 9, k + 12, Blocks.iron_bars);
            world.setBlock(i + 7, j - 7, k + 12, Blocks.iron_bars);
            world.setBlock(i + 7, j - 8, k + 12, Blocks.iron_bars);
            world.setBlock(i + 7, j - 9, k + 12, Blocks.iron_bars);
            world.setBlock(i + 3, j - 7, k + 10, Blocks.torch);
            world.setBlock(i + 3, j - 7, k + 13, Blocks.torch);
            world.setBlock(i + 2, j + 0, k + 2, Blocks.air);

            for(i7 = -9; i7 < 1; ++i7) {
                world.setBlock(i + 2, j + i7, k + 2, Blocks.ladder);
                world.setBlockMetadataWithNotify(i + 2, j + i7, k + 2, 3, 2);
            }

            world.setBlock(i + 3, j - 7, k + 15, Blocks.wall_sign);
            world.setBlockMetadataWithNotify(i + 3, j - 7, k + 15, 2, 2);
            TileEntitySign tileentitysign3 = (TileEntitySign)world.getTileEntity(i + 3, j - 7, k + 15);
            tileentitysign3.signText[0] = "Experiment 1:";
            tileentitysign3.signText[1] = "Slime weakening";
            tileentitysign3.signText[2] = "FAILED";
            world.setBlock(i + 3, j - 8, k + 15, Blocks.wall_sign);
            world.setBlockMetadataWithNotify(i + 3, j - 8, k + 15, 2, 2);
            TileEntitySign tileentitysign4 = (TileEntitySign)world.getTileEntity(i + 3, j - 8, k + 15);
            tileentitysign4.signText[0] = "Experiment 2:";
            tileentitysign4.signText[1] = "Slime melting";
            tileentitysign4.signText[2] = "FAILED";
            world.setBlock(i + 1, j - 7, k + 15, Blocks.wall_sign);
            world.setBlockMetadataWithNotify(i + 1, j - 7, k + 15, 2, 2);
            TileEntitySign tileentitysign5 = (TileEntitySign)world.getTileEntity(i + 1, j - 7, k + 15);
            tileentitysign5.signText[0] = "Experiment 3:";
            tileentitysign5.signText[1] = "Slime exploding";
            tileentitysign5.signText[2] = "FAILED";
            world.setBlock(i + 1, j - 8, k + 15, Blocks.wall_sign);
            world.setBlockMetadataWithNotify(i + 1, j - 8, k + 15, 2, 2);
            TileEntitySign tileentitysign6 = (TileEntitySign)world.getTileEntity(i + 1, j - 8, k + 15);
            tileentitysign6.signText[0] = "Experiment 4:";
            tileentitysign6.signText[1] = "Slime travel";
            tileentitysign6.signText[2] = "Minor success";
            EntityDocSlime docslime = new EntityDocSlime(world);
            docslime.setLocationAndAngles((double)(i + 2), (double)(j - 9), (double)(k + 7), 0.0F, 0.0F);
            world.spawnEntityInWorld(docslime);
            EntityOrangeSlime orslime = new EntityOrangeSlime(world);
            orslime.setLocationAndAngles((double)(i - 2), (double)(j - 9), (double)(k + 6), 0.0F, 0.0F);
            world.spawnEntityInWorld(orslime);
            EntityRedSlime plslime = new EntityRedSlime(world);
            plslime.setLocationAndAngles((double)(i + 5), (double)(j - 9), (double)(k + 6), 0.0F, 0.0F);
            world.spawnEntityInWorld(plslime);
            EntityBlueSlime hislime = new EntityBlueSlime(world);
            hislime.setLocationAndAngles((double)(i + 5), (double)(j - 9), (double)(k + 12), 0.0F, 0.0F);
            world.spawnEntityInWorld(hislime);
            EntityVillagerSlime vslime = new EntityVillagerSlime(world);
            vslime.setLocationAndAngles((double)(i - 2), (double)(j - 9), (double)(k + 2), 0.0F, 0.0F);
            world.spawnEntityInWorld(vslime);
            return true;
        } else {
            return false;
        }
    }
}
