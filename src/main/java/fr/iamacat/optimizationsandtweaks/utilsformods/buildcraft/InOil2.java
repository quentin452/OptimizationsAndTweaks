package fr.iamacat.optimizationsandtweaks.utilsformods.buildcraft;

public enum InOil2 {

    NONE,
    HALF,
    FULL;

    public boolean halfOfFull() {
        return this != NONE;
    }
}
