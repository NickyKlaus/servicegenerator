package com.github.origami.plugin;

import com.github.origami.plugin.processing.configuration.context.properties.Storage;

import java.util.List;

public class PluginConfiguration {
    private String inputSpec;
    private String sourcesLocation;
    private String projectOutputDirectory;
    private String basePackage;
    private String configurationPackage;
    private String modelPackage;
    private String controllerPackage;
    private Storage.DbType dbType;
    private List<Transformation> transformations;

    public String getInputSpec() {
        return inputSpec;
    }

    public void setInputSpec(String inputSpec) {
        this.inputSpec = inputSpec;
    }

    public String getSourcesLocation() {
        return sourcesLocation;
    }

    public void setSourcesLocation(String sourcesLocation) {
        this.sourcesLocation = sourcesLocation;
    }

    public String getProjectOutputDirectory() {
        return projectOutputDirectory;
    }

    public void setProjectOutputDirectory(String projectOutputDirectory) {
        this.projectOutputDirectory = projectOutputDirectory;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
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
