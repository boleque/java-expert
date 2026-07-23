package org.java.expert.profiling.agent;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;

public final class ProfilingAgent {

    private ProfilingAgent() {
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        try {
            AgentConfig config = readConfig(agentArgs);
            start(config, instrumentation);
        } catch (RuntimeException e) {
            System.err.println("Profiling agent failed to start: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    private static AgentConfig readConfig(String agentArgs) {
        if (agentArgs == null || agentArgs.isBlank()) {
            throw new IllegalArgumentException("Path to profiling XML config must be passed as javaagent argument");
        }

        return AgentConfigParser.parse(Path.of(agentArgs.trim()));
    }

    private static void start(AgentConfig config, Instrumentation instrumentation) {
        System.out.println("Profiling agent started");
        System.out.println("Collection duration: " + config.collectionDuration().toSeconds() + " seconds");
        System.out.println("Output file: " + config.outputFile());
        System.out.println("Configured methods:");

        for (MethodSignature method : config.methods()) {
            System.out.println("- " + method.displayName());
        }
    }
}
