package fr.iamacat.multithreading.mixins.common.core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S20PacketEntityProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry {

    @Shadow
    private static final Logger logger = LogManager.getLogger();
    @Shadow
    public Entity myEntity;
    @Shadow
    public int blocksDistanceThreshold;
    /** check for sync when ticks % updateFrequency==0 */
    @Shadow
    public int updateFrequency;
    @Shadow
    public int lastScaledXPosition;
    @Shadow
    public int lastScaledYPosition;
    @Shadow
    public int lastScaledZPosition;
    @Shadow
    public int lastYaw;
    @Shadow
    public int lastPitch;
    @Shadow
    public int lastHeadMotion;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public int ticks;
    @Shadow
    private double posX;
    @Shadow
    private double posY;
    @Shadow
    private double posZ;
    /** set to true on first sendLocationToClients */
    @Shadow
    private boolean isDataInitialized;
    @Shadow
    private boolean sendVelocityUpdates;
    /**
     * every 400 ticks a full teleport packet is sent, rather than just a "move me +x" command, so that position
     * remains fully synced.
     */
    @Shadow
    private int ticksSinceLastForcedTeleport;
    @Shadow
    private Entity field_85178_v;
    @Shadow
    private boolean ridingEntity;
    @Shadow
    public boolean playerEntitiesUpdated;
    /** Holds references to all the players that are currently receiving position updates for this entity. */
    @Shadow
    public Set trackingPlayers = new HashSet();

    private final ExecutorService packetExecutor = Executors
        .newFixedThreadPool(MultithreadingandtweaksConfig.numberofcpus);

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void sendMetadataToAllAssociatedPlayers() {
        if (MultithreadingandtweaksConfig.enableMixinEntityTrackerEntry) {
            DataWatcher datawatcher = this.myEntity.getDataWatcher();

            if (datawatcher.hasChanges()) {
                this.packetExecutor.submit(
                    () -> this
                        .func_151261_b(new S1CPacketEntityMetadata(this.myEntity.getEntityId(), datawatcher, false)));
            }

            if (this.myEntity instanceof EntityLivingBase) {
                ServersideAttributeMap serversideattributemap = (ServersideAttributeMap) ((EntityLivingBase) this.myEntity)
                    .getAttributeMap();
                Set set = serversideattributemap.getAttributeInstanceSet();

                if (!set.isEmpty()) {
                    this.packetExecutor.submit(
                        () -> this.func_151261_b(new S20PacketEntityProperties(this.myEntity.getEntityId(), set)));
                }

                set.clear();
            }
        }
    }

    @Unique
    public void func_151261_b(Packet p_151261_1_) {
        this.func_151259_a(p_151261_1_);

        if (this.myEntity instanceof EntityPlayerMP) {
            ((EntityPlayerMP) this.myEntity).playerNetServerHandler.sendPacket(p_151261_1_);
        }
    }

    @Unique
    public void func_151259_a(Packet p_151259_1_) {

        for (Object trackingPlayer : this.trackingPlayers) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) trackingPlayer;
            this.packetExecutor.submit(() -> entityplayermp.playerNetServerHandler.sendPacket(p_151259_1_));
        }
    }
}
