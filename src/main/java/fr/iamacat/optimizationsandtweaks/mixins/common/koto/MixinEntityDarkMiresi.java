package fr.iamacat.optimizationsandtweaks.mixins.common.koto;

import com.ani.koto.EntityDarkMiresi;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityDarkMiresi.class)
public abstract class MixinEntityDarkMiresi extends EntityMob implements IRangedAttackMob {

    @Shadow
    private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0, 20, 60, 15.0F);
    @Shadow
    private EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, 1.0, true);
    @Shadow
    private int healTimer = 0;
    @Shadow
    public boolean usingBow = false;
    @Shadow
    public boolean usingSword = true;
    @Shadow
    public int chooseArmor = 0;
    @Unique
    private List<Boolean> healthDecisions = new ArrayList<>();
    @Unique
    private boolean isUsingSword = true;

    public MixinEntityDarkMiresi(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    /**
     * @author iamacatfr
     * @reason fix <a href="https://github.com/quentin452/OptimizationsAndTweaks/issues/75">...</a>
     */
    @Overwrite(remap = false)
    public void func_70636_d() {
        super.onLivingUpdate();

        boolean isHealthAboveThreshold = getHealth() <= 15.0F;

        if (healthDecisions.isEmpty() || Boolean.TRUE.equals(Boolean.TRUE.equals(isHealthAboveThreshold != healthDecisions.get(healthDecisions.size() - 1)))) {
            healthDecisions.add(isHealthAboveThreshold);

            if (isHealthAboveThreshold) {
                if (!isUsingSword) {
                    tasks.removeTask(aiArrowAttack);
                    tasks.addTask(4, aiAttackOnCollide);
                    setCurrentItemOrArmor(0, new ItemStack(Items.diamond_axe));
                    isUsingSword = true;
                }
            } else {
                if (isUsingSword) {
                    tasks.removeTask(aiAttackOnCollide);
                    tasks.addTask(4, aiArrowAttack);
                    setCurrentItemOrArmor(0, new ItemStack(Items.bow));
                    isUsingSword = false;
                }
            }
        }
    }
}
