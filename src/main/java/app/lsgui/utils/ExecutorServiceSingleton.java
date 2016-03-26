package app.lsgui.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorServiceSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceSingleton.class);
    private static final ExecutorService EXECUTORSERVICE = Executors.newCachedThreadPool();

    public static void shutdown() {
        try {
            EXECUTORSERVICE.shutdown();
            EXECUTORSERVICE.awaitTermination(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Error when shutting down ExecutorService", e);
        }
    }

    public static ExecutorService instance() {
        return EXECUTORSERVICE;
    }

}
