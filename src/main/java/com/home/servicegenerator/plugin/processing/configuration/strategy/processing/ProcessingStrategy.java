package com.home.servicegenerator.plugin.processing.configuration.strategy.processing;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

public interface ProcessingStrategy {
    void process(
            Stage initialStage,
            AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine,
            PluginConfiguration configuration
    );
}
