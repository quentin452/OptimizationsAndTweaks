package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Shadow
    private INetHandler netHandler;
    @Unique
    private static final Logger optimizationsAndTweaks$logger = LogManager.getLogger();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setNetHandler(INetHandler handler) {
        Validate.notNull(handler, "packetListener");

        if (this.netHandler != handler) {
            optimizationsAndTweaks$logger.debug("Set listener of {} to {}", new Object[] { this, handler });
            this.netHandler = handler;
        } else {
            optimizationsAndTweaks$logger.debug("Handler already set to {}", new Object[] { handler });
        }
    }
}
