package fr.iamacat.optimizationsandtweaks.mixins.common.hamsterific;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.reflect.ClassPath;

import es.razzleberri.hamsterrific.EntityHamster;

@Mixin(EntityHamster.class)
public abstract class MixinEntityHamster extends EntityTameable {

    @Shadow
    private static List<String> hamsterColorList;

    protected MixinEntityHamster(World p_i1604_1_) {
        super(p_i1604_1_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void setInBall(boolean b) {
        this.dataWatcher.updateObject(20, (b ? 1 : 0));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean isInBall() {
        return this.dataWatcher.getWatchableObjectInt(20) == 1;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int getBallColor() {
        return this.dataWatcher.getWatchableObjectInt(21);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void setBallColor(int b) {
        this.dataWatcher.updateObject(21, b);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private String getRandomHamsterColor() {
        this.hamsterColorInitialize();
        if (!hamsterColorList.isEmpty()) {
            Collections.shuffle(hamsterColorList);
            return hamsterColorList.get(0);
        }
        return "";
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void hamsterColorInitialize() {
        if (hamsterColorList == null) {
            hamsterColorList = new ArrayList<>();

            try {
                Pattern p = Pattern.compile("assets/minecraft/(mob/hamster/hamster_.*)");

                for (ClassPath.ResourceInfo i : ClassPath.from(
                    this.getClass()
                        .getClassLoader())
                    .getResources()) {
                    Matcher m = p.matcher(i.getResourceName());
                    if (m.matches()) {
                        String s = m.group(1);
                        hamsterColorList.add(s);
                    }
                }
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }
    }
}
