package fr.iamacat.optimizationsandtweaks.mixins.common.animalsplus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import clickme.animals.EntityManager;
import clickme.animals.entity.ambient.*;
import clickme.animals.entity.passive.*;
import clickme.animals.entity.water.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mixin(EntityManager.class)
public abstract class MixinEntityManager {

    @Overwrite(remap = false)
     public static void registerEntities() {
        EntityRegistry.registerGlobalEntityID(EntityCentipede.class, "CentipedeP", EntityRegistry.findGlobalUniqueEntityId(), 15708256, 5848090);
        EntityRegistry.registerGlobalEntityID(EntityCricket.class, "CricketP", EntityRegistry.findGlobalUniqueEntityId(), 8343842, 2100236);
        EntityRegistry.registerGlobalEntityID(EntityButterfly.class, "ButterflyP", EntityRegistry.findGlobalUniqueEntityId(), 15493137, 721666);
        EntityRegistry.registerGlobalEntityID(EntityMoth.class, "MothP", EntityRegistry.findGlobalUniqueEntityId(), 13614758, 6704950);
        EntityRegistry.registerGlobalEntityID(EntityFish.class, "FishP", EntityRegistry.findGlobalUniqueEntityId(), 6928807, 6057867);
        EntityRegistry.registerGlobalEntityID(EntityTropiFish.class, "TropicalFishP", EntityRegistry.findGlobalUniqueEntityId(), 15887623, 15725300);
        EntityRegistry.registerGlobalEntityID(EntityAngler.class, "AnglerP", EntityRegistry.findGlobalUniqueEntityId(), 5397296, 15392616);
        EntityRegistry.registerGlobalEntityID(EntityMantaRay.class, "MantaRayP", EntityRegistry.findGlobalUniqueEntityId(), 1052965, 14474460);
        EntityRegistry.registerGlobalEntityID(EntityWhale.class, "WhaleP", EntityRegistry.findGlobalUniqueEntityId(), 12772830, 8497600);
        EntityRegistry.registerGlobalEntityID(EntitySnake.class, "SnakeP", EntityRegistry.findGlobalUniqueEntityId(), 7096116, 14531977);
        EntityRegistry.registerGlobalEntityID(EntityLizard.class, "LizardP", EntityRegistry.findGlobalUniqueEntityId(), 13815232, 8219967);
        EntityRegistry.registerGlobalEntityID(EntityMouse.class, "MouseP", EntityRegistry.findGlobalUniqueEntityId(), 5986381, 15902877);
        EntityRegistry.registerGlobalEntityID(EntityBird.class, "BirdP", EntityRegistry.findGlobalUniqueEntityId(), 4934535, 15910160);
        EntityRegistry.registerGlobalEntityID(EntityDuck.class, "DuckP", EntityRegistry.findGlobalUniqueEntityId(), 4413191, 13155998);
        EntityRegistry.registerGlobalEntityID(EntityPenguin.class, "PinguinP", EntityRegistry.findGlobalUniqueEntityId(), 1066089, 13948116);
        EntityRegistry.registerGlobalEntityID(EntityPiranha.class, "PiranhaP", EntityRegistry.findGlobalUniqueEntityId(), 2829109, 14634030);
        EntityRegistry.registerGlobalEntityID(EntityShark.class, "SharkP", EntityRegistry.findGlobalUniqueEntityId(), 11053224, 7631988);
        LanguageRegistry.instance().addStringLocalization("entity.CentipedeP.name", "en_US", "Centipede");
        LanguageRegistry.instance().addStringLocalization("entity.CricketP.name", "en_US", "Cricket");
        LanguageRegistry.instance().addStringLocalization("entity.ButterflyP.name", "en_US", "Butterfly");
        LanguageRegistry.instance().addStringLocalization("entity.MothP.name", "en_US", "Moth");
        LanguageRegistry.instance().addStringLocalization("entity.FishP.name", "en_US", "Fish");
        LanguageRegistry.instance().addStringLocalization("entity.TropicalFishP.name", "en_US", "Tropical Fish");
        LanguageRegistry.instance().addStringLocalization("entity.AnglerP.name", "en_US", "Angler Fish");
        LanguageRegistry.instance().addStringLocalization("entity.MantaRayP.name", "en_US", "Manta Ray");
        LanguageRegistry.instance().addStringLocalization("entity.WhaleP.name", "en_US", "Whale");
        LanguageRegistry.instance().addStringLocalization("entity.SnakeP.name", "en_US", "Snake");
        LanguageRegistry.instance().addStringLocalization("entity.LizardP.name", "en_US", "Lizard");
        LanguageRegistry.instance().addStringLocalization("entity.MouseP.name", "en_US", "Mouse");
        LanguageRegistry.instance().addStringLocalization("entity.BirdP.name", "en_US", "Bird");
        LanguageRegistry.instance().addStringLocalization("entity.DuckP.name", "en_US", "Duck");
        LanguageRegistry.instance().addStringLocalization("entity.PinguinP.name", "en_US", "Penguin");
        LanguageRegistry.instance().addStringLocalization("entity.PiranhaP.name", "en_US", "Piranha");
        LanguageRegistry.instance().addStringLocalization("entity.SharkP.name", "en_US", "Shark");
    }
}
