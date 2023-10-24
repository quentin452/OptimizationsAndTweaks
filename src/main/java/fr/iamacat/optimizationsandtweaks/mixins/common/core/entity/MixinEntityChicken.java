package fr.iamacat.optimizationsandtweaks.mixins.common.core.entity;

import java.util.Iterator;

import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import fr.iamacat.optimizationsandtweaks.utils.multithreadingandtweaks.entity.ai.EntityAIFollowParent2;

@Mixin(EntityChicken.class)
public abstract class MixinEntityChicken extends EntityAnimal {

    public MixinEntityChicken(World p_i1681_1_) {
        super(p_i1681_1_);
    }

    @Inject(at = @At(value = "RETURN"), method = "<init>(Lnet/minecraft/world/World;)V")
    private void modifyTasks(World worldIn, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinEntityChicken) {
            Iterator<EntityAITasks.EntityAITaskEntry> iterator = ((EntityChicken) (Object) this).tasks.taskEntries
                .iterator();
            while (iterator.hasNext()) {
                EntityAITasks.EntityAITaskEntry entry = iterator.next();
                if (entry.action instanceof EntityAIFollowParent) {
                    iterator.remove();
                    break;
                }
            }
            ((EntityChicken) (Object) this).tasks
                .addTask(4, new EntityAIFollowParent2((EntityChicken) (Object) this, 1.1D));
        }
    }

}
