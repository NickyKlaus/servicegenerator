package com.home.servicegenerator.plugin.processing.configurator;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.Processor;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.Condition;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

public class ProcessorConfigurator {
    private final AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine;
    private final ProcessingConfiguration processingConfiguration;

    public ProcessorConfigurator(ProcessingConfiguration processingConfiguration) {
        this.stateMachine = prepareStateMachine(( this.processingConfiguration = processingConfiguration ));
    }

    public Processor configure() {
        return new Processor(processingConfiguration.getProcessingStrategy(), stateMachine);
    }

    private AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> prepareStateMachine(
            ProcessingConfiguration processingConfiguration
    ) {
        final AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine;
        var stages = processingConfiguration.getProcessingPlan().getProcessingStages();
        var stateMachineBuilder =
                StateMachineBuilderFactory.create(ProcessingStateMachine.class, Stage.class, String.class, Context.class);

        if (stages.isEmpty()) {
            return null;
        }

        for (var stage : stages) {
            stateMachineBuilder
                    .internalTransition()
                    .within(stage)
                    .on("GENERATE_" + stage.getName())
                    .when(
                            new Condition<>() {
                                @Override
                                public boolean isSatisfied(Context context) {
                                    return stage.getExecutingStageCondition().test(context);
                                }

                                @Override
                                public String name() {
                                    return stage.getName();
                                }
                            }
                    )
                    .callMethod("generate");
            stateMachineBuilder
                    .transit()
                    .from(stage)
                    .toAny()
                    .on("REGISTER_" + stage.getName())
                    .callMethod("register");
        }

        stateMachineBuilder.defineFinalState(stages.get(stages.size()-1));
        return stateMachineBuilder.newStateMachine(stages.get(0));
    }
}
