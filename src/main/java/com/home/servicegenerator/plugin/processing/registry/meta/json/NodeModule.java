package com.home.servicegenerator.plugin.processing.registry.meta.json;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.javaparser.ast.Node;

public class NodeModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public NodeModule() {
        super(PackageVersion.VERSION);
        this.addDeserializer(Node.class, NodeDeserializer.INSTANCE);
        this.addSerializer(Node.class, NodeSerializer.INSTANCE);
    }
}
