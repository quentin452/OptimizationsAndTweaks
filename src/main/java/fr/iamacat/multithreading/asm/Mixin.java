package fr.iamacat.multithreading.asm;

import java.util.*;
import java.util.function.Predicate;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Mixin implements IMixin {


    // OPTIMIZATIONS MIXINS

    common_core_MixinEntityLivingUpdate(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityLivingUpdate,
        "core.MixinEntityLivingUpdate"),
    common_core_MixinDataWatcher(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinNibbleArray,
        "core.MixinDataWatcher"),

    // common_practicallogistics_MixinEventRegistry(Side.COMMON,
    // m -> MultithreadingandtweaksConfig.enableMixinNibbleArray, "practicallogistics.MixinEventRegistry"),
    common_core_MixinNibbleArray(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinNibbleArray,
        "core.MixinNibbleArray"),
    common_blocklings_MixinEntityBlockling(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinSteamcraftEventHandler, "blocklings.MixinEntityBlockling"),
    common_flaxbeardssteampower_MixinSteamcraftEventHandler(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinSteamcraftEventHandler,
        "flaxbeardssteampower.MixinSteamcraftEventHandler"),
    common_catwalks2_MixinCommonProxy(Side.COMMON,
        avoid(TargetedMod.CATWALK2OFFICIAL).and(m -> MultithreadingandtweaksConfig.enableMixinCommonProxyForCatWalks2),
        "catwalks2.MixinCommonProxy"),
    client_core_MixinRenderManager(Side.CLIENT,
        avoid(TargetedMod.SKINPORT).and(m -> MultithreadingandtweaksConfig.enableMixinRenderManager),
        "core.MixinRenderManager"),
    common_core_MixinOilTweakEventHandler(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinOilTweakEventHandler,
        "buildcraft.addon.oiltweaks.MixinOilTweakEventHandler"),
    common_core_MixinMinecraftServer(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinMinecraftServer,
        "core.MixinMinecraftServer"),
    common_core_MixinChunk(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinChunk, "core.MixinChunk"),
    common_core_MixinMapStorage(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinMapStorage,
        "core.MixinMapStorage"),
    common_akatsuki_MixinEntitySasori(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntitySasosri,
        "akatsuki.MixinEntitySasori"),
    common_akatsuki_MixinEntitySasori2(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntitySasosri2,
        "akatsuki.MixinEntitySasori2"),
    common_akatsuki_MixinPuppetKadz(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPuppetKadz,
        "akatsuki.MixinPuppetKadz"),
    common_akatsuki_MixinAnimTickHandler(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinAnimTickHandler,
        "akatsuki.MixinAnimTickHandler"),
    common_akatsuki_MixinAnimationHandler(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinAnimationHandler,
        "akatsuki.MixinAnimationHandler"),
    common_aether_MixinPlayerAether(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPlayerAether,
        "aether.MixinPlayerAether"),
    common_notenoughpets_MixinEventHandlerNEP(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinEventHandlerNEP, "notenoughpets.MixinEventHandlerNEP"),
    common_pneumaticraft_MixinHackTickHandler(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinHackTickHandler, "pneumaticraft.MixinHackTickHandler"),
    common_ic2_MixinPriorityExecutor(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPriorityExecutor,
        "ic2.MixinPriorityExecutor"),
    common_core_MixinBlockGrass(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinBlockGrass,
        "core.MixinBlockGrass"),
    common_adventurersamulet_MixinEntityEagle(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityEagle,
        "adventurersamulet.MixinEntityEagle"),
    common_core_MixinEntityLiving(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityLiving,
        "core.MixinEntityLiving"),
    common_core_entity_MixinEntityChicken(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityChicken,
        "core.entity.MixinEntityChicken"),
    common_core_MixinEntityAgeable(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityAgeable,
        "core.MixinEntityAgeable"),
    common_nei_MixinNEIServerUtils(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinNEIServerUtils,
        "nei.MixinNEIServerUtils"),
    common_nei_MixinConfig(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinConfig, "ic2.MixinConfig"),
    common_core_MixinBlockLiquid(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinBlockLiquid,
        "core.MixinBlockLiquid"),
    common_core_entity_MixinEntityZombie(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityZombie,
        "core.entity.MixinEntityZombie"),

    common_core_entity_MixinEntityItem(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityItem,
        "core.entity.MixinEntityItem"),
    common_ic2_MixinTickHandler(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinTickHandler,
        "ic2.MixinTickHandler"),

    common_core_entity_MixinEntityAnimal(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityAnimal,
        "core.entity.MixinEntityAnimal"),
    common_core_entity_MixinEntitySquid(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntitySquid,
        "core.entity.MixinEntitySquid"),
    common_core_MixinEntityAITasks(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityAITasks,
        "core.MixinEntityAITasks"),
    common_core_MixinEntityMoveHelper(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityMoveHelper,
        "core.MixinEntityMoveHelper"),
    common_core_MixinWorld(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinWorld, "core.MixinWorld"),

    common_core_MixinLeaves(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinLeaves, "core.MixinLeaves"),

    common_core_MixinEntityAIFollowParent(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinEntityAIFollowParent, "core.MixinEntityAIFollowParent"),
    common_core_pathfinding_MixinPathFinder(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPathFinding,
        "core.pathfinding.MixinPathFinder"),
    common_core_pathfinding_MixinPath(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPathFinding,
        "core.pathfinding.MixinPath"),
    // todo WIP
    /*
     * common_core_pathfinding_MixinPathEntity(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPathFinding,
     * "core.pathfinding.MixinPathEntity"),
     */
    common_core_pathfinding_MixinPathNavigate(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPathFinding,
        "core.pathfinding.MixinPathNavigate"),
    common_core_pathfinding_MixinPathPoint(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPathFinding,
        "core.pathfinding.MixinPathPoint"),
    common_core_MixinEntityLookHelper(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntityLookHelper,
        "core.MixinEntityLookHelper"),
    common_koto_MixinPatchWorldGenCloudNine(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinPatchWorldGenCloudNine, "koto.MixinPatchWorldGenCloudNine"),
    common_thaumcraft_MixinPatchBiomeGenMagicalForest(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinPatchBiomeGenMagicalForest,
        "thaumcraft.MixinPatchBiomeGenMagicalForest"),
    common_thaumcraft_MixinPatchBlockMagicalLeavesPerformances(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinPatchBlockMagicalLeavesPerformances,
        "thaumcraft.MixinPatchBlockMagicalLeavesPerformances"),

    common_core_MixinPatchSpawnerAnimals(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinPatchSpawnerAnimals,
        "core.MixinPatchSpawnerAnimals"),
    // BUGFIX MIXINS

    common_farlanders_MixinEntityEnderGolem(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityEnderGolem"),
    common_farlanders_MixinEntityEnderminion(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityEnderminion"),
    common_farlanders_MixinEntityFarlander(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityFarlander"),
    common_farlanders_MixinEntityLootr(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityLootr"),
    common_farlanders_MixinEntityMysticEnderminion(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityMysticEnderminion"),
    common_farlanders_MixinEntityWanderer(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityWanderer"),
    common_farlanders_MixinEntityTitan(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityTitan"),
    common_farlanders_MixinEntityRebel(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityRebel"),
    common_farlanders_MixinEntityMystic(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityMystic"),
    common_farlanders_MixinEntityEnderGuardian(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityEnderGuardian"),
    common_farlanders_MixinEntityElder(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityElder"),
    common_farlanders_MixinEntityFanEnderman(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixUnableToPlayUnknowSoundEventFromFarlandersmod,
        "farlanders.MixinEntityFanEnderman"),
    common_thaumcraft_MixinFixCascadingWorldGenFromThaumcraftWorldGenerator(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingWorldGenFromThaumcraftWorldGenerator,
        "thaumcraft.MixinFixCascadingWorldGenFromThaumcraftWorldGenerator"),
    common_minefactoryreloaded_MixinFixCascadingforMineFactoryReloadedWorldGen(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingforMineFactoryReloadedWorldGen,
        "minefactoryreloaded.MixinFixCascadingforMineFactoryReloadedWorldGen"),
    common_pamsharvestcraft_MixinFixWorldGenPamFruitTree(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixWorldGenPamFruitTree,
        "pamsharvestcraft.MixinFixWorldGenPamFruitTree"),
    common_steamcraft2_MixinFixCascadingFromWorldGenBrassTree(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromWorldGenBrassTree,
        "steamcraft2.MixinFixCascadingFromWorldGenBrassTree"),

    common_core_MixinEntity(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinEntity, "core.MixinEntity"),
    common_cofhcore_fixoredictcrash_MixinOreDictionaryArbiter(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinOreDictCofhFix,
        "cofhcore.fixoredictcrash.MixinOreDictionaryArbiter"),
    common_slimecarnage_MixinFixCascadingFromWorldGenSlimeCarnage(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromWorldGenSlimeCarnage,
        "slimecarnage.MixinFixCascadingFromWorldGenSlimeCarnage"),
    common_pamsharvestcraft_MixinFixPamsTreesCascadingWorldgenLag(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixPamsTreesCascadingWorldgenLag,
        "pamsharvestcraft.MixinFixPamsTreesCascadingWorldgenLag"),

    common_MixinFixCascadingFromShipwreckGen(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromShipwreckGen,
        "shipwreck.MixinFixCascadingFromShipwreckGen"),
    common_shincolle_MixinFixCascadingFromWorldGenPolyGravel(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromWorldGenPolyGravel,
        "shincolle.MixinFixCascadingFromWorldGenPolyGravel"),
    common_shincolle_MixinFixCascadingFromShinColleWorldGen(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixCascadingFromShinColleWorldGen,
        "shincolle.MixinFixCascadingFromShinColleWorldGen"),
    common_minefactoryreloaded_MixinFixWorldGenLakesMetaCascadingWorldgenLag(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixWorldGenLakesMetaMinefactoryReloadedCascadingWorldgenFix,
        "minefactoryreloaded.MixinFixWorldGenLakesMetaCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixRubberTreesCascadingWorldgenLag(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixRubberTreesMinefactoryReloadedCascadingWorldgenFix,
        "minefactoryreloaded.MixinFixRubberTreesCascadingWorldgenLag"),
    common_minefactoryreloaded_MixinFixNoSuchMethodException(Side.COMMON,
        m -> MultithreadingandtweaksConfig.enableMixinFixNoSuchMethodException,
        "minefactoryreloaded.MixinFixNoSuchMethodException"),

    common_core_MixinGodZillaFix(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinGodZillaFix,
        "core.MixinGodZillaFix"),
    common_core_MixinStatList(Side.COMMON, m -> MultithreadingandtweaksConfig.enableMixinStatList,
        "core.MixinStatList"),

    // CLIENT MIXINS

    client_core_MixinEntitySpellParticleFX(Side.CLIENT,
        m -> MultithreadingandtweaksConfig.enableMixinEntitySpellParticleFX, "core.MixinEntitySpellParticleFX"),
    client_core_MixinModelRenderer(Side.CLIENT, m -> MultithreadingandtweaksConfig.enableMixinModelRenderer,
        "core.MixinModelRenderer"),
    client_core_MixinTesselator(Side.CLIENT,
        avoid(TargetedMod.OPTIFINE).and(m -> MultithreadingandtweaksConfig.enableMixinTesselator),
        "core.MixinTesselator"),
    client_core_MixinOpenGlHelper(Side.CLIENT, m -> MultithreadingandtweaksConfig.enableMixinOpenGlHelper,
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
