package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachineContext;

import java.nio.file.Path;
import java.util.function.Function;

public interface Stage {
    ASTProcessingSchema getSchema();
    Function<StateContext<Stage, String>, Context> getContext();
    Function<StateContext<Stage, String>, String> getSourceLocation();
    boolean isRepeatable();
    String getName();
}
