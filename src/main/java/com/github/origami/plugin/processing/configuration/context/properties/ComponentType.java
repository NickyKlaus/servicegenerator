package com.github.origami.plugin.processing.configuration.context.properties;

public enum ComponentType {
    REPOSITORY("Repository"),
    SERVICE("Service"),
    SERVICE_IMPLEMENTATION("ServiceImpl"),
    CONTROLLER("Controller"),
    CONFIGURATION("Configuration"),
    MAPPER("Mapper"),
    MODEL("Model"),
    UNKNOWN("")
    ;

    ComponentType(String componentType) {
        this.componentType = componentType;
    }

    private final String componentType;

    @Override
    public String toString() {
        return componentType;
    }
}
