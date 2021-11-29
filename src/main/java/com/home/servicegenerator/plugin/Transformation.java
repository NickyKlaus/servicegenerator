package com.home.servicegenerator.plugin;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Collections;
import java.util.Set;

public class Transformation {

    @Parameter(name = "baseClassPackage", required = true)
    private String baseClassPackage;

    @Parameter(name = "baseClassName", required = true)
    private String baseClassName;

    @Parameter(name = "processingSchemaLocation")
    private File processingSchemaLocation;

    @Parameter(name = "processingSchemaClass")
    private String processingSchemaClass;

    @Parameter(name = "postfix")
    private String postfix;

    @Parameter(name = "transformationProperties")
    private Set<TransformationProperty> transformationProperties;

    public String getBaseClassPackage() {
        return baseClassPackage;
    }

    public String getBaseClassName() {
        return baseClassName;
    }

    public File getProcessingSchemaLocation() {
        return processingSchemaLocation;
    }

    public String getProcessingSchemaClass() {
        return processingSchemaClass;
    }

    public String getPostfix() {
        return postfix;
    }

    public static Transformation of(String baseClassLocation, String baseClassName) {
        Transformation transformation = new Transformation();
        transformation.baseClassPackage = baseClassLocation;
        transformation.baseClassName = baseClassName;
        return transformation;
    }

    public static Transformation of(String baseClassLocation, String baseClassName, Set<TransformationProperty> transformationProperties) {
        Transformation transformation = Transformation.of(baseClassLocation, baseClassName);
        transformation.transformationProperties = Collections.unmodifiableSet(transformationProperties);
        return transformation;
    }
}
