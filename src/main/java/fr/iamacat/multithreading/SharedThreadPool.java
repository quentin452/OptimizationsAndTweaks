package fr.iamacat.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class SharedThreadPool {

    private static final ThreadPoolExecutor EXECUTOR_SERVICE = new ThreadPoolExecutor(
        0,
        Integer.MAX_VALUE,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("My-Thread-Pool-%d")
            .build());

    static {
        EXECUTOR_SERVICE.allowCoreThreadTimeOut(true);
    }

    public static ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }
}
