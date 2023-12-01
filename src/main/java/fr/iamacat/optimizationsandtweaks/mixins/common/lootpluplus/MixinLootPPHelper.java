package fr.iamacat.optimizationsandtweaks.mixins.common.lootpluplus;

import java.io.File;
import java.util.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.tmtravlr.lootplusplus.LootPPHelper;

@Mixin(LootPPHelper.class)
public class MixinLootPPHelper {

    @Shadow
    public static File idFolder;

    @Shadow
    public static boolean generateFiles = true;

    /**
     * @author
     * @reason
     */
    /*
     * @Overwrite
     * public static void loadRecipesAndSuch(World world) {
     * if (generateFiles) {
     * generateIDFiles();
     * }
     * ConfigLoaderOreDict.loadOreDict();
     * ConfigLoaderRecipes.loadRecipes(world);
     * ConfigLoaderRecipes.loadFurnaceRecipes();
     * ConfigLoaderEffects.loadItemEffects();
     * }
     */

    /**
     * @author iamacatfr
     * @reason optimize generateIDFiles
     */
    // todo fix Incompatible types. Found: 'java.lang.String', required: 'int'
    /*
     * @Overwrite
     * public static void generateIDFiles() {
     * boolean singlePlayer = MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer();
     * File blockItemIds = new File(idFolder, "Block and Item IDs.txt");
     * File biomeIds = new File(idFolder, "Biome IDs.txt");
     * File entityIds = new File(idFolder, "Entity IDs.txt");
     * File effectIds = new File(idFolder, "Effect IDs.txt");
     * File enchantIds = new File(idFolder, "Enchantment IDs.txt");
     * File foods = new File(idFolder, "Food and Drink Info.txt");
     * File oreDictionary = new File(idFolder, "Ore Dictionary Info.txt");
     * try {
     * PrintStream writeStream = new PrintStream(blockItemIds);
     * writeStream.println("## Item and Block IDs: in the form <string id> - <number id> ##");
     * ArrayList<String> itemNames = new ArrayList<>(Item.itemRegistry.getKeys());
     * Collections.sort(itemNames);
     * for (String key : itemNames) {
     * if (Item.itemRegistry.getObject(key) instanceof Item) {
     * writeStream.println(key + " - " + Item.getIdFromItem((Item) Item.itemRegistry.getObject(key)));
     * }
     * }
     * writeStream.close();
     * writeStream = new PrintStream(entityIds);
     * writeStream.println("## Entity IDs ##");
     * ArrayList<String> entityStrings = new ArrayList<>(EntityList.stringToClassMapping.keySet());
     * Collections.sort(entityStrings);
     * for (String key : entityStrings) {
     * writeStream.println(key);
     * }
     * writeStream.println("## Spawn Egg IDs: in the form <number id> - <entity string id (which it spawns)> ##");
     * ArrayList<Integer> eggIds = new ArrayList<>(EntityList.entityEggs.keySet());
     * Collections.sort(eggIds);
     * Iterator var25 = eggIds.iterator();
     * int key;
     * while(var25.hasNext()) {
     * key = (Integer)var25.next();
     * EntityList.EntityEggInfo eggInfo = (EntityList.EntityEggInfo)EntityList.entityEggs.get(key);
     * writeStream.println(key + " - " + EntityList.classToStringMapping.get(EntityList.IDtoClassMapping.get(key)) +
     * "\n");
     * }
     * writeStream.close();
     * writeStream = new PrintStream(biomeIds);
     * StringBuilder biomeStringBuilder = new StringBuilder();
     * biomeStringBuilder.append("## Biome IDs                                           ##\n");
     * biomeStringBuilder.append("#########################################################\n");
     * biomeStringBuilder.append("## In the form:                                        ##\n");
     * biomeStringBuilder.append("## <biome id>                                          ##\n");
     * biomeStringBuilder.append("##    - Name: <biome name>                             ##\n");
     * biomeStringBuilder.append("##    - Forge Biome Types: <list of forge biome types> ##\n");
     * biomeStringBuilder.append("#########################################################\n");
     * BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
     * for (BiomeGenBase biome : biomes) {
     * if (biome != null) {
     * biomeStringBuilder.append(biome.biomeID).append("\n");
     * biomeStringBuilder.append("    - Name: ").append(biome.biomeName).append("\n");
     * biomeStringBuilder.append("    - Forge Biome Types: ").append(Arrays.toString(biome.getBiomeClass().
     * getEnumConstants())).append("\n\n");
     * }
     * }
     * writeStream.println(biomeStringBuilder);
     * BiomeGenBase[] var26 = BiomeGenBase.getBiomeGenArray();
     * key = var26.length;
     * int var14;
     * for (var14 = 0; var14 < key; ++var14) {
     * BiomeGenBase biome = var26[var14];
     * if (biome != null) {
     * biomeStringBuilder.append(biome.biomeID).append("\n");
     * biomeStringBuilder.append("\t- Name: ").append(biome.biomeName).append("\n");
     * biomeStringBuilder.append("\t- Forge Biome Types: ");
     * BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
     * for (BiomeDictionary.Type type : types) {
     * biomeStringBuilder.append(type.name()).append(" ");
     * }
     * biomeStringBuilder.append("\n\n");
     * }
     * }
     * writeStream.close();
     * writeStream = new PrintStream(effectIds);
     * StringBuilder potionStringBuilder = new StringBuilder();
     * potionStringBuilder.append("## Effect IDs                                                        ##\n");
     * potionStringBuilder.append("#######################################################################\n");
     * potionStringBuilder.append("## In the form:                                                      ##\n");
     * potionStringBuilder.append("## <effect id>                                                       ##\n");
     * potionStringBuilder.append("##    - Name: <effect name>                                          ##\n");
     * potionStringBuilder.append("##    - Color: <effect color>                                        ##\n");
     * potionStringBuilder.append("##    - Is Instant? <true if an instant effect (like health)>        ##\n");
     * potionStringBuilder.append("##    - Is Bad? <true if considered a 'bad' effect (only on client)> ##\n");
     * potionStringBuilder.append("#######################################################################\n");
     * for (Potion effect : Potion.potionTypes) {
     * if (effect != null) {
     * potionStringBuilder.append(effect.id).append("\n");
     * potionStringBuilder.append("\t- Name: ").append(effect.getName()).append("\n");
     * potionStringBuilder.append("\t- Color: ").append(effect.getLiquidColor()).append("\n");
     * potionStringBuilder.append("\t- Is Instant? ").append(effect.isInstant()).append("\n");
     * if (singlePlayer) {
     * potionStringBuilder.append("\t- Is Bad? ").append(effect.isBadEffect()).append("\n");
     * }
     * potionStringBuilder.append("\n\n");
     * }
     * }
     * writeStream.close();
     * writeStream = new PrintStream(enchantIds);
     * StringBuilder enchantmentStringBuilder = new StringBuilder();
     * enchantmentStringBuilder.append("## Enchantment IDs                                ##\n");
     * enchantmentStringBuilder.append("####################################################\n");
     * enchantmentStringBuilder.append("## In the form:                                   ##\n");
     * enchantmentStringBuilder.append("## <enchantment id>                               ##\n");
     * enchantmentStringBuilder.append("##    - Name: <enchantment name>                  ##\n");
     * enchantmentStringBuilder.append("##    - Weight: <enchantment weight (rarity)>     ##\n");
     * enchantmentStringBuilder.append("##    - Min Level: <min xp level for enchantment> ##\n");
     * enchantmentStringBuilder.append("##    - Max Level: <max xp level for enchantment> ##\n");
     * enchantmentStringBuilder.append("####################################################\n");
     * for (Enchantment enchant : Enchantment.enchantmentsList) {
     * if (enchant != null) {
     * enchantmentStringBuilder.append(enchant.effectId).append("\n");
     * enchantmentStringBuilder.append("\t- Name: ").append(enchant.getName()).append("\n");
     * enchantmentStringBuilder.append("\t- Weight: ").append(enchant.getWeight()).append("\n");
     * enchantmentStringBuilder.append("\t- Min Level: ").append(enchant.getMinLevel()).append("\n");
     * enchantmentStringBuilder.append("\t- Max Level: ").append(enchant.getMaxLevel()).append("\n");
     * enchantmentStringBuilder.append("\n\n");
     * }
     * }
     * writeStream.close();
     * writeStream = new PrintStream(foods);
     * StringBuilder foodStringBuilder = new StringBuilder();
     * foodStringBuilder.append("## Foods and Drinks (Note it's still a bit WIP)                ##\n");
     * foodStringBuilder.append("#################################################################\n");
     * foodStringBuilder.append("## In the form:                                                ##\n");
     * foodStringBuilder.append("## <effect id>                                                 ##\n");
     * foodStringBuilder.append("##    - Metadata: <item metadata>                              ##\n");
     * foodStringBuilder.append("##    - Food or Drink? <Food or Drink (or Unknown)>            ##\n");
     * foodStringBuilder.append("##    - Food Amount: <Number of food points restored>          ##\n");
     * foodStringBuilder.append("##    - Saturation Amount: <Number of saturation points given> ##\n");
     * foodStringBuilder.append("################################################################\n");
     * var25 = itemNames.iterator();
     * while (true) {
     * do {
     * do {
     * if (!var25.hasNext()) {
     * StringBuilder stringBuilder = new StringBuilder();
     * writeStream.close();
     * writeStream = new PrintStream(oreDictionary);
     * stringBuilder.append("## Ore Dictionary Info                      ##\n");
     * stringBuilder.append("##############################################\n");
     * stringBuilder.append("## In the form:                             ##\n");
     * stringBuilder.append("## <ore dictionary entry>                   ##\n");
     * stringBuilder.append("##     <item id registered> - <item damage> ##\n");
     * stringBuilder.append("##     etc...                               ##\n");
     * stringBuilder.append("##############################################\n");
     * List<String> registeredEntries = Arrays.asList(OreDictionary.getOreNames());
     * Collections.sort(registeredEntries);
     * for (String entry : registeredEntries) {
     * stringBuilder.append(entry).append("\n");
     * for (ItemStack stack : OreDictionary.getOres(entry)) {
     * stringBuilder.append(Item.itemRegistry.getNameForObject(stack.getItem()))
     * .append(" - ")
     * .append(stack.getItemDamage() == 32767 ? "any" : stack.getItemDamage())
     * .append("\n");
     * }
     * stringBuilder.append("\n\n");
     * }
     * writeStream.println(stringBuilder);
     * writeStream.close();
     * return;
     * }
     * key = (String) var25.next();
     * } while (Item.itemRegistry.getObject(key) == null);
     * } while (!(Item.itemRegistry.getObject(key) instanceof Item));
     * Item possibleItem = (Item) Item.itemRegistry.getObject(key);
     * try {
     * possibleItem.getHasSubtypes();
     * List<ItemStack> subItems = new ArrayList<>();
     * if (singlePlayer) {
     * possibleItem.getSubItems(possibleItem, CreativeTabs.tabAllSearch, subItems);
     * }
     * if (subItems.isEmpty()) {
     * subItems.add(new ItemStack(possibleItem));
     * }
     * Iterator<ItemStack> var38 = subItems.iterator();
     * StringBuilder stringBuilder = new StringBuilder();
     * while (var38.hasNext()) {
     * ItemStack itemStack = var38.next();
     * EnumAction action = itemStack.getItemUseAction();
     * stringBuilder.append(key).append("\n");
     * stringBuilder.append("\t- Megadata: ").append(itemStack.getItemDamage()).append("\n");
     * if (itemStack.getItem() instanceof ItemFood) {
     * ItemFood itemFood = (ItemFood) itemStack.getItem();
     * stringBuilder.append("\t- Food or Drink? ").append(action == EnumAction.eat ? "Food" : (action ==
     * EnumAction.drink ? "Drink" : "Unknown")).append("\n");
     * stringBuilder.append("\t- Food Amount: ").append(itemFood.func_150905_g(new ItemStack(itemFood))).append("\n");
     * stringBuilder.append("\t- Saturation Amount: ").append(itemFood.func_150906_h(new
     * ItemStack(itemFood))).append("\n\n");
     * } else if (action == EnumAction.eat || action == EnumAction.drink) {
     * stringBuilder.append("\t- Food or Drink? ").append(action == EnumAction.eat ? "Food" : (action ==
     * EnumAction.drink ? "Drink" : "Unknown")).append("\n");
     * stringBuilder.append("\t- Food Info Unknown!\n\n");
     * }
     * }
     * writeStream.println(stringBuilder);
     * } catch (Exception var20) {
     * System.out.println("[Loot++] Caught Exception while writing food and drink info file.");
     * var20.printStackTrace();
     * }
     * }
     * } catch (IOException var21) {
     * System.err.format("IOException: %s%n", var21);
     * }
     * }
     */
}
