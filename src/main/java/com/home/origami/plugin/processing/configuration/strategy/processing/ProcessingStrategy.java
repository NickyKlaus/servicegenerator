package com.home.origami.plugin.processing.configuration.strategy.processing;

import com.home.origami.plugin.PluginConfiguration;
import com.home.origami.api.context.Context;
import com.home.origami.plugin.processing.configuration.stages.Stage;
import com.home.origami.plugin.processing.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

public interface ProcessingStrategy {
    void process(
            Stage initialStage,
            AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine,
            PluginConfiguration configuration
    );
}
