package com.home.servicegenerator.plugin.utils;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public final class MethodNormalizer extends ModifierVisitor<String> {
    private final String pipelineIdReplacingSymbol;

    public MethodNormalizer(String pipelineIdReplacingSymbol) {
        super();
        this.pipelineIdReplacingSymbol = pipelineIdReplacingSymbol;
    }

    @Override
    public Visitable visit(MethodDeclaration n, String pipelineId) {
        super.visit(n, pipelineId);

        if (!n.getType().isClassOrInterfaceType()) {
            return n;
        }

        final MethodDeclaration method = n.clone();
        final ClassOrInterfaceType _returnType = method.getType().asClassOrInterfaceType();
        Type returnType = n.getType();

        if (_returnType.getName().getIdentifier().equals("ResponseEntity") &&
                _returnType.getTypeArguments().isPresent()) {
            returnType = _returnType.getTypeArguments().get().get(0).clone().asClassOrInterfaceType();
        }

        final ClassOrInterfaceTypeNormalizer classOrInterfaceTypeNormalizer = new ClassOrInterfaceTypeNormalizer(pipelineIdReplacingSymbol);
        returnType.accept(classOrInterfaceTypeNormalizer, pipelineId);
        method.setType(returnType);
        method.getParameters().stream()
                .map(Parameter::getType)
                .filter(Type::isClassOrInterfaceType)
                .forEach(t -> t.accept(classOrInterfaceTypeNormalizer, pipelineId));

        return method;
    }

    public static MethodDeclaration normalize(
            MethodDeclaration methodDeclaration, String pipelineId, String pipelineIdReplacingSymbol
    ) {
        return (MethodDeclaration) methodDeclaration.clone()
                .accept(new MethodNormalizer(pipelineIdReplacingSymbol), pipelineId);
    }

    public static MethodDeclaration denormalize(
            MethodDeclaration methodDeclaration, String pipelineId, String pipelineIdReplacingSymbol
    ) {
        return normalize(methodDeclaration, pipelineIdReplacingSymbol, pipelineId);
    }
}
