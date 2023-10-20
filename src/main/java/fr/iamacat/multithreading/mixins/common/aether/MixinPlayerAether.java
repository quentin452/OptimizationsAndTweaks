package fr.iamacat.multithreading.mixins.common.aether;

import com.gildedgames.the_aether.AetherConfig;
import com.gildedgames.the_aether.api.player.util.IAetherAbility;
import com.gildedgames.the_aether.api.player.util.IAetherBoss;
import com.gildedgames.the_aether.blocks.BlocksAether;
import com.gildedgames.the_aether.entities.passive.mountable.EntityParachute;
import com.gildedgames.the_aether.items.ItemsAether;
import com.gildedgames.the_aether.items.tools.ItemValkyrieTool;
import com.gildedgames.the_aether.network.AetherNetwork;
import com.gildedgames.the_aether.network.packets.*;
import com.gildedgames.the_aether.player.PlayerAether;
import com.gildedgames.the_aether.player.perks.util.DonatorMoaSkin;
import com.gildedgames.the_aether.player.perks.util.EnumAetherPerkType;
import com.gildedgames.the_aether.registry.achievements.AchievementsAether;
import com.gildedgames.the_aether.world.TeleporterAether;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.UUID;

@Mixin(PlayerAether.class)
public class MixinPlayerAether {
    @Shadow
    private EntityPlayer player;
    @Shadow
    private IAetherBoss focusedBoss;
    @Shadow
    private final ArrayList<IAetherAbility> abilities = new ArrayList();
    @Shadow
    public final ArrayList<Entity> clouds = new ArrayList(2);
    @Shadow
    public int shardCount;
    @Shadow
    public DonatorMoaSkin donatorMoaSkin = new DonatorMoaSkin();
    @Shadow
    public boolean shouldRenderHalo = true;
    @Shadow
    public boolean shouldRenderGlow = false;
    @Shadow
    public boolean shouldRenderCape = true;
    @Shadow
    public boolean seenSpiritDialog = false;
    @Shadow
    private boolean isJumping;
    @Shadow
    private boolean isMountSneaking;
    @Shadow
    private boolean inPortal;
    @Shadow
    private int portalCounter;
    @Shadow
    public int teleportDirection;
    @Shadow
    private String hammerName = StatCollector.translateToLocal("item.notch_hammer.name");
    @Shadow
    private int cooldown;
    @Shadow
    private int cooldownMax;
    @Shadow
    public float wingSinage;
    @Shadow
    public float timeInPortal;
    @Shadow
    public float prevTimeInPortal;
    @Shadow
    public Entity riddenEntity;
    @Shadow
    private ChunkCoordinates bedLocation;
    @Shadow
    public boolean isPoisoned = false;
    @Shadow
    public boolean isCured = false;
    @Shadow
    public boolean shouldGetPortal = true;
    @Shadow
    public int poisonTime = 0;
    @Shadow
    public int cureTime = 0;
    @Shadow
    private UUID uuid = UUID.fromString("df6eabe7-6947-4a56-9099-002f90370706");
    @Shadow
    private AttributeModifier healthModifier;
    @Inject(method = "onUpdate", at = @At("HEAD"), remap = false, cancellable = true)
    public void onUpdate(CallbackInfo ci) {
        if (MultithreadingandtweaksConfig.enableMixinPlayerAether){
            if (!this.player.worldObj.isRemote) {

        AetherNetwork.sendToAll(new PacketPerkChanged(this.getEntity().getEntityId(), EnumAetherPerkType.Halo, this.shouldRenderHalo));
        AetherNetwork.sendToAll(new PacketPerkChanged(this.getEntity().getEntityId(), EnumAetherPerkType.Glow, this.shouldRenderGlow));
        AetherNetwork.sendToAll(new PacketCapeChanged(this.getEntity().getEntityId(), this.shouldRenderCape));
        AetherNetwork.sendToAll(new PacketSendPoisonTime(this.getEntity(), this.poisonTime));
        AetherNetwork.sendToAll(new PacketSendSeenDialogue(this.getEntity(), this.seenSpiritDialog));
        AetherNetwork.sendToAll(new PacketPortalItem(this.getEntity(), this.shouldGetPortal));
        }
        if (this.isPoisoned) {
            this.poisonTime = Math.max(0, this.poisonTime - 1);
            this.isPoisoned = this.poisonTime > 0;
        }

        if (this.isCured) {
            this.cureTime = Math.max(0, this.cureTime - 1);
            this.isCured = this.cureTime > 0;
        }

            int i;
            for(i = 0; i < this.getAbilities().size(); ++i) {
                IAetherAbility ability = this.getAbilities().get(i);
                if (ability.shouldExecute()) {
                    ability.onUpdate();
                }
            }

            for(i = 0; i < this.clouds.size(); ++i) {
                Entity entity = this.clouds.get(i);
                if (entity.isDead) {
                    this.clouds.remove(i);
                }
            }


            this.cooldown = Math.max(0, this.cooldown - 2);

        if (this.isInsideBlock(BlocksAether.aercloud)) {
            this.getEntity().fallDistance = 0.0F;
        }

        if (this.getEntity().motionY < -2.0) {
            this.activateParachute();
        }

        this.wingSinage += this.getEntity().onGround ? 0.15F : 0.75F;
        this.wingSinage %= 6.283186F;

        boolean hasJumped = ReflectionHelper.getPrivateValue(EntityLivingBase.class, this.getEntity(), new String[]{"isJumping", "field_70703_bu"});
        this.setJumping(hasJumped);

        this.getEntity().worldObj.theProfiler.startSection("portal");
        if (this.getEntity().dimension == AetherConfig.getAetherDimensionID() && this.getEntity().posY < -2.0) {
            this.teleportPlayer(false);
            if (this.riddenEntity != null) {
                this.getEntity().mountEntity(this.riddenEntity);
                this.riddenEntity = null;
            }
        }

        if (this.inPortal) {
            if (this.getEntity().timeUntilPortal <= 0) {
                int limit = this.getEntity().getMaxInPortalTime();
                if (this.getEntity().ridingEntity == null) {
                    if (this.portalCounter >= limit) {
                        this.portalCounter = 0;
                        this.getEntity().timeUntilPortal = this.getEntity().getPortalCooldown();
                        if (!this.getEntity().worldObj.isRemote) {
                            this.teleportPlayer(true);
                            this.getEntity().triggerAchievement(AchievementsAether.enter_aether);
                        }
                    } else {
                        ++this.portalCounter;
                    }
                }
            } else {
                this.getEntity().timeUntilPortal = this.getEntity().getPortalCooldown();
            }

            if (this.getEntity().worldObj.getBlock((int)this.getEntity().posX, (int)this.getEntity().posY - 1, (int)this.getEntity().posZ) != Blocks.air) {
                AxisAlignedBB playerBounding = this.getEntity().boundingBox;
                int var10001 = (int)playerBounding.minX;
                int var10002 = (int)playerBounding.minY;
                if (this.getEntity().worldObj.getBlock(var10001, var10002, (int)playerBounding.minZ) != BlocksAether.aether_portal) {
                    var10001 = (int)playerBounding.minX;
                    var10002 = (int)playerBounding.minY;
                    if (this.getEntity().worldObj.getBlock(var10001, var10002, (int)playerBounding.minZ) != BlocksAether.aether_portal) {
                        this.inPortal = false;
                    }
                }
            }
        } else {
            if (this.portalCounter > 0) {
                this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
                this.portalCounter = 0;
            }
        }

        this.getEntity().worldObj.theProfiler.endSection();
        if (!this.getEntity().worldObj.isRemote) {
            ItemStack stack = this.getEntity().getCurrentEquippedItem();
            double distance = this.getEntity().capabilities.isCreativeMode ? 5.0 : 4.5;
            if (stack != null && stack.getItem() instanceof ItemValkyrieTool) {
                distance = 8.0;
            }

            ((EntityPlayerMP)this.getEntity()).theItemInWorldManager.setBlockReachDistance(distance);
        } else {
            this.prevTimeInPortal = this.timeInPortal;
            if (this.isInsideBlock(BlocksAether.aether_portal)) {
                this.timeInPortal += 0.0125F;
                if (this.timeInPortal >= 1.0F) {
                    this.timeInPortal = 1.0F;
                }
            } else if (this.getEntity().isPotionActive(Potion.confusion) && this.getEntity().getActivePotionEffect(Potion.confusion).getDuration() > 60) {
                this.timeInPortal += 0.006666667F;
                if (this.timeInPortal > 1.0F) {
                    this.timeInPortal = 1.0F;
                }
            } else {
                if (this.timeInPortal > 0.0F) {
                    this.timeInPortal -= 0.05F;
                }

                if (this.timeInPortal < 0.0F) {
                    this.timeInPortal = 0.0F;
                }
            }
        }

        if (!this.player.worldObj.isRemote && this.bedLocation != null && this.player.dimension == AetherConfig.getAetherDimensionID() && this.player.worldObj.getBlock(this.bedLocation.posX, this.bedLocation.posY, this.bedLocation.posZ) != BlocksAether.skyroot_bed) {
            this.setBedLocation((ChunkCoordinates)null);
        }
        ci.cancel();
        }

    }
    @Unique
    public EntityPlayer getEntity() {
        return this.player;
    }
    @Unique
    public ArrayList<IAetherAbility> getAbilities() {
        return this.abilities;
    }
    @Unique
    public boolean isInsideBlock(Block block) {
        AxisAlignedBB boundingBox = this.getEntity().boundingBox;
        int i = MathHelper.floor_double(boundingBox.minX);
        int j = MathHelper.floor_double(boundingBox.maxX + 1.0);
        int k = MathHelper.floor_double(boundingBox.minY);
        int l = MathHelper.floor_double(boundingBox.maxY + 1.0);
        int i1 = MathHelper.floor_double(boundingBox.minZ);
        int j1 = MathHelper.floor_double(boundingBox.maxZ + 1.0);

        for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
                for(int i2 = i1; i2 < j1; ++i2) {
                    if (this.getEntity().worldObj.getBlock(k1, l1, i2) == block) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    @Unique
    private void activateParachute() {
        if (!this.player.capabilities.isCreativeMode) {
            EntityParachute parachute = null;
            ItemStack itemstack = null;

            for(int i = 0; i < this.getEntity().inventory.getSizeInventory(); ++i) {
                ItemStack stackInSlot = this.getEntity().inventory.getStackInSlot(i);
                if (stackInSlot != null && stackInSlot.getItem() == ItemsAether.cloud_parachute) {
                    itemstack = stackInSlot;
                    break;
                }

                if (stackInSlot != null && stackInSlot.getItem() == ItemsAether.golden_parachute) {
                    itemstack = stackInSlot;
                    break;
                }
            }

            if (itemstack != null) {
                if (itemstack.getItem() == ItemsAether.cloud_parachute) {
                    parachute = new EntityParachute(this.getEntity().worldObj, this.getEntity(), false);
                    parachute.setPosition(this.getEntity().posX, this.getEntity().posY, this.getEntity().posZ);
                    this.getEntity().worldObj.spawnEntityInWorld(parachute);
                    this.getEntity().inventory.consumeInventoryItem(itemstack.getItem());
                } else if (itemstack.getItem() == ItemsAether.golden_parachute) {
                    itemstack.damageItem(1, this.getEntity());
                    parachute = new EntityParachute(this.getEntity().worldObj, this.getEntity(), true);
                    parachute.setPosition(this.getEntity().posX, this.getEntity().posY, this.getEntity().posZ);
                    this.getEntity().worldObj.spawnEntityInWorld(parachute);
                }
            }
        }

    }

    @Unique
    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    @Unique
    private void teleportPlayer(boolean shouldSpawnPortal) {
        if (this.getEntity() instanceof EntityPlayerMP) {
            int previousDimension = this.getEntity().dimension;
            int transferDimension = previousDimension == AetherConfig.getAetherDimensionID() ? AetherConfig.getTravelDimensionID() : AetherConfig.getAetherDimensionID();
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            TeleporterAether teleporter = new TeleporterAether(shouldSpawnPortal, server.worldServerForDimension(transferDimension));
            if (this.getEntity().ridingEntity != null) {
                this.getEntity().ridingEntity.mountEntity((Entity)null);
            }

            if (this.getEntity().riddenByEntity != null) {
                this.getEntity().riddenByEntity.mountEntity((Entity)null);
            }

            if (server != null && server.getConfigurationManager() != null) {
                server.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)this.getEntity(), transferDimension, teleporter);
            }
        }
    }
    @Unique
    public void setBedLocation(ChunkCoordinates bedLocation) {
        this.bedLocation = bedLocation;
    }
}

