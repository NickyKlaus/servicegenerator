package com.home.servicegenerator.plugin.utils;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import static com.home.servicegenerator.plugin.utils.ResolverUtils.createJavaSymbolSolver;

public class MethodNormalizer extends ModifierVisitor<String> {
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

        var method = n.clone();
        var _returnType = method.getType().asClassOrInterfaceType();
        Type returnType = n.getType();

        if (_returnType.getName().getIdentifier().equals("ResponseEntity") &&
                _returnType.getTypeArguments().isPresent()) {
            returnType = _returnType.getTypeArguments().get().get(0).clone().asClassOrInterfaceType();
        }

        var classOrInterfaceTypeNormalizer = new ClassOrInterfaceTypeNormalizer(pipelineIdReplacingSymbol);
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
