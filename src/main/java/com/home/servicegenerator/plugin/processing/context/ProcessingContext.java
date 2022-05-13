package com.home.servicegenerator.plugin.processing.context;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.api.context.Property;
import com.home.servicegenerator.plugin.processing.context.properties.ProcessingProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

public final class ProcessingContext implements Context {
    private final Map<String, Object> properties = Collections.synchronizedMap(new HashMap<>());

    public ProcessingContext() {
    }

    public ProcessingContext(Map<String, Object> properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }

    /*public Name getPipelineId() {
        return properties == null ?
                null :
                (Name)properties.getOrDefault(PropertyName.PIPELINE_ID.name(), null);
    }

    public MethodDeclaration getPipeline() {
        return properties == null ?
                null :
                (MethodDeclaration) properties.getOrDefault(PropertyName.PIPELINE.name(), null);
    }*/

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
        return new ProcessingContext(properties);
    }
}
