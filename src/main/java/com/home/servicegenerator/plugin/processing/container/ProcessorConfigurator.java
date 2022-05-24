package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.Condition;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ProcessorConfigurator {
    private final AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine;
    private final ProcessingConfiguration[] processingConfigurations;

    public ProcessorConfigurator(ProcessingConfiguration[] processingConfigurations) {
        this.stateMachine = prepareStateMachine(( this.processingConfigurations = processingConfigurations ));
    }

    public Processor configure() {
        return new Processor(processingConfigurations, stateMachine);
    }

    private AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> prepareStateMachine(
            ProcessingConfiguration[] processingConfigurations
    ) {
        if (processingConfigurations.length == 0 ||
                processingConfigurations[0].getProcessingPlan().getProcessingStages().isEmpty()) {
            return null;
        }

        var stateMachineBuilder =
                StateMachineBuilderFactory.create(ProcessingStateMachine.class, Stage.class, String.class, Context.class);
        var _globalInitialStage = processingConfigurations[0].getProcessingPlan().getProcessingStages().get(0);

        var stages = new ArrayList<Stage>();

        for (var config : processingConfigurations) {
            stages.addAll(config.getProcessingPlan().getProcessingStages());
        }

        if (stages.isEmpty()) return null;

        IntStream.range(1, stages.size())
                .mapToObj(i -> List.of(stages.get(i-1), stages.get(i)))
                .forEach(
                        pair -> {
                            var fromStage = pair.get(0);
                            var toStage = pair.get(1);
                            stateMachineBuilder
                                    .externalTransition()
                                    .from(fromStage)
                                    .to(toStage)
                                    .on("GENERATE_" + fromStage.getName())
                                    .when(
                                            new Condition<>() {
                                                @Override
                                                public boolean isSatisfied(Context context) {
                                                    return fromStage.getExecutingStageCondition().test(context);
                                                }

                                                @Override
                                                public String name() {
                                                    return fromStage.getName();
                                                }
                                            })
                                    .callMethod("generate");
                        }
                );

        var lastStage = stages.get(stages.size()-1);

        stateMachineBuilder
                .internalTransition()
                .within(lastStage)
                .on("GENERATE_" + lastStage.getName())
                .when(
                        new Condition<>() {
                            @Override
                            public boolean isSatisfied(Context context) {
                                return lastStage.getExecutingStageCondition().test(context);
                            }

                            @Override
                            public String name() {
                                return lastStage.getName();
                            }
                        })
                .callMethod("generate");

        return stateMachineBuilder.newStateMachine(_globalInitialStage);
    }
}
