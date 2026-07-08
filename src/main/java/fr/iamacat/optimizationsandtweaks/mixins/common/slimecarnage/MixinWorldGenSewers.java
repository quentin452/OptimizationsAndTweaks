package fr.iamacat.optimizationsandtweaks.mixins.common.slimecarnage;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import supremopete.SlimeCarnage.commom.SlimeCarnage;
import supremopete.SlimeCarnage.worldgen.WorldGenSewers;

@Mixin(WorldGenSewers.class)
public abstract class MixinWorldGenSewers {

    @Shadow
    protected Block[] GetValidSpawnBlocks() {
        return new Block[] { Blocks.grass };
    }

    /**
     * @author iamacatfr
     * @reason tried fixing cascadings , but some cascading remains caused by LocationIsValidSpawn and can't fix it
     */
    @Overwrite(remap = false)
    public boolean LocationIsValidSpawn(World world, int posX, int posY, int posZ) {
        int chunkX = posX >> 4;
        int chunkZ = posZ >> 4;

        int distanceToAir = optimizationsAndTweaks$calculateDistanceToAir(world, posX, posY, posZ);

        if (distanceToAir > 3) {
            return false;
        }

        posY += distanceToAir - 1;
        if (posY >= world.getHeight()) {
            return false;
        }

        Block block = world.getBlock(posX, posY, posZ);
        Block blockBelow = world.getBlock(posX, posY - 1, posZ);
        Block[] validSpawnBlocks = GetValidSpawnBlocks();

        return optimizationsAndTweaks$isValidSpawnBlock(block, blockBelow, validSpawnBlocks);
    }

    @Unique
    private int optimizationsAndTweaks$calculateDistanceToAir(World world, int posX, int posY, int posZ) {
        int maxDistance = 3;
        int distance = 0;
        int chunkX = posX >> 4;
        int chunkZ = posZ >> 4;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        int chunkMinY = Math.max(0, posY);
        int chunkMaxY = Math.min(world.getHeight(), posY + maxDistance);

        for (int y = chunkMinY; y < chunkMaxY; y++) {
            Block currentBlock = chunk.getBlock(posX & 15, y, posZ & 15);

            if (currentBlock == Blocks.air) {
                break;
            }
            distance++;
        }
        return distance;
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidSpawnBlock(Block block, Block blockBelow, Block[] validSpawnBlocks) {
        for (Block validBlock : validSpawnBlocks) {
            if (block == validBlock || (block == Blocks.snow && blockBelow == validBlock)) {
                return true;
            }
        }
        return false;
    }

    // func_76484_a: the ORIGINAL SlimeCarnage WorldGenSewers#func_76484_a (verified against the
    // decompiled 1.0.5d jar) has no chunk-corner/bounds restriction at all - it runs whenever the 4x
    // LocationIsValidSpawn(...) checks pass. The OaT delta is exactly one additional guard: require the
    // call to land on the chunk's own origin (i/k multiples of 16, in [0,16)) and j in [0,256), evaluated
    // BEFORE the (still-untouched, still-@Overwrite'n) LocationIsValidSpawn checks via short-circuit &&.
    // No RNG is consumed anywhere in this method, so converting the guard to a HEAD @Inject that cancels
    // with `false` reproduces the exact same short-circuit behavior: the original bytecode (with its own
    // LocationIsValidSpawn calls + inline generation, now unmodified) only runs when the guard passes.
    @Inject(method = "func_76484_a", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizationsAndTweaks$guardChunkOrigin(World world, Random rand, int i, int j, int k,
        CallbackInfoReturnable<Boolean> cir) {
        int chunkX = i >> 4;
        int chunkZ = k >> 4;
        boolean inBounds = i >= 0 && i < 16
            && k >= 0
            && k < 16
            && j >= 0
            && j < 256
            && chunkX * 16 == i
            && chunkZ * 16 == k;
        if (!inBounds) {
            cir.setReturnValue(false);
        }
    }

    // generate1-generate5 (previously @Unique helpers extracted from the func_76484_a @Overwrite body)
    // were removed: they are dead code now that the original (unmodified) bytecode - which inlines the
    // same generation directly - runs whenever the guard above passes.

    @Shadow
    private ItemStack pickCheckLootItem(Random random) {
        int i = random.nextInt(31);
        if (i == 0) {
            return new ItemStack(SlimeCarnage.PizzaSlice, random.nextInt(2) + 1);
        } else if (i == 1) {
            return new ItemStack(SlimeCarnage.Banana, random.nextInt(2) + 1);
        } else if (i == 2 && random.nextInt(200) == 0) {
            return null;
        } else if (i == 3) {
            return null;
        } else if (i == 4) {
            return new ItemStack(Items.bucket, 1);
        } else if (i == 5) {
            return new ItemStack(SlimeCarnage.LimeJam, random.nextInt(4) + 1);
        } else if (i == 6) {
            return new ItemStack(Items.experience_bottle, random.nextInt(20) + 1);
        } else if (i == 7 && random.nextInt(5) == 0) {
            return new ItemStack(Items.name_tag, 1);
        } else if (i == 8) {
            return new ItemStack(Items.gold_ingot, random.nextInt(4) + 1);
        } else if (i == 9 && random.nextInt(10) == 0) {
            return new ItemStack(Items.record_cat, 1);
        } else if (i == 10) {
            return new ItemStack(Items.iron_ingot, random.nextInt(4) + 1);
        } else if (i == 11 && random.nextInt(20) == 0) {
            return new ItemStack(SlimeCarnage.GreenGelBoots, 1);
        } else if (i == 12 && random.nextInt(20) == 0) {
            return new ItemStack(SlimeCarnage.GreenGelLeggings, 1);
        } else if (i == 13 && random.nextInt(20) == 0) {
            return new ItemStack(SlimeCarnage.GreenGelChestplate, 1);
        } else if (i == 14 && random.nextInt(20) == 0) {
            return new ItemStack(SlimeCarnage.GreenGelHelmet, 1);
        } else if (i == 15 && random.nextInt(3) == 0) {
            return new ItemStack(SlimeCarnage.BlueGel, random.nextInt(12) + 1);
        } else if (i == 16 && random.nextInt(3) == 0) {
            return new ItemStack(SlimeCarnage.RedGel, random.nextInt(12) + 1);
        } else if (i == 17 && random.nextInt(3) == 0) {
            return new ItemStack(SlimeCarnage.YellowGel, random.nextInt(12) + 1);
        } else if (i == 18 && random.nextInt(3) == 0) {
            return new ItemStack(SlimeCarnage.GreenGel, random.nextInt(12) + 1);
        } else if (i == 19) {
            return new ItemStack(SlimeCarnage.OrangeGel, random.nextInt(12) + 1);
        } else if (i == 20) {
            return new ItemStack(SlimeCarnage.ScrollField, 1);
        } else if (i == 21) {
            return new ItemStack(SlimeCarnage.ScrollChurch, 1);
        } else if (i == 22) {
            return new ItemStack(SlimeCarnage.ScrollWell, 1);
        } else if (i == 23) {
            return new ItemStack(SlimeCarnage.ScrollBlacksmith, 1);
        } else if (i == 24) {
            return new ItemStack(SlimeCarnage.ScrollHouse1, 1);
        } else if (i == 25) {
            return new ItemStack(SlimeCarnage.ScrollHouse2, 1);
        } else if (i == 26 && random.nextInt(10) == 0) {
            return new ItemStack(SlimeCarnage.ScrollHouse3, 1);
        } else if (i == 27 && random.nextInt(10) == 0) {
            return new ItemStack(SlimeCarnage.ScrollHouse4, 1);
        } else if (i == 28) {
            return new ItemStack(Items.arrow, 16);
        } else if (i == 29) {
            return new ItemStack(Items.golden_apple, 1);
        } else {
            return i == 30 ? new ItemStack(Items.slime_ball, 4) : null;
        }
    }
}
