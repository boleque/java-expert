package org.java.expert.profiling.agent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class MethodCallCounter {

    private static final ConcurrentHashMap<String, AtomicLong> COUNTERS = new ConcurrentHashMap<>();

    private MethodCallCounter() {
    }

    public static void increment(String methodId) {
        COUNTERS.computeIfAbsent(methodId, x -> new AtomicLong()).getAndIncrement();
    }

    public static long get(String methodId) {
        AtomicLong counter = COUNTERS.get(methodId);
        return counter == null ? 0 : counter.get();
    }
}
