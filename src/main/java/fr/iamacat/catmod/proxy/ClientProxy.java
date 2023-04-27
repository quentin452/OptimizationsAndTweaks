package fr.iamacat.catmod.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import fr.iamacat.catmod.Catmod;
import fr.iamacat.catmod.biomes.catbiome.CatBiome;
import fr.iamacat.catmod.entities.CatAgressiveEntity;
import fr.iamacat.catmod.entities.CatPassiveEntity;
import fr.iamacat.catmod.entities.tnt.EntityCatTnt;
import fr.iamacat.catmod.models.ModelCatAgressiveEntity;
import fr.iamacat.catmod.models.ModelCatPassiveEntity;
import fr.iamacat.catmod.renders.RenderCatAgressiveEntity;
import fr.iamacat.catmod.renders.RenderCatPassiveEntity;
import fr.iamacat.catmod.renders.tnt.RenderCatTnt;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

import java.awt.*;

public class ClientProxy extends CommonProxy{

    @Override
    public  void registerRenders()
    {
        // Register your renders here
        RenderingRegistry.registerEntityRenderingHandler(CatAgressiveEntity.class, new RenderCatAgressiveEntity(new ModelCatAgressiveEntity(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(CatPassiveEntity.class, new RenderCatPassiveEntity(new ModelCatPassiveEntity(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityCatTnt.class, new RenderCatTnt());//tnt render not terminated or not fixed
    }
    @Override
    public void registerEntities(){
        // Register any client-only entities here/spawnegg/spawners

        entityID = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(CatAgressiveEntity.class, "CatAgressiveEntity", entityID++, new Color(60, 253, 1).getRGB(), new Color(0, 235, 243).getRGB());// to make eggs for the entity
        EntityRegistry.addSpawn("CatAgressiveEntity", 100, 2,4, EnumCreatureType.monster, BiomeGenBase.beach,BiomeGenBase.forest,BiomeGenBase.plains, CatBiome.INSTANCE);

        EntityRegistry.registerGlobalEntityID(CatPassiveEntity.class, "CatPassiveEntity", entityID++, new Color(0, 221, 243).getRGB(), new Color(243, 0, 0).getRGB());// to make eggs for the entity
        EntityRegistry.addSpawn("CatPassiveEntity", 100, 2,4, EnumCreatureType.creature, BiomeGenBase.beach,BiomeGenBase.desert,BiomeGenBase.forest,BiomeGenBase.plains, CatBiome.INSTANCE);
    }
}
