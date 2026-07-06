package fr.iamacat.optimizationsandtweaks.utilsformods.spiritores;

public enum SpiritOreConfig {

    COPPER(true),
    ALUMINUM(true),
    STEEL(true),
    SPIRIT(true),
    CHARRED_LEAD(true),
    AMETHYST(true),
    MAGNETITE(true),
    MAGMA_SPIRIT(true),
    IRIDITE(true),
    LILITHITE(true),
    ENDESPIRIT(true),
    TELENIUM(true);

    private boolean enabled;

    SpiritOreConfig(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
