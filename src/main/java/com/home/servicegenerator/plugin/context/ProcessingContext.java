package com.home.servicegenerator.plugin.context;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.api.context.Property;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class ProcessingContext implements Context {
    private final Name pipelineId;
    private final MethodDeclaration pipeline;
    private final Map<ProcessingProperty.Name, Object> properties;

    public ProcessingContext(Name pipelineId, MethodDeclaration pipeline, Map<ProcessingProperty.Name, Object> properties) {
        this.pipelineId = pipelineId;
        this.pipeline = pipeline;
        this.properties =
                properties
                        .entrySet()
                        .stream()
                        .map(e -> Map.entry(e.getKey(), e.getValue()))
                        .collect(
                                toUnmodifiableMap(
                                        Map.Entry<ProcessingProperty.Name, Object>::getKey,
                                        Map.Entry<ProcessingProperty.Name, Object>::getValue));
    }

    public ProcessingContext(Name pipelineId, MethodDeclaration pipeline, Set<ProcessingProperty> properties) {
        this.pipelineId = pipelineId;
        this.pipeline = pipeline;
        this.properties = properties
                .stream()
                .collect(toUnmodifiableMap(ProcessingProperty::getPropertyName, ProcessingProperty::getValue));
    }

    @Override
    public Name getPipelineId() {
        return pipelineId;
    }

    @Override
    public MethodDeclaration getPipeline() {
        return pipeline;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey().name(), e.getValue()))
                .collect(
                        toUnmodifiableMap(
                                Map.Entry<String, Object>::getKey,
                                Map.Entry<String, Object>::getValue));
    }

    @Override
    public Optional<Property> getPropertyByName(String name) {
        if (Arrays.stream(ProcessingProperty.Name.values()).anyMatch(n -> n.name().equals(name)) &&
                properties.containsKey(ProcessingProperty.Name.valueOf(name))
        ) {
            return of(
                    ProcessingProperty.of(
                            ProcessingProperty.Name.valueOf(name),
                            properties.get(ProcessingProperty.Name.valueOf(name))));
        }
        return empty();
    }

    public Optional<Property> getPropertyByName(ProcessingProperty.Name name) {
        if (properties.containsKey(name)) {
            return of(ProcessingProperty.of(name, properties.get(name)));
        }
        return empty();
    }
}
