package fr.iamacat.optimizationsandtweaks.mixins.common.therealketer;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import fr.keter.KMOD.Entities.*;
import fr.keter.KMOD.Main.KMOD_Main;
import fr.keter.KMOD.Main.KMOD_Main_Entities;
import fr.keter.KMOD.TileEntities.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(KMOD_Main_Entities.class)
public class MixinKMOD_Main_Entities {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void init() {
        int entityId;
        do {
            entityId = EntityRegistry.findGlobalUniqueEntityId();
        } while (entityId < 1001);

        GameRegistry.registerTileEntity(TileEntityMycena.class, "TileEntityMycena");
        GameRegistry.registerTileEntity(TileEntityCrystals.class, "TileEntityCrystals");
        GameRegistry.registerTileEntity(TileEntityHealingFlower.class, "TileEntityHealingFlower");
        GameRegistry.registerTileEntity(TileEntityKetherStonePillarBase.class, "TileEntityKetherStonePillarBase");
        GameRegistry.registerTileEntity(TileEntityKetherStonePillarCenter.class, "TileEntityKetherStonePillarCenter");
        GameRegistry.registerTileEntity(TileEntityKetherStonePillarTop.class, "TileEntityKetherStonePillarTop");
        GameRegistry.registerTileEntity(TileEntityKetherYellowStoneBrickPillarBase.class, "TileEntityKetherYellowStoneBrickBase");
        GameRegistry.registerTileEntity(TileEntityKetherYellowStoneBrickPillarCenter.class, "TileEntityKetherYellowStoneBrickPillarCenter");
        GameRegistry.registerTileEntity(TileEntityKetherYellowStoneBrickPillarTop.class, "TileEntityKetherYellowStoneBrickPillarTop");
        GameRegistry.registerTileEntity(TileEntityEnergyConverter.class, "TileEntityEnergyConverter");
        GameRegistry.registerTileEntity(TileEntityGiftsBlock.class, "TileEntityGiftsBlock");
        GameRegistry.registerTileEntity(TileEntityEnergyEnchanter.class, "TileEntityEnergyEnchanter");
        EntityRegistry.registerModEntity(EntityYellowEssence.class, "YellowEssence", 49, KMOD_Main.instance, 160, 3, true);
        LanguageRegistry.instance().addStringLocalization("entity.YellowEssence.name", "Yellow Essence");
        EntityRegistry.registerModEntity(EntityBlueEssence.class, "BlueEssence", 50, KMOD_Main.instance, 160, 3, true);
        LanguageRegistry.instance().addStringLocalization("entity.BlueEssence.name", "Blue Essence");
        EntityRegistry.registerModEntity(EntityRedEssence.class, "RedEssence", 51, KMOD_Main.instance, 160, 3, true);
        LanguageRegistry.instance().addStringLocalization("entity.RedEssence.name", "Red Essence");
        EntityRegistry.registerModEntity(EntityKeherEnergy.class, "KetherEnergy", 52, KMOD_Main.instance, 160, 3, true);
        LanguageRegistry.instance().addStringLocalization("entity.KetherEnergy.name", "Kether Energy");
        EntityRegistry.registerModEntity(EntityDarkEnergy.class, "DarkEnergy", 59, KMOD_Main.instance, 160, 3, true);
        LanguageRegistry.instance().addStringLocalization("entity.DarkEnergy.name", "Dark Energy");
        EntityRegistry.registerGlobalEntityID(EntityKetherGhast.class, "Dementor", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Dementor.name", "Dementor");
        EntityRegistry.registerGlobalEntityID(EntityStoneDevourer.class, "Stone Devourer", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Stone Devourer.name", "Stone Devourer");
        EntityRegistry.registerGlobalEntityID(EntityKetherSlime.class, "Kether Slime", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Kether Slime.name", "Kether Slime");
        EntityRegistry.registerGlobalEntityID(EntityKetherBoar.class, "Kether Boar", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Kether Boar.name", "Kether Boar");
        EntityRegistry.registerGlobalEntityID(EntityEyeBright.class, "Eye Bright", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Eye Bright.name", "Eye Bright");
        EntityRegistry.registerGlobalEntityID(EntityDemonEye.class, "Demon Eye", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Demon Eye.name", "Demon Eye");
        EntityRegistry.registerGlobalEntityID(EntityMorf.class, "Morf",entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Morf.name", "Morf");
        EntityRegistry.registerGlobalEntityID(EntityLame.class, "Lame", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Lame.name", "Lame");
        EntityRegistry.registerGlobalEntityID(EntityLostyGhost.class, "Losty Ghost", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Losty Ghost.name", "Losty Ghost");
        EntityRegistry.registerGlobalEntityID(EntityKetherCrocodile.class, "Kether Crocodile", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Kether Crocodile.name", "Kether Crocodile");
        EntityRegistry.registerGlobalEntityID(EntityCaveGuardian.class, "Cave Guardian", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Cave Guardian.name", "Cave Guardian");
        EntityRegistry.registerGlobalEntityID(EntityKetherButterfly.class, "Kether Butterfly", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Kether Butterfly.name", "Kether Butterfly");
        EntityRegistry.registerGlobalEntityID(EntityKetherFlyingBoar.class, "Kether Flying Boar", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Kether Flying Boar.name", "Kether Flying Boar");
        EntityRegistry.registerGlobalEntityID(EntityKetherCow.class, "Kether Cow", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Kether Cow.name", "Kether Cow");
        EntityRegistry.registerGlobalEntityID(EntitySimple.class, "Drago", entityId, 1461248, 12886784);
        LanguageRegistry.instance().addStringLocalization("entity.Drago.name", "Drago");
    }
}
