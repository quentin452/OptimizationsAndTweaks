package fr.iamacat.optimizationsandtweaks.mixins.common.pneumaticraft;

import java.util.Iterator;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.gameevent.TickEvent;
import org.spongepowered.libraries.com.google.common.eventbus.Subscribe;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;
import pneumaticCraft.client.render.pneumaticArmor.hacking.HackableHandler;
import pneumaticCraft.common.HackTickHandler;
import pneumaticCraft.common.util.WorldAndCoord;

@Mixin(HackTickHandler.class)
public class MixinHackTickHandler {

    @Final
    @Unique
    private final Object2ObjectHashMap<WorldAndCoord, IHackableBlock> optimizationsAndTweaks$hackedBlocks = new Object2ObjectHashMap<>();
    @Unique
    private final Object2ObjectHashMap<Class<? extends IHackableBlock>, Block> optimizationsAndTweaks$hackableBlockMap = new Object2ObjectHashMap<>();

    @Unique
    private final Object2ObjectHashMap<Entity, HackableHandler.HackingEntityProperties> optimizationsAndTweaks$entityPropertiesMap = new Object2ObjectHashMap<>();
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void trackBlock(WorldAndCoord coord, IHackableBlock iHackable) {
        this.optimizationsAndTweaks$hackedBlocks.put(coord, iHackable);
    }

    /**
     * @author iamacatfr
     * @reason optimizations
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<WorldAndCoord> iterator = optimizationsAndTweaks$hackedBlocks.keySet()
                .iterator();
            while (iterator.hasNext()) {
                WorldAndCoord hackedBlock = iterator.next();
                IHackableBlock hackableBlock = (IHackableBlock) optimizationsAndTweaks$hackableBlockMap
                    .get(hackedBlock.getClass());
                if (hackableBlock != null && !hackableBlock
                    .afterHackTick((World) hackedBlock.world, hackedBlock.x, hackedBlock.y, hackedBlock.z)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * @author iamacatfr
     * @reason optimizations
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (Object entityObject : event.world.loadedEntityList) {
                if (entityObject instanceof Entity) {
                    Entity entity = (Entity) entityObject;
                    HackableHandler.HackingEntityProperties hackingProps = optimizationsAndTweaks$getHackingProperties(
                        entity);
                    if (hackingProps != null) {
                        hackingProps.update(entity);
                    }
                }
            }
        }
    }

    @Unique
    private HackableHandler.HackingEntityProperties optimizationsAndTweaks$getHackingProperties(Entity entity) {
        HackableHandler.HackingEntityProperties hackingProps = optimizationsAndTweaks$entityPropertiesMap.get(entity);
        if (hackingProps == null) {
            hackingProps = (HackableHandler.HackingEntityProperties) entity
                .getExtendedProperties("PneumaticCraftHacking");
            if (hackingProps != null) {
                optimizationsAndTweaks$entityPropertiesMap.put(entity, hackingProps);
            }
        }
        return hackingProps;
    }
}
