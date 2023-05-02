package fr.iamacat.multithreading;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

public class SharedThreadPool {

    private static final ThreadPoolExecutor EXECUTOR_SERVICE = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        Integer.MAX_VALUE,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("My-Thread-Pool-%d")
            .build());

    static {
        EXECUTOR_SERVICE.allowCoreThreadTimeOut(true);
    }

    public static ThreadPoolExecutor getExecutorService() {
        return EXECUTOR_SERVICE;
    }
}
