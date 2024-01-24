package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.collections.maps;

import java.util.Objects;

public class LongHashMap2 {

    /** the array of all elements in the hash */
    private transient LongHashMap2.Entry[] hashArray = new LongHashMap2.Entry[16];
    /** the number of elements in the hash array */
    private transient int numHashElements;
    /** the maximum amount of elements in the hash (probably 3/4 the size due to meh hashing function) */
    private int capacity = 12;
    /** percent of the hasharray that can be used without hash colliding probably */
    private final float percentUseable = 0.75F;
    /** count of times elements have been added/removed */
    private transient volatile int modCount;

    /**
     * @author TheMasterCaver, embeddedt (mixin version)
     * @reason Use a better hash (from TMCW) that avoids collisions.
     */
    private static int getHashedKey(long p_76155_0_) {
        return (int) p_76155_0_ + (int) (p_76155_0_ >>> 32) * 92821;
    }

    /**
     * the hash function
     */
    private static int hash(int p_76157_0_) {
        p_76157_0_ ^= p_76157_0_ >>> 20 ^ p_76157_0_ >>> 12;
        return p_76157_0_ ^ p_76157_0_ >>> 7 ^ p_76157_0_ >>> 4;
    }

    /**
     * gets the index in the hash given the array length and the hashed key
     */
    private static int getHashIndex(int p_76158_0_, int p_76158_1_) {
        return p_76158_0_ & p_76158_1_ - 1;
    }

    public int getNumHashElements() {
        return this.numHashElements;
    }

    /**
     * get the value from the map given the key
     */
    public Object getValueByKey(long p_76164_1_) {
        int j = getHashedKey(p_76164_1_);
        int index = getHashIndex(j, this.hashArray.length);

        LongHashMap2.Entry entry = this.hashArray[index];
        while (entry != null) {
            if (entry.key == p_76164_1_) {
                return entry.value;
            }
            entry = entry.nextEntry;
        }

        return null;
    }

    public boolean containsItem(long p_76161_1_) {
        return this.getEntry(p_76161_1_) != null;
    }

    final LongHashMap2.Entry getEntry(long p_76160_1_) {
        int index = getHashIndex(getHashedKey(p_76160_1_), this.hashArray.length);
        LongHashMap2.Entry entry = this.hashArray[index];
        while (entry != null) {
            if (entry.key == p_76160_1_) {
                return entry;
            }
            entry = entry.nextEntry;
        }
        return null;
    }

    /**
     * Add a key-value pair.
     */
    public void add(long p_76163_1_, Object p_76163_3_) {
        int j = getHashedKey(p_76163_1_);
        int k = getHashIndex(j, this.hashArray.length);

        for (LongHashMap2.Entry entry = this.hashArray[k]; entry != null; entry = entry.nextEntry) {
            if (entry.key == p_76163_1_) {
                entry.value = p_76163_3_;
                return;
            }
        }

        ++this.modCount;
        this.createKey(j, p_76163_1_, p_76163_3_, k);
    }

    /**
     * resizes the table
     */
    private void resizeTable(int p_76153_1_) {
        LongHashMap2.Entry[] aentry = this.hashArray;
        int j = aentry.length;

        if (j == 1073741824) {
            this.capacity = Integer.MAX_VALUE;
        } else {
            LongHashMap2.Entry[] aentry1 = new LongHashMap2.Entry[p_76153_1_];
            this.copyHashTableTo(aentry1);
            this.hashArray = aentry1;
            this.capacity = (int) (p_76153_1_ * this.percentUseable);
        }
    }

    /**
     * copies the hash table to the specified array
     */
    private void copyHashTableTo(LongHashMap2.Entry[] p_76154_1_) {
        LongHashMap2.Entry[] aentry = this.hashArray;
        int i = p_76154_1_.length;

        for (int j = 0; j < aentry.length; ++j) {
            LongHashMap2.Entry entry = aentry[j];

            if (entry != null) {
                aentry[j] = null;
                LongHashMap2.Entry entry1;

                do {
                    entry1 = entry.nextEntry;
                    int k = getHashIndex(entry.hash, i);
                    entry.nextEntry = p_76154_1_[k];
                    p_76154_1_[k] = entry;
                    entry = entry1;
                } while (entry1 != null);
            }
        }
    }

    /**
     * calls the removeKey method and returns removed object
     */
    public Object remove(long p_76159_1_) {
        LongHashMap2.Entry entry = this.removeKey(p_76159_1_);
        return entry == null ? null : entry.value;
    }

    /**
     * removes the key from the hash linked list
     */
    final LongHashMap2.Entry removeKey(long p_76152_1_) {
        int j = getHashedKey(p_76152_1_);
        int k = getHashIndex(j, this.hashArray.length);
        LongHashMap2.Entry entry = this.hashArray[k];
        LongHashMap2.Entry entry1;
        LongHashMap2.Entry entry2;

        for (entry1 = entry; entry1 != null; entry1 = entry2) {
            entry2 = entry1.nextEntry;

            if (entry1.key == p_76152_1_) {
                ++this.modCount;
                --this.numHashElements;

                if (entry == entry1) {
                    this.hashArray[k] = entry2;
                } else {
                    entry.nextEntry = entry2;
                }

                return entry1;
            }

            entry = entry1;
        }

        return entry1;
    }

    /**
     * creates the key in the hash table
     */
    private void createKey(int p_76156_1_, long p_76156_2_, Object p_76156_4_, int p_76156_5_) {
        LongHashMap2.Entry entry = this.hashArray[p_76156_5_];
        this.hashArray[p_76156_5_] = new LongHashMap2.Entry(p_76156_1_, p_76156_2_, p_76156_4_, entry);

        if (this.numHashElements++ >= this.capacity) {
            this.resizeTable(2 * this.hashArray.length);
        }
    }

    static class Entry {

        /** the key as a long (for playerInstances it is the x in the most significant 32 bits and then y) */
        final long key;
        /** the value held by the hash at the specified key */
        Object value;
        /** the next hashentry in the table */
        LongHashMap2.Entry nextEntry;
        final int hash;

        Entry(int p_i1553_1_, long p_i1553_2_, Object p_i1553_4_, LongHashMap2.Entry p_i1553_5_) {
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
            if (!(p_equals_1_ instanceof LongHashMap2.Entry)) {
                return false;
            }

            LongHashMap2.Entry entry = (LongHashMap2.Entry) p_equals_1_;
            return this.getKey() == entry.getKey() && Objects.equals(this.getValue(), entry.getValue());
        }

        public final int hashCode() {
            return LongHashMap2.getHashedKey(this.key);
        }

        public final String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }
}
