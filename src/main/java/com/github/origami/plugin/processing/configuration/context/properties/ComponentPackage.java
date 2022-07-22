package com.github.origami.plugin.processing.configuration.context.properties;

public enum ComponentPackage {
    REPOSITORY("repository"),
    SERVICE("service"),
    SERVICE_IMPLEMENTATION("service.impl"),
    ;

    ComponentPackage(String componentPackage) {
        this.componentPackage = componentPackage;
    }

    private final String componentPackage;

    @Override
    public String toString() {
        return componentPackage;
    }
}
