package com.home.servicegenerator.plugin.processing.container.registry;

import com.github.javaparser.ast.CompilationUnit;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ProjectUnitsRegistry {
    private static final ConcurrentMap<String, CompilationUnit> projectsUnitsIndex = new ConcurrentHashMap<>();

    public static void register(final ProcessingUnit unit) {
        projectsUnitsIndex.put(unit.getId(), unit.getCompilationUnit());
    }

    public static ProcessingUnit getOrDefault(
            final String unitId,
            final Supplier<ProcessingUnit> unitSupplier
    ) throws MojoFailureException {
        return isRegistered(unitId) ? ProcessingUnit.convert(projectsUnitsIndex.get(unitId)) : unitSupplier.get();
    }

    public static boolean isRegistered(final String unitId) {
        return projectsUnitsIndex.containsKey(unitId);
    }

    public static List<CompilationUnit> getAll() {
        return projectsUnitsIndex.values().stream().collect(Collectors.toUnmodifiableList());
    }
}
