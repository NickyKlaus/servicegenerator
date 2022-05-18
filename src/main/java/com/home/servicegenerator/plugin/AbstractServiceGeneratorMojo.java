package com.home.servicegenerator.plugin;

import com.home.servicegenerator.plugin.processing.context.properties.Storage;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

public abstract class AbstractServiceGeneratorMojo extends AbstractMojo {

    /**
     * Mappings for locations of base classes and corresponding processing schemas (location and class name).
     * Plugin uses processing schema mapped to base class to transform it and generate resulted version of this class.
     */
    @Parameter(name = "transformations")
    private List<Transformation> transformations = List.of();

    @Parameter(name = "dbType")
    private Storage.DbType dbType;

    @Parameter(name = "controllerPackage")
    private String controllerPackage;

    @Parameter(name = "modelPackage")
    private String modelPackage;

    @Parameter(name = "configurationPackage")
    private String configurationPackage;

    /**
     * The base directory of the project.
     */
    @Parameter(defaultValue = "${basedir}", readonly = true)
    private String projectBaseDirectory;

    @Parameter(name = "basePackage")
    private String basePackage;

    /**
     * Location of the output directory.
     */
    @Parameter(name = "projectOutputDirectory", defaultValue = "${project.build.directory}/target/generated-sources/swagger")
    private File projectOutputDirectory;

    /**
     * Location of the project sources directory.
     */
    @Parameter(defaultValue = "${sourceDir}")
    private File sourcesDirectory;

    @Parameter(name = "project", defaultValue = "${project}")
    private MavenProject project;

    public File getProjectOutputDirectory() {
        return projectOutputDirectory;
    }

    public List<Transformation> getTransformations() {
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

    public String getProjectBaseDirectory() {
        return projectBaseDirectory;
    }

    public MavenProject getProject() {
        return project;
    }

    public Storage.DbType getDbType() {
        return dbType;
    }
}
