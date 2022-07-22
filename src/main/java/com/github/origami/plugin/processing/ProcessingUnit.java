package com.github.origami.plugin.processing;

import com.github.javaparser.ast.CompilationUnit;

import org.apache.maven.plugin.MojoFailureException;

import java.nio.file.Path;
import java.util.Objects;

public final class ProcessingUnit {
    private final CompilationUnit compilationUnit;
    private final String id;

    public ProcessingUnit(String path, CompilationUnit compilationUnit) {
        this.id = path;
        this.compilationUnit = (compilationUnit == null ?
                new CompilationUnit().setStorage(Path.of(path)) :
                compilationUnit.clone());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessingUnit that = (ProcessingUnit) o;
        return id.equals(that.id) && compilationUnit.equals(that.compilationUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, compilationUnit);
    }
}
