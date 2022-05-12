package com.home.servicegenerator.plugin.processing.context;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.api.context.Property;
import com.home.servicegenerator.plugin.processing.context.properties.ProcessingProperty;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.empty;

public final class ProcessingContext implements Context {
    private final Name pipelineId;
    private final MethodDeclaration pipeline;
    private final Map<String, Object> properties;

    public ProcessingContext(Name pipelineId, MethodDeclaration pipeline, Map<String, Object> properties) {
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
        this.properties = properties;
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
        return properties;
    }

    //TODO: change logic
    @Override
    public Optional<Property> getPropertyByName(String name) {
        if (properties.containsKey(name)) {
            return Optional.of(ProcessingProperty.of(name, properties.get(name)));
        }
        return empty();
    }

    public static ProcessingContext of(
            Map<String, Object> properties
    ) {
        return new ProcessingContext(null, null, properties);
    }

    public static ProcessingContext of(
            Name pipelineId,
            MethodDeclaration pipeline,
            Map<String, Object> properties
    ) {
        return new ProcessingContext(pipelineId, pipeline, properties);
    }
}
