package com.home.origami.plugin.processing.configuration.strategy.processing;

import com.home.origami.plugin.PluginConfiguration;
import com.home.origami.plugin.processing.configuration.context.ProcessingContext;
import com.home.origami.api.context.Context;
import com.home.origami.plugin.processing.configuration.stages.Stage;
import com.home.origami.plugin.processing.statemachine.ProcessingStateMachine;
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