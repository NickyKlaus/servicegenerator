package com.home.servicegenerator.plugin;

import java.nio.file.Path;

public class PluginConfigurationMapper {
    public static PluginConfiguration toPluginConfiguration(AbstractServiceGeneratorMojo mojo) {
        final PluginConfiguration configuration = new PluginConfiguration();
        configuration.setInputSpec(mojo.getInputSpec());
        configuration.setConfigurationPackage(mojo.getConfigurationPackage());
        configuration.setBasePackage(mojo.getBasePackage());
        configuration.setControllerPackage(mojo.getControllerPackage());
        configuration.setDbType(mojo.getDbType());
        configuration.setModelPackage(mojo.getModelPackage());
        configuration.setTransformations(mojo.getTransformations());
        configuration.setProjectOutputDirectory(mojo.getProjectOutputDirectory());
        configuration.setSourcesLocation(mojo.getSourcesDirectory());
        return configuration;
    }
}
