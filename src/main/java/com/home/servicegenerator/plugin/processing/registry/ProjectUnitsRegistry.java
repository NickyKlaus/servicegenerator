package com.home.servicegenerator.plugin.processing.registry;

import com.github.javaparser.ast.CompilationUnit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProjectUnitsRegistry {
    private static final Map<String, CompilationUnit> PROJECT_UNITS_REGISTRY = Collections.synchronizedMap(new HashMap<>());

    private ProjectUnitsRegistry() {
    }

    public static CompilationUnit register(final CompilationUnit unit) throws IllegalArgumentException {
        synchronized (PROJECT_UNITS_REGISTRY) {
            Objects.requireNonNull(unit, "Unit must not be null");
            var _unit = unit.clone();
            if (_unit.getPrimaryTypeName().isEmpty()) {
                throw new IllegalArgumentException("Unit has not primary type");
            }
            return PROJECT_UNITS_REGISTRY.put(_unit.getPrimaryTypeName().get(), _unit);
        }
    }

    public static CompilationUnit lookup(final String unitId) {
        return PROJECT_UNITS_REGISTRY.get(unitId);
    }
}
