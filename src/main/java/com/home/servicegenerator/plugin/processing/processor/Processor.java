package com.home.servicegenerator.plugin.processing.processor;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import com.home.servicegenerator.plugin.processing.strategy.ProcessingStrategy;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

public class Processor {
    private final AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine;
    private final ProcessingStrategy processingStrategy;

    public Processor(
            ProcessingStrategy processingStrategy,
            AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine
    ) {
        this.processingStrategy = processingStrategy;
        this.stateMachine = stateMachine;
    }

    public void process(PluginConfiguration pluginConfiguration) {
        processingStrategy.getStrategy().accept(stateMachine, pluginConfiguration);
    }
}
