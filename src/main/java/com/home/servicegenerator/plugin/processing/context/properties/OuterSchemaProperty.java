package com.home.servicegenerator.plugin.processing.context.properties;

import com.home.servicegenerator.api.context.Property;

public class OuterSchemaProperty implements Property {
    private final String name;
    private final String value;

    public OuterSchemaProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }
}
