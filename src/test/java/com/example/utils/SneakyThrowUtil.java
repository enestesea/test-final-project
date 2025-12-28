package com.example.utils;

import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * Утилита для "скрытого" проброса checked исключений без объявления в сигнатуре метода
 */
public class SneakyThrowUtil {

    /**
     * Пробрасывает checked исключение как unchecked.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrow(final Throwable throwable) throws E {
        throw (E) throwable;
    }

    /**
     * Выполняет код с возможностью проброса checked исключений
     */
    public static void sneakyRun(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable e) {
            sneakyThrow(e);
        }
    }

    /**
     * Выполняет код с возвращаемым значением и возможностью проброса checked исключений
    */
    public static <T> T sneakyGet(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            sneakyThrow(e);
            return null; // никогда не выполнится
        }
    }

    /**
     * Выполняет Callable с возможностью проброса checked исключений
     */
    public static <T> T sneakyCall(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Throwable e) {
            sneakyThrow(e);
            return null; // никогда не выполнится
        }
    }

    /**
     * Функциональный интерфейс для кода, который может выбрасывать исключения
     */
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    /**
     * Функциональный интерфейс для поставщика, который может выбрасывать исключения
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }

    /**
     * Функциональный интерфейс для потребителя, который может выбрасывать исключения
     */
    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Throwable;
    }

    /**
     * Функциональный интерфейс для функции, которая может выбрасывать исключения
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Throwable;
    }

    /**
     * Преобразует throwing-функцию в стандартную Function
     */
    public static <T, R> Function<T, R> function(ThrowingFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Throwable e) {
                sneakyThrow(e);
                return null;
            }
        };
    }

    /**
     * Преобразует throwing-потребителя в стандартный Consumer
     */
    public static <T> Consumer<T> consumer(ThrowingConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Throwable e) {
                sneakyThrow(e);
            }
        };
    }
}
