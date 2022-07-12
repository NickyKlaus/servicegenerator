package com.home.servicegenerator.plugin.processing.registry.meta.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.javaparser.ast.Node;
import com.home.servicegenerator.plugin.utils.ASTNodeConverter;

import java.io.IOException;

public class NodeDeserializer extends JsonDeserializer<Node> {
    public static final NodeDeserializer INSTANCE = new NodeDeserializer();
    private static final ASTNodeConverter astNodeConverter = new ASTNodeConverter();

    protected NodeDeserializer() {
    }

    @Override
    public Node deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        return astNodeConverter.convertToASTNode(jsonParser.getValueAsString());
    }
}
