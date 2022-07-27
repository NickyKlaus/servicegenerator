package com.github.origami.plugin.processing.configuration.strategy.processing;

import com.github.origami.plugin.PluginConfiguration;
import com.github.origami.plugin.processing.configuration.context.ProcessingContext;
import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.stages.Stage;
import com.github.origami.plugin.processing.statemachine.ProcessingStateMachine;

import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

public class SequentialProcessingStrategy implements ProcessingStrategy {
    @Override
    public void process(
            /*Stage initialStage,*/
            AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine,
            PluginConfiguration configuration
    ) {
        // Process stages
        stateMachine
                .fire(
                        "GENERATE_" + stateMachine.getInitialState().getName(),
                        stateMachine.getInitialState().getContext());
    }
}
