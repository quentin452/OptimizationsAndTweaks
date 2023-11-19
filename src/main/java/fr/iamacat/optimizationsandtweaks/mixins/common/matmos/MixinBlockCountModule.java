package fr.iamacat.optimizationsandtweaks.mixins.common.matmos;

import eu.ha3.matmos.core.expansion.ExpansionManager;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.SheetDataPackage;
import eu.ha3.matmos.data.modules.AbstractThingCountModule;
import eu.ha3.matmos.data.modules.BlockCountModule;
import eu.ha3.matmos.data.modules.VirtualCountModule;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.block.Block;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Mixin(BlockCountModule.class)
public class MixinBlockCountModule  extends AbstractThingCountModule<Pair<Block, Integer>> {
    @Shadow
    private static final int INITIAL_SIZE = 4096;
    @Shadow
    private boolean[] wasZero;
    @Shadow
    private int[] counts;
    @Shadow
    private int[] zeroMetadataCounts;
    @Shadow
    private TreeMap<Integer, Integer>[] metadatas;
    @Shadow
    int size;
    @Shadow
    VirtualCountModule<Pair<Block, Integer>> thousand;
    @Shadow
    SheetDataPackage sheetData;
    @Shadow
    int blocksCounted;

    public MixinBlockCountModule(DataPackage data, String name) {
        this(data, name, false);
    }

    public MixinBlockCountModule(DataPackage data, String name, boolean doNotUseDelta) {
        this(data, name, doNotUseDelta, null);
    }

    public MixinBlockCountModule(DataPackage data, String name, boolean doNotUseDelta, VirtualCountModule<Pair<Block, Integer>> thousand) {
        super(data, name, doNotUseDelta);
        this.wasZero = new boolean[4096];
        this.counts = new int[4096];
        this.zeroMetadataCounts = new int[4096];
        this.metadatas = new TreeMap[4096];
        this.size = 4096;
        this.blocksCounted = 0;
        this.thousand = thousand;
        data.getSheet(name).setDefaultValue("0");
        if (!doNotUseDelta) {
            data.getSheet(name + "_delta").setDefaultValue("0");
        }

        this.sheetData = (SheetDataPackage)data;
    }
    @Shadow
    protected void doProcess() {
        this.count();
        this.apply();
    }
    @Shadow
    public void increment(Pair<Block, Integer> blockMeta) {
        this.increment(blockMeta, 1);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void increment(Pair<Block, Integer> blockMeta, int amount) {
        Block block = blockMeta.getLeft();
        int meta = blockMeta.getRight();
        int id = ExpansionManager.dealiasToID(block, this.sheetData);
        if (id >= this.size) {
            this.resize(id + 1);
        }

        int[] var10000 = this.counts;
        var10000[id] += amount;
        if (meta != -1 && meta != 0) {
            if (this.metadatas[id] == null) {
                this.metadatas[id] = new TreeMap<>();
            }

            Integer metaCount = this.metadatas[id].get(meta);
            this.metadatas[id].put(meta, metaCount == null ? 0 : metaCount + amount);
        } else if (meta == 0) {
            var10000 = this.zeroMetadataCounts;
            var10000[id] += amount;
        }

        this.blocksCounted += amount;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int get(Pair<Block, Integer> blockMeta) {
        Block block = blockMeta.getLeft();
        int meta = blockMeta.getRight();
        int id = Block.getIdFromBlock(block);
        if (id >= this.size) {
            return 0;
        } else {
            return meta == -1 ? this.counts[id] : this.metadatas[id].get(meta);
        }
    }
    @Shadow
    public void count() {
    }
    /**
     * @author
     * @reason
     */
    @Override
    public void apply() {
        for (int i = 0; i < this.counts.length; ++i) {
            processCount(i, this.counts[i], false);
            processCount(i, this.zeroMetadataCounts[i], true);

            if (this.metadatas[i] != null) {
                processMetadata(i, this.metadatas[i]);
            }

            this.wasZero[i] = isZero(i);
        }

        resetValues();
    }
    @Unique
    private void processCount(int id, int count, boolean isZeroMetadata) {
        if (count > 0 || !this.wasZero[id]) {
            String name = isZeroMetadata ? MAtUtil.asPowerMeta(Block.getBlockById(id), 0) : MAtUtil.nameOf(Block.getBlockById(id));
            setValue(name, count);

            if (this.thousand != null) {
                float floatVal = (float) count / (float) this.blocksCounted * 1000.0F;
                this.thousand.setValue(name, (int) Math.ceil(floatVal));
            }
        }
    }
    @Unique
    private void processMetadata(int id, Map<Integer, Integer> metadata) {
        for (Map.Entry<Integer, Integer> entry : metadata.entrySet()) {
            int value = entry.getValue();
            if (value > 0 || !this.wasZero[id]) {
                String name = MAtUtil.asPowerMeta(Block.getBlockById(id), entry.getKey());
                setValue(name, value);

                if (this.thousand != null) {
                    float floatVal = (float) value / (float) this.blocksCounted * 1000.0F;
                    this.thousand.setValue(name, (int) Math.ceil(floatVal));
                }
            }
        }
    }
    @Unique
    private boolean isZero(int index) {
        return this.counts[index] == 0 && this.zeroMetadataCounts[index] == 0 &&
            (this.metadatas[index] == null || this.metadatas[index].values().stream().allMatch(val -> val == 0));
    }
    @Unique
    private void resetValues() {
        this.blocksCounted = 0;
        Arrays.fill(this.counts, 0);
        Arrays.fill(this.zeroMetadataCounts, 0);
        Arrays.stream(this.metadatas)
            .filter(Objects::nonNull)
            .forEach(m -> m.replaceAll((k, v) -> 0));
    }

   /**
    * @author
    * @reason
    */
   @Overwrite(remap = false)
    private void resize(int newSize) {
        int stepSize = 1024;
        newSize = (int)Math.ceil((double)newSize / (double)stepSize) * stepSize;
        this.wasZero = Arrays.copyOf(this.wasZero, newSize);
        this.counts = Arrays.copyOf(this.counts, newSize);
        this.zeroMetadataCounts = Arrays.copyOf(this.zeroMetadataCounts, newSize);
        this.metadatas = Arrays.copyOf(this.metadatas, newSize);
        this.size = newSize;
    }
}
