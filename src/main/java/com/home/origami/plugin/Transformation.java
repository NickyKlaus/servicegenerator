package com.home.origami.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Collections;
import java.util.Set;

public class Transformation {

    @Parameter(name = "sourceClassPackage", defaultValue = "${basePackage}")
    private String sourceClassPackage;

    @Parameter(name = "sourceClassName")
    private String sourceClassName;

    @Parameter(name = "sourceDirectory")
    private String sourceDirectory;

    @Parameter(name = "targetClassPackage")
    private String targetClassPackage;

    @Parameter(name = "targetClassName")
    private String targetClassName;

    @Parameter(name = "targetDirectory")
    private String targetDirectory;

    @Parameter(name = "processingSchemaLocation")
    private File processingSchemaLocation;

    @Parameter(name = "processingSchemaClass")
    private String processingSchemaClass;

    @Parameter(name = "dependencies")
    private Set<Dependency> dependencies = Set.of();

    @Parameter(name = "transformationProperties")
    private Set<TransformationProperty> transformationProperties = Set.of();

    public String getSourceClassPackage() {
        return sourceClassPackage;
    }

    public String getSourceClassName() {
        return sourceClassName;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public String getTargetClassPackage() {
        return StringUtils.isEmpty(targetClassPackage) ? this.sourceClassPackage : targetClassPackage;
    }

    public String getTargetClassName() {
        return StringUtils.isEmpty(targetClassName) ? this.sourceClassName : targetClassName;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public File getProcessingSchemaLocation() {
        return processingSchemaLocation;
    }

    public String getProcessingSchemaClass() {
        return processingSchemaClass;
    }

    /*public Set<org.apache.maven.model.Dependency> getDependencies() {
        return dependencies;
    }*/
    public Set<Dependency> getDependencies() {
        return dependencies;
    }

    public Set<TransformationProperty> getTransformationProperties() {
        return transformationProperties;
    }

    public Transformation() {
    }

    public static Transformation of(
            String sourceClassPackage,
            String sourceClassName,
            String targetClassPackage,
            String targetClassName,
            File processingSchemaLocation,
            String processingSchemaClass
    ) {
        Transformation transformation = new Transformation();
        transformation.sourceClassPackage = sourceClassPackage;
        transformation.sourceClassName = sourceClassName;
        transformation.targetClassPackage = targetClassPackage;
        transformation.targetClassName = targetClassName;
        transformation.processingSchemaLocation = processingSchemaLocation;
        transformation.processingSchemaClass = processingSchemaClass;
        return transformation;
    }

    public static Transformation of(
            String sourceClassPackage,
            String sourceClassName,
            String targetClassPackage,
            String targetClassName,
            File processingSchemaLocation,
            String processingSchemaClass,
            Set<TransformationProperty> transformationProperties
    ) {
        Transformation transformation =
                Transformation.of(
                        sourceClassPackage,
                        sourceClassName,
                        targetClassPackage,
                        targetClassName,
                        processingSchemaLocation,
                        processingSchemaClass);
        transformation.transformationProperties = Collections.unmodifiableSet(transformationProperties);
        return transformation;
    }
}
