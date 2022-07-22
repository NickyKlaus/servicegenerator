package com.github.origami.plugin.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Name;

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
}
