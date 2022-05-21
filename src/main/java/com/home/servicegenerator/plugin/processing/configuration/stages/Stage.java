package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Stage {
    ASTProcessingSchema getSchema();
    Map<String, Object> getProcessingData();
    Stage setProcessingData(Map<String, Object> processingData);
    String getSourceLocation();
    Stage setSourceLocation(String sourceLocation);
    String getName();
    Stage postProcessingAction(Consumer<Context> action);
    Consumer<Context> getPostProcessingAction();
    Stage setSourceLocation(Function<Context, String> locationProvider);
    default Predicate<Context> getExecutingStageCondition() {
        return ctx -> true;
    }
    Stage setExecutingStageCondition(Predicate<Context> condition);
}
