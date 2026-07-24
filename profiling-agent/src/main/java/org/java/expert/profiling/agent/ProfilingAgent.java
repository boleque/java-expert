package org.java.expert.profiling.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ProfilingAgent {

    private static final Logger log = LoggerFactory.getLogger(ProfilingAgent.class);

    private ProfilingAgent() {
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        try {
            AgentConfig config = readConfig(agentArgs);
            start(config, instrumentation);
            registerResultWriter(config);
        } catch (RuntimeException e) {
            log.error("Profiling agent failed to start", e);
        }
    }

    private static AgentConfig readConfig(String configPath) {
        if (configPath == null || configPath.isBlank()) {
            throw new IllegalArgumentException(
                    "Path to profiling XML config must be passed as a javaagent argument"
            );
        }

        return AgentConfigParser.parse(Path.of(configPath.trim()));
    }

    private static void start(AgentConfig config, Instrumentation instrumentation) {
        log.info("Profiling agent started");

        for (MethodSignature method : config.methods()) {
            instrumentation.addTransformer(
                    new ProfilingTransformer(method.className(), method.methodName(), ClassLoader.getSystemClassLoader())
            );
        }

    }

    private static void registerResultWriter(AgentConfig config) {
        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        () -> writeToFile(config),
                        "profiling-results-writer"
                )
        );
    }

    private static void writeToFile(AgentConfig config) {
        Path outputFile = config.outputFile();

        List<String> lines = new ArrayList<>();
        lines.add("method,calls");

        for (MethodSignature method : config.methods()) {
            lines.add(
                    method.displayName()
                            + ","
                            + MethodCallCounter.get(method.displayName())
            );
        }

        try {
            Path parent = outputFile.getParent();

            if (parent != null) Files.createDirectories(parent);

            Files.write(outputFile, lines);

            log.info("Profiling results written to {}", outputFile.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write profiling results to {}", outputFile, e);
        }
    }
}
