package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.*;
import net.minecraft.world.storage.ISaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World {

    public MixinWorldServer(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_, WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }

    /**
     * Runs a single tick for the world
     */
    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo info)
    {
        if (MultithreadingandtweaksConfig.enableMixinWorldServer){
        boolean isHardcore = this.getWorldInfo().isHardcoreModeEnabled();

            if (isHardcore && this.difficultySetting != EnumDifficulty.HARD) {
                this.difficultySetting = EnumDifficulty.HARD;
            }
        }
    }
}

