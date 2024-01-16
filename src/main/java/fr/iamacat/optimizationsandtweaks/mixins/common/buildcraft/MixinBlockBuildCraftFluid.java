package fr.iamacat.optimizationsandtweaks.mixins.common.buildcraft;

import buildcraft.core.lib.block.BlockBuildCraftFluid;
import buildcraft.core.lib.render.EntityDropParticleFX;
import buildcraft.core.lib.utils.ResourceUtils;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
@Mixin(BlockBuildCraftFluid.class)
public abstract class MixinBlockBuildCraftFluid extends BlockFluidClassic {
    @Shadow
    protected float particleRed;
    @Shadow
    protected float particleGreen;
    @Shadow
    protected float particleBlue;
    @Shadow
    @SideOnly(Side.CLIENT)
    protected IIcon[] theIcon;
    @Shadow
    protected boolean flammable;
    @Shadow
    protected boolean dense = false;
    @Shadow
    protected int flammability = 0;
    @Shadow
    private MapColor mapColor;

    public MixinBlockBuildCraftFluid(Fluid fluid, Material material) {
        super(fluid, material);
    }

    @Shadow
    public IIcon getIcon(int side, int meta) {
        return side != 0 && side != 1 ? this.theIcon[1] : this.theIcon[0];
    }

    @Shadow
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        String prefix = ResourceUtils.getObjectPrefix(Block.blockRegistry.getNameForObject(this));
        prefix = prefix.substring(0, prefix.indexOf(":") + 1) + "fluids/";
        this.theIcon = new IIcon[] { iconRegister.registerIcon(prefix + fluidName + "_still"),
            iconRegister.registerIcon(prefix + fluidName + "_flow") };
    }
    @Shadow
    public static boolean isFluidExplosive(World world, int x, int z) {
        return world.provider.dimensionId == -1;
    }

    @Shadow
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (flammable && isFluidExplosive(world, x, z)) {
            world.setBlock(x, y, z, Blocks.air, 0, 2); // Do not cause block updates!
            world.newExplosion(null, x, y, z, 4F, true, true);
        } else {
            super.updateTick(world, x, y, z, rand);
        }
    }

    @Shadow
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
        world.setBlock(x, y, z, Blocks.air, 0, 2); // Do not cause block updates!
        for (ForgeDirection fd : ForgeDirection.VALID_DIRECTIONS) {
            Block block = world.getBlock(x + fd.offsetX, y + fd.offsetY, z + fd.offsetZ);
            if (block instanceof BlockBuildCraftFluid) {
                world.scheduleBlockUpdate(x + fd.offsetX, y + fd.offsetY, z + fd.offsetZ, block, 2);
            } else {
                world.notifyBlockOfNeighborChange(x + fd.offsetX, y + fd.offsetY, z + fd.offsetZ, block);
            }
        }
        onBlockDestroyedByExplosion(world, x, y, z, explosion);
    }

    @Overwrite
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if(entity.isBurning()) entity.extinguish();
        if (!dense) {
            return;
        }

        entity.motionY = Math.min(0.0, entity.motionY);

        if (entity.motionY < -0.05) {
            entity.motionY *= 0.05;
        }

        entity.motionX = Math.max(-0.05, Math.min(0.05, entity.motionX * 0.05));
        entity.motionY -= 0.05;
        entity.motionZ = Math.max(-0.05, Math.min(0.05, entity.motionZ * 0.05));
    }

    @Overwrite(remap = false)
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 0;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 0;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return false;
    }

    @Overwrite(remap = false)
    public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Shadow
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        super.randomDisplayTick(world, x, y, z, rand);

        if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)
            && !world.getBlock(x, y - 2, z).getMaterial().blocksMovement()) {

            double px = x + rand.nextFloat();
            double py = y - 1.05D;
            double pz = z + rand.nextFloat();

            EntityFX fx = new EntityDropParticleFX(world, px, py, pz, particleRed, particleGreen, particleBlue);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }

    @Shadow
    public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
        if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
            return false;
        }
        return super.canDisplace(world, x, y, z);
    }

    @Shadow
    public boolean displaceIfPossible(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
            return false;
        }
        return super.displaceIfPossible(world, x, y, z);
    }

    @Shadow
    public MapColor getMapColor(int meta) {
        return mapColor;
    }

    @Shadow
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }
    @Inject(method="<init>", at=@At("RETURN"))
    private void initializeFlammability(CallbackInfo ci) {
        this.flammability = 0;
    }
}

