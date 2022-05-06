package com.home.servicegenerator.plugin.processing.context;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.api.context.Property;
import com.home.servicegenerator.plugin.processing.context.properties.ProcessingProperty;
import com.home.servicegenerator.plugin.processing.context.properties.PropertyName;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class ProcessingContext implements Context {
    private final Name pipelineId;
    private final MethodDeclaration pipeline;
    private final Map<PropertyName, Object> properties;

    public ProcessingContext(Name pipelineId, MethodDeclaration pipeline, Map<PropertyName, Object> properties) {
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
        if (Arrays.stream(PropertyName.values()).anyMatch(n -> n.name().equals(name)) &&
                properties.containsKey(PropertyName.valueOf(name))
        ) {
            return Optional.of(
                    ProcessingProperty.of(
                            PropertyName.valueOf(name),
                            properties.get(PropertyName.valueOf(name))));
        }
        return empty();
    }

    public static ProcessingContext of(
            Name pipelineId,
            MethodDeclaration pipeline,
            Map<PropertyName, Object> properties
    ) {
        return new ProcessingContext(pipelineId, pipeline, properties);
    }
}
