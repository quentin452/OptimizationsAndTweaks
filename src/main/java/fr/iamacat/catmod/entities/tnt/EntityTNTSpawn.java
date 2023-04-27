//needed to make a custom tnt(entity)
package fr.iamacat.catmod.entities.tnt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
//no usage
public class EntityTNTSpawn extends Entity {
    private int fuse;
    public EntityTNTSpawn(World world) {
        this(world, 0.0, 0.0, 0.0, null);
    }

    public EntityTNTSpawn(World world, double x, double y, double z, EntityLivingBase placer) {
        super(world);
        this.fuse = 80;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.setPosition(x, y, z);
        if (placer != null) {
            float f = (float)(Math.random() * Math.PI * 2.0D);
            this.motionX = (double)(-((float)Math.sin((double)f)) * 0.02F);
            this.motionY = 0.20000000298023224D;
            this.motionZ = (double)(-((float)Math.cos((double)f)) * 0.02F);
        }
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }


    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;
        }

        if (this.fuse-- <= 0) {
            this.explode();
        } else {
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void explode() {
        if (!this.worldObj.isRemote) {
            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 4.0F, true);
            this.setDead();
        }
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.fuse = nbt.getShort("Fuse");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setShort("Fuse", (short)this.fuse);
    }
    }
