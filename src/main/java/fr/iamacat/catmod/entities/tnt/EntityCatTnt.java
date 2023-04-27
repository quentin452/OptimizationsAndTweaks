package fr.iamacat.catmod.entities.tnt;

import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityCatTnt extends EntityTNTPrimed {

    public int fuse;
    private String customName;

    public EntityCatTnt(World world) {
        super(world);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if (this.customName != null && !this.customName.isEmpty()) {
            compound.setString("CustomName", this.customName);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("CustomName")) {
            this.customName = compound.getString("CustomName");
        }
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

}
