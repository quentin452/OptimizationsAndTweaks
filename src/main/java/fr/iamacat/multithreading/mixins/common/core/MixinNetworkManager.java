package fr.iamacat.multithreading.mixins.common.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(value = NetworkManager.class, remap = false)
public abstract class MixinNetworkManager {

    private static final int MAX_PACKETS_PER_TICK = MultithreadingandtweaksMultithreadingConfig.batchsize; // Adjust to
                                                                                                           // your
                                                                                                           // liking
    private final List<Packet> packetsToSend = Collections.synchronizedList(new ArrayList<Packet>());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private WorldServer worldServer;
    private Thread serverThread;
    private EntityPlayerMP player;

    public INetHandler getNetHandler() {
        throw new UnsupportedOperationException("MixinNetworkManager.getNetHandler() should never be called directly");
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"))
    private void onSendPacket(Packet packetIn, CallbackInfo ci) {
        packetsToSend.add(packetIn);
    }

    @Inject(method = "processReceivedPackets()V", at = @At("HEAD"))
    private void onProcessReceivedPackets(CallbackInfo ci) {
        // Do nothing, we're not interested in the main thread execution of this method.
    }

    @Inject(method = "update()V", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        if (worldServer == null) {
            player = ((NetHandlerPlayServer) this.getNetHandler()).playerEntity;
            worldServer = player.getServerForPlayer();
            serverThread = Thread.currentThread();
        }
        List<Packet> packetList = new ArrayList<Packet>(packetsToSend);
        packetsToSend.clear();
        int numBatches = (int) Math.ceil((double) packetList.size() / MAX_PACKETS_PER_TICK);
        for (int i = 0; i < numBatches; i++) {
            int start = i * MAX_PACKETS_PER_TICK;
            int end = Math.min(start + MAX_PACKETS_PER_TICK, packetList.size());
            List<Packet> batch = packetList.subList(start, end);
            CompletableFuture<Void>[] futures = new CompletableFuture[batch.size()];
            int j = 0;
            for (Packet packet : batch) {
                futures[j] = CompletableFuture.runAsync(() -> {
                    int dimensionId = player.getEntityWorld().provider.dimensionId;
                    for (Object obj : worldServer.playerEntities) {
                        if (obj instanceof EntityPlayerMP) {
                            EntityPlayerMP otherPlayer = (EntityPlayerMP) obj;
                            if (otherPlayer.getEntityWorld().provider.dimensionId == dimensionId) {
                                ((EntityPlayerMP) otherPlayer).playerNetServerHandler.sendPacket(packet);
                            }
                        }
                    }
                }, executorService)
                    .thenRunAsync((Runnable) () -> { serverThread.run(); }, (Executor) serverThread);
                j++;
            }
            CompletableFuture.allOf(futures)
                .join();
        }
    }
}
