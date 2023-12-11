package fr.iamacat.optimizationsandtweaks.mixins.common.opis;

import com.google.common.collect.HashBasedTable;
import cpw.mods.fml.common.ModContainer;
import mcp.mobius.opis.data.profilers.Clock;
import mcp.mobius.opis.data.profilers.ProfilerAbstract;
import mcp.mobius.opis.data.profilers.ProfilerEvent;
import mcp.mobius.shadow.org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ProfilerEvent.class)
public abstract class MixinopisProfilerEvent extends ProfilerAbstract {
    @Shadow
    private Clock.IClock clock = Clock.getNewClock();
    @Shadow
    public HashBasedTable<Class, String, DescriptiveStatistics> data = HashBasedTable.create();
    @Shadow
    public HashBasedTable<Class, String, String> dataMod = HashBasedTable.create();
    @Shadow
    public HashBasedTable<Class, String, DescriptiveStatistics> dataTick = HashBasedTable.create();
    @Shadow
    public HashBasedTable<Class, String, String> dataModTick = HashBasedTable.create();
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void stop(Object event, Object pkg, Object handler, Object mod) {
        clock.stop();
        String eventName = event.getClass().getSimpleName();

        if (eventName.contains("TickEvent")) {
            try {
                String name = pkg + "|" + handler.getClass().getSimpleName();
                if (dataTick.get(event.getClass(), name) == null) {
                    dataTick.put(event.getClass(), name, new DescriptiveStatistics(250));
                    dataModTick.put(event.getClass(), name, ((ModContainer) mod).getName());
                }
                dataTick.get(event.getClass(), name).addValue(clock.getDelta());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                String name = pkg + "|" + handler.getClass().getSimpleName();
                if (data.get(event.getClass(), name) == null) {
                    data.put(event.getClass(), name, new DescriptiveStatistics(250));
                    dataMod.put(event.getClass(), name, ((ModContainer) mod).getName());
                }
                data.get(event.getClass(), name).addValue(clock.getDelta());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
