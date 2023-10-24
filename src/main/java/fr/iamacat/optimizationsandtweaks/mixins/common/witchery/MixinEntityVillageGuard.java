package fr.iamacat.optimizationsandtweaks.mixins.common.witchery;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.emoniph.witchery.entity.EntityVillageGuard;
import com.emoniph.witchery.entity.ai.EntityAIAttackOnCollide2;
import com.emoniph.witchery.entity.ai.EntityAIDefendVillageGeneric;

import fr.iamacat.optimizationsandtweaks.utils.multithreadingandtweaks.entity.ai.EntityAIArrowAttack2;

@Mixin(EntityVillageGuard.class)
public abstract class MixinEntityVillageGuard extends EntityCreature
    implements IRangedAttackMob, EntityAIDefendVillageGeneric.IVillageGuard, IEntitySelector {

    @Unique
    private EntityAIArrowAttack2 multithreadingandtweaks$aiArrowAttack = new EntityAIArrowAttack2(
        this,
        1.0,
        20,
        60,
        15.0F);
    @Unique
    private EntityAIAttackOnCollide2 multithreadingandtweaks$aiAttackOnCollide = new EntityAIAttackOnCollide2(
        this,
        EntityPlayer.class,
        1.2,
        false);

    public MixinEntityVillageGuard(World p_i1602_1_) {
        super(p_i1602_1_);
    }

    @Inject(method = "setCombatTask", at = @At("HEAD"), remap = false, cancellable = true)
    public void setCombatTask(CallbackInfo ci) {
        this.tasks.removeTask(this.multithreadingandtweaks$aiAttackOnCollide);
        this.tasks.removeTask(this.multithreadingandtweaks$aiArrowAttack);
        ItemStack itemstack = this.getHeldItem();
        if (itemstack != null && itemstack.getItem() == Items.bow) {
            this.tasks.addTask(4, this.multithreadingandtweaks$aiArrowAttack);
        } else {
            this.tasks.addTask(4, this.multithreadingandtweaks$aiAttackOnCollide);
        }
        ci.cancel();
    }
}
