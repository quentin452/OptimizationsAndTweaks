package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.buildcraft;

public enum InOil2 {

    NONE,
    HALF,
    FULL;

    public boolean halfOfFull() {
        return this != NONE;
    }
}
