package fr.iamacat.optimizationsandtweaks.mixins.common.netherlicious;

import DelirusCrux.Netherlicious.Common.BlockItemUtility.ModBlocks;
import DelirusCrux.Netherlicious.Common.Entities.Passive.EntityHoglin;
import DelirusCrux.Netherlicious.Common.Entities.Passive.EntityPiglin;
import DelirusCrux.Netherlicious.Utility.NetherliciousEventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NetherliciousEventHandler.class)
public class MixinNetherliciousEventHandler {
    @Shadow
    public static final NetherliciousEventHandler INSTANCE = new NetherliciousEventHandler();
    @Shadow
    public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setFireDamage();
    @Shadow
    public static final DamageSource BAD_AIR = (new DamageSource("badAir")).setDamageBypassesArmor();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.entityLiving;
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;

        if (!entity.worldObj.isRemote && !entity.isSneaking() && entity.onGround) {
            optimizationsAndTweaks$checkBlockEffects(entity, x, y, z, ModBlocks.MagmaBlock, HOT_FLOOR, EntityPiglin.class, EntityHoglin.class);

            optimizationsAndTweaks$checkBlockDamageAndPotion(entity, x, y, z, ModBlocks.BlackstoneVent, 4, 9, HOT_FLOOR);
            optimizationsAndTweaks$checkBlockPotion(entity, x, y, z, ModBlocks.BlackstoneVent, 4, Potion.blindness, 60);
            optimizationsAndTweaks$checkBlockPotion(entity, x, y, z, ModBlocks.BlackstoneVent, 9, Potion.blindness, 60);

            optimizationsAndTweaks$checkBlockDamageAndPotion(entity, x, y, z, ModBlocks.BasaltVent, 4, 9, HOT_FLOOR);
            optimizationsAndTweaks$checkBlockPotion(entity, x, y, z, ModBlocks.BasaltVent, 4, Potion.blindness, 60);
            optimizationsAndTweaks$checkBlockPotion(entity, x, y, z, ModBlocks.BasaltVent, 9, Potion.blindness, 60);

            optimizationsAndTweaks$checkBlockDamageAndPotion(entity, x, y, z, ModBlocks.NetherrackVent, 4, 9, HOT_FLOOR);
            optimizationsAndTweaks$checkBlockPotion(entity, x, y, z, ModBlocks.NetherrackVent, 4, Potion.blindness, 60);
            optimizationsAndTweaks$checkBlockPotion(entity, x, y, z, ModBlocks.NetherrackVent, 9, Potion.blindness, 60);
        }
    }
    @Unique
    private void optimizationsAndTweaks$checkBlockEffects(Entity entity, double x, double y, double z, Block block, DamageSource damageSource, Class<?>... excludedEntities) {
        if (!optimizationsAndTweaks$isExcludedEntity(entity, excludedEntities) && entity.worldObj.getBlock(MathHelper.floor_double(x), (int) (y - 0.45), MathHelper.floor_double(z)) == block) {
            entity.attackEntityFrom(damageSource, 1.0F);
        }
    }
    @Unique
    private void optimizationsAndTweaks$checkBlockDamageAndPotion(Entity entity, double x, double y, double z, Block block, int damageMeta, int potionMeta, DamageSource damageSource) {
        if (entity.worldObj.getBlock(MathHelper.floor_double(x), (int) (y - 0.45), MathHelper.floor_double(z)) == block &&
            entity.worldObj.getBlockMetadata(MathHelper.floor_double(x), (int) (y - 0.45), MathHelper.floor_double(z)) == damageMeta) {
            entity.attackEntityFrom(damageSource, 1.0F);
        }
    }
    @Unique
    private void optimizationsAndTweaks$checkBlockPotion(Entity entity, double x, double y, double z, Block block, int potionMeta, Potion potion, int duration) {

        if(entity instanceof EntityLivingBase) {

            EntityLivingBase livingEntity = (EntityLivingBase)entity;

            if (entity.worldObj.getBlock(MathHelper.floor_double(x), (int) (y - 0.45), MathHelper.floor_double(z)) == block &&
                entity.worldObj.getBlockMetadata(MathHelper.floor_double(x), (int) (y - 0.45), MathHelper.floor_double(z)) == potionMeta) {

                livingEntity.addPotionEffect(new PotionEffect(potion.getId(), duration, 0));

            }

        }

    }
    @Unique
    private boolean optimizationsAndTweaks$isExcludedEntity(Entity entity, Class<?>... excludedEntities) {
        for (Class<?> excludedEntity : excludedEntities) {
            if (excludedEntity.isInstance(entity)) {
                return true;
            }
        }
        return false;
    }
}
