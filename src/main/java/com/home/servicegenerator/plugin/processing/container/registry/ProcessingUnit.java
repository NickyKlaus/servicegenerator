package com.home.servicegenerator.plugin.processing.container.registry;

import com.github.javaparser.ast.CompilationUnit;
import org.apache.maven.plugin.MojoFailureException;

public final class ProcessingUnit {
    private final CompilationUnit compilationUnit;
    private final String id;

    public ProcessingUnit(final String path, final CompilationUnit compilationUnit) {
        this.id = path;
        this.compilationUnit = compilationUnit.clone();
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public String getId() {
        return id;
    }

    public static ProcessingUnit convert(final CompilationUnit unit) throws MojoFailureException {
        if (unit.getStorage().isEmpty()) {
            throw new MojoFailureException("Compilation unit must contain storage path");
        }
        return new ProcessingUnit(unit.getStorage().get().getPath().toString(), unit);
    }
}
