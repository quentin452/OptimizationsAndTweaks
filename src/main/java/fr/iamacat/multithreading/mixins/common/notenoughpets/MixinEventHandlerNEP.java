package fr.iamacat.multithreading.mixins.common.notenoughpets;

import astrotibs.notenoughpets.config.GeneralConfig;
import astrotibs.notenoughpets.entity.EntityOcelotNEP;
import astrotibs.notenoughpets.entity.EntityWolfNEP;
import astrotibs.notenoughpets.events.EventHandlerNEP;
import astrotibs.notenoughpets.util.LogHelper;
import astrotibs.notenoughpets.util.SkinVariations;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(EventHandlerNEP.class)
public class MixinEventHandlerNEP {
    @Inject(method = "spawnStraysWithVillageTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void spawnStraysWithVillageTick(TickEvent.WorldTickEvent event, CallbackInfo ci) {
        if (!MultithreadingandtweaksConfig.enableMixinEventHandlerNEP) {
            ci.cancel();
            return;
        }

        if (Arrays.binarySearch(GeneralConfig.strayDimensions, event.world.provider.dimensionId) < 0
            || (GeneralConfig.enableNECCats || GeneralConfig.revertAnimalTypes)
            || !event.world.isRemote
            || event.phase != TickEvent.Phase.END) {
            ci.cancel();
            return;
        }

        VillageCollection vc = event.world.villageCollectionObj;
        int vcTickCounter = ReflectionHelper.getPrivateValue(VillageCollection.class, vc, new String[]{"tickCounter", "field_75553_e"});

        if (vcTickCounter % 30 != 0) {
            ci.cancel();
            return;
        }

        for (Object o : vc.getVillageList()) {
            Village village = (Village) o;

            if (GeneralConfig.debugMessages && vcTickCounter % 90 == 0) {
                LogHelper.info("Village " + village + " cats: " + multithreadingandtweaks$countEntityAroundVillage(EntityOcelot.class, village, event.world, 32) + ", dogs: " + multithreadingandtweaks$countEntityAroundVillage(EntityWolf.class, village, event.world, 32));
            }

            int catCount = GeneralConfig.enableNECCats ? multithreadingandtweaks$countEntityAroundVillage(EntityOcelotNEP.class, village, event.world, 32) : 0;
            int dogCount = GeneralConfig.enableNECDogs ? multithreadingandtweaks$countEntityAroundVillage(EntityWolfNEP.class, village, event.world, 32) : 0;
            int maxSpawnCount = Math.min(GeneralConfig.strayCapFromVillageRadius ? village.getVillageRadius() * village.getVillageRadius() / 500 : Math.min(village.getNumVillagers(), village.getNumVillageDoors() / 4), GeneralConfig.revertAnimalTypes ? 0 : Math.max(GeneralConfig.villageSpawnCapCat, GeneralConfig.villageSpawnCapDog));

            if ((catCount + dogCount) < maxSpawnCount && event.world.rand.nextInt(100) < GeneralConfig.villageSpawnRatePercent) {
                multithreadingandtweaks$trySpawnStray(village, event.world);
            }
        }

        ci.cancel();
    }

    @Unique
    private static void multithreadingandtweaks$trySpawnStray(Village village, World world) {
        int spawnCap;
        Class strayClass;
        float strayFraction;
        EntityTameable entityStray;
        if (world.rand.nextFloat() < (!GeneralConfig.enableNECDogs ? 1.0F : GeneralConfig.strayFractionCat) && GeneralConfig.enableNECCats) {
            spawnCap = GeneralConfig.villageSpawnCapCat;
            strayClass = EntityOcelot.class;
            strayFraction = !GeneralConfig.enableNECDogs ? 1.0F : GeneralConfig.strayFractionCat;
            entityStray = new EntityOcelotNEP(world);
            ((EntityOcelotNEP)entityStray).setTameSkin(world.rand.nextInt((GeneralConfig.enableNECCats && !GeneralConfig.revertAnimalTypes ? SkinVariations.catSkinArray.length : 4) - 1) + 1);
        } else {
            if (!GeneralConfig.enableNECDogs) {
                return;
            }

            spawnCap = GeneralConfig.villageSpawnCapDog;
            strayClass = EntityWolf.class;
            strayFraction = GeneralConfig.enableNECCats ? 1.0F - GeneralConfig.strayFractionCat : 1.0F;
            entityStray = new EntityWolfNEP(world);
            ((EntityWolfNEP)entityStray).setTameSkin(GeneralConfig.enableNECDogs && !GeneralConfig.revertAnimalTypes ? world.rand.nextInt(SkinVariations.dogSkinArray.length - 1) + 1 : 0);
        }

        if (spawnCap > 0 && multithreadingandtweaks$countEntityAroundVillage(strayClass, village, world,32) < Math.min(Math.round(GeneralConfig.strayCapFromVillageRadius ? (float)(village.getVillageRadius() * village.getVillageRadius() / 500) : (float)Math.min(village.getNumVillagers(), village.getNumVillageDoors() / 4) * strayFraction), spawnCap)) {
            Vec3 vec3 = multithreadingandtweaks$tryGetStraySpawningLocation(village, world, 2, 4, 2);
            if (vec3 != null) {
                entityStray.setLocationAndAngles(vec3.xCoord, vec3.yCoord, vec3.zCoord, world.rand.nextFloat() * 360.0F, 0.0F);
                entityStray.setTamed(false);
                entityStray.setGrowingAge(Math.min(24000 - world.rand.nextInt(48001), 0));
                world.spawnEntityInWorld(entityStray);
            }
        }

    }
    @Unique
    private static int multithreadingandtweaks$countEntityAroundVillage(Class entityClass, Village village, World world, int villageBuffer) {
        int vilX = village.getCenter().posX;
        int vilY = village.getCenter().posY;
        int vilZ = village.getCenter().posZ;
        int vilR = village.getVillageRadius();
        List list = world.getEntitiesWithinAABB(entityClass, AxisAlignedBB.getBoundingBox(vilX - vilR - villageBuffer, vilY - 4, vilZ - vilR - villageBuffer, vilX + vilR + villageBuffer, (vilY + 4), (vilZ + vilR + villageBuffer)));
        return list != null ? list.size() : 0;

    }
    @Unique
    private static Vec3 multithreadingandtweaks$tryGetStraySpawningLocation(Village village, World world, int rangeX, int rangeY, int rangeZ) {
        int vilCenterX = village.getCenter().posX;
        int vilCenterY = village.getCenter().posY;
        int vilCenterZ = village.getCenter().posZ;
        int vilRadius = village.getVillageRadius();

        for(int k1 = 0; k1 < 10; ++k1) {
            int tryX = vilCenterX + world.rand.nextInt(2 * vilRadius + 1) - vilRadius;
            int tryY = vilCenterY + world.rand.nextInt(8) - 4;
            int tryZ = vilCenterZ + world.rand.nextInt(2 * vilRadius + 1) - vilRadius;
            if (multithreadingandtweaks$isInRange(tryX, tryY, tryZ, village) && multithreadingandtweaks$isValidSpawningLocation(tryX, tryY, tryZ, rangeX, rangeY, rangeZ, world)) {
                return Vec3.createVectorHelper(tryX, tryY, tryZ);
            }
        }

        return null;
    }
    @Unique
    private static boolean multithreadingandtweaks$isInRange(int x, int y, int z, Village village) {
        return village.getCenter().getDistanceSquared(x, y, z) < (float)((village.getVillageRadius() + 32) * (village.getVillageRadius() + 32));
    }

    @Unique
    private static boolean multithreadingandtweaks$isValidSpawningLocation(int spawnposX, int spawnposY, int spawnposZ, int xRange, int yRange, int zRange, World world) {
        if (!World.doesBlockHaveSolidTopSurface(world, spawnposX, spawnposY - 1, spawnposZ)) {
            return false;
        } else {
            int k1 = spawnposX - xRange / 2;
            int l1 = spawnposZ - zRange / 2;

            for(int checkX = k1; checkX < k1 + xRange; ++checkX) {
                for(int checkY = spawnposY; checkY < spawnposY + yRange; ++checkY) {
                    for(int checkZ = l1; checkZ < l1 + zRange; ++checkZ) {
                        if (world.getBlock(checkX, checkY, checkZ).isNormalCube()) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }
}
