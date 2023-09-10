package fr.iamacat.multithreading.mixins.common.core;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import fr.iamacat.multithreading.config.MultithreadingandtweaksConfig;

@Mixin(MinecraftServer.class)
public class MixinDisableinitialWorldChunkLoad {

    /**
     * @reason Disable initial world chunk load. This makes world load much faster, but in exchange
     *         the player may see incomplete chunks (like when teleporting to a new area).
     * @author ZombieHDGaming
     *         taken from
     *         https://github.com/quentin452/BattleTowersFixes/blob/1.7.10-backport/src/main/java/mod/acgaming/btfixes/mixin/vanilla/MinecraftServerMixin.java
     */
    @Overwrite
    public void initialWorldChunkLoad() {
        if (MultithreadingandtweaksConfig.enableMixinDisableinitialWorldChunkLoad) {}
    }
}
