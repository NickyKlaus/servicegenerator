package com.home.servicegenerator.plugin.processing.processor;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface MatchingMethodStrategy {
    BiPredicate<MethodDeclaration, MethodDeclaration> matchMethods(final String pipelineId);
}
