package com.home.origami.plugin;

import com.home.origami.plugin.processing.configuration.context.properties.Storage;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

public abstract class AbstractServiceGeneratorMojo extends AbstractMojo {
    /**
     * Mappings for locations of base classes and corresponding processing schemas (location and class name).
     * Plugin uses processing schema mapped to base class to transform it and generate resulted version of this class.
     */
    @Parameter(required = true)
    private String inputSpec;

    @Parameter(name = "transformations")
    private List<Transformation> transformations = List.of();

    @Parameter(name = "dbType")
    private Storage.DbType dbType;

    @Parameter(defaultValue = "api")
    private String controllerPackage;

    @Parameter(defaultValue = "model")
    private String modelPackage;

    @Parameter(defaultValue = "invoker")
    private String configurationPackage;

    /**
     * The base directory of the project.
     */
    @Parameter(defaultValue = "${basedir}", readonly = true)
    private String projectBaseDirectory;

    @Parameter(defaultValue = "${project.groupId}.${project.artifactId}")
    private String basePackage;

    /**
     * Location of the output directory.
     */
    @Parameter(name = "projectOutputDirectory", defaultValue = "${project.build.directory}/generated-sources/swagger")
    private String projectOutputDirectory;

    /**
     * Location of the project sources directory.
     */
    @Parameter(defaultValue = "/src/main/java")
    private String sourcesDirectory;

    @Parameter(name = "project", defaultValue = "${project}")
    private MavenProject project;

    public String getInputSpec() {
        return inputSpec;
    }

    public void setInputSpec(String inputSpec) {
        this.inputSpec = inputSpec;
    }

    public String getProjectOutputDirectory() {
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

    public String getSourcesDirectory() {
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
