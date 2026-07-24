package org.java.expert.profiling.agent;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public record AgentConfig(
        Duration collectionDuration,
        Path outputFile,
        List<MethodSignature> methods
) {

    public AgentConfig {
        if (collectionDuration == null || collectionDuration.isZero() || collectionDuration.isNegative()) {
            throw new IllegalArgumentException("collectionDuration must be positive");
        }
        if (outputFile == null) {
            throw new IllegalArgumentException("outputFile must not be null");
        }
        if (methods == null || methods.isEmpty()) {
            throw new IllegalArgumentException("At least one method must be configured");
        }

        methods = List.copyOf(methods);
    }
}
