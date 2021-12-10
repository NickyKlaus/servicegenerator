package com.home.servicegenerator.plugin.processing.registry;

import com.github.javaparser.ast.CompilationUnit;

import java.util.*;

public class ProjectUnitsRegistry {
    private static final Map<String, CompilationUnit> PROJECT_UNITS_REGISTRY = Collections.synchronizedMap(new HashMap<>());

    private ProjectUnitsRegistry() {
    }

    public static CompilationUnit register(final CompilationUnit unit) throws IllegalArgumentException {
        synchronized (PROJECT_UNITS_REGISTRY) {
            Objects.requireNonNull(unit, "Unit must not be null");
            var _unit = unit.clone();
            if (_unit.getPrimaryTypeName().isEmpty() || _unit.getPrimaryTypeName().get().isEmpty()) {
                throw new IllegalArgumentException("Unit id must not be empty");
            }
            return PROJECT_UNITS_REGISTRY.put(_unit.getPrimaryTypeName().get(), _unit);
        }
    }

    public static void registerAll(final Collection<CompilationUnit> units) {
        units.stream()
                .filter(u -> Objects.nonNull(u) && u.getPrimaryTypeName().isPresent() && !u.getPrimaryTypeName().get().isEmpty())
                .forEach(ProjectUnitsRegistry::register);
    }

    public static void registerAll(final CompilationUnit... units) {
        Arrays.stream(units)
                .filter(u -> Objects.nonNull(u) && u.getPrimaryTypeName().isPresent() && !u.getPrimaryTypeName().get().isEmpty())
                .forEach(ProjectUnitsRegistry::register);
    }

    public static CompilationUnit lookup(final String unitId) {
        return PROJECT_UNITS_REGISTRY.get(unitId);
    }
}
