package fr.iamacat.optimizationsandtweaks.mixins.common.blocklings;

import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.blocklings.entity.EntityBlockling;
import com.blocklings.items.ItemBlockling;

@Mixin(ItemBlockling.class)
public class MixinItemBlockling extends Item {

    /**
     * @author iamacatfr
     * @reason add null checks
     */
    @Overwrite(remap = false)
    public String func_77653_i(ItemStack itemStack) {
        if (itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey("name", 8)) {
            return EnumChatFormatting.GOLD + itemStack.stackTagCompound.getString("name");
        } else {
            return EnumChatFormatting.GOLD + "Blockling";
        }
    }

    /**
     * @author iamacatfr
     * @reason add null checks
     */
    @Overwrite(remap = false)
    public ItemStack func_77659_a(ItemStack itemStack, World world, EntityPlayer player) {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (movingobjectposition == null || itemStack.stackTagCompound == null) {
            return itemStack;
        }

        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k = movingobjectposition.blockZ;
            if (!world.canMineBlock(player, i, j, k)
                || !player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemStack)) {
                return itemStack;
            }

            EntityBlockling b;
            if (!world.isRemote && world.getBlock(i, j, k) instanceof BlockLiquid) {
                b = new EntityBlockling(
                    world,
                    itemStack.stackTagCompound.getInteger("level"),
                    itemStack.stackTagCompound.getInteger("xp"),
                    itemStack.stackTagCompound.getInteger("upgrade"),
                    itemStack.stackTagCompound.getInteger("special"),
                    itemStack.stackTagCompound.getString("name"),
                    player);
                b.setLocationAndAngles(i, j, k, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
                b.onSpawnWithEgg(null);
                world.spawnEntityInWorld(b);
                world.playSoundAtEntity(b, "fireworks.twinkle", 1.0F, 1.0F);
                --itemStack.stackSize;
                if (!player.capabilities.isCreativeMode) {
                    player.setHealth(player.getHealth() - 5.0F);
                }

                if (!player.capabilities.isCreativeMode) {
                    player.getFoodStats()
                        .setFoodLevel(
                            player.getFoodStats()
                                .getFoodLevel() - 5);
                }
            } else if (!world.isRemote) {
                b = new EntityBlockling(
                    world,
                    itemStack.stackTagCompound.getInteger("level"),
                    itemStack.stackTagCompound.getInteger("xp"),
                    itemStack.stackTagCompound.getInteger("upgrade"),
                    itemStack.stackTagCompound.getInteger("special"),
                    itemStack.stackTagCompound.getString("name"),
                    player);
                b.setLocationAndAngles(
                    (float) i + 0.5F,
                    j + 1,
                    (float) k + 0.5F,
                    MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F),
                    0.0F);
                b.onSpawnWithEgg(null);
                world.spawnEntityInWorld(b);
                world.playSoundAtEntity(b, "fireworks.twinkle", 1.0F, 1.0F);
                --itemStack.stackSize;
                if (!player.capabilities.isCreativeMode) {
                    player.setHealth(player.getHealth() - 5.0F);
                }

                if (!player.capabilities.isCreativeMode) {
                    player.getFoodStats()
                        .setFoodLevel(
                            player.getFoodStats()
                                .getFoodLevel() - 5);
                }
            }
        }

        return itemStack;
    }
}
