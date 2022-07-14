package com.home.origami.plugin.db.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.javaparser.ast.Node;
import com.home.origami.plugin.utils.ASTNodeConverter;

import java.io.IOException;

public class NodeDeserializer extends JsonDeserializer<Node> {
    public static final NodeDeserializer INSTANCE = new NodeDeserializer();
    private static final ASTNodeConverter astNodeConverter = new ASTNodeConverter();

    public NodeDeserializer() {
    }

    @Override
    public Node deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        return astNodeConverter.convertToASTNode(jsonParser.getValueAsString());
    }
}
