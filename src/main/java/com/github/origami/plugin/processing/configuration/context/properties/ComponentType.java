package com.github.origami.plugin.processing.configuration.context.properties;

import java.io.File;
import java.nio.file.Path;

public enum ComponentType {
    REPOSITORY("Repository", Path.of("repository")),
    SERVICE("Service", Path.of("service")),
    SERVICE_IMPLEMENTATION("ServiceImpl", Path.of("service", "impl")),
    //CONTROLLER("Controller", Path.of("controller")),
    //CONFIGURATION("Configuration", Path.of("configuration")),
    MAPPER("Mapper", Path.of("mapper")),
    //MODEL("Model", Path.of("model")),
    UNKNOWN("", Path.of(""))
    ;

    ComponentType(String componentType, Path componentPackage) {
        this.componentType = componentType;
        this.componentPackage = componentPackage;
    }

    private final String componentType;
    private final Path componentPackage;

    public String getComponentType() {
        return componentType;
    }

    public String getComponentPackage() {
        return componentPackage.toString().replaceAll(File.pathSeparator, ".");
    }

    @Override
    public String toString() {
        return componentType;
    }
}
