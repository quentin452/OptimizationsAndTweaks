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
    /**
     * @author
     * @reason
     */
    @Overwrite
    private Packet func_151260_c()
    {
        if (this.myEntity.isDead)
        {
            logger.warn("Fetching addPacket for removed entity");
        }

        Packet pkt = FMLNetworkHandler.getEntitySpawningPacket(this.myEntity);

        if (pkt != null)
        {
            return pkt;
        }
        if (this.myEntity instanceof EntityItem)
        {
            return new S0EPacketSpawnObject(this.myEntity, 2, 1);
        }
        else if (this.myEntity instanceof EntityPlayerMP)
        {
            return new S0CPacketSpawnPlayer((EntityPlayer)this.myEntity);
        }
        else if (this.myEntity instanceof EntityMinecart)
        {
            EntityMinecart entityminecart = (EntityMinecart)this.myEntity;
            return new S0EPacketSpawnObject(this.myEntity, 10, entityminecart.getMinecartType());
        }
        else if (this.myEntity instanceof EntityBoat)
        {
            return new S0EPacketSpawnObject(this.myEntity, 1);
        }
        else if (!(this.myEntity instanceof IAnimals) && !(this.myEntity instanceof EntityDragon))
        {
            if (this.myEntity instanceof EntityFishHook)
            {
                EntityPlayer entityplayer = ((EntityFishHook)this.myEntity).field_146042_b;
                return new S0EPacketSpawnObject(this.myEntity, 90, entityplayer != null ? entityplayer.getEntityId() : this.myEntity.getEntityId());
            }
            else if (this.myEntity instanceof EntityArrow)
            {
                Entity entity = ((EntityArrow)this.myEntity).shootingEntity;
                return new S0EPacketSpawnObject(this.myEntity, 60, entity != null ? entity.getEntityId() : this.myEntity.getEntityId());
            }
            else if (this.myEntity instanceof EntitySnowball)
            {
                return new S0EPacketSpawnObject(this.myEntity, 61);
            }
            else if (this.myEntity instanceof EntityPotion)
            {
                return new S0EPacketSpawnObject(this.myEntity, 73, ((EntityPotion)this.myEntity).getPotionDamage());
            }
            else if (this.myEntity instanceof EntityExpBottle)
            {
                return new S0EPacketSpawnObject(this.myEntity, 75);
            }
            else if (this.myEntity instanceof EntityEnderPearl)
            {
                return new S0EPacketSpawnObject(this.myEntity, 65);
            }
            else if (this.myEntity instanceof EntityEnderEye)
            {
                return new S0EPacketSpawnObject(this.myEntity, 72);
            }
            else if (this.myEntity instanceof EntityFireworkRocket)
            {
                return new S0EPacketSpawnObject(this.myEntity, 76);
            }
            else
            {
                S0EPacketSpawnObject s0epacketspawnobject;

                if (this.myEntity instanceof EntityFireball)
                {
                    EntityFireball entityfireball = (EntityFireball)this.myEntity;
                    s0epacketspawnobject = null;
                    byte b0 = 63;

                    if (this.myEntity instanceof EntitySmallFireball)
                    {
                        b0 = 64;
                    }
                    else if (this.myEntity instanceof EntityWitherSkull)
                    {
                        b0 = 66;
                    }

                    if (entityfireball.shootingEntity != null)
                    {
                        s0epacketspawnobject = new S0EPacketSpawnObject(this.myEntity, b0, ((EntityFireball)this.myEntity).shootingEntity.getEntityId());
                    }
                    else
                    {
                        s0epacketspawnobject = new S0EPacketSpawnObject(this.myEntity, b0, 0);
                    }

                    s0epacketspawnobject.func_149003_d((int)(entityfireball.accelerationX * 8000.0D));
                    s0epacketspawnobject.func_149000_e((int)(entityfireball.accelerationY * 8000.0D));
                    s0epacketspawnobject.func_149007_f((int)(entityfireball.accelerationZ * 8000.0D));
                    return s0epacketspawnobject;
                }
                else if (this.myEntity instanceof EntityEgg)
                {
                    return new S0EPacketSpawnObject(this.myEntity, 62);
                }
                else if (this.myEntity instanceof EntityTNTPrimed)
                {
                    return new S0EPacketSpawnObject(this.myEntity, 50);
                }
                else if (this.myEntity instanceof EntityEnderCrystal)
                {
                    return new S0EPacketSpawnObject(this.myEntity, 51);
                }
                else if (this.myEntity instanceof EntityFallingBlock)
                {
                    EntityFallingBlock entityfallingblock = (EntityFallingBlock)this.myEntity;
                    return new S0EPacketSpawnObject(this.myEntity, 70, Block.getIdFromBlock(entityfallingblock.func_145805_f()) | entityfallingblock.field_145814_a << 16);
                }
                else if (this.myEntity instanceof EntityPainting)
                {
                    return new S10PacketSpawnPainting((EntityPainting)this.myEntity);
                }
                else if (this.myEntity instanceof EntityItemFrame)
                {
                    EntityItemFrame entityitemframe = (EntityItemFrame)this.myEntity;
                    s0epacketspawnobject = new S0EPacketSpawnObject(this.myEntity, 71, entityitemframe.hangingDirection);
                    s0epacketspawnobject.func_148996_a(MathHelper.floor_float((float)(entityitemframe.field_146063_b * 32)));
                    s0epacketspawnobject.func_148995_b(MathHelper.floor_float((float)(entityitemframe.field_146064_c * 32)));
                    s0epacketspawnobject.func_149005_c(MathHelper.floor_float((float)(entityitemframe.field_146062_d * 32)));
                    return s0epacketspawnobject;
                }
                else if (this.myEntity instanceof EntityLeashKnot)
                {
                    EntityLeashKnot entityleashknot = (EntityLeashKnot)this.myEntity;
                    s0epacketspawnobject = new S0EPacketSpawnObject(this.myEntity, 77);
                    s0epacketspawnobject.func_148996_a(MathHelper.floor_float((float)(entityleashknot.field_146063_b * 32)));
                    s0epacketspawnobject.func_148995_b(MathHelper.floor_float((float)(entityleashknot.field_146064_c * 32)));
                    s0epacketspawnobject.func_149005_c(MathHelper.floor_float((float)(entityleashknot.field_146062_d * 32)));
                    return s0epacketspawnobject;
                }
                else if (this.myEntity instanceof EntityXPOrb)
                {
                    return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.myEntity);
                }
                else
                {
                    throw new IllegalArgumentException("Don\'t know how to add " + this.myEntity.getClass() + "!");
                }
            }
        }
        else
        {
            this.lastHeadMotion = MathHelper.floor_float(this.myEntity.getRotationYawHead() * 256.0F / 360.0F);
            return new S0FPacketSpawnMob((EntityLivingBase)this.myEntity);
        }
    }
}
