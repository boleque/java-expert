package org.java.expert.profiling.agent;

import java.time.Duration;

public final class ProfilingRuntime {

    private static long startedAtNanos;

    private static long durationNanos;

    private ProfilingRuntime() {
    }

    public static void start(Duration duration) {
        durationNanos = duration.toNanos();
        startedAtNanos = System.nanoTime();
    }

    public static void recordCall(String methodId) {
        if (isCollectionActive()) {
            MethodCallCounter.increment(methodId);
        }
    }

    private static boolean isCollectionActive() {
        long elapsed = System.nanoTime() - startedAtNanos;
        return elapsed < durationNanos;
    }
}
