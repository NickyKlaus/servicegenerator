package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;

import java.util.Map;
import java.util.function.Predicate;

public interface Stage {
    ASTProcessingSchema getSchema();
    Map<String, Object> getProcessingData();
    Stage setProcessingData(Map<String, Object> processingData);
    String getSourceLocation();
    Stage setSourceLocation(String sourceLocation);
    Context getContext();
    Stage setContext(Context context);
    String getName();
    Predicate<Context> getExecutingStageCondition();
    Stage setExecutingStageCondition(Predicate<Context> condition);
}
