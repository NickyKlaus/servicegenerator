package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;

import java.util.Map;

public interface Stage {
    ASTProcessingSchema getSchema();
    Map<String, Object> getProcessingData();
    Stage setProcessingData(Map<String, Object> processingData);
    String getSourceLocation();
    Stage setSourceLocation(String sourceLocation);
    boolean isRepeatable();
    String getName();
}
