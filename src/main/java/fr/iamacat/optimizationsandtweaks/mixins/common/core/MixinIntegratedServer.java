package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MinecraftServer {

    @Shadow
    private static final Logger logger = LogManager.getLogger();

    @Shadow
    private final Minecraft mc;
    @Shadow
    private final WorldSettings theWorldSettings;
    @Shadow
    private boolean isGamePaused;
    @Shadow
    private boolean isPublic;
    @Shadow
    private ThreadLanServerPing lanServerPing;

    public MixinIntegratedServer(Minecraft p_i1317_1_, String p_i1317_2_, String p_i1317_3_, WorldSettings p_i1317_4_) {
        super(new File(p_i1317_1_.mcDataDir, "saves"), p_i1317_1_.getProxy());
        this.setServerOwner(
            p_i1317_1_.getSession()
                .getUsername());
        this.setFolderName(p_i1317_2_);
        this.setWorldName(p_i1317_3_);
        this.setDemo(p_i1317_1_.isDemo());
        this.canCreateBonusChest(p_i1317_4_.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.mc = p_i1317_1_;
        this.theWorldSettings = p_i1317_4_;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
        boolean flag = this.isGamePaused;
        this.isGamePaused = Minecraft.getMinecraft()
            .getNetHandler() != null && Minecraft.getMinecraft()
                .isGamePaused();

        if (!flag && this.isGamePaused) {
            logger.info("Saving and pausing game...");
            this.getConfigurationManager()
                .saveAllPlayerData();
            this.saveAllWorlds(false);
        }

        if (!this.isGamePaused) {
            super.tick();

            if (this.mc.gameSettings.renderDistanceChunks != this.getConfigurationManager()
                .getViewDistance()) {
                logger.info(
                    "Changing view distance to {}, from {}",
                    this.mc.gameSettings.renderDistanceChunks,
                    this.getConfigurationManager()
                        .getViewDistance());
                this.getConfigurationManager()
                    .func_152611_a(this.mc.gameSettings.renderDistanceChunks);
            }
        }
    }
}
