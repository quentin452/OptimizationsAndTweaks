package fr.iamacat.optimizationsandtweaks.utilsformods.sgstreasure;

import com.someguyssoftware.plans.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlansProcessor2 {
    public void construct(World world, int x, int y, int z, Plans plans) {
        List<Coords> postProcessBlocks = new ArrayList<>();
        Random random = new Random();
        Plans.log.debug("Processing the block map...");

        for(int mapY = 0; mapY < plans.getHeight(); ++mapY) {
            for(int mapZ = 0; mapZ < plans.getDepth(); ++mapZ) {
                for(int mapX = 0; mapX < plans.getWidth(); ++mapX) {
                    PlansBlockRule rule = plans.getRules().get(plans.getBlockMap()[mapY][mapZ][mapX]);
                    Plans.log.trace("Got rule " + rule);
                    int yOffset = y + plans.getVerticalOffset();
                    if (!plans.getPostProcessBlocks().isEmpty() && plans.getPostProcessBlocks().contains(rule.getBlockName())) {
                        Plans.log.trace("Save block for post process " + rule.getBlockName());
                        postProcessBlocks.add(new Coords(x + mapX, yOffset + mapY, z + mapZ, rule));
                    } else if (!plans.getPostProcess().isEmpty() && plans.getPostProcess().contains(rule.getBlockId())) {
                        Plans.log.trace("Save block for post process " + rule.getBlockId());
                        postProcessBlocks.add(new Coords(x + mapX, yOffset + mapY, z + mapZ, rule));
                    } else {
                        this.processBlock(world, random, x + mapX, yOffset + mapY, z + mapZ, rule, plans);
                    }
                }
            }
        }

        for (Coords coords : postProcessBlocks) {
            this.processBlock(world, random, coords.x, coords.y, coords.z, coords.rule, plans);
        }

        postProcessBlocks.clear();
    }

    private void processBlock(World world, Random random, int x, int y, int z, PlansBlockRule rule, Plans plans) {
        PlansProcessor plansProcessor = new PlansProcessor();
        Block block = null;
        byte meta = 0;
        Block worldBlock;
        if ((rule.getBlockName().equals("air") || rule.getBlockId() == 0) && !plans.getPreservePlansAir()) {
            Plans.log.trace("Equals air and preserving air");
        } else if (!plansProcessor.isEnableSpawners() && rule.getBlockName().equals("mob_spawner")) {
            Plans.log.info("Block is a SPAWNER, returning without generating - gen air instead");
            world.setBlock(x, y, z, Blocks.air, 0, 2);
        } else if (rule.getProbability() == 100 && (rule.getBlockName().equals("null_block") || rule.getSubstituteName().equals("null_block") || rule.getBlockId() == -1000 || rule.getSubstituteId() == -1000)) {
            Plans.log.trace("Equals Null block");
        } else if (plans.getSubstituteWithAirByName() != null && !plans.getSubstituteWithAirByName().isEmpty() && plans.getSubstituteWithAirByName().contains(rule.getBlockName())) {
            world.setBlock(x, y, z, Blocks.air, 0, 2);
            Plans.log.trace("Substituting for air");
        } else if (plans.getSubstituteWithAir() != null && !plans.getSubstituteWithAir().isEmpty() && plans.getSubstituteWithAir().contains(rule.getBlockId())) {
            world.setBlock(x, y, z, Blocks.air, 0, 2);
            Plans.log.trace("Substituting for air");
        } else {
            worldBlock = world.getBlock(x, y, z);
            Plans.log.trace("Got world block " + worldBlock.getUnlocalizedName());
            if (plans.getPreserveWorldWater() && worldBlock == Blocks.water) {
                Plans.log.trace("World block is water and preserve water is on");
            } else if (plans.getPreserveWorldLava() && worldBlock == Blocks.lava) {
                Plans.log.trace("World block is lava and preserve lava is on");
            } else if (plans.getPreserveWorldPlants() && worldBlock.isFoliage(world, x, y, z)) {
                Plans.log.trace("World block is plant and preserve is on");
            } else {
                if (rule.getProbability() != 100 && random.nextInt(100) > rule.getProbability()) {
                    Plans.log.trace("Block did not meet probablity threshold.");
                    if (!rule.getUseProbabilitySub()) {
                        return;
                    }

                    block = this.getProbabilitySubBlockFromRule(rule);
                    meta = rule.getProbabilityMeta();
                } else if (rule.isUseWorldBlockCondition()) {
                    Block conditionBlock = Block.getBlockFromName(rule.getWorldBlockName());
                    int conditionMeta = rule.getWorldBlockMeta();
                    if (worldBlock == conditionBlock && world.getBlockMetadata(x, y, z) == conditionMeta) {
                        block = Block.getBlockFromName(rule.getSubstituteName());
                        meta = (byte)rule.getSubstituteMeta();
                    }
                } else {
                    block = this.getBlockFromRule(rule);
                    meta = rule.getMetaId();
                }

                if (block == null) {
                    Plans.log.trace("Unable to get block for rule " + rule.getRuleId() + ". Block will not be processed. Moving to next block.");
                } else if (!rule.getRequiresSupport() || this.isSupportingBlock(world, x + 1, y, z) || this.isSupportingBlock(world, x - 1, y, z) || this.isSupportingBlock(world, x, y + 1, z) || this.isSupportingBlock(world, x, y - 1, z) || this.isSupportingBlock(world, x, y, z + 1) || this.isSupportingBlock(world, x, y, z - 1)) {
                    this.updateWorldBlock(world, x, y, z, block, meta);
                    this.processSpecials(world, random, x, y, z, block, rule, plans);
                }
            }
        }
    }
    private void processSpecials(World world, Random random, int x, int y, int z, Block block, PlansBlockRule rule, Plans plans) {
        if (block == Blocks.mob_spawner) {
            String spawns = "Zombie";
            if (rule.getSpawns() != null && !rule.getSpawns().isEmpty()) {
                spawns = rule.getSpawns().get(random.nextInt(rule.getSpawns().size()));
            }

            TileEntityMobSpawner spawner = new TileEntityMobSpawner();
            spawner.func_145881_a().setEntityName(spawns);
            world.setTileEntity(x, y, z, spawner);
        } else if (block == Blocks.chest) {
            Plans.log.debug("Block is Chest with inventory id of " + rule.getInventoryId());
            Inventory inventory = plans.getInventories().get(rule.getInventoryId());
            if (inventory != null) {
                TileEntityChest chest = (TileEntityChest)world.getTileEntity(x, y, z);
                if (chest != null) {

                    for (InventoryItem inventoryItem : inventory.getItems()) {
                        ItemStack itemStack;
                        if (inventoryItem.getName() != null && !inventoryItem.getName().isEmpty()) {
                            itemStack = new ItemStack((Item) Item.itemRegistry.getObject(inventoryItem.getName()), inventoryItem.getCount());
                        } else {
                            itemStack = new ItemStack(Item.getItemById(inventoryItem.getId()), inventoryItem.getCount());
                        }

                        itemStack.setItemDamage(inventoryItem.getDamage());
                        chest.setInventorySlotContents(inventoryItem.getSlot(), itemStack);
                    }
                }
            }
        }

    }
    public boolean isSupportingBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block != Blocks.air && block != Blocks.water && block != Blocks.lava && !block.isFoliage(world, x, y, z) && !block.getMaterial().isReplaceable() && !block.isLeaves(world, x, y, z);
    }

    public void updateWorldBlock(World world, int x, int y, int z, Block block, byte meta) {
        world.setBlock(x, y, z, block, meta, 2);
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
        Plans.log.trace("Updated world at " + x + " " + y + " " + z + ", meta " + meta);
    }

    public Block getProbabilitySubBlockFromRule(PlansBlockRule rule) {
        Block block = null;
        if (rule.getProbabilitySubName() != null && !rule.getProbabilitySubName().isEmpty()) {
            Plans.log.trace("Getting probability substitute by name:" + rule.getProbabilitySubName());
            block = Block.getBlockFromName(rule.getProbabilitySubName());
        }

        return block;
    }

    public Block getBlockFromRule(PlansBlockRule rule) {
        Block block;
        if (rule.getSubstituteName() != null && !rule.getSubstituteName().isEmpty()) {
            Plans.log.trace("Getting substitute by name:" + rule.getSubstituteName());
            if (rule.getSubstituteName().equals("null_block")) {
                return null;
            }

            block = Block.getBlockFromName(rule.getSubstituteName());
        } else if (rule.getSubstituteId() != -1) {
            Plans.log.trace("Getting substitute by id:" + rule.getSubstituteId());
            if (rule.getSubstituteId() == -1000) {
                return null;
            }

            block = Block.getBlockById(rule.getSubstituteId());
        } else if (rule.getBlockName() != null && !rule.getBlockName().equals("")) {
            Plans.log.trace("Getting block by name:" + rule.getBlockName());
            block = Block.getBlockFromName(rule.getBlockName());
        } else {
            Plans.log.trace("Getting block by name:" + rule.getBlockId());
            block = Block.getBlockById(rule.getBlockId());
        }

        return block;
    }

    private static class Coords {
        int x;
        int y;
        int z;
        PlansBlockRule rule;

        public Coords() {
        }

        public Coords(int x, int y, int z, PlansBlockRule rule) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rule = rule;
        }
    }
}
