package com.home.servicegenerator.plugin.utils;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class ClassOrInterfaceTypeNormalizer extends ModifierVisitor<String> {
    private final String pipelineIdReplacingSymbol;

    public ClassOrInterfaceTypeNormalizer(String pipelineIdReplacingSymbol) {
        super();
        this.pipelineIdReplacingSymbol = pipelineIdReplacingSymbol;
    }

    @Override
    public Visitable visit(ClassOrInterfaceType n, String pipelineId) {
        super.visit(n, pipelineId);

        var type = n.clone();
        type.setScope(null);
        if (type.getNameAsString().equals(pipelineId)) {
            type.setName(pipelineIdReplacingSymbol);
        }
        return type;
    }
}
