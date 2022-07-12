package com.home.origami.plugin;

public class TransformationProperty {
    private String name;
    private String value;

    public TransformationProperty() {
    }

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
