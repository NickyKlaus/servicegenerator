package com.home.servicegenerator.plugin.processing.strategy;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.util.function.Consumer;

@FunctionalInterface
public interface ProcessingStrategy {
    Consumer<AbstractStateMachine<ProcessingStateMachine, Stage, String, Context>> getStrategy();
}
