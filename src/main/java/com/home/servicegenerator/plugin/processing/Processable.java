package com.home.servicegenerator.plugin.processing;

import com.github.javaparser.ast.CompilationUnit;

public interface Processable {
    CompilationUnit process();
}
