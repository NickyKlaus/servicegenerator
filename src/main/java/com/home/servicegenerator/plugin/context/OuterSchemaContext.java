package com.home.servicegenerator.plugin.context;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.context.Context;

import java.util.Map;
import java.util.Optional;

public class OuterSchemaContext implements Context {
    private final Map<String, String> properties;

    public OuterSchemaContext(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public Name getPipelineId() {
        throw new IllegalStateException("There is not defined pipelineId in outer scheme!");
    }

    @Override
    public MethodDeclaration getPipeline() {
        throw new IllegalStateException("There is not defined pipeline in outer scheme!");
    }

    @Override
    public Optional<OuterSchemaProperty> getPropertyByName(String name) {
        if (properties.containsKey(name)) {
            return Optional.of(new OuterSchemaProperty(name, properties.get(name)));
        }
        return Optional.empty();
    }

}
