package org.example.agent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgentConfigParserTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesValidConfig() throws Exception {
        Path configFile = tempDir.resolve("profiling-config.xml");
        Files.writeString(configFile, """
                <?xml version="1.0" encoding="UTF-8"?>
                <profiling durationSeconds="10" outputFile="target/profiling-result.csv">
                    <method className="org.example.Main" methodName="main"/>
                    <method className="org.example.Service" methodName="process"/>
                </profiling>
                """);

        AgentConfig config = AgentConfigParser.parse(configFile);

        assertEquals(Duration.ofSeconds(10), config.collectionDuration());
        assertEquals(Path.of("target/profiling-result.csv"), config.outputFile());
        assertEquals(2, config.methods().size());
        assertEquals(new MethodSignature("org.example.Main", "main"), config.methods().getFirst());
        assertEquals(new MethodSignature("org.example.Service", "process"), config.methods().get(1));
    }
}
