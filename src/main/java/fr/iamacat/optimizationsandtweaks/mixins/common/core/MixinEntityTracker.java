package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityTracker.class)
public class MixinEntityTracker {

    @Shadow
    private final WorldServer theWorld;
    /** List of tracked entities, used for iteration operations on tracked entities. */
    @Shadow
    private Set trackedEntities = new HashSet();

    public MixinEntityTracker(WorldServer theWorld) {
        this.theWorld = theWorld;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateTrackedEntities() {
        ArrayList<EntityTrackerEntry> entriesToSend = optimizationsAndTweaks$collectEntriesToSend();
        optimizationsAndTweaks$updateEntriesWatchingPlayers(entriesToSend);
    }

    @Unique
    private ArrayList<EntityTrackerEntry> optimizationsAndTweaks$collectEntriesToSend() {
        ArrayList<EntityTrackerEntry> entriesToSend = new ArrayList<>();

        for (Object trackedEntity : this.trackedEntities) {
            EntityTrackerEntry entityTrackerEntry = (EntityTrackerEntry) trackedEntity;
            entityTrackerEntry.sendLocationToAllClients(this.theWorld.playerEntities);

            if (optimizationsAndTweaks$shouldAddEntryToSend(entityTrackerEntry)) {
                entriesToSend.add(entityTrackerEntry);
            }
        }

        return entriesToSend;
    }

    @Unique
    private boolean optimizationsAndTweaks$shouldAddEntryToSend(EntityTrackerEntry entityTrackerEntry) {
        return entityTrackerEntry.playerEntitiesUpdated && entityTrackerEntry.myEntity instanceof EntityPlayerMP;
    }

    @Unique
    private void optimizationsAndTweaks$updateEntriesWatchingPlayers(ArrayList<EntityTrackerEntry> entriesToSend) {
        for (EntityTrackerEntry entryToSend : entriesToSend) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entryToSend.myEntity;

            for (Object trackedEntity : this.trackedEntities) {
                EntityTrackerEntry entityTrackerEntry = (EntityTrackerEntry) trackedEntity;

                if (entityTrackerEntry.myEntity != entityPlayerMP) {
                    entityTrackerEntry.tryStartWachingThis(entityPlayerMP);
                }
            }
        }
    }
}
