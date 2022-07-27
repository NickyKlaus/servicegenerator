package com.github.origami.plugin.processing.processor;

import com.github.origami.plugin.processing.configuration.ProcessingConfiguration;
import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.stages.ProcessingStageMapper;
import com.github.origami.plugin.processing.configuration.stages.Stage;
import com.github.origami.plugin.processing.statemachine.ProcessingStateMachine;

import org.squirrelframework.foundation.fsm.Condition;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProcessorConfigurator {
    private final ProcessingStageMapper stageMapper;
    private final List<ProcessingConfiguration> processingConfigurations;

    public ProcessorConfigurator(List<ProcessingConfiguration> processingConfigurations) {
        this.processingConfigurations = processingConfigurations;
        this.stageMapper = new ProcessingStageMapper();
    }

    public List<Processor> configure() {
        return processingConfigurations
                .stream()
                .map(config -> new Processor(config, prepareStateMachine(config)))
                .collect(Collectors.toUnmodifiableList());
    }

    private AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> prepareStateMachine(
            ProcessingConfiguration processingConfiguration
    ) {
        if (processingConfiguration.getProcessingPlan().getProcessingStages().isEmpty()) {
            return null;
        }

        var stages = processingConfiguration
                .getProcessingPlan()
                .getProcessingStages()
                .stream()
                .map(stage -> stageMapper.fromStage(stage, processingConfiguration))
                .collect(Collectors.toUnmodifiableList());

        var stateMachineBuilder =
                StateMachineBuilderFactory.create(ProcessingStateMachine.class, Stage.class, String.class, Context.class);
        var _globalInitialStage = stages.get(0);

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
                                                    return fromStage.getExecutingCondition().test(context);
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
                                return lastStage.getExecutingCondition().test(context);
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
