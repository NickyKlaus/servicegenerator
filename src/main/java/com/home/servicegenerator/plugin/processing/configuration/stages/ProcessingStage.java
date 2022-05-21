package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Deprecated
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
    public String getSourceLocation() {
        return wrappedStage.getSourceLocation();
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public Map<String, Object> getProcessingData() {
        return wrappedStage.getProcessingData();
    }

    @Override
    public Stage setProcessingData(Map<String, Object> processingData) {
        return wrappedStage.setProcessingData(processingData);
    }

    @Override
    public Stage setSourceLocation(String sourceLocation) {
        return wrappedStage.setSourceLocation(sourceLocation);
    }

    @Override
    public Predicate<Context> getExecutingStageCondition() {
        return wrappedStage.getExecutingStageCondition();
    }

    @Override
    public Stage setExecutingStageCondition(Predicate<Context> condition) {
        return wrappedStage.setExecutingStageCondition(condition);
    }

    @Override
    public Stage postProcessingAction(Consumer<Context> action) {
        return wrappedStage.postProcessingAction(action);
    }

    @Override
    public Stage setSourceLocation(Function<Context, String> locationProvider) {
        return wrappedStage.setSourceLocation(locationProvider);
    }

    @Override
    public Consumer<Context> getPostProcessingAction() {
        return wrappedStage.getPostProcessingAction();
    }
}
