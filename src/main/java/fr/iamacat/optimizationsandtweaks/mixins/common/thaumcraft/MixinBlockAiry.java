package fr.iamacat.optimizationsandtweaks.mixins.common.thaumcraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.blocks.BlockAiry;

@Mixin(BlockAiry.class)
public class MixinBlockAiry {
    /**
     * @author
     * @reason fix ghost EntityItem caused by onEntityCollidedWithBlock from Aura Node to prevent lags
     */
    @Overwrite(remap = false)
    public void func_149670_a(World world, int x, int y, int z, Entity entity) {
        int metadata = world.getBlockMetadata(x, y, z);

        if (metadata == 10) {
            entity.attackEntityFrom(DamageSource.magic, (float)(1 + world.rand.nextInt(2)));
            entity.motionX *= 0.8;
            entity.motionZ *= 0.8;

            if (!world.isRemote && world.rand.nextInt(100) == 0) {

                int blockDamage = 10;
                world.destroyBlockInWorldPartially(world.provider.dimensionId, x, y, z, blockDamage);

            }
        } else if (metadata == 11 && !(entity instanceof IEldritchMob)) {
            if (world.rand.nextInt(100) == 0) {
                entity.attackEntityFrom(DamageSource.wither, 1.0F);
            }

            entity.motionX *= 0.66;
            entity.motionZ *= 0.66;

            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).addExhaustion(0.05F);
            }

            if (entity instanceof EntityLivingBase) {
                PotionEffect pe = new PotionEffect(Potion.weakness.id, 100, 1, true);
                ((EntityLivingBase) entity).addPotionEffect(pe);
            }
        }
    }
}
