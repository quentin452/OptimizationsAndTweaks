package fr.iamacat.multithreading.mixins.common.akatsuki;

import com.akazuki.animation.common.MCACommonLibrary.IMCAnimatedEntity;
import com.akazuki.animation.common.MCACommonLibrary.animation.AnimTickHandler;
import com.akazuki.animation.common.MCACommonLibrary.animation.AnimationHandler;
import com.akazuki.animation.common.MCACommonLibrary.animation.Channel;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@Mixin(AnimationHandler.class)
public class MixinAnimationHandler {
    @Shadow
    public static AnimTickHandler animTickHandler;
    @Shadow
    private IMCAnimatedEntity animatedEntity;
    @Shadow
    public ArrayList<Channel> animCurrentChannels = new ArrayList();
    @Shadow
    public HashMap<String, Long> animPrevTime = new HashMap();
    @Shadow
    public HashMap<String, Float> animCurrentFrame = new HashMap();
    @Shadow
    private HashMap<String, ArrayList<String>> animationEvents = new HashMap();
    /**
     * @author
     * @reason
     */
    @Inject(method = "animationsUpdate", at = @At("HEAD"), remap = false, cancellable = true)
    public void animationsUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinAnimationHandler){
        Iterator<Channel> it = this.animCurrentChannels.iterator();

        while(it.hasNext()) {
            Channel anim = it.next();
            float prevFrame = this.animCurrentFrame.get(anim.name);
            boolean animStatus = updateAnimation(this.animatedEntity, anim, this.animPrevTime, this.animCurrentFrame);
            if (this.animCurrentFrame.get(anim.name) != null) {
                this.fireAnimationEvent(anim, prevFrame, this.animCurrentFrame.get(anim.name));
            }

            if (!animStatus) {
                it.remove();
                this.animPrevTime.remove(anim.name);
                this.animCurrentFrame.remove(anim.name);
                this.animationEvents.get(anim.name).clear();
            }
        }
        ci.cancel();
        }
    }
    @Unique
    private static boolean updateAnimation(IMCAnimatedEntity entity, Channel channel, HashMap<String, Long> prevTimeAnim, HashMap<String, Float> prevFrameAnim) {
        long prevTime;
        if (!FMLCommonHandler.instance().getEffectiveSide().isServer() && (!FMLCommonHandler.instance().getEffectiveSide().isClient() || isGamePaused())) {
            prevTime = System.nanoTime();
            prevTimeAnim.put(channel.name, prevTime);
            return true;
        } else if (channel.mode != 3) {
            prevTime = prevTimeAnim.get(channel.name);
            float prevFrame = prevFrameAnim.get(channel.name);
            long currentTime = System.nanoTime();
            double deltaTime = (double)(currentTime - prevTime) / 1.0E9;
            float numberOfSkippedFrames = (float)(deltaTime * (double)channel.fps);
            float currentFrame = prevFrame + numberOfSkippedFrames;
            if (currentFrame < (float)(channel.totalFrames - 1)) {
                prevTimeAnim.put(channel.name, currentTime);
                prevFrameAnim.put(channel.name, currentFrame);
                return true;
            } else if (channel.mode == 1) {
                prevTimeAnim.put(channel.name, currentTime);
                prevFrameAnim.put(channel.name, 0.0F);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    @Unique
    private void fireAnimationEvent(Channel anim, float prevFrame, float frame) {
        if (isWorldRemote(this.animatedEntity)) {
            this.fireAnimationEventClientSide(anim, prevFrame, frame);
        } else {
            this.fireAnimationEventServerSide(anim, prevFrame, frame);
        }

    }
    @Unique
    private static boolean isWorldRemote(IMCAnimatedEntity animatedEntity) {
        return ((Entity)animatedEntity).worldObj.isRemote;
    }

    @Unique
    public void fireAnimationEventServerSide(Channel var1, float var2, float var3) {

    }

    @Unique
    @SideOnly(Side.CLIENT)
    public void fireAnimationEventClientSide(Channel var1, float var2, float var3) {

    }
    @Unique
    @SideOnly(Side.CLIENT)
    private static boolean isGamePaused() {
        Minecraft MC = Minecraft.getMinecraft();
        return MC.isSingleplayer() && MC.currentScreen != null && MC.currentScreen.doesGuiPauseGame() && !MC.getIntegratedServer().getPublic();
    }
}
