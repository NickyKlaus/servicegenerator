package com.home.servicegenerator.plugin;

public class PluginConfigurationMapper {
    public static PluginConfiguration toPluginConfiguration(AbstractServiceGeneratorMojo mojo) {
        final PluginConfiguration configuration = new PluginConfiguration();
        configuration.setConfigurationPackage(mojo.getConfigurationPackage());
        configuration.setBasePackage(mojo.getBasePackage());
        configuration.setControllerPackage(mojo.getControllerPackage());
        configuration.setDbType(mojo.getDbType());
        configuration.setModelPackage(mojo.getModelPackage());
        configuration.setProjectBaseDirectory(mojo.getProjectBaseDirectory());
        configuration.setTransformations(mojo.getTransformations());
        configuration.setProjectOutputDirectory(mojo.getProjectOutputDirectory().toPath());
        configuration.setSourcesLocation(mojo.getSourcesDirectory().toPath());
        return configuration;
    }
}
