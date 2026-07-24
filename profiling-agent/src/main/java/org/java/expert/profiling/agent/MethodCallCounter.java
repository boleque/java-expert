package org.java.expert.profiling.agent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MethodCallCounter {

    private static final ConcurrentHashMap<String, AtomicLong> COUNTERS = new ConcurrentHashMap<>();

    public static void increment(String methodId) {
        COUNTERS.computeIfAbsent(methodId, x -> new AtomicLong()).getAndIncrement();
    }

    public static long get(String methodId) {
        AtomicLong counter = COUNTERS.get(methodId);
        return Objects.nonNull(counter) ? counter.get() : 0;
    }
}
