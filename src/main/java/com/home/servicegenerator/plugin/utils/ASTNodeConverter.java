package com.home.servicegenerator.plugin.utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.serialization.JavaParserJsonDeserializer;
import com.github.javaparser.serialization.JavaParserJsonSerializer;
import org.apache.commons.lang3.StringUtils;

import javax.json.Json;
import java.io.StringReader;
import java.io.StringWriter;

public class ASTNodeConverter {
    private final JavaParserJsonSerializer serializer;
    private final JavaParserJsonDeserializer deserializer;

    public ASTNodeConverter() {
        this.serializer = new JavaParserJsonSerializer();
        this.deserializer = new JavaParserJsonDeserializer();
    }

    public Node convertToASTNode(String json) {
        if (StringUtils.isNotBlank(json)) {
            return deserializer.deserializeObject(Json.createReader(new StringReader(json)));
        }
        return null;
    }

    public <T extends Node> T convertToASTNode(String json, Class<T> clazz) {
        if (StringUtils.isNotBlank(json)) {
            var node = deserializer.deserializeObject(Json.createReader(new StringReader(json)));
            if (node.getClass().isAssignableFrom(clazz)) {
                return clazz.cast(node);
            }
        }
        return null;
    }

    public String convertToJson(Node node) {
        if (node == null) {
            return StringUtils.EMPTY;
        }
        var stringWriter = new StringWriter();
        serializer.serialize(node, Json.createGenerator(stringWriter));
        return stringWriter.toString();
    }
}
