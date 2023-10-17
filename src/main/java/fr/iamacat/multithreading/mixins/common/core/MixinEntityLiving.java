package fr.iamacat.multithreading.mixins.common.core;

import cpw.mods.fml.common.eventhandler.Event;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityLiving.class, priority = 999)
public abstract class MixinEntityLiving extends EntityLivingBase {
    @Unique
    private EntityLiving entityLiving;
    @Shadow
    private boolean persistenceRequired;
    @Shadow
    private EntitySenses senses;
    @Shadow
    public final EntityAITasks tasks;
    @Shadow
    public final EntityAITasks targetTasks;
    @Shadow
    private PathNavigate navigator;
    @Shadow
    private EntityLookHelper lookHelper;
    @Shadow
    private EntityMoveHelper moveHelper;

    /** Entity jumping helper */
    @Shadow
    private EntityJumpHelper jumpHelper;
    @Shadow
    private EntityBodyHelper bodyHelper;

    public MixinEntityLiving(World p_i1595_1_, EntityAITasks tasks, EntityAITasks targetTasks, EntityBodyHelper bodyHelper)
    {
        super(p_i1595_1_);
        this.tasks = tasks;
        this.targetTasks = targetTasks;
        this.bodyHelper = bodyHelper;
    }

    /**
     * @author iamacatfr
     * @reason r
     */
    @Overwrite
    protected void updateAITasks()
    {
        if (MultithreadingandtweaksConfig.enableMixinEntityLiving){
        ++this.entityAge;
        this.worldObj.theProfiler.startSection("checkDespawn");
        this.despawnEntity();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("sensing");
        this.senses.clearSensingCache();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("targetSelector");
        this.targetTasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("goalSelector");
        this.tasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("navigation");
        this.navigator.onUpdateNavigation();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("mob tick");
        this.updateAITick();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("controls");
        this.worldObj.theProfiler.startSection("move");
        this.moveHelper.onUpdateMoveHelper();
        this.worldObj.theProfiler.endStartSection("look");
        this.lookHelper.onUpdateLook();
        this.worldObj.theProfiler.endStartSection("jump");
        this.jumpHelper.doJump();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.endSection();
        }
    }

    /**
     * @author iamacatfr
     * @reason r
     */
    @Overwrite
    protected void despawnEntity() {
        if (MultithreadingandtweaksConfig.enableMixinEntityLiving){
            Event.Result result;

            if (this.persistenceRequired) {
                this.entityAge = 0;
                return;
            }

            if ((this.entityAge & 0x1F) == 0x1F && (result = ForgeEventFactory.canEntityDespawn(entityLiving)) != Event.Result.DEFAULT) {
                if (result == Event.Result.DENY) {
                    this.entityAge = 0;
                } else {
                    this.setDead();
                }
                return;
            }

            EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, -1.0D);

            if (entityplayer != null) {
                double d0 = entityplayer.posX - this.posX;
                double d1 = entityplayer.posY - this.posY;
                double d2 = entityplayer.posZ - this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (this.multithreadingandtweaks$canDespawn()) {
                    if (d3 > 16384.0D || (this.entityAge > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0D)) {
                        this.setDead();
                    }
                }

                if (d3 < 1024.0D) {
                    this.entityAge = 0;
                }
            }
        }
    }
    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Unique
    protected boolean multithreadingandtweaks$canDespawn()
    {
        return true;
    }
}
