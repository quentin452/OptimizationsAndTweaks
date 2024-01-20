package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ThreadedFileIOBase.class)
public class MixinThreadedFileIOBase {

    @Shadow
    private List threadedIOQueue = Collections.synchronizedList(new ArrayList<>());
    @Shadow
    private volatile long writeQueuedCounter;
    @Shadow
    private volatile long savedIOCounter;
    @Shadow
    private volatile boolean isThreadWaiting;
    @Unique
    private final Object queueLock = new Object();

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void processQueue() {
        synchronized (queueLock) {
            while (!threadedIOQueue.isEmpty()) {
                IThreadedFileIO ithreadedfileio = (IThreadedFileIO) this.threadedIOQueue.get(0);
                boolean flag = ithreadedfileio.writeNextIO();

                if (!flag) {
                    this.threadedIOQueue.remove(ithreadedfileio);
                    ++this.savedIOCounter;
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void waitForFinish() {
        this.isThreadWaiting = true;

        while (this.writeQueuedCounter != this.savedIOCounter) {
            try {
                TimeUnit.MILLISECONDS.sleep(10L);
            } catch (InterruptedException e) {
                Thread.currentThread()
                    .interrupt();
            }
        }

        this.isThreadWaiting = false;
    }
}
