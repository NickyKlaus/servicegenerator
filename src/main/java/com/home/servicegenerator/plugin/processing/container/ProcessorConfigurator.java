package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.Condition;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProcessorConfigurator {
    private final Map<ProcessingConfiguration, Processor> prepared = new HashMap<>();

    public ProcessorConfigurator(ProcessingConfiguration[] processingConfigurations) {
        for (var config : processingConfigurations) {
            prepared.put(config, new Processor(config, prepareStateMachine(config)));
        }
    }

    public List<Processor> configure() {
        return prepared.values().stream().collect(Collectors.toUnmodifiableList());
    }

    private AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> prepareStateMachine(
            ProcessingConfiguration processingConfiguration
    ) {
        if (processingConfiguration.getProcessingPlan().getProcessingStages().isEmpty()) {
            return null;
        }

        var stateMachineBuilder =
                StateMachineBuilderFactory.create(ProcessingStateMachine.class, Stage.class, String.class, Context.class);
        var _globalInitialStage = processingConfiguration.getProcessingPlan().getProcessingStages().get(0);

        var stages = processingConfiguration.getProcessingPlan().getProcessingStages();

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
