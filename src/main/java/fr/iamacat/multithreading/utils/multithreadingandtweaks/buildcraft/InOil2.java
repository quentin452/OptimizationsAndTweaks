package fr.iamacat.multithreading.utils.multithreadingandtweaks.buildcraft;

public enum InOil2 {
    NONE,
    HALF,
    FULL;

    public boolean halfOfFull() {
        return this != NONE;
    }
}

