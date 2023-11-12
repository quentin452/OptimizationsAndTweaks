package fr.iamacat.optimizationsandtweaks.mixins.common.advancedgenetics;

import com.advGenetics.Net.Packet.AbstractPacket;
import com.advGenetics.Net.PacketPipeline;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@ChannelHandler.Sharable
@Mixin(PacketPipeline.class)
public class MixinPacketPipeline extends MessageToMessageCodec<FMLProxyPacket, AbstractPacket> {
    @Shadow
    private EnumMap<Side, FMLEmbeddedChannel> channels;
    @Unique
    private List<Class<? extends AbstractPacket>> multithreadingandtweaks$packets = new ArrayList<>();
    @Shadow
    private boolean isPostInitialised = false;


    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean registerPacket(Class<? extends AbstractPacket> clazz) {
        if(multithreadingandtweaks$packets.size() > 256) {
            return false;
        }
        if(multithreadingandtweaks$packets.contains(clazz)) {
            return false;
        }
        if(isPostInitialised) {
            return false;
        }
        multithreadingandtweaks$packets.add(clazz);
        return true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected void encode(ChannelHandlerContext ctx, AbstractPacket msg, List<Object> out) throws Exception {
        ByteBuf buffer = Unpooled.buffer();
        Class<? extends AbstractPacket> clazz = msg.getClass();
        if (!this.multithreadingandtweaks$packets.contains(msg.getClass())) {
            throw new NullPointerException("No Packet Registered for: " + msg.getClass().getCanonicalName());
        } else {
            byte discriminator = (byte)this.multithreadingandtweaks$packets.indexOf(clazz);
            buffer.writeByte(discriminator);
            msg.encodeInto(ctx, buffer);
            FMLProxyPacket proxyPacket = new FMLProxyPacket(buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
            out.add(proxyPacket);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
        ByteBuf payload = msg.payload();
        byte discriminator = payload.readByte();

        // Check if the list is empty or if the discriminator is out of bounds
        if (multithreadingandtweaks$packets.isEmpty() || discriminator >= multithreadingandtweaks$packets.size()) {
            // Handle the error or log a message
            return;
        }

        Class<? extends AbstractPacket> clazz = this.multithreadingandtweaks$packets.get(discriminator);

        if (clazz == null) {
            throw new NullPointerException("No packet registered for discriminator: " + discriminator);
        }

        AbstractPacket pkt = clazz.getDeclaredConstructor().newInstance();
        ByteBuf stream = payload.slice();
        pkt.decodeInto(ctx, stream);

        EntityPlayer player;

        Side effectiveSide = FMLCommonHandler.instance().getEffectiveSide();

        if (effectiveSide == Side.CLIENT) {
            player = this.getClientPlayer();
            pkt.handleClientSide(payload.slice(), pkt, player);
        } else if (effectiveSide == Side.SERVER) {
            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            player = ((NetHandlerPlayServer) netHandler).playerEntity;
            pkt.handleServerSide(payload.slice(), pkt, player);
        }

        // Clear the packets after processing
        clearPackets();
        out.add(pkt);
    }


    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void initalise() {
        this.channels = NetworkRegistry.INSTANCE.newChannel("Advanced Genetics Network", this);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void postInitialise() {
        if (!this.isPostInitialised) {
            this.isPostInitialised = true;
            this.multithreadingandtweaks$packets.sort((clazz1, clazz2) -> {
                int com = String.CASE_INSENSITIVE_ORDER.compare(clazz1.getCanonicalName(), clazz2.getCanonicalName());
                if (com == 0) {
                    com = clazz1.getCanonicalName().compareTo(clazz2.getCanonicalName());
                }

                return com;
            });
        }
    }



    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SideOnly(Side.CLIENT)
    private EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void sendToAll(AbstractPacket message) {
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        this.channels.get(Side.SERVER).writeAndFlush(message);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void sendTo(AbstractPacket message, EntityPlayerMP player) {
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        this.channels.get(Side.SERVER).writeAndFlush(message);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void sendToAllAround(AbstractPacket message, NetworkRegistry.TargetPoint point) {
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        this.channels.get(Side.SERVER).writeAndFlush(message);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void sendToDimension(AbstractPacket message, int dimensionId) {
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
        this.channels.get(Side.SERVER).writeAndFlush(message);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void sendToServer(AbstractPacket message) {
        this.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        this.channels.get(Side.CLIENT).writeAndFlush(message);
    }
    public void clearPackets() {
        multithreadingandtweaks$packets.clear();
    }
}
