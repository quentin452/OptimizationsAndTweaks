package fr.iamacat.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FireExecutorService {
    public static final ExecutorService INSTANCE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
}
