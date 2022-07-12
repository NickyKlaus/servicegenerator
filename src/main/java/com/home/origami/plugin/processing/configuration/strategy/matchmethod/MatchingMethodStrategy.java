package com.home.origami.plugin.processing.configuration.strategy.matchmethod;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface MatchingMethodStrategy {
    BiPredicate<MethodDeclaration, MethodDeclaration> matchMethods(final String pipelineId);
}
