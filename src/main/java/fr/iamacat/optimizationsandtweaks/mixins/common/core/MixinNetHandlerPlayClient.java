package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S0FPacketSpawnMob;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Tries to fix a "Default value cannot be lower than minimum value" and a "Server attempted to spawn an unknown
 * entity" crash, both caused by thread-unsafety in NetHandlerPlayClient.
 */
@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Shadow
    private Minecraft gameController;
    @Shadow
    private WorldClient clientWorldController;

    /**
     * @author quentin452
     * @reason Fix Server attempted to spawn an unknown entity caused by thread unsafety from NetHandlerPlayClient class
     */
    @Overwrite
    public synchronized void handleSpawnMob(S0FPacketSpawnMob packetIn) {
        double d0 = (double) packetIn.func_149023_f() / 32.0D;
        double d1 = (double) packetIn.func_149034_g() / 32.0D;
        double d2 = (double) packetIn.func_149029_h() / 32.0D;
        float f = (float) (packetIn.func_149028_l() * 360) / 256.0F;
        float f1 = (float) (packetIn.func_149030_m() * 360) / 256.0F;
        net.minecraft.entity.Entity spawned = EntityList
            .createEntityByID(packetIn.func_149025_e(), this.gameController.theWorld);
        if (spawned == null) {
            cpw.mods.fml.common.FMLLog.info(
                "Server attempted to spawn an unknown entity using ID: {0} at ({1}, {2}, {3}) Skipping!",
                packetIn.func_149025_e(),
                d0,
                d1,
                d2);
            return;
        }
        // A mod (e.g. OreSpawn's UltimateFishHook) can wrongly send a non-living entity via the spawn-mob packet;
        // the raw vanilla cast to EntityLivingBase then crashes the client (issue #127). Skip it instead.
        if (!(spawned instanceof EntityLivingBase)) {
            cpw.mods.fml.common.FMLLog.info(
                "Server attempted to spawn a non-living entity ({0}, ID {1}) via the spawn-mob packet at ({2}, {3}, {4}); skipping!",
                spawned.getClass()
                    .getName(),
                packetIn.func_149025_e(),
                d0,
                d1,
                d2);
            return;
        }
        EntityLivingBase entitylivingbase = (EntityLivingBase) spawned;
        entitylivingbase.serverPosX = packetIn.func_149023_f();
        entitylivingbase.serverPosY = packetIn.func_149034_g();
        entitylivingbase.serverPosZ = packetIn.func_149029_h();
        entitylivingbase.rotationYawHead = (float) (packetIn.func_149032_n() * 360) / 256.0F;
        Entity[] aentity = entitylivingbase.getParts();

        if (aentity != null) {
            int i = packetIn.func_149024_d() - entitylivingbase.getEntityId();

            for (Entity entity : aentity) {
                entity.setEntityId(entity.getEntityId() + i);
            }
        }

        entitylivingbase.setEntityId(packetIn.func_149024_d());
        entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
        entitylivingbase.motionX = (float) packetIn.func_149026_i() / 8000.0F;
        entitylivingbase.motionY = (float) packetIn.func_149033_j() / 8000.0F;
        entitylivingbase.motionZ = (float) packetIn.func_149031_k() / 8000.0F;
        this.clientWorldController.addEntityToWorld(packetIn.func_149024_d(), entitylivingbase);
        List list = packetIn.func_149027_c();

        if (list != null) {
            entitylivingbase.getDataWatcher()
                .updateWatchedObjectsFromList(list);
        }
    }
}
