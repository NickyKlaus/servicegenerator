package com.github.origami.plugin.processing.configuration.stages;

import com.github.origami.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.github.origami.api.ASTProcessingSchema;
import com.github.origami.api.context.Context;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Stage {
    ASTProcessingSchema getProcessingSchema();
    Context getContext();
    String getProcessingUnitLocation();
    String getName();
    Consumer<Context> getPostProcessingAction();
    Predicate<Context> getExecutingCondition();
    String getProcessingUnitType();
    NamingStrategy getNamingStrategy();
    String getProcessingUnitName();
    String getProcessingUnitBasePackage();
}
