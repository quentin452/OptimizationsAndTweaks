package fr.iamacat.optimizationsandtweaks.mixins.common.codechickencore;

import codechicken.nei.ItemMobSpawner;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemMobSpawner.class)
public class MixinItemMobSpawner extends ItemBlock {
    @Final
    @Shadow
    private static final Map<Integer, EntityLiving> entityHashMap = new HashMap<>();
    @Final
    @Shadow
    private static final Map<Integer, String> IDtoNameMap = new HashMap<>();
    @Shadow
    public static int idPig;
    @Shadow
    private static boolean loaded;

    public MixinItemMobSpawner(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    /**
     * @author iamacatfr
     * @reason remove spawner loading to reduce CPU time
     */
    @Overwrite
    public static void loadSpawners(World world) {
    }
}
