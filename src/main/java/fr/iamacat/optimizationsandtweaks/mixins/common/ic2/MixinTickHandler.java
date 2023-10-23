package fr.iamacat.optimizationsandtweaks.mixins.common.ic2;

import java.lang.reflect.Field;
import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.TickHandler;
import ic2.core.WorldData;
import ic2.core.energy.EnergyNetGlobal;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import ic2.core.util.Util;

@Mixin(TickHandler.class)
public class MixinTickHandler {

    @Shadow
    private static final boolean debugTickCallback = System.getProperty("ic2.debugtickcallback") != null;
    @Unique
    private static WeakHashMap<ITickCallback, Throwable> multithreadingandtweaks$debugTraces;

    @Shadow
    private static final Field updateEntityTick;

    @Shadow
    private static Throwable lastDebugTrace;

    /**
     * @author
     * @reason
     */
    @Inject(method = "onWorldTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void onWorldTickmultiandtweaks(TickEvent.WorldTickEvent event, CallbackInfo ci) {
        if (OptimizationsandTweaksConfig.enableMixinTickHandler) {
            World world = event.world;
            if (!IC2.platform.isSimulating() || world.isRemote) {
                return;
            }

            if (event.phase == TickEvent.Phase.START) {
                IC2.platform.profilerStartSection("Wind");
                WorldData.get(world).windSim.updateWind();
                IC2.platform.profilerEndStartSection("TickCallbacks");
                multithreadingandtweaks$processTickCallbacks(world);

                if (ConfigUtil.getBool(MainConfig.get(), "balance/disableEnderChest")) {
                    multithreadingandtweaks$removeEnderChests(world);
                }
                IC2.platform.profilerEndSection();
            } else {
                IC2.platform.profilerStartSection("EnergyNet");
                EnergyNetGlobal.onTickEnd(world);
                IC2.platform.profilerEndStartSection("Networking");
                IC2.network.get()
                    .onTickEnd(world);
                IC2.platform.profilerEndSection();
            }
            ci.cancel();
        }
    }

    @Unique
    private void multithreadingandtweaks$removeEnderChests(World world) {
        for (Object obj : world.loadedTileEntityList) {
            if (obj instanceof TileEntity) {
                TileEntity te = (TileEntity) obj;
                if (te instanceof TileEntityEnderChest && !te.isInvalid()
                    && world.blockExists(te.xCoord, te.yCoord, te.zCoord)) {
                    world.setBlockToAir(te.xCoord, te.yCoord, te.zCoord);
                    IC2.log.info(LogCategory.General, "Removed vanilla ender chest at %s.", Util.formatPosition(te));
                }
            }
        }
    }

    @Unique
    private static void multithreadingandtweaks$processTickCallbacks(World world) {
        WorldData worldData = WorldData.get(world);
        IC2.platform.profilerStartSection("SingleTickCallback");

        for (ITickCallback tickCallback = worldData.singleTickCallbacks.poll(); tickCallback
            != null; tickCallback = (ITickCallback) worldData.singleTickCallbacks.poll()) {
            if (debugTickCallback) {
                lastDebugTrace = multithreadingandtweaks$debugTraces.remove(tickCallback);
            }

            IC2.platform.profilerStartSection(
                tickCallback.getClass()
                    .getName());
            tickCallback.tickCallback(world);
            IC2.platform.profilerEndSection();
        }

        IC2.platform.profilerEndStartSection("ContTickCallback");
        worldData.continuousTickCallbacksInUse = true;

        for (ITickCallback tickCallback : worldData.continuousTickCallbacks) {
            if (debugTickCallback) {
                lastDebugTrace = multithreadingandtweaks$debugTraces.remove(tickCallback);
            }

            IC2.platform.profilerStartSection(
                tickCallback.getClass()
                    .getName());
            tickCallback.tickCallback(world);
            IC2.platform.profilerEndSection();
        }

        worldData.continuousTickCallbacksInUse = false;
        if (debugTickCallback) {
            lastDebugTrace = null;
        }

        worldData.continuousTickCallbacks.addAll(worldData.continuousTickCallbacksToAdd);
        worldData.continuousTickCallbacksToAdd.clear();
        worldData.continuousTickCallbacksToRemove.forEach(worldData.continuousTickCallbacks::remove);
        worldData.continuousTickCallbacksToRemove.clear();
        IC2.platform.profilerEndSection();
    }

    static {
        if (debugTickCallback) {
            multithreadingandtweaks$debugTraces = new WeakHashMap<>();
        }

        updateEntityTick = ReflectionUtil.getField(WorldServer.class, "field_80004_Q", "updateEntityTick");
    }
}
