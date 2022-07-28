package com.github.origami.plugin.processing.configuration.stages;

import com.github.origami.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.github.origami.api.ASTProcessingSchema;
import com.github.origami.api.context.Context;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Stage {
    Stage processingUnitBasePackage(String processingUnitBasePackage);
    boolean isNonGeneration();
    ASTProcessingSchema getProcessingSchema();
    Context getContext();
    Function<Context, String> getProcessingUnitLocation();
    String getName();
    Consumer<Context> getPostProcessingAction();
    Predicate<Context> getExecutingCondition();
    String getProcessingUnitType();
    NamingStrategy getNamingStrategy();
    Function<Context, String> getProcessingUnitName();
    String getProcessingUnitBasePackage();
}
