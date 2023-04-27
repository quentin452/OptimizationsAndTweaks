package fr.iamacat.catmod.armors;

import fr.iamacat.catmod.utils.Reference;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;

public class CatArmors extends ItemArmor {

    public CatArmors(ArmorMaterial material, int renderIndex, int armorType) {
        super(material, renderIndex, armorType);
    }

    // set the texture path for the armor layers
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (this.armorType == 2) {
            return Reference.MOD_ID + ":textures/models/armor/cat_layer_2.png";
        }
            return Reference.MOD_ID + ":textures/models/armor/cat_layer_1.png";
    }
}