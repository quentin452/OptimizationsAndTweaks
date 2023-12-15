package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks;

import fr.iamacat.optimizationsandtweaks.mixins.common.core.MixinWorldServer;
import net.minecraft.entity.Entity;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MovingObjectPosition;

import java.util.ArrayList;

public class Classers {

    // MixinMinecraft

    public static final class SwitchMovingObjectType {

        public static final int[] field_152390_a = new int[MovingObjectPosition.MovingObjectType.values().length];

        static {
            try {
                field_152390_a[MovingObjectPosition.MovingObjectType.ENTITY.ordinal()] = 1;
            } catch (NoSuchFieldError var2) {
                ;
            }

            try {
                field_152390_a[MovingObjectPosition.MovingObjectType.BLOCK.ordinal()] = 2;
            } catch (NoSuchFieldError var1) {
                ;
            }
        }
    }
}
