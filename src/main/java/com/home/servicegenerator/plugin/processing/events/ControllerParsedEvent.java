package com.home.servicegenerator.plugin.processing.events;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.api.context.Context;

public class ControllerParsedEvent extends UnitProcessedEvent {
    public ControllerParsedEvent(CompilationUnit generatedUnit, Context context) {
        super(generatedUnit, context);
    }
}
