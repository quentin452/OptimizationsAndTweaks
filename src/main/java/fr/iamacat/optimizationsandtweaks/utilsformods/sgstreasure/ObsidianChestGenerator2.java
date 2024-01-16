package fr.iamacat.optimizationsandtweaks.utilsformods.sgstreasure;

import com.someguyssoftware.mod.Coords;
import com.someguyssoftware.treasure.Treasure;
import com.someguyssoftware.treasure.block.TreasureChestBlock;
import com.someguyssoftware.treasure.config.GeneralConfig;
import com.someguyssoftware.treasure.item.UniqueKeyItem;
import com.someguyssoftware.treasure.registry.PlaceOfInterest;
import com.someguyssoftware.treasure.registry.PoiType;
import com.someguyssoftware.treasure.tileentity.AbstractTreasureChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObsidianChestGenerator2 {
    public static Coords getNearestChestCoords(World world, int x, int y, int z) {
        List<PlaceOfInterest> chestsByCategory = Treasure.getPoiRegistry().getByCategory(PoiType.CHEST.getName());
        if (chestsByCategory == null) {
            Treasure.logger.info("Unable to locate a chests by category");
            return null;
        } else {
            PlaceOfInterest nearestPoi = null;
            float nearestDistance = -1.0F;
            Iterator<PlaceOfInterest> var8 = chestsByCategory.iterator();

            while(true) {
                PlaceOfInterest poi;
                float distance;
                do {
                    AbstractTreasureChestTileEntity te;
                    do {
                        TreasureChestBlock chest;
                        ArrayList chestTypes;
                        do {
                            Block block;
                            do {
                                if (!var8.hasNext()) {
                                    if (nearestPoi == null) {
                                        Treasure.logger.info("Unable to located a chest of type PIRATE_CHEST|DREAD|WITHER_CHEST. Placing in the first chest in POI.");
                                        poi = chestsByCategory.get(0);
                                        block = world.getBlock(poi.getCoords().getX(), poi.getCoords().getY(), poi.getCoords().getZ());
                                        if (block instanceof TreasureChestBlock) {
                                            return poi.getCoords();
                                        }

                                        return null;
                                    }

                                    return nearestPoi.getCoords();
                                }

                                poi = var8.next();
                                block = world.getBlock(poi.getCoords().getX(), poi.getCoords().getY(), poi.getCoords().getZ());
                            } while(!(block instanceof TreasureChestBlock));

                            chest = (TreasureChestBlock)block;
                            chestTypes = new ArrayList();
                            String[] var13 = GeneralConfig.obsidianKeyGenChests;

                            for (String s : var13) {
                                chestTypes.add(s.toUpperCase());
                            }
                        } while(!chestTypes.contains(chest.getChestType().getName()));

                        te = (AbstractTreasureChestTileEntity)world.getTileEntity(poi.getCoords().getX(), poi.getCoords().getY(), poi.getCoords().getZ());
                    } while(te.getProxy() != null && te.getProxy().func_70301_a(0) != null && te.getProxy().func_70301_a(0).getItem() instanceof UniqueKeyItem);

                    ChunkCoordinates chunkCoords = new ChunkCoordinates(x, y, z);
                    distance = chunkCoords.getDistanceSquared(poi.getCoords().getX(), poi.getCoords().getY(), poi.getCoords().getZ());
                } while(nearestDistance != -1.0F && !(distance < nearestDistance));

                nearestDistance = distance;
                nearestPoi = poi;
            }
        }
    }
}
