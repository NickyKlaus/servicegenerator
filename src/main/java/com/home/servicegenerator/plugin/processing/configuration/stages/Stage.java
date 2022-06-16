package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Stage {
    default String getComponentType() {
        return StringUtils.EMPTY;
    }
    Stage setSchema(ASTProcessingSchema schema);
    ASTProcessingSchema getSchema();
    Map<String, Object> getProcessingData();
    Stage setProcessingData(Map<String, Object> processingData);
    String getSourceLocation();
    Stage setSourceLocation(String sourceLocation);
    String getName();
    Stage postProcessingAction(Consumer<Context> action);
    default Consumer<Context> getPostProcessingAction() {
        return ctx -> {};
    }
    Stage setSourceLocation(Function<Context, String> locationProvider);
    default Predicate<Context> getExecutingStageCondition() {
        return ctx -> true;
    }
    Stage setExecutingStageCondition(Predicate<Context> condition);
}
