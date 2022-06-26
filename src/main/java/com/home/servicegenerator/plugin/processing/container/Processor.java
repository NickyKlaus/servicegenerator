package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

public class Processor {
    private final AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine;
    private final ProcessingConfiguration processingConfiguration;

    public Processor(
            ProcessingConfiguration processingConfiguration,
            AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine
    ) {
        this.processingConfiguration = processingConfiguration;
        this.stateMachine = stateMachine;
    }

    public void process(PluginConfiguration pluginConfiguration) {
        stateMachine.start(
                ProcessingContext.of(
                        stateMachine.getInitialState().getProcessingData()));

        processingConfiguration
                .getProcessingStrategy()
                .process(
                        processingConfiguration.getProcessingPlan().getProcessingStages().get(0),
                        stateMachine,
                        pluginConfiguration);

        stateMachine.terminate();
    }
}
