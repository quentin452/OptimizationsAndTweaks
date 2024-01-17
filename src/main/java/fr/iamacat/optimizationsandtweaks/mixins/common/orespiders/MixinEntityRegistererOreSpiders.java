package fr.iamacat.optimizationsandtweaks.mixins.common.orespiders;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenEnd;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import bendonnelly1.orespiders.OreSpiders;
import bendonnelly1.orespiders.entity.*;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mixin(EntityRegisterer.class)
public class MixinEntityRegistererOreSpiders {

    @Shadow
    static int zombieBackGround = 44975;
    @Shadow
    static int zombieSpots = 6269070;
    @Shadow
    static int whiteColour = 16777215;
    @Shadow
    static int blackColour = 0;
    @Shadow
    static int grayColour = 4342338;
    @Shadow
    static int lightGrayColour = 15658734;
    @Shadow
    static int lightBlueColour = 11531775;
    @Shadow
    static int blueishIcyColour = 4105935;
    @Shadow
    static int kindaBlueColour = 3374023;
    @Shadow
    static int purpleBlueishColour = 6560240;
    @Shadow
    static int redishPinkishColour = 15404632;
    @Shadow
    static int greenishColour = 10092390;
    @Shadow
    static int yellowishColour = 16777011;
    @Shadow
    static int brownishColour = 6510090;
    @Shadow
    static int purpleishColour = 7017389;
    @Shadow
    static int grayishIronishColour = 9736593;
    @Shadow
    static int enderColour = 13268463;
    @Shadow
    static int goldishColour = 16243550;
    @Shadow
    static int netherQuartzColour = 13290971;
    @Shadow
    static int lapisishColour = 5467360;
    @Shadow
    static int redishColour = 14558788;
    @Shadow
    static int muckyGreenColour = 3493680;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void registerEntities() {
        int entityId;
        do {
            entityId = EntityRegistry.findGlobalUniqueEntityId();
        } while (entityId < 1001);
        EntityRegistry.registerGlobalEntityID(EntityCoalSpider.class, "CoalSpider", entityId, blackColour, grayColour);
        EntityRegistry.addSpawn(
            EntityCoalSpider.class,
            6,
            3,
            4,
            EnumCreatureType.monster,
            BiomeGenBase.desert,
            BiomeGenBase.desertHills,
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsEdge,
            BiomeGenBase.forest,
            BiomeGenBase.forestHills,
            BiomeGenBase.frozenOcean,
            BiomeGenBase.frozenRiver,
            BiomeGenBase.iceMountains,
            BiomeGenBase.icePlains,
            BiomeGenBase.jungle,
            BiomeGenBase.jungleHills,
            BiomeGenBase.plains,
            BiomeGenBase.swampland,
            BiomeGenBase.taiga,
            BiomeGenBase.taigaHills);
        EntityRegistry
            .registerGlobalEntityID(EntityDiamondSpider.class, "DiamondSpider", entityId, blackColour, lightBlueColour);
        EntityRegistry.addSpawn(
            EntityDiamondSpider.class,
            15,
            2,
            4,
            EnumCreatureType.monster,
            BiomeGenBase.desert,
            BiomeGenBase.desertHills,
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsEdge,
            BiomeGenBase.forest,
            BiomeGenBase.forestHills,
            BiomeGenBase.frozenOcean,
            BiomeGenBase.frozenRiver,
            BiomeGenBase.iceMountains,
            BiomeGenBase.icePlains,
            BiomeGenBase.jungle,
            BiomeGenBase.jungleHills,
            BiomeGenBase.plains,
            BiomeGenBase.swampland,
            BiomeGenBase.taiga,
            BiomeGenBase.taigaHills);
        EntityRegistry.registerGlobalEntityID(
            EntityObsidianSpider.class,
            "ObsidianSpider",
            entityId,
            blackColour,
            purpleishColour);
        EntityRegistry.addSpawn(
            EntityObsidianSpider.class,
            15,
            2,
            4,
            EnumCreatureType.monster,
            BiomeGenBase.desert,
            BiomeGenBase.desertHills,
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsEdge,
            BiomeGenBase.forest,
            BiomeGenBase.forestHills,
            BiomeGenBase.frozenOcean,
            BiomeGenBase.frozenRiver,
            BiomeGenBase.iceMountains,
            BiomeGenBase.icePlains,
            BiomeGenBase.jungle,
            BiomeGenBase.jungleHills,
            BiomeGenBase.plains,
            BiomeGenBase.swampland,
            BiomeGenBase.taiga,
            BiomeGenBase.taigaHills);
        EntityRegistry
            .registerGlobalEntityID(EntityEmeraldSpider.class, "EmeraldSpider", entityId, blackColour, greenishColour);
        EntityRegistry.addSpawn(
            EntityEmeraldSpider.class,
            20,
            3,
            4,
            EnumCreatureType.monster,
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsEdge);
        EntityRegistry
            .registerGlobalEntityID(EntityIronSpider.class, "IronSpider", entityId, blackColour, grayishIronishColour);
        EntityRegistry.addSpawn(
            EntityIronSpider.class,
            20,
            4,
            5,
            EnumCreatureType.monster,
            BiomeGenBase.desert,
            BiomeGenBase.desertHills,
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsEdge,
            BiomeGenBase.forest,
            BiomeGenBase.forestHills,
            BiomeGenBase.frozenOcean,
            BiomeGenBase.frozenRiver,
            BiomeGenBase.iceMountains,
            BiomeGenBase.icePlains,
            BiomeGenBase.jungle,
            BiomeGenBase.jungleHills,
            BiomeGenBase.plains,
            BiomeGenBase.swampland,
            BiomeGenBase.taiga,
            BiomeGenBase.taigaHills);
        EntityRegistry
            .registerGlobalEntityID(EntityEnderSpider.class, "EnderSpider", entityId, blackColour, enderColour);
        EntityRegistry.addSpawn(EntityEnderSpider.class, 20, 2, 4, EnumCreatureType.monster, BiomeGenEnd.sky);
        EntityRegistry
            .registerGlobalEntityID(EntityGoldSpider.class, "GoldSpider", entityId, blackColour, goldishColour);
        EntityRegistry.addSpawn(
            EntityGoldSpider.class,
            20,
            5,
            6,
            EnumCreatureType.monster,
            BiomeGenBase.desert,
            BiomeGenBase.desertHills,
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsEdge,
            BiomeGenBase.forest,
            BiomeGenBase.forestHills,
            BiomeGenBase.frozenOcean,
            BiomeGenBase.frozenRiver,
            BiomeGenBase.iceMountains,
            BiomeGenBase.icePlains,
            BiomeGenBase.jungle,
            BiomeGenBase.jungleHills,
            BiomeGenBase.plains,
            BiomeGenBase.swampland,
            BiomeGenBase.taiga,
            BiomeGenBase.taigaHills);
        EntityRegistry.registerGlobalEntityID(
            EntityQuartzSpider.class,
            "NetherQuartzSpider",
            entityId,
            blackColour,
            netherQuartzColour);
        EntityRegistry.addSpawn(EntityQuartzSpider.class, 20, 4, 4, EnumCreatureType.monster, BiomeGenEnd.hell);
        EntityRegistry
            .registerGlobalEntityID(EntityLapisSpider.class, "LapisSpider", entityId, blackColour, lapisishColour);
        EntityRegistry.addSpawn(
            EntityLapisSpider.class,
            20,
            3,
            4,
            EnumCreatureType.monster,
            BiomeGenBase.desert,
            BiomeGenBase.desertHills,
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsEdge,
            BiomeGenBase.forest,
            BiomeGenBase.forestHills,
            BiomeGenBase.frozenOcean,
            BiomeGenBase.frozenRiver,
            BiomeGenBase.iceMountains,
            BiomeGenBase.icePlains,
            BiomeGenBase.jungle,
            BiomeGenBase.jungleHills,
            BiomeGenBase.plains,
            BiomeGenBase.swampland,
            BiomeGenBase.taiga,
            BiomeGenBase.taigaHills);
        EntityRegistry
            .registerGlobalEntityID(EntityRedstoneSpider.class, "RedstoneSpider", entityId, blackColour, redishColour);
        EntityRegistry.addSpawn(
            EntityRedstoneSpider.class,
            20,
            4,
            5,
            EnumCreatureType.monster,
            BiomeGenBase.desert,
            BiomeGenBase.desertHills,
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsEdge,
            BiomeGenBase.forest,
            BiomeGenBase.forestHills,
            BiomeGenBase.frozenOcean,
            BiomeGenBase.frozenRiver,
            BiomeGenBase.iceMountains,
            BiomeGenBase.icePlains,
            BiomeGenBase.jungle,
            BiomeGenBase.jungleHills,
            BiomeGenBase.plains,
            BiomeGenBase.swampland,
            BiomeGenBase.taiga,
            BiomeGenBase.taigaHills);
        EntityRegistry
            .registerGlobalEntityID(EntityQueenSpider.class, "QueenSpider", entityId, blackColour, muckyGreenColour);
        EntityRegistry.registerModEntity(
            EntityQueenSpiderPotion.class,
            "queenSpiderPotion",
            0,
            OreSpiders.oreSpiderInstance,
            64,
            3,
            true);
        EntityRegistry
            .registerModEntity(EntityOrbWeaver.class, "orbWeaver", 1, OreSpiders.oreSpiderInstance, 64, 10, true);
    }
}
