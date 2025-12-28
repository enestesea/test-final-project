package com.example.utils;

public class RunWithWaitUtil {

    /**
     * Выполняет раннабл с ожиданием до выполнения
     */
    public static void runWithPreWait(final Runnable runnable, final long sleepTime) {
        try {
            Thread.sleep(sleepTime);
            runnable.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /**
     * Выполняет раннабл с ожиданием после выполнения
     */
    public static void runWithPostWait(final Runnable runnable, final long sleepTime) {
        runnable.run();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /**
     * Выполняет раннабл с ожиданием до и после выполнения
     */
    public static void runWithAroundWait(final Runnable runnable, final long sleepTime) {
        try {
            Thread.sleep(sleepTime);
            runnable.run();
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
