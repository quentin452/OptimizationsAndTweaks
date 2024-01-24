package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.mixins;

import java.util.Comparator;
import java.util.Objects;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.collections.maps.LongHashMap2;
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

        Quadrant() {}

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

    // MixinBlockFluidClassic

    public static class FlowCostContext {

        public final int x;
        public final int y;
        public final int z;
        public final int recurseDepth;
        public final int adjSide;

        public FlowCostContext(int x, int y, int z, int recurseDepth, int adjSide) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.recurseDepth = recurseDepth;
            this.adjSide = adjSide;
        }
    }

    // MixinLongHashMap

    public static class Entry {

        /** the key as a long (for playerInstances it is the x in the most significant 32 bits and then y) */
        public final long key;
        /** the value held by the hash at the specified key */
        public Object value;
        /** the next hashentry in the table */
        public Entry nextEntry;
        public final int hash;

        public Entry(int p_i1553_1_, long p_i1553_2_, Object p_i1553_4_, Entry p_i1553_5_) {
            this.value = p_i1553_4_;
            this.nextEntry = p_i1553_5_;
            this.key = p_i1553_2_;
            this.hash = p_i1553_1_;
        }

        public final long getKey() {
            return this.key;
        }

        public final Object getValue() {
            return this.value;
        }

        public final boolean equals(Object p_equals_1_) {
            if (!(p_equals_1_ instanceof Entry)) {
                return false;
            }

            Entry entry = (Entry) p_equals_1_;
            return this.getKey() == entry.getKey() && Objects.equals(this.getValue(), entry.getValue());
        }

        public final int hashCode() {
            return getHashedKey(this.key);
        }

        public final String toString() {
            return this.getKey() + "=" + this.getValue();
        }

        public static int getHashedKey(long p_76155_0_) {
            return (int) p_76155_0_ + (int) (p_76155_0_ >>> 32) * 92821;
        }
    }
}
