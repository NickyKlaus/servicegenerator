package com.home.servicegenerator.plugin;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Collections;
import java.util.Set;

public class Transformation {

    @Parameter(name = "sourceClassPackage", defaultValue = "${basePackage}")
    private String sourceClassPackage;

    @Parameter(name = "sourceClassName")
    private String sourceClassName;

    @Parameter(name = "targetClassPackage", defaultValue = "${basePackage}")
    private String targetClassPackage;

    @Parameter(name = "targetClassName")
    private String targetClassName;

    @Parameter(name = "processingSchemaLocation")
    private File processingSchemaLocation;

    @Parameter(name = "processingSchemaClass")
    private String processingSchemaClass;

    @Parameter(name = "transformationProperties")
    private Set<TransformationProperty> transformationProperties;

    public String getSourceClassPackage() {
        return sourceClassPackage;
    }

    public String getSourceClassName() {
        return sourceClassName;
    }

    public String getTargetClassPackage() {
        return targetClassPackage;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public File getProcessingSchemaLocation() {
        return processingSchemaLocation;
    }

    public String getProcessingSchemaClass() {
        return processingSchemaClass;
    }

    public Set<TransformationProperty> getTransformationProperties() {
        return transformationProperties;
    }

    public static Transformation of(String baseClassLocation, String baseClassName) {
        Transformation transformation = new Transformation();
        transformation.sourceClassPackage = baseClassLocation;
        transformation.sourceClassName = baseClassName;
        return transformation;
    }

    public static Transformation of(String baseClassLocation, String baseClassName, Set<TransformationProperty> transformationProperties) {
        Transformation transformation = Transformation.of(baseClassLocation, baseClassName);
        transformation.transformationProperties = Collections.unmodifiableSet(transformationProperties);
        return transformation;
    }
}
