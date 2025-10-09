package fr.iamacat.optimizationsandtweaks.mixins.common.thecampingmod;

import com.rikmuld.camping.common.world.WorldGenerator;
import com.rikmuld.camping.core.Objs$;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(WorldGenerator.class)
public abstract class MixinWorldGeneratorTheCamping {

    @Shadow(remap = false)
    public abstract void xBlock_$eq(int x);
    
    @Shadow(remap = false)
    public abstract void yBlock_$eq(int y);
    
    @Shadow(remap = false)
    public abstract void zBlock_$eq(int z);    

    @Overwrite(remap = false)
    public void generateSurface(World world, Random random, int blockX, int blockZ) {
        if (Objs$.MODULE$.config().worldGenHemp()) {
            int hempMulti = Math.max(1, Objs$.MODULE$.config().hempGenMulti());
            for (int i = 0; i < hempMulti; i++) {
                optimizationsAndTweaks$generateHemp(world, random, blockX, blockZ);
            }
        }
        
        optimizationsAndTweaks$generateCampsite(world, random, blockX, blockZ);
    }

    @Unique
    private void optimizationsAndTweaks$generateHemp(World world, Random random, int blockX, int blockZ) {
        int x = blockX + random.nextInt(16);
        int y = random.nextInt(15) + 55;
        int z = blockZ + random.nextInt(16);

        if (!optimizationsAndTweaks$isChunkLoaded(world, x, z)) {
            return;
        }

        this.xBlock_$eq(x);
        this.yBlock_$eq(y);
        this.zBlock_$eq(z);

        for (int i = 0; i < 20; i++) {
            if (!optimizationsAndTweaks$generateSingleHemp(world, random, x, y, z)) {
                break;
            }
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateCampsite(World world, Random random, int blockX, int blockZ) {
        if (Objs$.MODULE$.config().coreOnly()) {
            return;
        }

        BiomeGenBase biome = world.getBiomeGenForCoords(blockX, blockZ);
        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST)) {
            return;
        }

        int checkX = blockX + random.nextInt(16);
        int checkZ = blockZ + random.nextInt(16);
        
        if (!optimizationsAndTweaks$isChunkLoaded(world, checkX, checkZ)) {
            return;
        }

        BiomeGenBase.TempCategory tempCategory = world.getBiomeGenForCoords(checkX, checkZ).getTempCategory();
        if (tempCategory != BiomeGenBase.TempCategory.MEDIUM) {
            return;
        }

        if (random.nextInt(Objs$.MODULE$.config().campsiteRareness()) == 0 && 
            Objs$.MODULE$.config().worldGenCampsite()) {
            
            this.xBlock_$eq(checkX);
            this.yBlock_$eq(50);
            this.zBlock_$eq(checkZ);
            
            optimizationsAndTweaks$generateCampsiteStructure(world, random, checkX, 50, checkZ);
        }
    }

    @Unique
    private void optimizationsAndTweaks$generateCampsiteStructure(World world, Random rand, int x, int yCoord, int z) {
        int y = yCoord;
        
        while (true) {
            if (!optimizationsAndTweaks$isChunkLoaded(world, x, z)) {
                return;
            }
            
            if (!world.blockExists(x, y + 1, z) || !world.blockExists(x, y + 2, z)) {
                return;
            }
            
            if (world.isAirBlock(x, y + 1, z) && world.isAirBlock(x, y + 2, z)) {
                if (optimizationsAndTweaks$isValidCampsiteLocation(world, x, y, z)) {
                    optimizationsAndTweaks$placeCampsite(world, rand, x, y, z);
                    return;
                }
                return;
            }
            ++y;
            
            if (y > yCoord + 20) {
                return;
            }
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidCampsiteLocation(World world, int x, int y, int z) {
        if (!optimizationsAndTweaks$isChunkLoaded(world, x - 1, z) ||
            !optimizationsAndTweaks$isChunkLoaded(world, x - 1, z + 2) ||
            !optimizationsAndTweaks$isChunkLoaded(world, x + 4, z) ||
            !optimizationsAndTweaks$isChunkLoaded(world, x + 4, z + 2)) {
            return false;
        }
        
        if (!optimizationsAndTweaks$locationIsValidSpawn(world, x - 1, y, z) ||
            !optimizationsAndTweaks$locationIsValidSpawn(world, x - 1, y, z + 2) ||
            !optimizationsAndTweaks$locationIsValidSpawn(world, x + 4, y, z + 2) ||
            !optimizationsAndTweaks$locationIsValidSpawn(world, x + 4, y, z)) {
            return false;
        }
        
        return optimizationsAndTweaks$isValidSpawnArea(world, x, y, z, 6, 3);
    }

    @Unique
    private boolean optimizationsAndTweaks$locationIsValidSpawn(World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) {
            return false;
        }
        return world.getBlock(x, y, z) == net.minecraft.init.Blocks.grass;
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidSpawnArea(World world, int x, int y, int z, int xLength, int zLength) {
        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < zLength; j++) {
                int checkX = x + i;
                int checkZ = z + j;
                
                if (!optimizationsAndTweaks$isChunkLoaded(world, checkX, checkZ)) {
                    return false;
                }
                
                if (!world.blockExists(checkX, y + 1, checkZ)) {
                    return false;
                }
                
                if (!world.isAirBlock(checkX, y + 1, checkZ)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Unique
    private void optimizationsAndTweaks$placeCampsite(World world, Random rand, int x, int y, int z) {
        if (world.blockExists(x, y + 1, z + 1)) {
            world.setBlock(x, y + 1, z + 1, Objs$.MODULE$.campfire(), 0, 2);
        }
        
        if (world.blockExists(x + 2, y + 1, z + 1)) {
            world.setBlock(x + 2, y + 1, z + 1, Objs$.MODULE$.tent(), 2, 2);
            
            optimizationsAndTweaks$configureTentTileEntity(world, x + 2, y + 1, z + 1);
        }
        
        optimizationsAndTweaks$spawnCamper(world, x, y + 1, z);
    }

    @Unique
    private void optimizationsAndTweaks$configureTentTileEntity(World world, int x, int y, int z) {
        try {
            net.minecraft.tileentity.TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null) {
                java.lang.reflect.Method setRotation = tileEntity.getClass().getMethod("setRotation", int.class);
                setRotation.invoke(tileEntity, 3);
                
                Class<?> tileEntityTentClass = Class.forName("com.rikmuld.camping.common.objs.tile.TileEntityTent$");
                Object module = tileEntityTentClass.getField("MODULE$").get(null);
                java.lang.reflect.Method bedsMethod = tileEntityTentClass.getMethod("BEDS");
                Object beds = bedsMethod.invoke(module);
                
                java.lang.reflect.Method setContends = tileEntity.getClass().getMethod("setContends", 
                    int.class, Object.class, boolean.class, int.class);
                setContends.invoke(tileEntity, 1, beds, true, 0);
            }
        } catch (Throwable e) {
        }
    }

    @Unique
    private void optimizationsAndTweaks$spawnCamper(World world, int x, int y, int z) {
        try {
            Class<?> camperClass = Class.forName("com.rikmuld.camping.common.objs.entity.Camper");
            java.lang.reflect.Constructor<?> constructor = camperClass.getConstructor(World.class, int.class, int.class, int.class);
            net.minecraft.entity.Entity camper = (net.minecraft.entity.Entity) constructor.newInstance(world, x, y, z);
            world.spawnEntityInWorld(camper);
        } catch (Throwable e) {
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$generateSingleHemp(World world, Random random, int baseX, int baseY, int baseZ) {
        int targetX = baseX + random.nextInt(4) - random.nextInt(4);
        int targetZ = baseZ + random.nextInt(4) - random.nextInt(4);

        if (!optimizationsAndTweaks$isChunkLoaded(world, targetX, targetZ)) {
            return false;
        }

        if (!world.isAirBlock(targetX, baseY, targetZ)) {
            return true; 
        }

        if (!optimizationsAndTweaks$hasAdjacentWater(world, targetX, baseY, targetZ)) {
            return true;
        }

        int growthStage = random.nextInt(random.nextInt(4) + 1);
        
        if (!Objs$.MODULE$.hemp().canBlockStay(world, targetX, baseY, targetZ)) {
            return true; 
        }

        if (world.blockExists(targetX, baseY, targetZ)) {
            world.setBlock(targetX, baseY, targetZ, Objs$.MODULE$.hemp(), growthStage, 2);
            
            if (growthStage == 4 && world.blockExists(targetX, baseY + 1, targetZ)) {
                world.setBlock(targetX, baseY + 1, targetZ, Objs$.MODULE$.hemp(), 5, 2);
            }
        }

        return true;
    }

    @Unique
    private boolean optimizationsAndTweaks$hasAdjacentWater(World world, int x, int y, int z) {
        return optimizationsAndTweaks$checkWaterBlock(world, x - 1, y - 1, z) ||
               optimizationsAndTweaks$checkWaterBlock(world, x + 1, y - 1, z) ||
               optimizationsAndTweaks$checkWaterBlock(world, x, y - 1, z - 1) ||
               optimizationsAndTweaks$checkWaterBlock(world, x, y - 1, z + 1);
    }

    @Unique
    private boolean optimizationsAndTweaks$checkWaterBlock(World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) {
            return false;
        }
        return world.getBlock(x, y, z).getMaterial() == net.minecraft.block.material.Material.water;
    }

    @Unique
    private static boolean optimizationsAndTweaks$isChunkLoaded(World world, int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        return world.getChunkProvider().chunkExists(chunkX, chunkZ);
    }
}