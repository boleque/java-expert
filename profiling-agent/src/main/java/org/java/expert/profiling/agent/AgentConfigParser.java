package org.java.expert.profiling.agent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class AgentConfigParser {

    private AgentConfigParser() {
    }

    public static AgentConfig parse(Path configPath) {
        if (configPath == null) {
            throw new IllegalArgumentException("Config path must not be null");
        }

        Document document = readDocument(configPath);
        Element root = document.getDocumentElement();
        if (!"profiling".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root XML element must be <profiling>");
        }

        Duration collectionDuration = Duration.ofSeconds(readLongAttribute(root, "durationSeconds"));
        Path outputFile = Path.of(readRequiredAttribute(root, "outputFile"));
        List<MethodSignature> methods = readMethods(root);

        return new AgentConfig(collectionDuration, outputFile, methods);
    }

    private static Document readDocument(Path configPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setExpandEntityReferences(false);

            Document document = factory.newDocumentBuilder().parse(configPath.toFile());
            document.getDocumentElement().normalize();
            return document;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Cannot read config file: " + configPath, e);
        }
    }

    private static List<MethodSignature> readMethods(Element root) {
        NodeList methodNodes = root.getElementsByTagName("method");
        List<MethodSignature> methods = new ArrayList<>();

        for (int i = 0; i < methodNodes.getLength(); i++) {
            Node node = methodNodes.item(i);
            if (node instanceof Element methodElement) {
                methods.add(new MethodSignature(
                        readRequiredAttribute(methodElement, "className"),
                        readRequiredAttribute(methodElement, "methodName")
                ));
            }
        }

        return methods;
    }

    private static long readLongAttribute(Element element, String attributeName) {
        String value = readRequiredAttribute(element, attributeName);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Attribute '" + attributeName + "' must be a number", e);
        }
    }

    private static String readRequiredAttribute(Element element, String attributeName) {
        String value = element.getAttribute(attributeName);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required attribute '" + attributeName + "'");
        }
        return value.trim();
    }
}
