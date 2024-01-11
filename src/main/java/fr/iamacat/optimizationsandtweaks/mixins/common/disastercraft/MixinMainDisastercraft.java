package fr.iamacat.optimizationsandtweaks.mixins.common.disastercraft;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import fr.emerald.disaster.MainDisastercraft;
import fr.emerald.disaster.blocks.Agapanthus;
import fr.emerald.disaster.blocks.Brambles;
import fr.emerald.disaster.items.Boiled;
import fr.iamacat.optimizationsandtweaks.utilsformods.disastercraft.DisastercraftConfigBiomeID;

@Mixin(MainDisastercraft.class)
public class MixinMainDisastercraft {

    @Shadow
    public static Block agapanthus;
    @Shadow
    public static Block brambles;
    @Shadow
    public static Item boiled;
    @Shadow
    @Mod.Instance("disastercraft")
    public static MainDisastercraft instance;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        DisastercraftConfigBiomeID.setupAndLoad(e);
        agapanthus = (new Agapanthus()).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("agapanthus")
            .setBlockTextureName("disastercraft:Agapanthus");
        GameRegistry.registerBlock(agapanthus, "Agapanthus");
        brambles = (new Brambles()).setHardness(0.0F)
            .setStepSound(Block.soundTypeGrass)
            .setBlockName("brambles")
            .setBlockTextureName("disastercraft:Brambles");
        GameRegistry.registerBlock(brambles, "Brambles");
        boiled = (new Boiled(5, 0.3F, false)).setPotionEffect(Potion.confusion.id, 17, 0, 0.8F)
            .setTextureName("disastercraft:Boiled")
            .setUnlocalizedName("boiled");
        GameRegistry.registerItem(boiled, "Boiled");
    }
}
