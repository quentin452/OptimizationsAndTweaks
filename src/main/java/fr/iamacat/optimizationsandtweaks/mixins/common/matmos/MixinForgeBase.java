package fr.iamacat.optimizationsandtweaks.mixins.common.matmos;

import eu.ha3.matmos.lib.eu.ha3.mc.haddon.Haddon;
import eu.ha3.matmos.lib.eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.matmos.lib.eu.ha3.mc.haddon.forge.ForgeBase;
import eu.ha3.matmos.lib.eu.ha3.mc.haddon.implem.HaddonUtilityImpl;
import eu.ha3.matmos.lib.eu.ha3.mc.haddon.implem.ProfilerHelper;
import eu.ha3.matmos.lib.eu.ha3.mc.haddon.supporting.*;
import eu.ha3.matmos.lib.eu.ha3.mc.haddon.supporting.event.BlockChangeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Mixin(ForgeBase.class)
public abstract class MixinForgeBase implements OperatorCaster {
    @Shadow
    private static Logger logger;
    @Shadow
    protected final Haddon haddon;
    @Shadow
    protected final boolean shouldTick;
    @Shadow
    protected final boolean suTick;
    @Shadow
    protected final boolean suClientTick;
    @Shadow
    protected final boolean suFrame;
    @Shadow
    protected final boolean suFrameP;
    @Shadow
    protected final boolean suInGame;
    @Shadow
    protected final boolean suSound;
    @Shadow
    protected final boolean suBlockChange;
    @Shadow
    protected final boolean suSoundSetup;
    @Shadow
    protected int tickCounter;
    @Shadow
    protected boolean enableTick;
    @Shadow
    protected boolean enableFrame;
    @Shadow
    private int ticksSinceLastRender = 0;
    @Shadow
    private boolean wasInGame;
    @Shadow
    private Queue<BlockChangeEvent> blockEventQueue = new LinkedBlockingQueue();

    public MixinForgeBase(Haddon haddon) {
        this.haddon = haddon;
        this.suTick = haddon instanceof SupportsTickEvents;
        this.suClientTick = haddon instanceof SupportsClientTickEvents;
        this.suFrame = haddon instanceof SupportsFrameEvents;
        this.suFrameP = haddon instanceof SupportsPlayerFrameEvents;
        this.suInGame = haddon instanceof SupportsInGameChangeEvents;
        this.suBlockChange = haddon instanceof SupportsBlockChangeEvents;
        this.suSound = haddon instanceof SupportsSoundEvents;
        this.suSoundSetup = haddon instanceof SupportsSoundSetupEvents;
        this.shouldTick = this.suTick || this.suFrame;
        haddon.setUtility(new HaddonUtilityImpl() {
            public long getClientTick() {
                return getTicks();
            }
        });
        haddon.setOperator(this);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void onTickLiteLoaderStyle(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock, boolean trueClock) {
        if (trueClock && !this.suClientTick) {
            return;
        }

        if (inGame != this.wasInGame && this.suInGame) {
            ((SupportsInGameChangeEvents) this.haddon).onInGameChange(inGame);
        }
        this.wasInGame = inGame;

        if (!this.shouldTick || !inGame) {
            return;
        }

        Profiler p = minecraft.mcProfiler;
        List<String> profilerSections = ProfilerHelper.goToRoot(p);
        p.startSection(this.haddon.getIdentity().getHaddonName());

        if ((this.enableTick && this.suClientTick && !trueClock) || (this.enableTick && !this.suClientTick && clock)) {
            if (this.suTick) {
                ((SupportsTickEvents) this.haddon).onTick();
            }
            ++this.tickCounter;
        }

        if (this.suBlockChange) {
            while (!this.blockEventQueue.isEmpty()) {
                ((SupportsBlockChangeEvents) this.haddon).onBlockChanged(this.blockEventQueue.remove());
            }
        }

        if (this.enableFrame && !trueClock) {
            if (this.suFrame) {
                ((SupportsFrameEvents) this.haddon).onFrame(partialTicks);
            }
            if (this.suFrameP) {
                for (EntityPlayer ply : this.haddon.getUtility().getClient().getAllPlayers()) {
                    if (ply != null) {
                        ((SupportsPlayerFrameEvents) this.haddon).onFrame(ply, partialTicks);
                    }
                }
            }
        }

        p.endSection();
        ProfilerHelper.startNestedSection(p, profilerSections);
    }

    @Shadow
    public int getTicks() {
        return this.tickCounter;
    }
}
