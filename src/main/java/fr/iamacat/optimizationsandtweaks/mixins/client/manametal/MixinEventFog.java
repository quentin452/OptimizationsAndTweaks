package fr.iamacat.optimizationsandtweaks.mixins.client.manametal;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import project.studio.manametalmod.core.IBiomeFogM3;
import project.studio.manametalmod.event.EventFog;

@Mixin(EventFog.class)
public class MixinEventFog {
    @Shadow private static double fogX;
    @Shadow private static double fogZ;
    @Shadow private static boolean fogInit;
    @Shadow private static float fogFarPlaneDistance;
    @Unique
    private BiomeGenBase[][] biomeCache = new BiomeGenBase[41][41]; 

    @SubscribeEvent
    @Overwrite(remap = false)
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        EntityLivingBase entity = event.entity;
        World world = entity.worldObj;
        int playerX = MathHelper.floor_double(entity.posX);
        int playerY = MathHelper.floor_double(entity.posY);
        int playerZ = MathHelper.floor_double(entity.posZ);
        
        if ((double)playerX == fogX && (double)playerZ == fogZ && fogInit) {
            renderFog(event.fogMode, fogFarPlaneDistance, 0.75F);
        } else {
            fogInit = true;
            int distance = 20;
            float fpDistanceBiomeFog = 0.0F;
            float weightBiomeFog = 0.0F;

            for (int x = -distance; x <= distance; ++x) {
                for (int z = -distance; z <= distance; ++z) {
                    
                    int cacheX = x + distance;
                    int cacheZ = z + distance;

                    BiomeGenBase biome = biomeCache[cacheX][cacheZ];
                    if (biome == null) {
                        biome = world.getBiomeGenForCoords(playerX + x, playerZ + z); 
                        biomeCache[cacheX][cacheZ] = biome;
                    }

                    if (biome instanceof IBiomeFogM3) {
                        float distancePart = ((IBiomeFogM3)biome).getFogDensity(playerX + x, playerY, playerZ + z);
                        float weightPart = 1.0F;
                        
                        if (x == -distance) {
                            double xDiff = (double)1.0F - (entity.posX - (double)playerX);
                            distancePart = (float)((double)distancePart * xDiff);
                            weightPart = (float)((double)weightPart * xDiff);
                        } else if (x == distance) {
                            double xDiff = entity.posX - (double)playerX;
                            distancePart = (float)((double)distancePart * xDiff);
                            weightPart = (float)((double)weightPart * xDiff);
                        }

                        if (z == -distance) {
                            double zDiff = (double)1.0F - (entity.posZ - (double)playerZ);
                            distancePart = (float)((double)distancePart * zDiff);
                            weightPart = (float)((double)weightPart * zDiff);
                        } else if (z == distance) {
                            double zDiff = entity.posZ - (double)playerZ;
                            distancePart = (float)((double)distancePart * zDiff);
                            weightPart = (float)((double)weightPart * zDiff);
                        }

                        fpDistanceBiomeFog += distancePart;
                        weightBiomeFog += weightPart;
                    } else {
                        biomeCache[cacheX][cacheZ] = null;
                    }
                }
            }

            float weightMixed = (float)(distance * 2 * distance * 2);
            float weightDefault = weightMixed - weightBiomeFog;
            float fpDistanceBiomeFogAvg = weightBiomeFog == 0.0F ? 0.0F : fpDistanceBiomeFog / weightBiomeFog;
            float farPlaneDistance = (fpDistanceBiomeFog * 240.0F + event.farPlaneDistance * weightDefault) / weightMixed;
            float farPlaneDistanceScaleBiome = 0.1F * (1.0F - fpDistanceBiomeFogAvg) + 0.75F * fpDistanceBiomeFogAvg;
            float farPlaneDistanceScale = (farPlaneDistanceScaleBiome * weightBiomeFog + 0.75F * weightDefault) / weightMixed;
            fogX = entity.posX; 
            fogZ = entity.posZ;
            fogFarPlaneDistance = Math.min(farPlaneDistance, event.farPlaneDistance);
            renderFog(event.fogMode, fogFarPlaneDistance, farPlaneDistanceScale);
        }
    }

    @Shadow
    private static void renderFog(int fogMode, float farPlaneDistance, float farPlaneDistanceScale) {
    }

}
