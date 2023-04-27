package fr.iamacat.catmod.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCatTnt extends ModelBase {

    private ModelRenderer tnt;

    public ModelCatTnt() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        // create the tnt model renderer
        this.tnt = new ModelRenderer(this, 0, 0);
        this.tnt.addBox(-4F, -4F, -4F, 8, 8, 8);
        this.tnt.setRotationPoint(0F, 16F, 0F);
    }

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale);
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale, entity);
        tnt.render(scale);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale, entity);
    }
}