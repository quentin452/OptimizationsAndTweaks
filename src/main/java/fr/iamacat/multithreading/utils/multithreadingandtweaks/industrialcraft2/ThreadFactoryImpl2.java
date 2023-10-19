package fr.iamacat.multithreading.utils.multithreadingandtweaks.industrialcraft2;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactoryImpl2 implements ThreadFactory {
    private final ThreadGroup group = Thread.currentThread().getThreadGroup();
    private static final AtomicInteger number = new AtomicInteger(1);

    public ThreadFactoryImpl2() {
    }

    public Thread newThread(Runnable r) {
        Thread thread = new Thread(this.group, r, "ic2-poolthread-" + number.getAndIncrement(), 0L);
        thread.setDaemon(true);
        thread.setPriority(5);
        return thread;
    }
}
