package fr.iamacat.optimizationsandtweaks.mixins.common.practicallogistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import sonar.core.helpers.NBTRegistryHelper;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.info.types.*;
import sonar.logistics.registries.InfoTypeRegistry;

import java.util.HashMap;
import java.util.Map;

@Mixin(InfoTypeRegistry.class)
public class MixinInfoTypeRegistry extends NBTRegistryHelper.Buf<ILogicInfo> {
    private Map<Class<? extends ILogicInfo>, ILogicInfo> registeredClasses = new HashMap<>();


    public void registerObject(ILogicInfo object) {
        registeredClasses.put(object.getClass(), object);
        this.registerObject(object);
    }
    public void register() {
        this.registerObject(new BlockCoordsInfo());
        this.registerObject(new CategoryInfo());
        this.registerObject(new LogicInfo());
        this.registerObject(new StoredStackInfo());
        this.registerObject(new ProgressInfo());
        this.registerObject(new FluidInfo());
        this.registerObject(new ThaumcraftAspectInfo());
        this.registerObject(new ManaInfo());
        this.registerObject(new FluidStackInfo());
        this.registerObject(new InventoryInfo());
        this.registerObject(new StoredEnergyInfo());
        this.registerObject(new FluidInventoryInfo());
        this.registerObject(new BlockNameInfo());
        this.registerObject(new ModidInfo());
    }

    public String registeryType() {
        return "Info Type";
    }

    public boolean areTypesEqual(ILogicInfo target, ILogicInfo current) {
        if (target == null && current == null) {
            return true;
        } else if (target != null && current != null) {
            return target.checkInfoTypes(current) && target.equals(current);
        } else {
            return false;
        }
    }
}
