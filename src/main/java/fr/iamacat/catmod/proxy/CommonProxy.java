package fr.iamacat.catmod.proxy;

import cpw.mods.fml.common.registry.EntityRegistry;
import fr.iamacat.catmod.Catmod;
import fr.iamacat.catmod.biomes.catbiome.CatBiome;
import fr.iamacat.catmod.entities.CatAgressiveEntity;
import fr.iamacat.catmod.entities.CatPassiveEntity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

import java.awt.*;

public class CommonProxy{
    int entityID;
    public  void registerRenders()
    {

    }
    public void registerEntities()
    {
        // Register your entities here
        EntityRegistry.registerModEntity(CatPassiveEntity.class,"CatPassiveEntity",entityID++,Catmod.instance,80,1,false);
        EntityRegistry.registerModEntity(CatAgressiveEntity.class,"CatAgressiveEntity",entityID++, Catmod.instance,80,1,false);
       }
}
