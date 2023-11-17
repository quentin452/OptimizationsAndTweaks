package fr.iamacat.optimizationsandtweaks.mixins.common.lotrimprovements;

import com.jediexe.lotrimprovements.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Main.class)
public class MixinMain {

    /**
     * @author iamacatfr
     * @reason disable attackindicator from Lotr Improvements
     */
    @Overwrite
    public static void LOTROverride() {
    }
}
