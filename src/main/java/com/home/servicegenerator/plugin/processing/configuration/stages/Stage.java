package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.home.servicegenerator.plugin.processing.configuration.strategy.naming.SimpleNamingStrategy;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Stage {
    default Stage setSchema(ASTProcessingSchema schema) {
        // method does not change the object state (schema must be predefined in constructor)
        return this;
    }
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
    Stage setComponentPackage(String packageName);
    default String getComponentPackage() {
        return StringUtils.EMPTY;
    }
    default Stage setComponentType(String componentType) {
        return this;
    }
    default String getComponentType() {
        return StringUtils.EMPTY;
    }
    default NamingStrategy getNamingStrategy() {
        return new SimpleNamingStrategy();
    }
    default Stage setNamingStrategy(NamingStrategy namingStrategy) {
        return this;
    }
    default Stage setComponentName(String componentName) {
        return this;
    }
    default String getComponentName() {
        return "Component";
    }
}
