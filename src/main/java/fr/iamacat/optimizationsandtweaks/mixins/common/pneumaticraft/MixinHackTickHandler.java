package fr.iamacat.optimizationsandtweaks.mixins.common.pneumaticraft;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import fr.iamacat.optimizationsandtweaks.utils.agrona.collections.Object2ObjectHashMap;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;
import pneumaticCraft.client.render.pneumaticArmor.hacking.HackableHandler;
import pneumaticCraft.common.HackTickHandler;
import pneumaticCraft.common.util.WorldAndCoord;

// NOTE (2026-07-08 mixin @Overwrite->injector conversion pass): reclassify COMPLEX, entire class left untouched.
// All 3 @Overwrite methods (trackBlock, onServerTick, worldTick) read/write the SAME @Unique replacement fields
// (optimizationsAndTweaks$hackedBlocks, $hackableBlockMap, $entityPropertiesMap) -- same all-or-nothing risk as
// MixinSheetDataPackage (falling back to vanilla body for just one method would desync it from the others'
// state). Beyond that, onServerTick and worldTick are also genuine algorithm rewrites, not simple call swaps:
// - onServerTick: vanilla does an O(hackedBlocks x registeredHackableBlocks) linear scan of
// PneumaticCraftAPIHandler's registry per tracked block every tick; this version replaces it with an O(1)
// direct lookup via the cached $hackableBlockMap. Real perf fix (matches the class doc), but a full search-
// strategy rewrite, not a call redirect. Also subtly changes behavior on a lookup miss: vanilla removes the
// tracked entry when no matching registry entry is found; this version does nothing on a null lookup (relies
// on $hackableBlockMap always having an entry for every tracked block's class, an invariant not verifiable
// from this file alone).
// - worldTick: removes vanilla's try/catch(Throwable) (exceptions now propagate instead of being swallowed) and
// removes a per-entity-without-hacking-properties Log.warning call (likely THE actual "reduces TPS lag" fix --
// logging a warning for every ordinary entity every tick is a serious log-spam/perf issue), and adds its own
// caching layer via $entityPropertiesMap. Multiple interleaved real changes; not mechanically convertible.
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
