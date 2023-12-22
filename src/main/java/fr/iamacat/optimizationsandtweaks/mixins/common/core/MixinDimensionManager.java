package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentMap;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;

import cpw.mods.fml.common.FMLLog;

@Mixin(DimensionManager.class)
public class MixinDimensionManager {

    @Shadow
    private static Hashtable<Integer, WorldServer> worlds = new Hashtable<Integer, WorldServer>();

    @Shadow
    private static ConcurrentMap<World, World> weakWorldMap = new MapMaker().weakKeys()
        .weakValues()
        .<World, World>makeMap();
    @Shadow
    private static Multiset<Integer> leakedWorlds = HashMultiset.create();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Integer[] getIDs(boolean check) {
        if (check) {
            for (WorldServer worldServer : worlds.values()) {
                if (!weakWorldMap.containsKey(worldServer)) {
                    int hashCode = System.identityHashCode(worldServer);
                    leakedWorlds.add(hashCode);
                    int leakCount = leakedWorlds.count(hashCode);
                    if (leakCount == 5) {
                        FMLLog.fine(
                            "The world %x (%s) may have leaked: first encounter (5 occurrences).\n",
                            hashCode,
                            worldServer.getWorldInfo()
                                .getWorldName());
                    } else if (leakCount % 5 == 0) {
                        FMLLog.fine(
                            "The world %x (%s) may have leaked: seen %d times.\n",
                            hashCode,
                            worldServer.getWorldInfo()
                                .getWorldName(),
                            leakCount);
                    }
                }
            }
        }
        return getIDs();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Integer[] getIDs() {
        return worlds.keySet()
            .toArray(new Integer[0]); // Only loaded dims, since usually used to cycle through loaded worlds
    }
}
