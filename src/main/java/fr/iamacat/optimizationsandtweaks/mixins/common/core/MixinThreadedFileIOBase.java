package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mixin(ThreadedFileIOBase.class)
public class MixinThreadedFileIOBase {

    @Shadow
    private List threadedIOQueue = Collections.synchronizedList(new ArrayList());
    @Shadow
    private volatile long writeQueuedCounter;
    @Shadow
    private volatile long savedIOCounter;
    @Shadow
    private volatile boolean isThreadWaiting;

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void processQueue() {
        for (int i = 0; i < this.threadedIOQueue.size(); ++i) {
            IThreadedFileIO ithreadedfileio = (IThreadedFileIO) this.threadedIOQueue.get(i);
            boolean flag = ithreadedfileio.writeNextIO();

            if (!flag) {
                this.threadedIOQueue.remove(i--);
                ++this.savedIOCounter;
            }

            if (!this.isThreadWaiting) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (this.threadedIOQueue.isEmpty()) {
            try {
                TimeUnit.MILLISECONDS.sleep(25L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void waitForFinish() throws InterruptedException {
        this.isThreadWaiting = true;

        while (this.writeQueuedCounter != this.savedIOCounter) {
            try {
                TimeUnit.MILLISECONDS.sleep(10L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        this.isThreadWaiting = false;
    }
}
