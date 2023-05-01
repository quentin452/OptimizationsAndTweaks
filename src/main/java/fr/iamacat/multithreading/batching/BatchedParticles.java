package fr.iamacat.multithreading.batching;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;

public class BatchedParticles {
    public final double posX, posY, posZ;
    public static EntityFX[] particles;
    private final long startTime;
    private final long duration;

    public BatchedParticles(double posX, double posY, double posZ, EntityFX[] particles) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.particles = particles;
        this.startTime = System.nanoTime();
        this.duration = 5000000000L; // 5 seconds
    }

    public static void drawBatch(EntityFX[] particles, float partialTicks) {
        Tessellator tessellator = Tessellator.instance;
        for (int i = 0; i < particles.length; i++) {
            EntityFX particle = particles[i];
            double x = particle.lastTickPosX + (particle.posX - particle.lastTickPosX) * (double)(partialTicks * 1000.0F) - RenderManager.renderPosX;
            double y = particle.lastTickPosY + (particle.posY - particle.lastTickPosY) * (double)(partialTicks * 1000.0F) - RenderManager.renderPosY;
            double z = particle.lastTickPosZ + (particle.posZ - particle.lastTickPosZ) * (double)(partialTicks * 1000.0F) - RenderManager.renderPosZ;
            particle.renderParticle(tessellator, (float)x, (float)y, (float)z, 0.0F, 0.0F, 0.0F); // add the missing argument
        }
    }
}
