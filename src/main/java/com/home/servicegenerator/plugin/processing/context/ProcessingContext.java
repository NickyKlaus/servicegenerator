package com.home.servicegenerator.plugin.processing.context;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.api.context.Property;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class ProcessingContext implements Context {
    private final Name pipelineId;
    private final MethodDeclaration pipeline;
    private final Map<ProcessingProperty.Name, Object> properties;

    public ProcessingContext(Name pipelineId, MethodDeclaration pipeline, Map<ProcessingProperty.Name, Object> properties) {
        Objects.requireNonNull(properties, "Properties must not be null!");
        Context.requireNonNullValues(
                properties
                        .entrySet()
                        .stream()
                        .map(e -> ProcessingProperty.of(e.getKey(), e.getValue()))
                        .collect(Collectors.toUnmodifiableList()),
                "Property values must not be null!");
        this.pipelineId = pipelineId;
        this.pipeline = pipeline;
        this.properties = Map.copyOf(properties);
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
}
