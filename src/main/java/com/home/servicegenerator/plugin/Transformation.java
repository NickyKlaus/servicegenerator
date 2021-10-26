package com.home.servicegenerator.plugin;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public class Transformation {

    @Parameter(name = "baseClassLocation")
    private String baseClassLocation;

    @Parameter(name = "baseClassName")
    private String baseClassName;

    @Parameter(name = "processingSchemaLocation", required = true)
    private File processingSchemaLocation;

    @Parameter(name = "processingSchemaClass", required = true)
    private String processingSchemaClass;

    @Parameter(name = "postfix")
    private String postfix;

    public String getBaseClassLocation() {
        return baseClassLocation;
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
}
