package com.github.origami.plugin.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.origami.plugin.processing.configuration.strategy.matchmethod.MatchingMethodStrategy;

import java.util.List;
import java.util.Optional;

public class CompilationUnitUtils {
    private CompilationUnitUtils() {

    }

    public static Optional<Name> getPrimaryTypeName(CompilationUnit unit) {
        // Represents a fully qualified name of the primary type
        if (unit != null && unit.getPackageDeclaration().isPresent() && unit.getPrimaryTypeName().isPresent() ) {
            return Optional.of(
                    new Name()
                            .setQualifier(unit.getPackageDeclaration().get().getName())
                            .setIdentifier(unit.getPrimaryTypeName().get()));
        }
        return Optional.empty();
    }

    public static Optional<MethodDeclaration> getMethodMatchedWithPipeline(
            final MethodDeclaration pipeline,
            final List<MethodDeclaration> checkedMethods,
            final Name pipelineId,
            final MatchingMethodStrategy matchMethodStrategy
    ) {
        return checkedMethods
                .stream()
                .filter(checkedMethod -> matchMethodStrategy
                        .matchMethods(pipelineId.getIdentifier())
                        .test(pipeline, checkedMethod))
                .map(m -> MethodNormalizer.denormalize(m, pipelineId.getIdentifier(), NormalizerUtils.REPLACING_MODEL_TYPE_SYMBOL))
                .findFirst();
    }
}
