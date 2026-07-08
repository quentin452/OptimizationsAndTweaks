package fr.iamacat.optimizationsandtweaks.mixins.common.matmos;

import java.util.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.Sheet;
import eu.ha3.matmos.core.sheet.SheetDataPackage;

// NOTE (2026-07-08 mixin @Overwrite->injector conversion pass): reclassify COMPLEX, entire class left untouched.
// `sheets`/`referencedBlockIDs`/`referencedItemIDs` are declared @Unique with the SAME NAMES as the target's own
// private fields, instead of @Shadow -- meaning this class re-declares its OWN field storage rather than sharing
// the target's. All 6 @Overwrite methods below read/write these @Unique fields consistently. Individually 3 of
// the 6 (getSheet/clear/clearContents) are behavior-identical to vanilla and getSheetNames only adds an
// unmodifiable wrapper -- all would normally be clean injector conversions -- BUT converting any subset back to
// vanilla-body-via-injector would make THAT method operate on the vanilla class's own (different, currently-dead)
// field instance while the rest keep using the @Unique one: a silent state-desync bug. All 6 methods must be
// converted, or reclassified, as one unit; not attempted this pass given the risk of subtly splitting state.
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
        sheets.values()
            .forEach(Sheet::clear);
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
