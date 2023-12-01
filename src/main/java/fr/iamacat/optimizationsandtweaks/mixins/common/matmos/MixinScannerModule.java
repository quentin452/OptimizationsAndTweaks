package fr.iamacat.optimizationsandtweaks.mixins.common.matmos;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.*;
import eu.ha3.matmos.data.scanners.Progress;
import eu.ha3.matmos.data.scanners.Scan;
import eu.ha3.matmos.data.scanners.ScanOperations;
import eu.ha3.matmos.data.scanners.ScannerModule;
import eu.ha3.matmos.util.BlockPos;
import eu.ha3.matmos.util.IDontKnowHowToCode;
import eu.ha3.matmos.util.MAtUtil;

@Mixin(ScannerModule.class)
public abstract class MixinScannerModule implements PassOnceModule, ScanOperations, Progress {

    @Shadow
    public static final String THOUSAND_SUFFIX = "_p1k";
    @Shadow
    public static final String WEIGHTED_SUFFIX = "_w";
    @Shadow
    public static final String ABOVE_SUFFIX = "_above";
    @Shadow
    public static final String BELOW_SUFFIX = "_below";
    @Shadow
    private static final int WORLD_LOADING_DURATION = 100;
    @Shadow
    private final String passOnceName;
    @Shadow
    private final Set<ScannerModule.Submodule> requiredSubmodules;
    @Shadow
    private final int movement;
    @Shadow
    private final int passivePulse;
    @Shadow
    private final int pulse;
    @Shadow
    private final int xS;
    @Shadow
    private final int yS;
    @Shadow
    private final int zS;
    @Shadow
    private final int blocksPerCall;
    @Shadow
    private final AbstractThingCountModule base;
    @Shadow
    private final BlockCountModule weighted;
    @Shadow
    private final AbstractThingCountModule thousand;
    @Shadow
    private final BlockCountModule above;
    @Shadow
    private final BlockCountModule below;
    @Shadow
    private final Set<String> subModules;
    @Shadow
    private int ticksSinceBoot;
    @Shadow
    private boolean firstScan;
    @Shadow
    private boolean workInProgress;
    @Shadow
    private int lastScanTime;
    @Shadow
    private int dimension;
    @Shadow
    private int xx;
    @Shadow
    private int yy;
    @Shadow
    private int zz;
    @Shadow
    private final Scan scanner;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private eu.ha3.matmos.data.modules.Module initSubmodule(ScannerModule.Submodule sm, String baseName,
        DataPackage data) {
        boolean useExternalStringCountModule = false;

        if (sm != ScannerModule.Submodule.BASE && !this.requiredSubmodules.contains(sm)) {
            return null;
        }

        eu.ha3.matmos.data.modules.Module result = null;
        String submoduleName = baseName;

        switch (sm) {
            case BASE:
                result = useExternalStringCountModule ? new ExternalStringCountModule(data, baseName, true)
                    : new BlockCountModule(data, baseName, true, (VirtualCountModule) this.thousand);
                break;
            case THOUSAND:
                submoduleName = baseName + "_p1k";
                result = useExternalStringCountModule ? new ThousandStringCountModule(data, submoduleName, true)
                    : new VirtualCountModule<>(data, submoduleName, true);
                break;
            case WEIGHTED:
                submoduleName = baseName + "_w";
                result = new BlockCountModule(data, submoduleName, true, null);
                break;
            case ABOVE:
                submoduleName = baseName + "_above";
                result = new BlockCountModule(data, submoduleName, true, null);
                break;
            case BELOW:
                submoduleName = baseName + "_below";
                result = new BlockCountModule(data, submoduleName, true, null);
                break;
        }

        if (result != null) {
            this.subModules.add(submoduleName);
            data.getSheet(submoduleName)
                .setDefaultValue("0");
        }

        return result;
    }

    private MixinScannerModule(Class<? extends Scan> scannerClass, Object scannerArgument, boolean hasScannerArgument,
        DataPackage data, String passOnceName, String baseName, List<ScannerModule.Submodule> requiredSubmodules,
        int movement, int passivePulse, int pulse, int xS, int yS, int zS, int blocksPerCall) {
        this.subModules = new HashSet();
        this.lastScanTime = -1;
        this.dimension = Integer.MIN_VALUE;
        this.xx = Integer.MIN_VALUE;
        this.yy = Integer.MIN_VALUE;
        this.zz = Integer.MIN_VALUE;
        this.passOnceName = passOnceName;
        this.requiredSubmodules = new HashSet(requiredSubmodules);
        this.movement = movement;
        this.passivePulse = passivePulse;
        this.pulse = pulse;
        this.xS = xS;
        this.yS = yS;
        this.zS = zS;
        this.blocksPerCall = blocksPerCall;
        this.thousand = (AbstractThingCountModule) this.initSubmodule(ScannerModule.Submodule.THOUSAND, baseName, data);
        this.weighted = (BlockCountModule) this.initSubmodule(ScannerModule.Submodule.WEIGHTED, baseName, data);
        this.base = (AbstractThingCountModule) this.initSubmodule(ScannerModule.Submodule.BASE, baseName, data);
        this.above = (BlockCountModule) this.initSubmodule(ScannerModule.Submodule.ABOVE, baseName, data);
        this.below = (BlockCountModule) this.initSubmodule(ScannerModule.Submodule.BELOW, baseName, data);
        Scan theScanner = null;

        try {
            if (hasScannerArgument) {
                theScanner = scannerClass.getConstructor(Object.class)
                    .newInstance(scannerArgument);
            } else {
                theScanner = scannerClass.newInstance();
            }
        } catch (InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException
            | IllegalAccessException | IllegalArgumentException var17) {
            var17.printStackTrace();
        }

        this.scanner = theScanner;
        this.scanner.setPipeline(this);
        this.ticksSinceBoot = 0;
        this.firstScan = true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void inputAndReturnBlockMeta(int x, int y, int z, int weight, Block[] blockOut, int[] metaOut) {
        Block block = MAtUtil.getBlockAt(new BlockPos(x, y, z));
        int meta = MAtUtil.getMetaAt(new BlockPos(x, y, z), -1);

        if (this.base instanceof BlockCountModule) {
            this.base.increment(Pair.of(block, meta));
        } else if (this.base instanceof ExternalStringCountModule) {
            String name = MAtUtil.nameOf(block);
            this.base.increment(name);
            this.base.increment(MAtUtil.asPowerMeta(block, meta));
            if (this.thousand != null) {
                this.thousand.increment(name);
            }
        }

        if (this.weighted != null) {
            this.weighted.increment(Pair.of(block, meta), weight);
        } else if (weight != 1) {
            IDontKnowHowToCode.warnOnce(
                "Module " + this.getName()
                    + " doesn't have a weighted counter, but the scanner tried to input a block with a weight.");
        }

        if ((this.above != null && y >= this.yy) || (this.below != null && y < this.yy)) {
            Pair<Block, Integer> pair = Pair.of(block, meta);
            if (this.above != null && y >= this.yy) {
                this.above.increment(pair);
            } else {
                this.below.increment(pair);
            }
        }

        if (blockOut != null) {
            blockOut[0] = block;
        }

        if (metaOut != null) {
            metaOut[0] = meta;
        }
    }
}
