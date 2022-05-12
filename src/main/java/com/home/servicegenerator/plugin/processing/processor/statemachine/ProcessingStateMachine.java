package com.home.servicegenerator.plugin.processing.processor.statemachine;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.stages.ProcessingPlan;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;
import org.squirrelframework.foundation.fsm.impl.StateMachineDataImpl;

@Deprecated
public class ProcessingStateMachine extends AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> {
    /*private final ProcessingPlan processingPlan;

    public ProcessingStateMachine(ProcessingPlan processingPlan) {
        this.processingPlan = processingPlan;
    }

    @Override
    protected void afterTransitionCompleted(Stage fromState, Stage toState, String event, Context context) {
        fire();
        StateMachineDataImpl<ProcessingStateMachine, Stage, String, Context> stateMachineData = new StateMachineDataImpl<>();

        stateMachineData.initialState(processingPlan.getProcessingStages().get(0));
        stateMachineData.lastState(processingPlan.getProcessingStages().get(processingPlan.getProcessingStages().size()-1));
        stateMachineData.startContext();

    }*/
}
