package fr.iamacat.multithreading.tasking;

import net.minecraft.block.Block;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class ExplosionTask {

    private final Explosion explosion;
    private final World world;

    public ExplosionTask(Explosion explosion, World world) {
        this.explosion = explosion;
        this.world = world;
    }

    public void tickExplosion() {
        // Get the explosion position and strength
        int x = (int) Math.floor(explosion.explosionX);
        int y = (int) Math.floor(explosion.explosionY);
        int z = (int) Math.floor(explosion.explosionZ);
        float strength = explosion.explosionSize;

        // Loop through blocks in a subset of the explosion radius
        int radius = (int) Math.ceil(strength);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    // Calculate the distance from the explosion center to this block
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    // If the distance is less than the explosion strength, break the block and spawn particles
                    if (distance < strength) {
                        // Get the block position and ID
                        int blockX = x + dx;
                        int blockY = y + dy;
                        int blockZ = z + dz;
                        int blockID = world.getBlockMetadata(blockX, blockY, blockZ);

                        // Break the block and spawn particles
                        Block block = Block.getBlockById(blockID);
                        if (block != null && block.canDropFromExplosion(explosion)) {
                            block.dropBlockAsItem(
                                world,
                                blockX,
                                blockY,
                                blockZ,
                                world.getBlockMetadata(blockX, blockY, blockZ),
                                0);
                            world.setBlockToAir(blockX, blockY, blockZ);
                        }
                        world.spawnParticle("hugeexplosion", blockX + 0.5, blockY + 0.5, blockZ + 0.5, 0, 0, 0);
                    }
                }
            }
        }
    }
}
