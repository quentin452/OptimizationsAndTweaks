package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import fr.iamacat.multithreading.utils.multithreadingandtweaks.entity.ai.EntityAITasks2;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving extends EntityLivingBase {
    @Unique
    private World p_i1595_1_;
    @Shadow
    private boolean canPickUpLoot;

    @Unique
    public EntityAITasks2 multithreadingandtweaks$tasks;
    @Unique
    public EntityAITasks2 multithreadingandtweaks$targetTasks;

    public MixinEntityLiving(World p_i1594_1_, World pI15951, EntityAITasks2 tasks, EntityAITasks2 targetTasks) {
        super(p_i1594_1_);
        p_i1595_1_ = pI15951;
        this.multithreadingandtweaks$tasks = tasks;
        this.multithreadingandtweaks$targetTasks = targetTasks;
    }

    @Inject(at = @At(value = "RETURN"), method = "<init>(Lnet/minecraft/world/World;)V")
    private void modifyTasks(World worldIn, CallbackInfo ci) {
        if(MultithreadingandtweaksConfig.enableMixinEntityLiving){
            Iterator iterator = ((EntityLiving) (Object) this).tasks.taskEntries.iterator();
            while (iterator.hasNext()) {
                EntityLiving entry = (EntityLiving) iterator.next();
                if (entry instanceof EntityLiving) {
                    iterator.remove();
                    break;
                }
            }
            this.multithreadingandtweaks$tasks = new EntityAITasks2(p_i1595_1_ != null && p_i1595_1_.theProfiler != null ? p_i1595_1_.theProfiler : null);
            this.multithreadingandtweaks$targetTasks = new EntityAITasks2(p_i1595_1_ != null && p_i1595_1_.theProfiler != null ? p_i1595_1_.theProfiler : null);
        }
    }
    @Unique
    private boolean cachedCanPickUpLoot = false;
    @Unique
    private long lastCheckTime = 0L;
    @Unique
    private static final long CACHE_EXPIRATION_TIME = 1000L;

    @Inject(method = "canPickUpLoot", at = @At("HEAD"), remap = false, cancellable = true)
    public boolean canPickUpLoot(CallbackInfo ci)
    {
        if (MultithreadingandtweaksConfig.enableMixinEntityLiving){
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastCheckTime < CACHE_EXPIRATION_TIME) {
            return cachedCanPickUpLoot;
        } else {
            boolean canPickUpLoot = this.canPickUpLoot;
            cachedCanPickUpLoot = canPickUpLoot;
            lastCheckTime = currentTime;
            return canPickUpLoot;
        }
    }
        ci.cancel();
        return false;
    }
}
