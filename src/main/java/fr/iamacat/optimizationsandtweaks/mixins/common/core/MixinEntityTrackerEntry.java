package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;

import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry {

    @Shadow
    public Set trackingPlayers = new HashSet();

    @Shadow
    public Entity myEntity;
    @Shadow
    public int lastHeadMotion;

    @Shadow
    private static final Logger logger = LogManager.getLogger();
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void sendMetadataToAllAssociatedPlayers() {
        DataWatcher datawatcher = this.myEntity.getDataWatcher();

        if (datawatcher.hasChanges()) {
            this.func_151261_b(new S1CPacketEntityMetadata(this.myEntity.getEntityId(), datawatcher, false));
        }

        if (this.myEntity instanceof EntityLivingBase) {
            EntityLivingBase livingEntity = (EntityLivingBase) this.myEntity;
            ServersideAttributeMap serversideattributemap = (ServersideAttributeMap) livingEntity.getAttributeMap();
            Set<IAttributeInstance> attributeInstanceSet = serversideattributemap.getAttributeInstanceSet();

            if (!attributeInstanceSet.isEmpty()) {
                this.func_151261_b(new S20PacketEntityProperties(this.myEntity.getEntityId(), attributeInstanceSet));
                attributeInstanceSet.clear();
            }
        }
    }

    @Shadow
    public void func_151261_b(Packet p_151261_1_) {
        this.func_151259_a(p_151261_1_);

        if (this.myEntity instanceof EntityPlayerMP) {
            ((EntityPlayerMP) this.myEntity).playerNetServerHandler.sendPacket(p_151261_1_);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void func_151259_a(Packet p_151259_1_) {

        for (Object trackingPlayer : this.trackingPlayers) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) trackingPlayer;
            entityplayermp.playerNetServerHandler.sendPacket(p_151259_1_);
        }
    }
}
