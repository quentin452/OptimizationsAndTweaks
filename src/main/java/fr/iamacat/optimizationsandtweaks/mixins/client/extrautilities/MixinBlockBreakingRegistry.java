package fr.iamacat.optimizationsandtweaks.mixins.client.extrautilities;

import com.rwtema.extrautils.LogHelper;
import com.rwtema.extrautils.tileentity.enderquarry.BlockBreakingRegistry;
import com.rwtema.extrautils.tileentity.enderquarry.BlockDummy;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.extrautilities.ClassBytesCache;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fluids.IFluidBlock;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Method;
import java.util.*;

@Mixin(BlockBreakingRegistry.class)
public class MixinBlockBreakingRegistry {
    @Shadow
    public static BlockBreakingRegistry instance = new BlockBreakingRegistry();
    @Shadow
    public static HashMap<Block, BlockBreakingRegistry.entry> entries = new HashMap();
    @Shadow
    public static Set<String> methodNames = null;
    @Shadow
    public static Map<String, Boolean> names = new HashMap();
    @Shadow
    public static LaunchClassLoader cl = (LaunchClassLoader)BlockBreakingRegistry.class.getClassLoader();
    @Shadow
    public static boolean blackList(Block id) {
        return entries.get(id).blackList;
    }
    @Shadow
    public static boolean isSpecial(Block id) {
        return entries.get(id).isSpecial;
    }
    @Shadow
    public static boolean isFence(Block id) {
        return entries.get(id).isFence;
    }

    @Shadow
    public static boolean isFluid(Block id) {
        return entries.get(id).isFluid;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void setupBreaking() {
        if (methodNames == null) {
            methodNames = new HashSet<>();
            for (Method m : BlockDummy.class.getDeclaredMethods()) {
                methodNames.add(m.getName());
            }

            Iterator blockIterator = Block.blockRegistry.iterator();
            while (blockIterator.hasNext()) {
                Block block = (Block) blockIterator.next();
                entries.put(block, new BlockBreakingRegistry.entry());
            }

            entries.get(Blocks.torch).blackList = true;

            blockIterator = Block.blockRegistry.iterator();
            while (blockIterator.hasNext()) {
                Block block = (Block) blockIterator.next();
                BlockBreakingRegistry.entry e = entries.get(block);
                String name = block.getUnlocalizedName() != null ? block.getUnlocalizedName() : block.getClass().getName();
                try {
                    name = Block.blockRegistry.getNameForObject(block);
                } catch (Exception ignored) {
                    LogHelper.error("Error getting name for block " + name);
                }

                e.isFence = false;
                try {
                    int renderType = block.getRenderType();
                    e.isFence = renderType == 11 || block instanceof BlockFence;
                } catch (Exception | NoClassDefFoundError exception) {
                    LogHelper.error("Error checking block class code: Exception calling getRenderType() on block " + name);
                    exception.printStackTrace();
                    if (exception instanceof NoClassDefFoundError) {
                        throw new RuntimeException("Serious error calling getRenderType() on block " + name + " : Likely cause is client-side code is being called server-side", exception);
                    } else {
                        throw new RuntimeException("Serious error calling getRenderType() on block " + name, exception);
                    }
                }

                if (block instanceof BlockLiquid || block instanceof IFluidBlock) {
                    e.blackList = true;
                    e.isFluid = true;
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean hasSpecialBreaking(Class clazz) {
        if (clazz != null && !clazz.equals(Block.class)) {
            if (names.containsKey(clazz.getName())) {
                return names.get(clazz.getName());
            } else {
                LogHelper.fine("Checking class for special block breaking code: " + clazz.getName());

                try {
                    byte[] bytes = ClassBytesCache.getClassBytes(clazz.getClassLoader(), clazz.getName());

                    ClassNode classNode = new ClassNode();
                    ClassReader classReader = new ClassReader(bytes);
                    classReader.accept(classNode, 0);

                    for (MethodNode method : classNode.methods) {
                        if (methodNames.contains(method.name)) {
                            LogHelper.fine("Detected special block breaking code in class: " + clazz.getName());
                            names.put(clazz.getName(), true);
                            return true;
                        }
                    }
                } catch (Throwable var8) {
                    try {
                        Method[] arr$ = clazz.getDeclaredMethods();

                        for (Method m : arr$) {
                            if (methodNames.contains(m.getName())) {
                                LogHelper.fine("Detected special block breaking code in class: " + clazz.getName());
                                names.put(clazz.getName(), true);
                                return true;
                            }
                        }
                    } catch (Throwable var7) {
                        LogHelper.error("Error checking block class code: " + clazz.getName());
                        var8.printStackTrace();
                        var7.printStackTrace();
                        names.put(clazz.getName(), true);
                        return true;
                    }
                }

                boolean result = this.hasSpecialBreaking(clazz.getSuperclass());
                names.put(clazz.getName(), result);
                return result;
            }
        } else {
            return false;
        }
    }
}
