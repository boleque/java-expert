package org.java.expert.profiling.agent;

public record MethodSignature(String className, String methodName) {

    public MethodSignature {
        if (className == null || className.isBlank()) {
            throw new IllegalArgumentException("className must not be blank");
        }
        if (methodName == null || methodName.isBlank()) {
            throw new IllegalArgumentException("methodName must not be blank");
        }

        className = className.trim();
        methodName = methodName.trim();
    }

    public String displayName() {
        return className + "#" + methodName;
    }
}
