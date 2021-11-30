package com.home.servicegenerator.plugin;

import com.home.servicegenerator.plugin.context.ProcessingProperty;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public abstract class AbstractServiceGeneratorMojo extends AbstractMojo {

    /**
     * Mappings for locations of base classes and corresponding processing schemas (location and class name).
     * Plugin uses processing schema mapped to base class to transform it and generate resulted version of this class.
     */
    @Parameter(name = "transformations")
    private Transformation[] transformations;

    @Parameter(name = "dbType")
    private ProcessingProperty.DbType dbType;

    @Parameter(name = "controllerPackage", required = true)
    private String controllerPackage;

    @Parameter(name = "modelPackage", required = true)
    private String modelPackage;

    @Parameter(name = "configurationPackage", required = true)
    private String configurationPackage;

    /**
     * The base directory of the project.
     */
    @Parameter(name = "projectBaseDir", defaultValue = "${project.basedir}")
    private String projectBaseDir;

    @Parameter(name = "basePackage")
    private String basePackage;

    /**
     * Location of the output directory.
     */
    @Parameter(defaultValue = "${project.build.directory}/target/generated-sources/swagger")
    private File projectOutputDirectory;

    /**
     * Location of the project sources directory.
     */
    @Parameter(defaultValue = "${sourceDir}")
    private File sourcesDirectory;

    public File getProjectOutputDirectory() {
        return projectOutputDirectory;
    }

    public Transformation[] getTransformations() {
        return transformations;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public String getConfigurationPackage() {
        return configurationPackage;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public File getSourcesDirectory() {
        return sourcesDirectory;
    }

    public ProcessingProperty.DbType getDbType() {
        return dbType;
    }
}
