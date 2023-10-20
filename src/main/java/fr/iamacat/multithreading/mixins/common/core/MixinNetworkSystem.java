package fr.iamacat.multithreading.mixins.common.core;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

@Mixin(NetworkSystem.class)
public class MixinNetworkSystem {
    @Shadow
    private static final Logger logger = LogManager.getLogger();

    @Shadow
    private final List networkManagers = Collections.synchronizedList(new ArrayList());
    /**
     * Will try to process the packets received by each NetworkManager, gracefully manage processing failures and cleans
     * up dead connections
     */
    @Inject(method = "networkTick", at = @At("HEAD"), cancellable = true)
    public void networkTick(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinNetworkSystem) {
            synchronized (this.networkManagers) {
                Iterator iterator = this.networkManagers.iterator();
                List<NetworkManager> toRemove = new ArrayList<>();

                while (iterator.hasNext()) {
                    final NetworkManager networkmanager = (NetworkManager) iterator.next();

                    if (!networkmanager.isChannelOpen()) {
                        iterator.remove();
                        ChatComponentText disconnectMessage = (ChatComponentText) networkmanager.getExitMessage();
                        if (disconnectMessage == null) {
                            disconnectMessage = new ChatComponentText("Disconnected");
                        }

                        networkmanager.getNetHandler().onDisconnect(disconnectMessage);
                    } else {
                        try {
                            networkmanager.processReceivedPackets();
                        } catch (Exception exception) {
                            if (networkmanager.isLocalChannel()) {
                                CrashReport crashreport = CrashReport.makeCrashReport(exception, "Ticking memory connection");
                                CrashReportCategory category = crashreport.makeCategory("Ticking connection");
                                category.addCrashSection("Connection", (Callable<String>) networkmanager::toString);
                                throw new ReportedException(crashreport);
                            }

                            logger.warn("Failed to handle packet for " + networkmanager.getSocketAddress(), exception);
                            ChatComponentText errorText = new ChatComponentText("Internal server error");
                            networkmanager.scheduleOutboundPacket(new S40PacketDisconnect(errorText), p_operationComplete_1 -> {
                                networkmanager.closeChannel(errorText);
                                networkmanager.disableAutoRead();
                            });
                        }
                    }
                }
            }
        }

        ci.cancel();
    }
}
