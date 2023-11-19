package fr.iamacat.optimizationsandtweaks.mixins.common.matmos;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.Sheet;
import eu.ha3.matmos.core.sheet.SheetDataPackage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Mixin(SheetDataPackage.class)
public class MixinSheetDataPackage implements DataPackage {
    @Unique
    private final Map<String, Sheet> sheets = new HashMap<>();
    @Shadow
    private final Class<? extends Sheet> sheetType;
    @Unique
    private final Set<Integer> referencedBlockIDs = new HashSet<>();
    @Unique
    private final Set<Integer> referencedItemIDs = new HashSet<>();

    public MixinSheetDataPackage(Class<? extends Sheet> sheetType) {
        this.sheetType = sheetType;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public Sheet getSheet(String name) {
        return sheets.computeIfAbsent(name, k -> {
            try {
                return sheetType.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public Set<String> getSheetNames() {
        return Collections.unmodifiableSet(sheets.keySet());
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void clear() {
        sheets.clear();
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void clearContents() {
        sheets.values().forEach(Sheet::clear);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void addReferencedIDs(List<Integer> newReferencedBlockIDs, List<Integer> newReferencedItemIDs) {
        referencedBlockIDs.addAll(newReferencedBlockIDs);
        referencedItemIDs.addAll(newReferencedItemIDs);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public boolean isIDReferenced(int id, boolean isItem) {
        return (isItem ? referencedItemIDs : referencedBlockIDs).contains(id);
    }
}
