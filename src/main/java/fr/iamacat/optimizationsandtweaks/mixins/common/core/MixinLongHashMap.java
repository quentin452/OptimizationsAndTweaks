package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.Classers;
import net.minecraft.util.LongHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LongHashMap.class)
public class MixinLongHashMap {
    @Unique
    private transient Classers.Entry[] hashArray = new Classers.Entry[16];
    @Shadow
    private final float percentUseable = 0.75F;
    @Shadow
    private transient volatile int modCount;
    @Shadow
    private transient int numHashElements;
    @Shadow
    private int capacity = 12;
    @Shadow
    private static int getHashIndex(int p_76158_0_, int p_76158_1_) {
        return p_76158_0_ % p_76158_1_;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public Object getValueByKey(long p_76164_1_)
    {
        int j = Classers.Entry.getHashedKey(p_76164_1_);

        for (Classers.Entry entry = this.hashArray[getHashIndex(j, this.hashArray.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.key == p_76164_1_)
            {
                return entry.value;
            }
        }

        return null;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean containsItem(long p_76161_1_) {
        int hashIndex = getHashIndex(Classers.Entry.getHashedKey(p_76161_1_), this.hashArray.length);
        Classers.Entry entry = this.hashArray[hashIndex];

        while (entry != null) {
            if (entry.key == p_76161_1_) {
                return true;
            }
            entry = entry.nextEntry;
        }

        return false;
    }
    public final Classers.Entry getEntry(long p_76160_1_)
    {
        int j = Classers.Entry.getHashedKey(p_76160_1_);

        for (Classers.Entry entry = this.hashArray[getHashIndex(j, this.hashArray.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.key == p_76160_1_)
            {
                return entry;
            }
        }

        return null;
    }
    public void add(long p_76163_1_, Object p_76163_3_)
    {
        int j = Classers.Entry.getHashedKey(p_76163_1_);
        int k = getHashIndex(j, this.hashArray.length);

        for (Classers.Entry entry = this.hashArray[k]; entry != null; entry = entry.nextEntry)
        {
            if (entry.key == p_76163_1_)
            {
                entry.value = p_76163_3_;
                return;
            }
        }

        ++this.modCount;
        this.createKey(j, p_76163_1_, p_76163_3_, k);
    }
    private void resizeTable(int p_76153_1_)
    {
        Classers.Entry[] aentry = this.hashArray;
        int j = aentry.length;

        if (j == 1073741824)
        {
            this.capacity = Integer.MAX_VALUE;
        }
        else
        {
            Classers.Entry[] aentry1 = new Classers.Entry[p_76153_1_];
            this.optimizationsAndTweaks$copyHashTableTo(aentry1);
            this.hashArray = aentry1;
            this.capacity = (int)((float)p_76153_1_ * this.percentUseable);
        }
    }


    @Unique
    public void optimizationsAndTweaks$copyHashTableTo(Classers.Entry[] p_76154_1_)
    {
        Classers.Entry[] aentry = this.hashArray;
        int i = p_76154_1_.length;

        for (int j = 0; j < aentry.length; ++j)
        {
            Classers.Entry entry = aentry[j];

            if (entry != null)
            {
                aentry[j] = null;
                Classers.Entry entry1;

                do
                {
                    entry1 = entry.nextEntry;
                    int k = getHashIndex(entry.hash, i);
                    entry.nextEntry = p_76154_1_[k];
                    p_76154_1_[k] = entry;
                    entry = entry1;
                }
                while (entry1 != null);
            }
        }
    }
    @Unique
    public final Classers.Entry optimizationsAndTweaks$removeKey(long p_76152_1_)
    {
        int j = Classers.Entry.getHashedKey(p_76152_1_);
        int k = getHashIndex(j, this.hashArray.length);
        Classers.Entry entry = this.hashArray[k];
        Classers.Entry entry1;
        Classers.Entry entry2;

        for (entry1 = entry; entry1 != null; entry1 = entry2)
        {
            entry2 = entry1.nextEntry;

            if (entry1.key == p_76152_1_)
            {
                ++this.modCount;
                --this.numHashElements;

                if (entry == entry1)
                {
                    this.hashArray[k] = entry2;
                }
                else
                {
                    entry.nextEntry = entry2;
                }

                return entry1;
            }

            entry = entry1;
        }

        return entry1;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public Object remove(long p_76159_1_)
    {
        Classers.Entry entry = this.optimizationsAndTweaks$removeKey(p_76159_1_);
        return entry == null ? null : entry.value;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void createKey(int p_76156_1_, long p_76156_2_, Object p_76156_4_, int p_76156_5_)
    {
        Classers.Entry entry = this.hashArray[p_76156_5_];
        this.hashArray[p_76156_5_] = new Classers.Entry(p_76156_1_, p_76156_2_, p_76156_4_, entry);

        if (this.numHashElements++ >= this.capacity)
        {
            this.resizeTable(2 * this.hashArray.length);
        }
    }


}
