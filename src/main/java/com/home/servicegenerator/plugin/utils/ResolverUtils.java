package com.home.servicegenerator.plugin.utils;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ResolverUtils {
    public static JavaSymbolSolver createJavaSymbolSolver(Path... paths) throws IOException {
        var solver = new CombinedTypeSolver(new ReflectionTypeSolver());
        for (var path : paths) {
            switch (FilenameUtils.getExtension(path.toString())) {
                case "jar": {
                    solver.add(new JarTypeSolver(path));
                    break;
                }
                case "":
                default: {
                    solver.add(new JavaParserTypeSolver(path));
                    break;
                }
            }
        }
        return new JavaSymbolSolver(solver);
    }

    public static Optional<Name> lookupPipelineId(
            final MethodDeclaration pipeline,
            final List<Name> availableModelsNames
    ) {
        Optional<Name> pipelineId = Optional.empty();
        for (Name seekingType : availableModelsNames) {
            pipelineId = resolve(pipeline.getType(), seekingType);
            if (pipelineId.isPresent()) {
                return pipelineId;
            }

            for (Parameter parameter : pipeline.getParameters()) {
                pipelineId = resolve(parameter.getType(), seekingType);
                if (pipelineId.isPresent()) {
                    return pipelineId;
                }
            }
        }
        return pipelineId;
    }

    private static Optional<Name> resolve(final Type type, final Name seekingTypeName) {
        Optional<Name> _typeName = Optional.empty();
        if(type.isClassOrInterfaceType()) {
            if (type.asClassOrInterfaceType().isReferenceType() && !type.asClassOrInterfaceType().isVoidType() &&
                    !type.asClassOrInterfaceType().isBoxedType() && !type.asClassOrInterfaceType().isWildcardType()) {
                if (type.asClassOrInterfaceType().getTypeArguments().isPresent()) {
                    for (Type _type : type.asClassOrInterfaceType().getTypeArguments().get()) {
                        _typeName = resolve(_type, seekingTypeName);
                    }
                } else {
                    if (type.asClassOrInterfaceType().getNameAsString().equals(seekingTypeName.getIdentifier())) {
                        return Optional.of(seekingTypeName);
                    }
                }
            }
        }
        return _typeName;
    }

    private ResolverUtils() { }
}
