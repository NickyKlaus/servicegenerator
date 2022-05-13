package com.home.servicegenerator.plugin.processing.strategy;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface ProcessingStrategy {
    BiConsumer<AbstractStateMachine<ProcessingStateMachine, Stage, String, Context>, PluginConfiguration> getStrategy();
}
