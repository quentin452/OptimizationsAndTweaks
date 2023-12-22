package fr.iamacat.optimizationsandtweaks.asm;

import java.util.*;
import java.util.function.Predicate;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;

import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Mixin implements IMixin {

    // TWEAKING MIXINS

    common_core_MixinWorld(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinWorld,
        "core.MixinWorld"),
    common_lotrimprovements_MixinMain(Side.COMMON,
        require(TargetedMod.LORDOFTHERINGSFORK).and(m -> OptimizationsandTweaksConfig.enableMixinMain),
        "lotrimprovements.MixinMain"),
    common_lotr_MixinLOTRMod(Side.COMMON,
        require(TargetedMod.LORDOFTHERINGS).and(avoid(TargetedMod.LORDOFTHERINGSFORK)
            .and(m -> OptimizationsandTweaksConfig.enableMixinAddConfigForLOTRBIOMEIDS)),
        "lotr.MixinLOTRMod"),
    common_lotr_MixinLOTRBiome(Side.COMMON,
        require(TargetedMod.LORDOFTHERINGS).and(avoid(TargetedMod.LORDOFTHERINGSFORK)
            .and(m -> OptimizationsandTweaksConfig.enableMixinAddConfigForLOTRBIOMEIDS)),
        "lotr.MixinLOTRBiome"),
    common_lotr_MixinLOTRWorldProvider(Side.COMMON,
        avoid(TargetedMod.LORDOFTHERINGSFORK)
            .and(require(TargetedMod.ENDLESSIDS).and(m -> OptimizationsandTweaksConfig.MixinLOTRWorldProvider)),
        "lotr.MixinLOTRWorldProvider"),

    common_minestones_MixinItemMinestone(Side.COMMON, require(TargetedMod.MINESTONES).and(m -> OptimizationsandTweaksConfig.enableMixinItemMinestone),
        "minestones.MixinItemMinestone"),

    common_minestones_MixinMSConfig(Side.COMMON,
        require(TargetedMod.MINESTONES).and(m -> OptimizationsandTweaksConfig.enableMixinMinestoneSupportDecimalValue), "minestones.MixinMSConfig"),
    common_minestones_MixinMSEvents(Side.COMMON,
        require(TargetedMod.MINESTONES).and(m -> OptimizationsandTweaksConfig.enableMixinMinestoneSupportDecimalValue), "minestones.MixinMSEvents"),

    // OPTIMIZATIONS MIXINS
    server_core_MixinDedicatedServer(Side.SERVER, m -> OptimizationsandTweaksConfig.enableMixinDedicatedServer,
        "core.MixinDedicatedServer"),
    common_core_MixinFMLClientHandler(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinFMLClientHandler,
        "core.MixinFMLClientHandler"),
    common_core_MixinFMLServerHandler(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinFMLServerHandler,
        "core.MixinFMLServerHandler"),
    common_core_MixinMinecraft(Side.COMMON,
        avoid(TargetedMod.FALSETWEAKS).and(m -> OptimizationsandTweaksConfig.enableMixinMinecraft),
        "core.MixinMinecraft"),
    server_core_MixinMinecraftServerGui(Side.SERVER, m -> OptimizationsandTweaksConfig.enableMixinMinecraftServerGui,
        "core.MixinMinecraftServerGui"),
    common_core_MixinSaveFormatOld(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinSaveFormatOld,
        "core.MixinSaveFormatOld"),
    common_core_MixinThreadedFileIOBase(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinThreadedFileIOBase,
        "core.MixinThreadedFileIOBase"),

    common_core_MixinVec3(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinVec3, "core.MixinVec3"),

    common_core_MixinEntityAINearestAttackableTarget(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinEntityAINearestAttackableTarget,
        "core.MixinEntityAINearestAttackableTarget"),
    common_core_MixinEntityList(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityList,
        "core.MixinEntityList"),

    common_core_MixinNBTTagCompound(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinNBTTagCompound,
     "core.MixinNBTTagCompound"),
    common_core_MixinEntityArrowAttack(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityArrowAttack,
        "core.MixinEntityArrowAttack"),
    common_core_MixinEntityAITarget(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAITarget,
        "core.MixinEntityAITarget"),
    common_core_MixinAxisAlignedBB(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinAxisAlignedBB,
        "core.MixinAxisAlignedBB"),
    common_core_MixinEntityAITempt(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAITempt,
        "core.MixinEntityAITempt"),

    common_easybreeding_MixinEntityAIEatDroppedFood(Side.COMMON,
        require(TargetedMod.EASYBREEDING).and(m -> OptimizationsandTweaksConfig.enableMixinEntityAIEatDroppedFood),
        "easybreeding.MixinEntityAIEatDroppedFood"),
    common_core_MixinRandomPositionGenerator(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinRandomPositionGenerator, "core.MixinRandomPositionGenerator"),
    common_core_MixinEntityAIWander(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAIWander,
        "core.MixinEntityAIWander"),
    common_core_MixinEntityAIPlay(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAIPlay,
        "core.MixinEntityAIPlay"),
    common_core_MixinEntityAIAttackOnCollide(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinEntityAIAttackOnCollide, "core.MixinEntityAIAttackOnCollide"),

    common_core_MixinServersideAttributeMap(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinServersideAttributeMap, "core.MixinServersideAttributeMap"),
    common_core_MixinLowerStringMap(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinLowerStringMap,
        "core.MixinLowerStringMap"),
    common_core_MixinEntityLivingBase(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityLivingBase,
        "core.MixinEntityLivingBase"),
    common_core_MixinDataWatcher(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinDataWatcher,
        "core.MixinDataWatcher"),

    common_core_MixinNibbleArray(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinNibbleArray,
        "core.MixinNibbleArray"),
    common_blocklings_MixinEntityBlockling(Side.COMMON,
        require(TargetedMod.BLOCKLINGS).and(m -> OptimizationsandTweaksConfig.enableMixinSteamcraftEventHandler), "blocklings.MixinEntityBlockling"),
    common_flaxbeardssteampower_MixinSteamcraftEventHandler(Side.COMMON,
        require(TargetedMod.FLAXBEARDSTEAMPOWER).and(m -> OptimizationsandTweaksConfig.enableMixinSteamcraftEventHandler),
        "flaxbeardssteampower.MixinSteamcraftEventHandler"),
    common_catwalks2_MixinCommonProxy(Side.COMMON,
        require(TargetedMod.CATWALK2UNOFFICIAL).and(avoid(TargetedMod.CATWALK2OFFICIAL).and(m -> OptimizationsandTweaksConfig.enableMixinCommonProxyForCatWalks2)),
        "catwalks2.MixinCommonProxy"),
    common_buildcraft_addon_oiltweaks_MixinOilTweakEventHandler(Side.COMMON,
        require(TargetedMod.BUILDCRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinOilTweakEventHandler),
        "buildcraft.addon.oiltweaks.MixinOilTweakEventHandler"),
    common_core_MixinMinecraftServer(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinMinecraftServer,
        "core.MixinMinecraftServer"),
    common_core_MixinIntCache(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinIntCache,
        "core.MixinIntCache"),
    common_core_MixinNetworkManager(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinNetworkManager,
        "core.MixinNetworkManager"),
    common_core_MixinEntity(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntity,
        "core.MixinEntity"),

    common_core_MixinBlock(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinBlock,
        "core.MixinBlock"),
    common_core_MixinBlockLeaves(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinBlockLeaves,
        "core.MixinBlockLeaves"),

    common_core_MixinBiomeCache(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinBiomeCache,
        "core.MixinBiomeCache"),

    client_core_MixinRenderBlocks(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinRenderBlocks,
        "core.MixinRenderBlocks"),
    common_core_MixinStringTranslate(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinStringTranslate,
        "core.MixinStringTranslate"),
    common_core_MixinBlockDynamicLiquid(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinBlockDynamicLiquid,
        "core.MixinBlockDynamicLiquid"),
    common_core_MixinModifiableAttributeInstance(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinModifiableAttributeInstance,
        "core.MixinModifiableAttributeInstance"),
    common_core_MixinEntityTrackerEntry(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityTrackerEntry,
        "core.MixinEntityTrackerEntry"),

    common_core_MixinEntityTracker(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityTracker,
        "core.MixinEntityTracker"),
    common_core_MixinDimensionManager(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinDimensionManager,
        "core.MixinDimensionManager"),
    common_core_pathfinding_MixinPathFinder(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPathFinder,
        "core.pathfinding.MixinPathFinder"),
    server_core_MixinWorldServer(Side.SERVER, m -> OptimizationsandTweaksConfig.enableMixinWorldServer,
        "core.MixinWorldServer"),
    common_core_MixinStatsComponent(Side.SERVER, m -> OptimizationsandTweaksConfig.enableMixinStatsComponent,
        "core.MixinStatsComponent"),

    common_core_MixinChunk(Side.COMMON,
        avoid(TargetedMod.BLENDTRONIC).and(m -> OptimizationsandTweaksConfig.enableMixinChunk), "core.MixinChunk"),
    common_netherlicious_MixinNetherliciousEventHandler(Side.COMMON, require(TargetedMod.NETHERLICIOUS)
        .and(m -> OptimizationsandTweaksConfig.enableMixinNetherliciousEventHandler),
        "netherlicious.MixinNetherliciousEventHandler"),

    common_KoRIN_MixinKoRINEventHandler(Side.COMMON, require(TargetedMod.KORINBLUEBEDROCK)
        .and(m -> OptimizationsandTweaksConfig.enableMixinKoRINEventHandler),
        "KoRIN.MixinKoRINEventHandler"),
    common_minenautica_MixinBiomeRegistry(Side.COMMON, require(TargetedMod.MINENAUTICA)
        .and(m -> OptimizationsandTweaksConfig.enableMixinBiomeRegistryMinenautica),
        "minenautica.MixinBiomeRegistry"),
    common_minenautica_MixinMinenautica(Side.COMMON, require(TargetedMod.MINENAUTICA)
        .and(m -> OptimizationsandTweaksConfig.enableMixinBiomeRegistryMinenautica),
        "minenautica.MixinMinenautica"),
    common_minenautica_MixinAluminumOxideWorldGen(Side.COMMON, require(TargetedMod.MINENAUTICA)
        .and(m -> OptimizationsandTweaksConfig.enableMixinAluminumOxideWorldGen),
        "minenautica.MixinAluminumOxideWorldGen"),
    common_minenautica_MixinGenerateCoral(Side.COMMON, require(TargetedMod.MINENAUTICA)
        .and(m -> OptimizationsandTweaksConfig.enableMixinGenerateCoral),
        "minenautica.MixinGenerateCoral"),
    common_minenautica_MixinBloodgrass(Side.COMMON, require(TargetedMod.MINENAUTICA)
        .and(m -> OptimizationsandTweaksConfig.enableMixinBloodgrass),
        "minenautica.MixinBloodgrass"),
    common_minenautica_MixinCanBlockStay(Side.COMMON, require(TargetedMod.MINENAUTICA)
        .and(m -> OptimizationsandTweaksConfig.enableMixinCanBlockStay),
        "minenautica.MixinCanBlockStay"),
    common_minenautica_MixinBiomeGenGrassyPlateaus(Side.COMMON, require(TargetedMod.MINENAUTICA)
        .and(m -> OptimizationsandTweaksConfig.enableMixinBiomeGenGrassyPlateaus),
        "minenautica.MixinBiomeGenGrassyPlateaus"),
    common_runicdungeons_MixinCommonProxyRunicDungeons(Side.COMMON, require(TargetedMod.RUNICDUNGEONS).and(m -> OptimizationsandTweaksConfig.enableMixinCommonProxyRunicDungeons).and(require(TargetedMod.CONFIGHELPER)),
        "runicdungeons.MixinCommonProxyRunicDungeons"),
    common_akatsuki_MixinEntitySasori(Side.COMMON, require(TargetedMod.AKATSUKI).and(m -> OptimizationsandTweaksConfig.enableMixinEntitySasosri),
        "akatsuki.MixinEntitySasori"),
    common_akatsuki_MixinEntitySasori2(Side.COMMON, require(TargetedMod.AKATSUKI).and(m -> OptimizationsandTweaksConfig.enableMixinEntitySasosri2),
        "akatsuki.MixinEntitySasori2"),
    common_akatsuki_MixinPuppetKadz(Side.COMMON, require(TargetedMod.AKATSUKI).and(m -> OptimizationsandTweaksConfig.enableMixinPuppetKadz),
        "akatsuki.MixinPuppetKadz"),
    common_akatsuki_MixinAnimTickHandler(Side.COMMON, require(TargetedMod.AKATSUKI).and(m -> OptimizationsandTweaksConfig.enableMixinAnimTickHandler),
        "akatsuki.MixinAnimTickHandler"),
    common_akatsuki_MixinAnimationHandler(Side.COMMON, require(TargetedMod.AKATSUKI).and(m -> OptimizationsandTweaksConfig.enableMixinAnimationHandler),
        "akatsuki.MixinAnimationHandler"),
    common_aether_MixinPlayerAether(Side.COMMON, require(TargetedMod.AETHER).and(m -> OptimizationsandTweaksConfig.enableMixinPlayerAether),
        "aether.MixinPlayerAether"),
    common_aether_MixinPlayerAetherEvents(Side.COMMON, require(TargetedMod.AETHER).and(m -> OptimizationsandTweaksConfig.enableMixinPlayerAetherEvents),
        "aether.MixinPlayerAetherEvents"),
    common_cofhcore_MixinHooksCore(Side.COMMON, require(TargetedMod.COFHCORE).and(m -> OptimizationsandTweaksConfig.enableMixinHooksCore),
        "cofhcore.MixinHooksCore"),
    common_thaumicrevelations_MixinWardenicChargeEvents(Side.COMMON, require(TargetedMod.THAUMICREVELATIONS).and(m -> OptimizationsandTweaksConfig.enableMixinWardenicChargeEvents),
        "thaumicrevelations.MixinWardenicChargeEvents"),
    common_aether_MixinAetherTileEntities(Side.COMMON, require(TargetedMod.AETHER).and(m -> OptimizationsandTweaksConfig.enableMixinAetherTileEntities),
        "aether.MixinAetherTileEntities"),
    common_etfuturumrequiem_MixinUtils(Side.COMMON, require(TargetedMod.ETFUTURMREQUIEM).and(m -> OptimizationsandTweaksConfig.enableMixinUtils),
        "etfuturumrequiem.MixinUtils"),
    common_notenoughpets_MixinEventHandlerNEP(Side.COMMON, require(TargetedMod.NOTENOUGHPETS).and(m -> OptimizationsandTweaksConfig.enableMixinEventHandlerNEP),
        "notenoughpets.MixinEventHandlerNEP"),
    common_pneumaticraft_MixinHackTickHandler(Side.COMMON, require(TargetedMod.PNEUMATICRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinHackTickHandler),
        "pneumaticraft.MixinHackTickHandler"),
    common_ic2_MixinPriorityExecutor(Side.COMMON, require(TargetedMod.INDUSTRIALCRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinPriorityExecutor),
        "ic2.MixinPriorityExecutor"),

    common_growthcraft_MixinAppleFuelHandler(Side.COMMON,  require(TargetedMod.GROWTHCRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinAppleFuelHandler),
        "growthcraft.MixinAppleFuelHandler"),
    common_core_MixinBlockGrass(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinBlockGrass,
        "core.MixinBlockGrass"),
    common_adventurersamulet_MixinEntityEagle(Side.COMMON,  require(TargetedMod.ADVENTURERS_AMULETS).and(m -> OptimizationsandTweaksConfig.enableMixinEntityEagle),
        "adventurersamulet.MixinEntityEagle"),
    common_core_MixinEntityLiving(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityLiving,
        "core.MixinEntityLiving"),
    common_core_MixinEntityAgeable(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAgeable,
        "core.MixinEntityAgeable"),
    common_nei_MixinNEIServerUtils(Side.COMMON,  require(TargetedMod.NEI).and(m -> OptimizationsandTweaksConfig.enableMixinNEIServerUtils),
        "nei.MixinNEIServerUtils"),
    common_nei_MixinConfig(Side.COMMON,  require(TargetedMod.NEI).and(m -> OptimizationsandTweaksConfig.enableMixinConfig), "ic2.MixinConfig"),
    common_core_MixinBlockLiquid(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinBlockLiquid,
        "core.MixinBlockLiquid"),
    common_core_entity_MixinEntityZombie(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityZombie,
        "core.entity.MixinEntityZombie"),

    common_core_entity_MixinEntityItem(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityItem,
        "core.entity.MixinEntityItem"),
    common_ic2_MixinTickHandler(Side.COMMON,  require(TargetedMod.INDUSTRIALCRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinTickHandler),
        "ic2.MixinTickHandler"),

    common_core_entity_MixinEntityAnimal(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAnimal,
        "core.entity.MixinEntityAnimal"),
    common_core_entity_MixinEntitySquid(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntitySquid,
        "core.entity.MixinEntitySquid"),
    common_core_MixinEntityAITasks(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAITasks,
        "core.MixinEntityAITasks"),
    common_core_MixinEntityMoveHelper(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityMoveHelper,
        "core.MixinEntityMoveHelper"),
    common_core_MixinWorldGenMinable(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinWorldGenMinable,
        "core.MixinWorldGenMinable"),
    common_core_MixinLeaves(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinLeaves, "core.MixinLeaves"),

    common_core_MixinEntityAIFollowParent(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinEntityAIFollowParent, "core.MixinEntityAIFollowParent"),

    common_jewelrycraft2_MixinEntityEventHandler(Side.COMMON,
        require(TargetedMod.JEWELRYCRAFT2).and(m -> OptimizationsandTweaksConfig.enableMixinEntityEventHandler), "jewelrycraft2.MixinEntityEventHandler"),
    common_portalgun_MixinSettings(Side.COMMON,  require(TargetedMod.PORTALGUN).and(m -> OptimizationsandTweaksConfig.enableMixinSettings),
        "portalgun.MixinSettings"),
    common_core_MixinEntityLookHelper(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityLookHelper,
        "core.MixinEntityLookHelper"),
    common_core_MixinBlockFalling(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinBlockFalling,
        "core.MixinBlockFalling"),

    common_koto_MixinPatchWorldGenCloudNine(Side.COMMON,
        require(TargetedMod.KINGDOMSOFTHEOVERWORLD).and(m -> OptimizationsandTweaksConfig.enableMixinPatchWorldGenCloudNine), "koto.MixinPatchWorldGenCloudNine"),
    common_koto_MixinEntityDarkMiresi(Side.COMMON, require(TargetedMod.KINGDOMSOFTHEOVERWORLD).and(m -> OptimizationsandTweaksConfig.enableMixinEntityDarkMiresi),
        "koto.MixinEntityDarkMiresi"),
    common_thaumcraft_MixinMappingThread(Side.COMMON, require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinMappingThread),
        "thaumcraft.MixinMappingThread"),
    common_thaumcraft_MixinEventHandlerEntity(Side.COMMON, require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinEventHandlerEntity),
        "thaumcraft.MixinEventHandlerEntity"),
    common_codechickencore_MixinClassDiscoverer(Side.COMMON,
        require(TargetedMod.CODECHICKENCORE).and(m -> OptimizationsandTweaksConfig.enableMixinClassDiscoverer), "codechickencore.MixinClassDiscoverer"),

    common_lootpluplus_MixinLootPPHelper(Side.COMMON, require(TargetedMod.LOOTPLUSPLUS).and(m -> OptimizationsandTweaksConfig.enableMixinLootPPHelper),
        "lootpluplus.MixinLootPPHelper"),
    common_matmos_MixinForgeBase(Side.COMMON, require(TargetedMod.MATMOS).and(m -> OptimizationsandTweaksConfig.enableOptimizeMatmos),
        "matmos.MixinForgeBase"),
    common_matmos_MixinBlockCountModule(Side.COMMON, require(TargetedMod.MATMOS).and(m -> OptimizationsandTweaksConfig.enableOptimizeMatmos),
        "matmos.MixinBlockCountModule"),

    common_matmos_MixinScanVolumetric(Side.COMMON, require(TargetedMod.MATMOS).and(m -> OptimizationsandTweaksConfig.enableOptimizeMatmos),
        "matmos.MixinScanVolumetric"),

    common_matmos_MixinScanRaycast(Side.COMMON, require(TargetedMod.MATMOS).and(m -> OptimizationsandTweaksConfig.enableOptimizeMatmos),
        "matmos.MixinScanRaycast"),
    common_matmos_MixinScannerModule(Side.COMMON, require(TargetedMod.MATMOS).and(m -> OptimizationsandTweaksConfig.enableOptimizeMatmos),
        "matmos.MixinScannerModule"),
    common_matmos_MixinSheetDataPackage(Side.COMMON, require(TargetedMod.MATMOS).and(m -> OptimizationsandTweaksConfig.enableOptimizeMatmos),
        "matmos.MixinSheetDataPackage"),
    common_thaumcraftminusthaumcraft_MixinUnthaumic(Side.COMMON, require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinUnthaumic),
        "thaumcraftminusthaumcraft.MixinUnthaumic"),
    common_thaumcraft_MixinPatchBiomeGenMagicalForest(Side.COMMON,
        require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinPatchBiomeGenMagicalForest),
        "thaumcraft.MixinPatchBiomeGenMagicalForest"),
    common_thaumcraft_MixinPatchBlockMagicalLeavesPerformances(Side.COMMON,
        require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinPatchBlockMagicalLeavesPerformances),
        "thaumcraft.MixinPatchBlockMagicalLeavesPerformances"),

    // BUGFIX MIXINS
    common_ragdollcorpse_MixinEventHandler_Ragdoll(Side.COMMON, require(TargetedMod.RAGDOLLCORPSE).and(m -> OptimizationsandTweaksConfig.enableMixinRagdollCorpse),
        "ragdollcorpse.MixinEventHandler_Ragdoll"),
    common_thaumcraft_MixinScanManager(Side.COMMON, require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinScanManager),
        "thaumcraft.MixinScanManager"),
    common_eternalfrost_MixinEFConfiguration(Side.COMMON,
        require(TargetedMod.ETERNALFROST).and(m -> OptimizationsandTweaksConfig.enableMixinEFConfiguration),
        "eternalfrost.MixinEFConfiguration"),

    common_blocklings_MixinItemBlockling(Side.COMMON, require(TargetedMod.BLOCKLINGS).and(m -> OptimizationsandTweaksConfig.enableMixinItemBlockling),
        "blocklings.MixinItemBlockling"),
    common_nei_MixinWorldOverlayRenderer(Side.COMMON, require(TargetedMod.NEI).and(m -> OptimizationsandTweaksConfig.enableMixinWorldOverlayRenderer),
        "nei.MixinWorldOverlayRenderer"),
    common_buildcraft_addon_oiltweaks_MixinBuildCraftConfig(Side.COMMON,
        require(TargetedMod.BUILDCRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinBuildCraftConfig),
        "buildcraft.addon.oiltweaks.MixinBuildCraftConfig"),

    common_diseasecraft_MixinMedUtils(Side.COMMON,
        require(TargetedMod.DISEASECRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinMedUtils),
        "diseasecraft.MixinMedUtils"),

    common_industrialupgrade_MixinRegisterOreDict(Side.COMMON,
        require(TargetedMod.INDUSTRIALUPGRADE).and(m -> OptimizationsandTweaksConfig.enableMixinRegisterOreDict), "industrialupgrade.MixinRegisterOreDict"),
    common_gemsnjewels_MixinModBlocksGemsNJewels(Side.COMMON,
        require(TargetedMod.GEMSNJEWELS).and(m -> OptimizationsandTweaksConfig.enableMixinModBlocksGemsNJewels), "gemsnjewels.MixinModBlocksGemsNJewels"),
    common_farlanders_MixinEntityEnderGolem(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityEnderGolem"),
    common_farlanders_MixinEntityEnderminion(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityEnderminion"),
    common_farlanders_MixinEntityFarlander(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityFarlander"),
    common_farlanders_MixinEntityLootr(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityLootr"),
    common_farlanders_MixinEntityMysticEnderminion(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityMysticEnderminion"),
    common_farlanders_MixinEntityWanderer(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityWanderer"),
    common_farlanders_MixinEntityTitan(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityTitan"),
    common_farlanders_MixinEntityRebel(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityRebel"),
    common_farlanders_MixinEntityMystic(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityMystic"),
    common_farlanders_MixinEntityEnderGuardian(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityEnderGuardian"),
    common_farlanders_MixinEntityElder(Side.COMMON,
        require(TargetedMod.FARLANDERS).and( m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityElder"),
    common_farlanders_MixinEntityFanEnderman(Side.COMMON,
        require(TargetedMod.FARLANDERS).and(m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod),
        "farlanders.MixinEntityFanEnderman"),
    common_thaumcraft_MixinFixCascadingWorldGenFromThaumcraftWorldGenerator(Side.COMMON,
        require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator),
        "thaumcraft.MixinFixCascadingWorldGenFromThaumcraftWorldGenerator"),
    common_thaumcraft_MixinWorldGenGreatwoodTrees(Side.COMMON,
        require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinWorldGenGreatwoodTrees),
        "thaumcraft.MixinWorldGenGreatwoodTrees"),

    common_thaumcraft_MixinWorldGenEldritchRing(Side.COMMON,
        require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinWorldGenEldritchRing),
        "thaumcraft.MixinWorldGenEldritchRing"),

    common_thaumcraft_MixinThaumcraftUtils(Side.COMMON,
        require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinThaumcraftUtils),
        "thaumcraft.MixinThaumcraftUtils"),


    common_thaumcraft_MixinWorldGenCustomFlowersSide(Side.COMMON,
        require(TargetedMod.THAUMCRAFT4).and(m -> OptimizationsandTweaksConfig.enableMixinWorldGenCustomFlowers),
        "thaumcraft.MixinWorldGenCustomFlowers"),
    common_gardenstuff_MixinWorldGenCandelilla(Side.COMMON,
        require(TargetedMod.GARDENSTUFF).and(m -> OptimizationsandTweaksConfig.enableMixinWorldGenCandelilla),
        "gardenstuff.MixinWorldGenCandelilla"),
    common_pamsharvestcraft_MixinFixWorldGenPamFruitTree(
        Side.COMMON,
        require(TargetedMod.PAMSHARVESTCRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinFixWorldGenPamFruitTree),
        "pamsharvestcraft.MixinFixWorldGenPamFruitTree"
    ),
    common_steamcraft2_MixinFixCascadingFromWorldGenBrassTree(Side.COMMON,
        require(TargetedMod.STEAMCRAFT2).and(m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromWorldGenBrassTree),
        "steamcraft2.MixinFixCascadingFromWorldGenBrassTree"),
    common_cofhcore_fixoredictcrash_MixinOreDictionaryArbiter(Side.COMMON,
        require(TargetedMod.COFHCORE).and(m -> OptimizationsandTweaksConfig.enableMixinOreDictCofhFix),
        "cofhcore.fixoredictcrash.MixinOreDictionaryArbiter"),
    common_cofhcore_MixinBlockTickingWater(Side.COMMON, require(TargetedMod.COFHCORE).and(m -> OptimizationsandTweaksConfig.enableMixinBlockTickingWater),
        "cofhcore.MixinBlockTickingWater"),
    common_slimecarnage_MixinFixCascadingFromWorldGenSlimeCarnage(Side.COMMON,
        require(TargetedMod.SLIMECARNAGE).and(m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromWorldGenSlimeCarnage),
        "slimecarnage.MixinFixCascadingFromWorldGenSlimeCarnage"),

    common_slimecarnage_MixinWorldGenSewers(Side.COMMON,
        require(TargetedMod.SLIMECARNAGE).and(m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromWorldGenSlimeCarnage),
        "slimecarnage.MixinWorldGenSewers"),
    common_familliarsAPI_MixinFamiliar(Side.COMMON, require(TargetedMod.FAMILIARSAPI).and(m -> OptimizationsandTweaksConfig.enableMixinFamiliar),
        "familliarsAPI.MixinFamiliar"),
    common_pamsharvestcraft_MixinFixPamsTreesCascadingWorldgenLag(Side.COMMON,
        require(TargetedMod.PAMSHARVESTCRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinFixPamsTreesCascadingWorldgenLag),
        "pamsharvestcraft.MixinFixPamsTreesCascadingWorldgenLag"),
    common_hardcorewither_MixinEventHandler(Side.COMMON, require(TargetedMod.HARDCOREWITHER).and(m -> OptimizationsandTweaksConfig.enableMixinEventHandler),
        "hardcorewither.MixinEventHandler"),
    common_shincolle_MixinEVENT_BUS_EventHandler(Side.COMMON,
        require(TargetedMod.SHINCOLLE).and(m -> OptimizationsandTweaksConfig.enableMixinEVENT_BUS_EventHandler), "shincolle.MixinEVENT_BUS_EventHandler"),
    common_betterburning_MixinBetterBurning(Side.COMMON, require(TargetedMod.BETTERBURNING).and(m -> OptimizationsandTweaksConfig.enableMixinBetterBurning),
        "betterburning.MixinBetterBurning"),
    common_MixinFixCascadingFromShipwreckGen(Side.COMMON,
        require(TargetedMod.SHIPEWRECK).and(m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromShipwreckGen),
        "shipwreck.MixinFixCascadingFromShipwreckGen"),
    common_shincolle_MixinFixCascadingFromWorldGenPolyGravel(Side.COMMON,
        require(TargetedMod.SHINCOLLE).and(m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromWorldGenPolyGravel),
        "shincolle.MixinFixCascadingFromWorldGenPolyGravel"),
    common_shincolle_MixinFixCascadingFromShinColleWorldGen(Side.COMMON,
        require(TargetedMod.SHINCOLLE).and(m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromShinColleWorldGen),
        "shincolle.MixinFixCascadingFromShinColleWorldGen"),

    common_minefactoryreloaded_MixinFixCascadingforMineFactoryReloadedWorldGen(Side.COMMON,
        require(TargetedMod.MINEFACTORYRELOADED)
            .and(m -> OptimizationsandTweaksConfig.enableMixinFixCascadingforMineFactoryReloadedWorldGen),
        "minefactoryreloaded.MixinFixCascadingforMineFactoryReloadedWorldGen"),
    common_minefactoryreloaded_MixinFixWorldGenLakesMetaCascadingWorldgenLag(Side.COMMON,
        require(TargetedMod.MINEFACTORYRELOADED).and(
            m -> OptimizationsandTweaksConfig.enableMixinFixWorldGenLakesMetaMinefactoryReloadedCascadingWorldgenFix),
        "minefactoryreloaded.MixinFixWorldGenLakesMetaCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixRubberTreesCascadingWorldgenLag(Side.COMMON,
        require(TargetedMod.MINEFACTORYRELOADED)
            .and(m -> OptimizationsandTweaksConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix),
        "minefactoryreloaded.MixinFixRubberTreesCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixNoSuchMethodException(Side.COMMON,
        require(TargetedMod.MINEFACTORYRELOADED)
            .and(m -> OptimizationsandTweaksConfig.enableMixinFixNoSuchMethodException),
        "minefactoryreloaded.MixinFixNoSuchMethodException"),

    common_core_MixinGodZillaFix(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinGodZillaFix,
        "core.MixinGodZillaFix"),
    common_witchery_MixinGenericEventsWitchery(Side.COMMON,m -> OptimizationsandTweaksConfig.enableMixinGenericEventsWitchery,
        "witchery.MixinGenericEventsWitchery"),
    common_opis_MixinopisProfilerEvent(Side.COMMON, require(TargetedMod.OPIS).and(m -> OptimizationsandTweaksConfig.enableMixinopisProfilerEvent),
        "opis.MixinopisProfilerEvent"),
    common_core_MixinStatList(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinStatList, "core.MixinStatList"),

    common_malcore_MixinVersionInfo(Side.COMMON, require(TargetedMod.MALCORE).and(m -> OptimizationsandTweaksConfig.enableMixinVersionInfo),
        "malcore.MixinVersionInfo"),

    common_kitchencraft_MixinKitchenCraftMachines(Side.COMMON,
        require(TargetedMod.KITCHENCRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinKitchenCraftMachines), "kitchencraft.MixinKitchenCraftMachines"),

    // CLIENT MIXINS

    client_core_MixinGuiNewChat(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinGuiNewChat,
        "core.MixinGuiNewChat"),
    client_instrumentus_MixinPlayerSpecials(Side.CLIENT, require(TargetedMod.INSTRUMENTUS).and(m -> OptimizationsandTweaksConfig.enableMixinPlayerSpecials),
        "instrumentus.MixinPlayerSpecials"),

    client_core_MixinGui(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinGui, "core.MixinGui"),
    client_core_MixinRenderItem(Side.CLIENT,
        avoid(TargetedMod.FASTCRAFT).and(m -> OptimizationsandTweaksConfig.enableMixinRenderItem),
        "core.MixinRenderItem"),
    client_core_MixinRenderGlobal(Side.CLIENT, avoid(TargetedMod.FASTCRAFT).and(avoid(TargetedMod.OPTIFINE))
        .and(m -> OptimizationsandTweaksConfig.enableMixinRenderGlobal), "core.MixinRenderGlobal"),
    client_core_MixinRenderManager(Side.CLIENT,
        avoid(TargetedMod.SKINPORT).and(m -> OptimizationsandTweaksConfig.enableMixinRenderManager),
        "core.MixinRenderManager"),
    client_essenceofthegod_MixinBarTickHandler(Side.CLIENT,
        m -> OptimizationsandTweaksConfig.enableMixindisablingguifromEssenceofthegod,
        "essenceofthegod.MixinBarTickHandler"),
    client_essenceofthegod_MixinPlayerStats(Side.CLIENT,
        m -> OptimizationsandTweaksConfig.enableMixindisablingguifromEssenceofthegod,
        "essenceofthegod.MixinPlayerStats"),

    client_davincivessels_MixinShipKeyHandler(Side.CLIENT, require(TargetedMod.DAVINCIVESSELS).and(m -> OptimizationsandTweaksConfig.enableMixinShipKeyHandler),
        "davincivessels.MixinShipKeyHandler"),
    client_practicallogistics_MixinEventRegistry(Side.CLIENT,
        require(TargetedMod.PRACTICALLOGISTICS).and(m -> OptimizationsandTweaksConfig.enableMixinEventRegistry), "practicallogistics.MixinEventRegistry"),
    client_core_MixinTextureManager(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinTextureManager,
        "core.MixinTextureManager"),
    client_core_MixinEntitySpellParticleFX(Side.CLIENT,
        m -> OptimizationsandTweaksConfig.enableMixinEntitySpellParticleFX, "core.MixinEntitySpellParticleFX"),
    client_core_MixinCodecIBXM(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinCodecIBXM,
        "core.MixinCodecIBXM"),

    client_core_MixinTesselator(Side.CLIENT,
        avoid(TargetedMod.OPTIFINE).and(m -> OptimizationsandTweaksConfig.enableMixinTesselator),
        "core.MixinTesselator"),
    /*
     * common_core_MixinPatchSpawnerAnimals(Side.COMMON,
     * avoid(TargetedMod.JAS)
     * .and(avoid(TargetedMod.DRAGONBLOCKC))
     * .and(m -> OptimizationsandTweaksConfig.enableMixinPatchSpawnerAnimals),
     * "core.MixinPatchSpawnerAnimals"),
     */

    common_core_MixinPatchSpawnerAnimals(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPatchSpawnerAnimals,
        "core.MixinPatchSpawnerAnimals"),
    client_core_MixinOpenGlHelper(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinOpenGlHelper,
        "core.MixinOpenGlHelper"),
    client_core_MixinEntityRenderer(Side.CLIENT, avoid(TargetedMod.OPTIFINE).and(avoid(TargetedMod.FASTCRAFT))
        .and(m -> OptimizationsandTweaksConfig.enableMixinEntityRenderer), "core.MixinEntityRenderer"),
    client_core_MixinModelRenderer(Side.CLIENT,
        avoid(TargetedMod.OPTIFINE).and(m -> OptimizationsandTweaksConfig.enableMixinModelRenderer),
        "core.MixinModelRenderer"),
    client_core_MixinTextureUtil(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinTextureUtil,
        "core.MixinTextureUtil"),
    client_core_MixinItemRenderer(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinItemRenderer,
        "core.MixinItemRenderer"),
    client_core_MixinFontRenderer(
        Side.CLIENT,
        avoid(TargetedMod.OPTIFINE).and(m -> OptimizationsandTweaksConfig.enableMixinFontRenderer).and(avoid(TargetedMod.SMOOTHFONT)),
        "core.MixinFontRenderer"
    ),

    // MOD-FILTERED MIXINS

    // The modFilter argument is a predicate, so you can also use the .and(), .or(), and .negate() methods to mix and
    // match multiple predicates.
    ;

    @Getter
    public final Side side;
    @Getter
    public final Predicate<List<ITargetedMod>> filter;
    @Getter
    public final String mixin;

    static Predicate<List<ITargetedMod>> require(TargetedMod in) {
        return modList -> modList.contains(in);
    }

    static Predicate<List<ITargetedMod>> avoid(TargetedMod in) {
        return modList -> !modList.contains(in);
    }
}
