package com.home.origami.plugin.processing.configuration.stages;

import com.home.origami.plugin.processing.configuration.context.ProcessingContext;
import com.home.origami.api.ASTProcessingSchema;
import com.home.origami.api.context.Context;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ProcessingStage implements Stage {
    private final ASTProcessingSchema schema;
    private final Context context = new ProcessingContext();
    private String sourceLocation;
    private Function<Context, String> sourceLocationProvider;
    private Predicate<Context> executionCondition = ctx -> true;
    private Consumer<Context> afterProcessedAction = ctx -> {};

    private ProcessingStage(ASTProcessingSchema schema) {
        this.schema = schema;
    }

    public static Stage of(ASTProcessingSchema schema) {
        return new ProcessingStage(schema);
    }

    @Override
    public Stage setSourceLocation(String unitLocation) {
        this.sourceLocationProvider = null;
        this.sourceLocation = unitLocation;
        return this;
    }

    @Override
    public Stage setSourceLocation(Function<Context, String> locationProvider) {
        this.sourceLocation = null;
        this.sourceLocationProvider = locationProvider;
        return this;
    }

    @Override
    public Stage setSchema(ASTProcessingSchema schema) {
        // method does not change the object state (schema must be predefined in constructor)
        return this;
    }

    @Override
    public ASTProcessingSchema getSchema() {
        return schema;
    }

    @Override
    public Map<String, Object> getProcessingData() {
        return context.getProperties();
    }

    @Override
    public Stage setProcessingData(Map<String, Object> processingData) {
        this.context.getProperties().putAll(processingData);
        return this;
    }

    @Override
    public String getSourceLocation() {
        return sourceLocationProvider == null ? sourceLocation : sourceLocationProvider.apply(context);
    }

    @Override
    public String getName() {
        return getSourceLocation() + "_" + getSchema().hashCode();
    }

    @Override
    public Predicate<Context> getExecutingStageCondition() {
        return executionCondition;
    }

    @Override
    public Stage setExecutingStageCondition(Predicate<Context> executionCondition) {
        this.executionCondition = executionCondition;
        return this;
    }

    @Override
    public Stage postProcessingAction(Consumer<Context> action) {
        this.afterProcessedAction = action;
        return this;
    }

    @Override
    public Consumer<Context> getPostProcessingAction() {
        return afterProcessedAction;
    }

    @Override
    public Stage setComponentPackage(String packageName) {
        return this;
    }

    @Override
    public String getComponentPackage() {
        return StringUtils.EMPTY;
    }

    @Override
    public Stage setComponentType(String componentName) {
        return this;
    }

    @Override
    public String getComponentType() {
        return StringUtils.EMPTY;
    }
}
