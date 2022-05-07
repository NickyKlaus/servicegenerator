package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachineContext;

import java.util.function.Function;

public class ProcessingStage implements Stage {
    private final Stage wrappedStage;

    public ProcessingStage(Stage stage) {
        this.wrappedStage = stage;
    }

    @Override
    public ASTProcessingSchema getSchema() {
        return wrappedStage.getSchema();
    }

    @Override
    public Function<StateContext<Stage, String>, Context> getContext() {
        return wrappedStage.getContext();
    }

    @Override
    public Function<StateContext<Stage, String>, String> getSourceLocation() {
        return wrappedStage.getSourceLocation();
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public String getName() {
        return toString();
    }
}
