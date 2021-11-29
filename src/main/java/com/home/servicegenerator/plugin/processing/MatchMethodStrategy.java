package com.home.servicegenerator.plugin.processing;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface MatchMethodStrategy {
    BiPredicate<MethodDeclaration, MethodDeclaration> matchMethods(final String pipelineId);
}
