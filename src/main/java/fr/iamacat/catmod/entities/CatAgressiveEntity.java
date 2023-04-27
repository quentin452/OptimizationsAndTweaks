package fr.iamacat.catmod.entities;

import fr.iamacat.catmod.init.RegisterItems;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;

public class CatAgressiveEntity extends EntityMob {

    public CatAgressiveEntity(World p_i1738_1_) {
        super(p_i1738_1_);
        // addTask(0)the smaller the number, the faster the task will be executed
        //this.getNavigator().setBreakDoors(false);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));// does it attack player? 1.0D = 0.5 heart of attack
        //this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this,1.0D));
        //this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));//can attack other mobs?
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));//distance of watching player
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this,true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class,0,true));//can be capable to hit players?
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class,0,false));//can be capable to hit villagers?
        //this.setSize(0.6F,1,8F);//size in block, change this if your mob texture is longer/taller
    }

    protected void applyEntityAttribute(){
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20);
    }

    public Item getDropItem(){
        return RegisterItems.catCoin;
    }
}
