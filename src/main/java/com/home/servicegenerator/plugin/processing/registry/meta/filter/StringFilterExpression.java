package com.home.servicegenerator.plugin.processing.registry.meta.filter;

public class StringFilterExpression implements FilterExpression {
    private final String rawValue;

    public StringFilterExpression(String rawValue) {
        this.rawValue = rawValue;
    }

    public static StringFilterExpression of(String rawValue) {
        return new StringFilterExpression(rawValue);
    }

    @Override
    public String asString() {
        return rawValue;
    }
}
