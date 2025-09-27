package fr.iamacat.optimizationsandtweaks.mixins.common.spiriteores;

import howl01.spiritores.SpiritOresGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import java.util.Random;
import fr.iamacat.optimizationsandtweaks.utilsformods.spiritores.SpiritOreConfig;
import howl01.spiritores.SpiritOres;

@Mixin(SpiritOresGenerator.class)
public class MixinSpiritOresGenerator {

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGenerate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) {
        int xBase = chunkX * 16;
        int zBase = chunkZ * 16;

        switch (world.provider.dimensionId) {
            case -1: // Nether
                if (SpiritOreConfig.CHARRED_LEAD.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.charredLeadOre, 7, 7, 10, 108, "netherrack");
                }
                if (SpiritOreConfig.AMETHYST.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.amethystOre, 7, 10, 10, 108, "netherrack");
                }
                if (SpiritOreConfig.MAGNETITE.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.magnetiteOre, 7, 20, 10, 108, "netherrack");
                }
                if (SpiritOreConfig.MAGMA_SPIRIT.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.magmaSpiritOre, 5, 5, 10, 108, "netherrack");
                }
                break;

            case 0: // Overworld
                if (SpiritOreConfig.ALUMINUM.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.aluminumOre, 6, 20, 0, 64, null);
                }
                if (SpiritOreConfig.COPPER.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.copperOre, 6, 20, 0, 64, null);
                }
                if (SpiritOreConfig.STEEL.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.steelOre, 5, 20, 0, 42, null);
                }
                if (SpiritOreConfig.SPIRIT.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.spiritOre, 3, 20, 0, 20, null);
                }
                break;

            case 1: // End
                if (SpiritOreConfig.IRIDITE.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.iriditeOre, 7, 5, 10, 64, "end_stone");
                }
                if (SpiritOreConfig.LILITHITE.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.lilithiteOre, 7, 5, 10, 64, "end_stone");
                }
                if (SpiritOreConfig.ENDESPIRIT.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.enderspiritOre, 5, 3, 10, 64, "end_stone");
                }
                if (SpiritOreConfig.TELENIUM.isEnabled()) {
                    generateOre(world, random, xBase, zBase, SpiritOres.teleniumOre, 7, 4, 10, 64, "end_stone");
                }
                break;
        }
        ci.cancel();
    }

    private void generateOre(World world, Random random, int xBase, int zBase, Block oreBlock, int veinSize, int iterations, int minY, int maxY, String targetBlockName) {
        WorldGenMinable generator;
        if (targetBlockName != null) {
            generator = new WorldGenMinable(oreBlock, veinSize, (Block) Block.blockRegistry.getObject(targetBlockName));
        } else {
            generator = new WorldGenMinable(oreBlock, veinSize);
        }

        for (int i = 0; i < iterations; i++) {
            int x = xBase + random.nextInt(16);
            int y = minY + random.nextInt(maxY - minY + 1);
            int z = zBase + random.nextInt(16);
            generator.generate(world, random, x, y, z);
        }
    }
}
