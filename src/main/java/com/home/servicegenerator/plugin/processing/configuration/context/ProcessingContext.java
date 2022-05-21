package com.home.servicegenerator.plugin.processing.configuration.context;

import com.home.servicegenerator.api.context.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public final class ProcessingContext implements Context {
    private final Map<String, Object> properties = Collections.synchronizedMap(new HashMap<>());

    public ProcessingContext() {

    }

    public ProcessingContext(Map<String, Object> properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public Optional<Object> getPropertyByName(String name) {
        return ofNullable(properties.get(name));
    }

    public static ProcessingContext of(
            Map<String, Object> properties
    ) {
        return new ProcessingContext(properties);
    }
}
