package fr.iamacat.optimizationsandtweaks.mixins.common.mythandmonsters;

import com.hoopawolf.mam.blocks.*;
import com.hoopawolf.mam.entity.*;
import com.hoopawolf.mam.models.*;
import com.hoopawolf.mam.projectile.*;
import com.hoopawolf.mam.proxy.ClientProxy;
import com.hoopawolf.mam.registry.MAMItems;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ClientProxy.class)
public class MixinMAMClientProxy {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void registerRenderThings() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFairy.class, new RenderFairy(new ModelFairy(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityCetus.class, new RenderCetus(new ModelCetus(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityHillGiant.class, new RenderHillGiant(new ModelHillGiant(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityJackalopes.class, new RenderJackalopes(new ModelJackalopes(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityJackalopesOld.class, new RenderJackalopesOld(new ModelJackalopeOld(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityFrostJakalopes.class, new RenderFrostJakalope(new ModelJackalopes(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityFrostJakalopesOld.class, new RenderFrostJakalopeOld(new ModelJackalopeOld(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityDendroidElder.class, new RenderDendroidElder(new ModelDendroidElder(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityDendroid.class, new RenderDendroid(new ModelDendroid(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityGoodDendroid.class, new RenderGoodDendroid(new ModelDendroid(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityDendroidRoot.class, new RenderDendroidRoot(new ModelDendroidRoot(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityDendroidSeer.class, new RenderDendroidSeer(new ModelDendroidSeer(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityDendroidSentinel.class, new RenderDendroidSentinel(new ModelDendroidSeer(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntitySandWyrm.class, new RenderSandWyrm(new ModelSandWyrm(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityGoldenRam.class, new RenderGoldenRam(new ModelGoldenRam2(), new ModelGoldenRam1(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityUrsaBlack.class, new RenderUrsaBlack(new ModelUrsa(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityCoreRed.class, new RenderRedCore(new ModelCore(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityUrsaWhite.class, new RenderUrsaWhite(new ModelUrsa(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityCoreBlue.class, new RenderBlueCore(new ModelCore(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityWolpertinger.class, new RenderWolpertinger(new ModelWolpertinger(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityWolpertingerOld.class, new RenderWolpertingerOld(new ModelWolpertingerOld(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityHunter.class, new RenderHunter(new ModelBiped(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityEliteHunter.class, new RenderHunter(new ModelBiped(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityKitsune.class, new RenderKitsune(new ModelKitsune(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityKitsuneMinion.class, new RenderKitsuneMinion(new ModelKitsuneMinion(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(EntityDropBear.class, new RenderDropBear(new ModelDropBear(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityClayGolem.class, new RenderClay(new ModelClayGolem(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityClayMinion.class, new RenderClay(new ModelClayMinion(), 0.5F));
        MinecraftForgeClient.registerItemRenderer(MAMItems.dendroidroot, new RenderItem(new ModelDendroidRoot(), "mam:textures/entity/DendroidRoot.png"));
        MinecraftForgeClient.registerItemRenderer(MAMItems.depthcrystal, new RenderItem(new ModelDepthCrystal(), "mam:textures/items/DepthCrystal.png"));
        MinecraftForgeClient.registerItemRenderer(MAMItems.tusklance, new RenderItem(new ModelTuskLance(), "mam:textures/items/HeldTuskLance.png"));
        MinecraftForgeClient.registerItemRenderer(MAMItems.redcore, new RenderItem(new ModelCore(), "mam:textures/entity/UrsaBlack1.png"));
        MinecraftForgeClient.registerItemRenderer(MAMItems.bluecore, new RenderItem(new ModelCore(), "mam:textures/entity/UrsaWhitePeaceful.png"));
        MinecraftForgeClient.registerItemRenderer(MAMItems.jar, new RenderItem(new ModelJar(), "mam:textures/blocks/EmptyFairyJar.png"));
        MinecraftForgeClient.registerItemRenderer(MAMItems.angryfairyjar, new RenderItem(new ModelFairyJar(), "mam:textures/blocks/FairyJar.png"));
        MinecraftForgeClient.registerItemRenderer(MAMItems.happyfairyjar, new RenderItem(new ModelFairyJar(), "mam:textures/blocks/FairyJar.png"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityJar.class, new RenderJar());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAngryFairyJar.class, new RenderJarAngryFairy());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHappyFairyJar.class, new RenderJarHappyFairy());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDepthCrystal.class, new RenderDepthCrystal());
        RenderingRegistry.registerEntityRenderingHandler(EntityStick.class, new RenderSnowball(Items.stick));
        RenderingRegistry.registerEntityRenderingHandler(EntityDust.class, new RenderSnowball(MAMItems.redstardust));
        RenderingRegistry.registerEntityRenderingHandler(EntityDust2.class, new RenderSnowball(MAMItems.bluestardust));
        RenderingRegistry.registerEntityRenderingHandler(EntityDendroidChakram.class, new RenderSnowball(MAMItems.dendroidchakramprojectile));
        RenderingRegistry.registerEntityRenderingHandler(EntityGoldArrow.class, new RenderGoldArrow());
        RenderingRegistry.registerEntityRenderingHandler(EntityMusic.class, new RenderMusic(1.0F));
    }

}
