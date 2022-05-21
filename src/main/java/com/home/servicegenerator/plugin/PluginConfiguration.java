package com.home.servicegenerator.plugin;

import com.home.servicegenerator.plugin.processing.configuration.context.properties.Storage;

import java.nio.file.Path;
import java.util.List;

public class PluginConfiguration {
    private Path sourcesLocation;
    private Path projectOutputDirectory;
    private String basePackage;
    private String projectBaseDirectory;
    private String configurationPackage;
    private String modelPackage;
    private String controllerPackage;
    private Storage.DbType dbType;
    private List<Transformation> transformations;

    public Path getSourcesLocation() {
        return sourcesLocation;
    }

    public void setSourcesLocation(Path sourcesLocation) {
        this.sourcesLocation = sourcesLocation;
    }

    public Path getProjectOutputDirectory() {
        return projectOutputDirectory;
    }

    public void setProjectOutputDirectory(Path projectOutputDirectory) {
        this.projectOutputDirectory = projectOutputDirectory;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getProjectBaseDirectory() {
        return projectBaseDirectory;
    }

    public void setProjectBaseDirectory(String projectBaseDirectory) {
        this.projectBaseDirectory = projectBaseDirectory;
    }

    public String getConfigurationPackage() {
        return configurationPackage;
    }

    public void setConfigurationPackage(String configurationPackage) {
        this.configurationPackage = configurationPackage;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public Storage.DbType getDbType() {
        return dbType;
    }

    public void setDbType(Storage.DbType dbType) {
        this.dbType = dbType;
    }

    public List<Transformation> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<Transformation> transformations) {
        this.transformations = transformations;
    }
}
