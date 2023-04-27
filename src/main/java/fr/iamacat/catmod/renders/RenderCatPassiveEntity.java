package fr.iamacat.catmod.renders;

import fr.iamacat.catmod.entities.CatPassiveEntity;
import fr.iamacat.catmod.utils.Reference;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderCatPassiveEntity extends RenderLiving {
    private ResourceLocation texture = new ResourceLocation(Reference.MOD_ID + ":textures/entity/CatPassiveEntity.png");

    public RenderCatPassiveEntity(ModelBase p_i1262_1_, float p_i1262_2_) {
        super(p_i1262_1_, p_i1262_2_);
    }
    protected ResourceLocation getEntiyTexture(EntityLiving living)
    {
       return this.getEntityTexture((CatPassiveEntity) living);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return texture;
    }
}
