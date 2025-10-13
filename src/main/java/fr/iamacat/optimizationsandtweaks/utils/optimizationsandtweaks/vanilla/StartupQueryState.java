package fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.vanilla;

import cpw.mods.fml.common.FMLLog;

public final class StartupQueryState {

    private static boolean hasConfirmedOnce = false;
    private static String lastQueryText = "";

    private StartupQueryState() {
    }

    public static boolean hasConfirmedOnce() {
        return hasConfirmedOnce;
    }

    public static void markConfirmedOnce() {
        hasConfirmedOnce = true;
    }

    public static void updateLastQueryText(String text) {
        lastQueryText = text != null ? text : "";
    }

    public static boolean isWorldRepairQuery(String text) {
        if (text == null || text.isEmpty()) return false;

        return text.contains("save is damaged") ||
               text.contains("automatic repair") ||
               text.contains("world backup") ||
               text.contains("items need to be removed") ||
               text.contains("items need to be relocated") ||
               text.contains("mods are missing") ||
               text.contains("Missing mods") ||
               text.contains("Forge Mod Loader detected");
    }

    public static void resetConfirmation() {
        hasConfirmedOnce = false;
        lastQueryText = "";
        FMLLog.info("[Optimizations] Reset world repair auto-confirmation state.");
    }
}
