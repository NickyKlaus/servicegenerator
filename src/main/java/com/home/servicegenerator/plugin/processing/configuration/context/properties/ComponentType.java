package com.home.servicegenerator.plugin.processing.configuration.context.properties;

public enum ComponentType {
    REPOSITORY("Repository"),
    SERVICE("Service"),
    SERVICE_IMPLEMENTATION("ServiceImpl"),
    CONTROLLER("Controller"),
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
