package org.java.expert.profiling.agent;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Objects;

public class ProfilingTransformer implements ClassFileTransformer {

    private static final Logger log = LoggerFactory.getLogger(ProfilingTransformer.class);

    private final String classNameInstrumented;

    private final String methodNameInstrumented;

    private final ClassLoader classLoader;

    public ProfilingTransformer(
            String classNameInstrumented,
            String methodNameInstrumented,
            ClassLoader classLoader
    ) {
        this.classNameInstrumented = classNameInstrumented;
        this.methodNameInstrumented = methodNameInstrumented;
        this.classLoader = classLoader;
    }

    @Override
    public byte[] transform(Module module,
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[] classfileBuffer) {

        var bytecode = classfileBuffer;

        var classNameInstrumentedNormalized = classNameInstrumented.replaceAll("\\.", "/");
        if (!Objects.equals(classNameInstrumentedNormalized, className) || !Objects.equals(loader, this.classLoader)) {
            return bytecode;
        }

        ClassPool classPool = new ClassPool(true);

        if (loader != null) {
            classPool.insertClassPath(new LoaderClassPath(loader));
        }

        CtClass ctClass = null;

        try {
            ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtMethod ctMethod = ctClass.getDeclaredMethod(methodNameInstrumented);

            ctMethod.insertBefore(
                    "org.java.expert.profiling.agent.MethodCallCounter.increment(\""
                            + classNameInstrumented + "#" + methodNameInstrumented
                            + "\");"
            );

            bytecode = ctClass.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            log.error(
                    "Failed to instrument {}#{}",
                    classNameInstrumented,
                    methodNameInstrumented,
                    e
            );
        } finally {
            if (ctClass != null) ctClass.detach();
        }

        return bytecode;
    }
}
