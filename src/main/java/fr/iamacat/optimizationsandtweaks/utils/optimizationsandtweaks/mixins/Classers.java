package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.mixins;

import java.util.Comparator;

import com.jim.obsgreenery.world.WorldGenTreeBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import cpw.mods.fml.common.ModContainer;
import thaumcraft.api.internal.DummyInternalMethodHandler;

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

    // MixinBiomeCache

    public static class Block {

        public static WorldChunkManager chunkManager;
        /** An array of chunk rainfall values saved by this cache. */
        public float[] rainfallValues = new float[256];
        /** The array of biome types stored in this BiomeCacheBlock. */
        public BiomeGenBase[] biomes = new BiomeGenBase[256];
        /** The x coordinate of the BiomeCacheBlock. */
        public int xPosition;
        /** The z coordinate of the BiomeCacheBlock. */
        public int zPosition;
        /** The last time this BiomeCacheBlock was accessed, in milliseconds. */
        public long lastAccessTime;

        public Block(int p_i1972_2_, int p_i1972_3_, WorldChunkManager chunkManager) {
            Block.chunkManager = chunkManager;
            this.xPosition = p_i1972_2_;
            this.zPosition = p_i1972_3_;
            chunkManager.getRainfall(this.rainfallValues, p_i1972_2_ << 4, p_i1972_3_ << 4, 16, 16);
            chunkManager.getBiomeGenAt(this.biomes, p_i1972_2_ << 4, p_i1972_3_ << 4, 16, 16, false);
        }

        /**
         * Returns the BiomeGenBase related to the x, z position from the cache block.
         */
        public BiomeGenBase getBiomeGenAt(int p_76885_1_, int p_76885_2_) {
            return this.biomes[p_76885_1_ & 15 | (p_76885_2_ & 15) << 4];
        }
    }

    // MixinItemInfo

    public static class ItemStackKey2 {

        public final ItemStack stack;

        public ItemStackKey2(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int hashCode() {
            if (this.stack == null) return 1;
            int hashCode = 1;
            hashCode = 31 * hashCode + stack.stackSize;
            hashCode = 31 * hashCode + Item.getIdFromItem(stack.getItem());
            hashCode = 31 * hashCode + stack.getItemDamage();
            hashCode = 31 * hashCode + (!stack.hasTagCompound() ? 0
                : stack.getTagCompound()
                    .hashCode());
            return hashCode;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof ItemStackKey2)) return false;
            return ItemStack.areItemStacksEqual(this.stack, ((ItemStackKey2) o).stack);
        }
    }

    // MixinLoader

    public static class ModIdComparator implements Comparator<ModContainer> {

        @Override
        public int compare(ModContainer o1, ModContainer o2) {
            return o1.getModId()
                .compareTo(o2.getModId());
        }
    }

    // MixinWorldGenPyramid

    public static class Pair {

        public int x;
        public int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Object p) {
            if (!(p instanceof Pair)) {
                return false;
            } else {
                return ((Pair) p).x == this.x && ((Pair) p).y == this.y;
            }
        }
    }

    // MixinInfusionVisualDisguiseArmor

    public static class FakeMethodHandler extends DummyInternalMethodHandler {

        public FakeMethodHandler() {}

        public boolean isResearchComplete(String username, String researchkey) {
            return true;
        }
    }

    // MixinGuiResearchRecipe

    public static class Coord2D {

        public int x;
        public int y;

        public Coord2D(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // MixinWorldGenTreeBase

    public enum Quadrant {
        X_Z,
        X_NEGZ,
        NEGX_Z,
        NEGX_NEGZ;

        Quadrant() {
        }

        public Quadrant next() {
            switch (this) {
                case NEGX_Z:
                    return X_Z;
                case NEGX_NEGZ:
                    return NEGX_Z;
                case X_Z:
                    return X_NEGZ;
                case X_NEGZ:
                default:
                    return NEGX_NEGZ;
            }
        }

        public Quadrant previous() {
            switch (this) {
                case NEGX_Z:
                    return NEGX_NEGZ;
                case NEGX_NEGZ:
                    return X_NEGZ;
                case X_Z:
                    return NEGX_Z;
                case X_NEGZ:
                default:
                    return X_Z;
            }
        }
    }

    public static class XZCoord {
        public int x;
        public int z;

        public XZCoord(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public XZCoord() {
            this(0, 0);
        }

        public XZCoord offset(int dir, int amount) {
            int xOff = 0;
            int zOff = 0;
            switch (dir) {
                case 2:
                    zOff = -amount;
                    break;
                case 3:
                default:
                    zOff = amount;
                    break;
                case 4:
                    xOff = -amount;
                    break;
                case 5:
                    xOff = amount;
            }

            return new XZCoord(this.x + xOff, this.z + zOff);
        }

        public XZCoord offset(int dir) {
            return this.offset(dir, 1);
        }
    }
}
