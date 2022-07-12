package com.home.servicegenerator.plugin.processing.registry.meta.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.javaparser.ast.Node;
import com.home.servicegenerator.plugin.utils.ASTNodeConverter;

public class NodeSerializer extends JsonSerializer<Node> {
    public static final NodeSerializer INSTANCE = new NodeSerializer();
    private static final ASTNodeConverter astNodeConverter = new ASTNodeConverter();

    protected NodeSerializer() {
    }

    @Override
    public void serialize(Node node, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        jsonGenerator.setCurrentValue(astNodeConverter.convertToJson(node));
    }
}
