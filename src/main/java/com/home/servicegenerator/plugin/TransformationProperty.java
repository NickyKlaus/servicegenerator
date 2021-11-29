package com.home.servicegenerator.plugin;

public class TransformationProperty {
    private final String name;
    private final String value;

    public TransformationProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
