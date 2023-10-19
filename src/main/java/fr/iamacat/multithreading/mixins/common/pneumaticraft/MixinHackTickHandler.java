package fr.iamacat.multithreading.mixins.common.pneumaticraft;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.gameevent.TickEvent;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;
import pneumaticCraft.client.render.pneumaticArmor.hacking.HackableHandler;
import pneumaticCraft.common.HackTickHandler;
import pneumaticCraft.common.util.WorldAndCoord;

@Mixin(HackTickHandler.class)
public class MixinHackTickHandler {

    @Final
    @Shadow
    private final Map<WorldAndCoord, IHackableBlock> hackedBlocks = new HashMap<>();
    @Unique
    private final Map<Class<? extends IHackableBlock>, Block> multithreadingandtweaks$hackableBlockMap = new HashMap<>();

    @Unique
    private final Map<Entity, HackableHandler.HackingEntityProperties> multithreadingandtweaks$entityPropertiesMap = new HashMap<>();

    /**
     * @author iamacatfr
     * @reason optimizations
     */
    @Inject(method = "onServerTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void onServerTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<WorldAndCoord> iterator = hackedBlocks.keySet()
                .iterator();
            while (iterator.hasNext()) {
                WorldAndCoord hackedBlock = iterator.next();
                IHackableBlock hackableBlock = (IHackableBlock) multithreadingandtweaks$hackableBlockMap
                    .get(hackedBlock.getClass());
                if (hackableBlock != null && !hackableBlock
                    .afterHackTick((World) hackedBlock.world, hackedBlock.x, hackedBlock.y, hackedBlock.z)) {
                    iterator.remove();
                }
            }
        }
        ci.cancel();
    }

    /**
     * @author iamacatfr
     * @reason optimizations
     */
    @Inject(method = "worldTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void worldTick(TickEvent.WorldTickEvent event, CallbackInfo ci) {
        if (event.phase == TickEvent.Phase.END) {
            for (Object entityObject : event.world.loadedEntityList) {
                if (entityObject instanceof Entity) {
                    Entity entity = (Entity) entityObject;
                    HackableHandler.HackingEntityProperties hackingProps = multithreadingandtweaks$getHackingProperties(
                        entity);
                    if (hackingProps != null) {
                        hackingProps.update(entity);
                    }
                }
            }
            ci.cancel();
        }
    }

    @Unique
    private HackableHandler.HackingEntityProperties multithreadingandtweaks$getHackingProperties(Entity entity) {
        HackableHandler.HackingEntityProperties hackingProps = multithreadingandtweaks$entityPropertiesMap.get(entity);
        if (hackingProps == null) {
            hackingProps = (HackableHandler.HackingEntityProperties) entity
                .getExtendedProperties("PneumaticCraftHacking");
            if (hackingProps != null) {
                multithreadingandtweaks$entityPropertiesMap.put(entity, hackingProps);
            }
        }
        return hackingProps;
    }
}
