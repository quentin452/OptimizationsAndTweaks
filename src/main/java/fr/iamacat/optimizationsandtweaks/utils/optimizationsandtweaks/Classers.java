package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks;

import net.minecraft.util.LongHashMap;
import net.minecraft.util.MovingObjectPosition;

public class Classers {

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

    public static class Entry
    {
        /** the key as a long (for playerInstances it is the x in the most significant 32 bits and then y) */
        public final long key;
        /** the value held by the hash at the specified key */
        public Object value;
        /** the next hashentry in the table */
        public Entry nextEntry;
        public final int hash;
        public Entry(int p_i1553_1_, long p_i1553_2_, Object p_i1553_4_, Entry p_i1553_5_)
        {
            this.value = p_i1553_4_;
            this.nextEntry = p_i1553_5_;
            this.key = p_i1553_2_;
            this.hash = p_i1553_1_;
        }

        public final long getKey()
        {
            return this.key;
        }

        public final Object getValue()
        {
            return this.value;
        }

        public final boolean equals(Object p_equals_1_)
        {
            if (!(p_equals_1_ instanceof Entry))
            {
                return false;
            }
            else
            {
                Entry entry = (Entry)p_equals_1_;
                Long olong = Long.valueOf(this.getKey());
                Long olong1 = Long.valueOf(entry.getKey());

                if (olong == olong1 || olong != null && olong.equals(olong1))
                {
                    Object object1 = this.getValue();
                    Object object2 = entry.getValue();

                    if (object1 == object2 || object1 != null && object1.equals(object2))
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        public final int hashCode()
        {
            return getHashedKey(this.key);
        }

        public final String toString()
        {
            return this.getKey() + "=" + this.getValue();
        }

        public static int getHashedKey(long p_76155_0_)
        {
            return hash((int)(p_76155_0_ ^ p_76155_0_ >>> 32));
        }
        private static int hash(int p_76157_0_)
        {
            p_76157_0_ ^= p_76157_0_ >>> 20 ^ p_76157_0_ >>> 12;
            return p_76157_0_ ^ p_76157_0_ >>> 7 ^ p_76157_0_ >>> 4;
        }
    }
}
