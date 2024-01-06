package fr.iamacat.optimizationsandtweaks.mixins.common.buildcraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import buildcraft.BuildCraftCore;
import buildcraft.core.BlockSpring;
import buildcraft.core.SpringPopulate;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mixin(SpringPopulate.class)
public class MixinSpringPopulate {

    /**
     * @author
     * @reason
     */
    @SubscribeEvent
    @Overwrite(remap = false)
    public void populate(PopulateChunkEvent.Post event) {
        boolean doGen = TerrainGen.populate(
            event.chunkProvider,
            event.world,
            event.rand,
            event.chunkX,
            event.chunkZ,
            event.hasVillageGenerated,
            PopulateChunkEvent.Populate.EventType.CUSTOM);

        if (!doGen || !BlockSpring.EnumSpring.WATER.canGen) {
            event.setResult(Event.Result.ALLOW);
            return;
        }

        // shift to world coordinates
        int worldX = event.chunkX << 4;
        int worldZ = event.chunkZ << 4;

        doPopulate(event.world, event.rand, worldX, worldZ);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void doPopulate(World world, Random random, int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        if (!chunk.isChunkLoaded) {
            return;
        }
        int dimId = world.provider.dimensionId;
        // No water springs will generate in the Nether or End.
        if (dimId == -1 || dimId == 1) {
            return;
        }

        // A spring will be generated every 40th chunk.
        if (random.nextFloat() > 0.025f) {
            return;
        }

        int posX = x + random.nextInt(16);
        int posZ = z + random.nextInt(16);

        for (int i = 0; i < 5; i++) {
            Block candidate = world.getBlock(posX, i, posZ);

            if (candidate != Blocks.bedrock) {
                continue;
            }

            // Handle flat bedrock maps
            int y = i > 0 ? i : i - 1;

            world.setBlock(posX, y + 1, posZ, BuildCraftCore.springBlock);

            for (int j = y + 2; j < world.getHeight(); j++) {
                if (world.isAirBlock(posX, j, posZ)) {
                    break;
                } else {
                    world.setBlock(posX, j, posZ, Blocks.water);
                }
            }

            break;
        }
    }
}
