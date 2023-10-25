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

    //SERVER MIXINS

    server_core_MixinChunkEntityCollisionFPSFix(Side.SERVER, m -> OptimizationsandTweaksConfig.enableMixinChunkEntityCollisionFPSFix, "core.MixinChunkEntityCollisionFPSFix"),

    // TWEAKING MIXINS
    common_minestones_MixinItemMinestone(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinItemMinestone,
        "minestones.MixinItemMinestone"),

    // OPTIMIZATIONS MIXINS

    common_core_MixinEntityAITempt(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinEntityAITempt, "core.MixinEntityAITempt"),
    common_easybreeding_MixinEntityAIEatDroppedFood(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinEntityAIEatDroppedFood, "easybreeding.MixinEntityAIEatDroppedFood"),
    common_core_MixinRandomPositionGenerator(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinRandomPositionGenerator, "core.MixinRandomPositionGenerator"),
    common_core_MixinEntityAIWander(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAIWander,
        "core.MixinEntityAIWander"),
    common_core_MixinEntityAIPlay(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAIPlay,
        "core.MixinEntityAIPlay"),
    common_core_MixinEntityAIAttackOnCollide(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinEntityAIAttackOnCollide, "core.MixinEntityAIAttackOnCollide"),
    common_witchery_MixinEntityVillageGuard(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinEntityVillageGuard, "witchery.MixinEntityVillageGuard"),
    common_core_MixinServersideAttributeMap(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinServersideAttributeMap, "core.MixinServersideAttributeMap"),
    common_core_MixinLowerStringMap(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinLowerStringMap,
        "core.MixinLowerStringMap"),
    common_core_MixinEntityLivingUpdate(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityLivingUpdate,
        "core.MixinEntityLivingUpdate"),
    common_core_MixinDataWatcher(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinNibbleArray,
        "core.MixinDataWatcher"),

    // common_practicallogistics_MixinEventRegistry(Side.COMMON,
    // m -> MultithreadingandtweaksConfig.enableMixinNibbleArray, "practicallogistics.MixinEventRegistry"),
    common_core_MixinNibbleArray(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinNibbleArray,
        "core.MixinNibbleArray"),
    common_blocklings_MixinEntityBlockling(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinSteamcraftEventHandler, "blocklings.MixinEntityBlockling"),
    common_flaxbeardssteampower_MixinSteamcraftEventHandler(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinSteamcraftEventHandler,
        "flaxbeardssteampower.MixinSteamcraftEventHandler"),
    common_catwalks2_MixinCommonProxy(Side.COMMON,
        avoid(TargetedMod.CATWALK2OFFICIAL).and(m -> OptimizationsandTweaksConfig.enableMixinCommonProxyForCatWalks2),
        "catwalks2.MixinCommonProxy"),
    client_core_MixinRenderManager(Side.CLIENT,
        avoid(TargetedMod.SKINPORT).and(m -> OptimizationsandTweaksConfig.enableMixinRenderManager),
        "core.MixinRenderManager"),
    common_core_MixinOilTweakEventHandler(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinOilTweakEventHandler,
        "buildcraft.addon.oiltweaks.MixinOilTweakEventHandler"),
    common_core_MixinMinecraftServer(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinMinecraftServer,
        "core.MixinMinecraftServer"),
    common_core_MixinChunk(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinChunk, "core.MixinChunk"),
    common_core_MixinMapStorage(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinMapStorage,
        "core.MixinMapStorage"),
    common_akatsuki_MixinEntitySasori(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntitySasosri,
        "akatsuki.MixinEntitySasori"),
    common_akatsuki_MixinEntitySasori2(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntitySasosri2,
        "akatsuki.MixinEntitySasori2"),
    common_akatsuki_MixinPuppetKadz(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPuppetKadz,
        "akatsuki.MixinPuppetKadz"),
    common_akatsuki_MixinAnimTickHandler(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinAnimTickHandler,
        "akatsuki.MixinAnimTickHandler"),
    common_akatsuki_MixinAnimationHandler(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinAnimationHandler,
        "akatsuki.MixinAnimationHandler"),
    common_aether_MixinPlayerAether(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPlayerAether,
        "aether.MixinPlayerAether"),
    common_notenoughpets_MixinEventHandlerNEP(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEventHandlerNEP,
        "notenoughpets.MixinEventHandlerNEP"),
    common_pneumaticraft_MixinHackTickHandler(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinHackTickHandler,
        "pneumaticraft.MixinHackTickHandler"),
    common_ic2_MixinPriorityExecutor(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPriorityExecutor,
        "ic2.MixinPriorityExecutor"),
    common_core_MixinBlockGrass(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinBlockGrass,
        "core.MixinBlockGrass"),
    common_adventurersamulet_MixinEntityEagle(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityEagle,
        "adventurersamulet.MixinEntityEagle"),
    common_core_MixinEntityLiving(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityLiving,
        "core.MixinEntityLiving"),
    common_core_MixinEntityAgeable(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAgeable,
        "core.MixinEntityAgeable"),
    common_nei_MixinNEIServerUtils(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinNEIServerUtils,
        "nei.MixinNEIServerUtils"),
    common_nei_MixinConfig(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinConfig, "ic2.MixinConfig"),
    common_core_MixinBlockLiquid(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinBlockLiquid,
        "core.MixinBlockLiquid"),
    common_core_entity_MixinEntityZombie(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityZombie,
        "core.entity.MixinEntityZombie"),

    common_core_entity_MixinEntityItem(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityItem,
        "core.entity.MixinEntityItem"),
    common_ic2_MixinTickHandler(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinTickHandler,
        "ic2.MixinTickHandler"),

    common_core_entity_MixinEntityAnimal(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAnimal,
        "core.entity.MixinEntityAnimal"),
    common_core_entity_MixinEntitySquid(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntitySquid,
        "core.entity.MixinEntitySquid"),
    common_core_MixinEntityAITasks(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityAITasks,
        "core.MixinEntityAITasks"),
    common_core_MixinEntityMoveHelper(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityMoveHelper,
        "core.MixinEntityMoveHelper"),
    common_core_MixinWorld(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinWorld, "core.MixinWorld"),

    common_core_MixinLeaves(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinLeaves, "core.MixinLeaves"),

    common_core_MixinEntityAIFollowParent(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinEntityAIFollowParent, "core.MixinEntityAIFollowParent"),
    common_core_pathfinding_MixinPathFinder(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPathFinding,
        "core.pathfinding.MixinPathFinder"),
    common_core_pathfinding_MixinPath(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPathFinding,
        "core.pathfinding.MixinPath"),
    // todo WIP
    /*
     * common_core_pathfinding_MixinPathEntity(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPathFinding,
     * "core.pathfinding.MixinPathEntity"),
     */
    common_core_pathfinding_MixinPathNavigate(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPathFinding,
        "core.pathfinding.MixinPathNavigate"),
    common_core_pathfinding_MixinPathPoint(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPathFinding,
        "core.pathfinding.MixinPathPoint"),
    common_core_MixinEntityLookHelper(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntityLookHelper,
        "core.MixinEntityLookHelper"),
    common_koto_MixinPatchWorldGenCloudNine(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinPatchWorldGenCloudNine, "koto.MixinPatchWorldGenCloudNine"),
    common_thaumcraft_MixinPatchBiomeGenMagicalForest(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinPatchBiomeGenMagicalForest,
        "thaumcraft.MixinPatchBiomeGenMagicalForest"),
    common_thaumcraft_MixinPatchBlockMagicalLeavesPerformances(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinPatchBlockMagicalLeavesPerformances,
        "thaumcraft.MixinPatchBlockMagicalLeavesPerformances"),

    common_core_MixinPatchSpawnerAnimals(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinPatchSpawnerAnimals,
        "core.MixinPatchSpawnerAnimals"),
    // BUGFIX MIXINS

    common_farlanders_MixinEntityEnderGolem(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityEnderGolem"),
    common_farlanders_MixinEntityEnderminion(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityEnderminion"),
    common_farlanders_MixinEntityFarlander(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityFarlander"),
    common_farlanders_MixinEntityLootr(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityLootr"),
    common_farlanders_MixinEntityMysticEnderminion(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityMysticEnderminion"),
    common_farlanders_MixinEntityWanderer(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityWanderer"),
    common_farlanders_MixinEntityTitan(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityTitan"),
    common_farlanders_MixinEntityRebel(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityRebel"),
    common_farlanders_MixinEntityMystic(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityMystic"),
    common_farlanders_MixinEntityEnderGuardian(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityEnderGuardian"),
    common_farlanders_MixinEntityElder(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityElder"),
    common_farlanders_MixinEntityFanEnderman(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityFanEnderman"),
    common_thaumcraft_MixinFixCascadingWorldGenFromThaumcraftWorldGenerator(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator,
        "thaumcraft.MixinFixCascadingWorldGenFromThaumcraftWorldGenerator"),
    common_minefactoryreloaded_MixinFixCascadingforMineFactoryReloadedWorldGen(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixCascadingforMineFactoryReloadedWorldGen,
        "minefactoryreloaded.MixinFixCascadingforMineFactoryReloadedWorldGen"),
    common_pamsharvestcraft_MixinFixWorldGenPamFruitTree(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixWorldGenPamFruitTree,
        "pamsharvestcraft.MixinFixWorldGenPamFruitTree"),
    common_steamcraft2_MixinFixCascadingFromWorldGenBrassTree(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromWorldGenBrassTree,
        "steamcraft2.MixinFixCascadingFromWorldGenBrassTree"),

    common_core_MixinEntity(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinEntity, "core.MixinEntity"),
    common_cofhcore_fixoredictcrash_MixinOreDictionaryArbiter(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinOreDictCofhFix,
        "cofhcore.fixoredictcrash.MixinOreDictionaryArbiter"),
    common_slimecarnage_MixinFixCascadingFromWorldGenSlimeCarnage(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromWorldGenSlimeCarnage,
        "slimecarnage.MixinFixCascadingFromWorldGenSlimeCarnage"),
    common_pamsharvestcraft_MixinFixPamsTreesCascadingWorldgenLag(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixPamsTreesCascadingWorldgenLag,
        "pamsharvestcraft.MixinFixPamsTreesCascadingWorldgenLag"),

    common_MixinFixCascadingFromShipwreckGen(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromShipwreckGen,
        "shipwreck.MixinFixCascadingFromShipwreckGen"),
    common_shincolle_MixinFixCascadingFromWorldGenPolyGravel(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromWorldGenPolyGravel,
        "shincolle.MixinFixCascadingFromWorldGenPolyGravel"),
    common_shincolle_MixinFixCascadingFromShinColleWorldGen(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixCascadingFromShinColleWorldGen,
        "shincolle.MixinFixCascadingFromShinColleWorldGen"),
    common_minefactoryreloaded_MixinFixWorldGenLakesMetaCascadingWorldgenLag(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixWorldGenLakesMetaMinefactoryReloadedCascadingWorldgenFix,
        "minefactoryreloaded.MixinFixWorldGenLakesMetaCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixRubberTreesCascadingWorldgenLag(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix,
        "minefactoryreloaded.MixinFixRubberTreesCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixNoSuchMethodException(Side.COMMON,
        m -> OptimizationsandTweaksConfig.enableMixinFixNoSuchMethodException,
        "minefactoryreloaded.MixinFixNoSuchMethodException"),

    common_core_MixinGodZillaFix(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinGodZillaFix,
        "core.MixinGodZillaFix"),
    common_core_MixinStatList(Side.COMMON, m -> OptimizationsandTweaksConfig.enableMixinStatList, "core.MixinStatList"),

    // CLIENT MIXINS

    client_core_MixinEntitySpellParticleFX(Side.CLIENT,
        m -> OptimizationsandTweaksConfig.enableMixinEntitySpellParticleFX, "core.MixinEntitySpellParticleFX"),
    client_core_MixinModelRenderer(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinModelRenderer,
        "core.MixinModelRenderer"),
    client_core_MixinTesselator(Side.CLIENT,
        avoid(TargetedMod.OPTIFINE).and(m -> OptimizationsandTweaksConfig.enableMixinTesselator),
        "core.MixinTesselator"),
    client_core_MixinOpenGlHelper(Side.CLIENT, m -> OptimizationsandTweaksConfig.enableMixinOpenGlHelper,
        "core.MixinOpenGlHelper"),

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

    static Predicate<List<ITargetedMod>> avoid(TargetedMod in) {
        return modList -> !modList.contains(in);
    }
}
