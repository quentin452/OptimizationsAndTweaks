package fr.iamacat.optimizationsandtweaks.mixins.common.buildcraft;

import buildcraft.BuildCraftTransport;
import buildcraft.api.facades.FacadeType;
import buildcraft.api.recipes.BuildcraftRecipeRegistry;
import buildcraft.api.transport.PipeWire;
import buildcraft.transport.ItemFacade;
import com.google.common.base.Strings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

@Mixin(ItemFacade.class)
public class MixinItemFacade {
    @Shadow
    public static final ArrayList<ItemStack> allFacades = new ArrayList<ItemStack>();
    @Shadow
    public static final ArrayList<ItemStack> allHollowFacades = new ArrayList<ItemStack>();
    @Shadow
    public static final ArrayList<String> allFacadeIDs = new ArrayList<String>();
    @Shadow
    public static final ArrayList<String> blacklistedFacades = new ArrayList<String>();
    @Shadow

    private static final Block NULL_BLOCK = null;
    @Shadow
    private static final ItemStack NO_MATCH = new ItemStack(NULL_BLOCK, 0, 0);
    @Shadow

    private static final Block[] PREVIEW_FACADES = new Block[] { Blocks.planks, Blocks.stonebrick, Blocks.glass };
    @Shadow
    private static int RANDOM_FACADE_ID = -1;
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void registerValidFacades(Block block, Item item) {
        ArrayList<ItemStack> stacks = new ArrayList<>(16);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            for (CreativeTabs ct : item.getCreativeTabs()) {
                block.getSubBlocks(item, ct, stacks);
            }
        } else {
            for (int i = 0; i < 16; i++) {
                stacks.add(new ItemStack(item, 1, i));
            }
        }

        for (ItemStack stack : stacks) {
            try {
                int i = stack.getItemDamage();

                if (!block.hasTileEntity(i) && (optimizationsAndTweaks$isValidFacade(stack))) {
                        addFacade(stack);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Unique
    private boolean optimizationsAndTweaks$isValidFacade(ItemStack stack) {
        try {
            return stack.getDisplayName() != null && !Strings.isNullOrEmpty(stack.getUnlocalizedName());
        } catch (Throwable ignored) {
            return false;
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void addFacade(ItemStack itemStack) {
        optimizationsAndTweaks$validateStackSize(itemStack);

        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block == null || !block.getMaterial().blocksMovement()) {
            return;
        }

        String recipeId = optimizationsAndTweaks$createRecipeId(block, itemStack);

        ItemStack facade = getFacadeForBlock(block, itemStack.getItemDamage());

        if (!allFacadeIDs.contains(recipeId)) {
            optimizationsAndTweaks$addFacadeItems(recipeId, facade, itemStack);
        }
    }

    @Unique
    private void optimizationsAndTweaks$validateStackSize(ItemStack itemStack) {
        if (itemStack.stackSize == 0) {
            itemStack.stackSize = 1;
        }
    }

    @Unique
    private String optimizationsAndTweaks$createRecipeId(Block block, ItemStack itemStack) {
        return "buildcraft:facade{" + Block.blockRegistry.getNameForObject(block)
            + "#" + itemStack.getItemDamage()
            + "}";
    }

    @Unique
    private void optimizationsAndTweaks$addFacadeItems(String recipeId, ItemStack facade, ItemStack itemStack) {
        allFacadeIDs.add(recipeId);
        allFacades.add(facade);

        ItemStack facade6 = facade.splitStack(1);
        facade6.stackSize = 6;

        ItemFacade.FacadeState state = getFacadeStates(facade6)[0];
        ItemStack facadeHollow = getFacade(new ItemFacade.FacadeState(state.block, state.metadata, state.wire, true));
        allHollowFacades.add(facadeHollow);

        ItemStack facade6Hollow = facadeHollow.splitStack(1);
        facade6Hollow.stackSize = 6;

        if (Loader.isModLoaded("BuildCraft|Silicon") && !BuildCraftTransport.facadeForceNonLaserRecipe) {
            int time = 8000;

            BuildcraftRecipeRegistry.assemblyTable.addRecipe(recipeId, time, facade6, optimizationsAndTweaks$getPipeStructureCobblestone(3), itemStack);
            BuildcraftRecipeRegistry.assemblyTable.addRecipe(recipeId + ":hollow", time, facade6Hollow, optimizationsAndTweaks$getPipeStructureCobblestone(3), itemStack);
            BuildcraftRecipeRegistry.assemblyTable.addRecipe(recipeId + ":toHollow", 160, facadeHollow, facade);
            BuildcraftRecipeRegistry.assemblyTable.addRecipe(recipeId + ":fromHollow", 160, facade, facadeHollow);
        } else {
            GameRegistry.addShapedRecipe(facade6, "t ", "ts", "t ", 't', itemStack, 's', BuildCraftTransport.pipeStructureCobblestone);
            GameRegistry.addShapedRecipe(facade6Hollow, "t ", " s", "t ", 't', itemStack, 's', BuildCraftTransport.pipeStructureCobblestone);
        }
    }

    @Unique
    private ItemStack optimizationsAndTweaks$getPipeStructureCobblestone(int amount) {
        return new ItemStack(BuildCraftTransport.pipeStructureCobblestone, amount);
    }
    @Shadow
    public static ItemFacade.FacadeState[] getFacadeStates(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return new ItemFacade.FacadeState[0];
        }
        NBTTagCompound nbt = stack.getTagCompound();
        nbt = migrate(stack, nbt);
        if (!nbt.hasKey("states")) {
            return new ItemFacade.FacadeState[0];
        }
        return ItemFacade.FacadeState.readArray(nbt.getTagList("states", Constants.NBT.TAG_COMPOUND));
    }

    @Shadow
    private static NBTTagCompound migrate(ItemStack stack, NBTTagCompound nbt) {
        Block block = null, blockAlt = null;
        int metadata = 0, metadataAlt;
        PipeWire wire = null;
        if (nbt.hasKey("id")) {
            block = (Block) Block.blockRegistry.getObjectById(nbt.getInteger("id"));
        } else if (nbt.hasKey("name")) {
            block = (Block) Block.blockRegistry.getObject(nbt.getString("name"));
        }
        if (nbt.hasKey("name_alt")) {
            blockAlt = (Block) Block.blockRegistry.getObject(nbt.getString("name_alt"));
        }
        if (nbt.hasKey("meta")) {
            metadata = nbt.getInteger("meta");
        }
        if (nbt.hasKey("meta_alt")) {
            metadataAlt = nbt.getInteger("meta_alt");
        } else {
            metadataAlt = stack.getItemDamage() & 0x0000F;
        }
        if (nbt.hasKey("wire")) {
            wire = PipeWire.fromOrdinal(nbt.getInteger("wire"));
        }
        if (block != null) {
            ItemFacade.FacadeState[] states;
            ItemFacade.FacadeState mainState = ItemFacade.FacadeState.create(block, metadata);
            if (blockAlt != null && wire != null) {
                ItemFacade.FacadeState altState = ItemFacade.FacadeState.create(blockAlt, metadataAlt, wire);
                states = new ItemFacade.FacadeState[] { mainState, altState };
            } else {
                states = new ItemFacade.FacadeState[] { mainState };
            }
            NBTTagCompound newNbt = getFacade(states).getTagCompound();
            stack.setTagCompound(newNbt);
            return newNbt;
        }
        return nbt;
    }
    @Shadow
    public static ItemStack getFacade(ItemFacade.FacadeState... states) {
        if (states == null || states.length == 0) {
            return null;
        }
        final boolean basic = states.length == 1 && states[0].wire == null;

        ItemStack stack = new ItemStack(BuildCraftTransport.facadeItem, 1, 0);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("type", (byte) (basic ? FacadeType.Basic : FacadeType.Phased).ordinal());
        nbt.setTag("states", ItemFacade.FacadeState.writeArray(states));

        stack.setTagCompound(nbt);
        return stack;
    }
    @Shadow
    public ItemStack getFacadeForBlock(Block block, int metadata) {
        return getFacade(ItemFacade.FacadeState.create(block, metadata));
    }

}
