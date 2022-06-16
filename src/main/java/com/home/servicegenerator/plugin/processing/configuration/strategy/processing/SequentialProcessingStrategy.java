package com.home.servicegenerator.plugin.processing.configuration.strategy.processing;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

public class SequentialProcessingStrategy implements ProcessingStrategy {
    @Override
    public void process(
            Stage initialStage,
            AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine,
            PluginConfiguration configuration
    ) {
        // Process stages
        stateMachine
                .fire(
                        "GENERATE_" + initialStage.getName(),
                        ProcessingContext.of(initialStage.getProcessingData()));
    }
}
