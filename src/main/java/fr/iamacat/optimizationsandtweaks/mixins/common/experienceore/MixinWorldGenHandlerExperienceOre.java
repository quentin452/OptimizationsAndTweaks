package fr.iamacat.optimizationsandtweaks.mixins.common.experienceore;

import cpw.mods.fml.common.IWorldGenerator;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.experienceore.ExperienceOreConfig;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.shad0wb1ade.experienceore.init.EOGameObjects;
import net.shad0wb1ade.experienceore.world.WorldGenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(WorldGenHandler.class)
public class MixinWorldGenHandlerExperienceOre implements IWorldGenerator {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (!(chunkGenerator instanceof ChunkProviderHell) && !(chunkGenerator instanceof ChunkProviderEnd)) {
            for(int i = 0; i < ExperienceOreConfig.experienceorefrequency; ++i) {
                int randPosX = chunkX * 16 + random.nextInt(16);
                int randPosY = random.nextInt(256);
                int randPosZ = chunkZ * 16 + random.nextInt(16);
                (new WorldGenMinable(EOGameObjects.ExperienceOre, 0, 8, Blocks.stone)).generate(world, random, randPosX, randPosY, randPosZ);
            }
        }
    }
}

