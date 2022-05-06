package com.home.servicegenerator.plugin.processing.scanner;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Name;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;

public interface Scanner {
    List<CompilationUnit> scanController() throws MojoFailureException;
    List<CompilationUnit> scanModel() throws MojoFailureException;
    List<CompilationUnit> scanConfiguration() throws MojoFailureException;
    List<Name> getModelNames(List<CompilationUnit> modelUnits) throws MojoFailureException;
}
