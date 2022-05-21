package com.home.servicegenerator.plugin.processing.configurator;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.Processor;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import org.squirrelframework.foundation.fsm.Condition;
import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.util.List;
import java.util.stream.IntStream;

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
        final List<Stage> stages = processingConfiguration.getProcessingPlan().getProcessingStages();
        final StateMachineBuilder<ProcessingStateMachine, Stage, String, Context> stateMachineBuilder =
                StateMachineBuilderFactory.create(ProcessingStateMachine.class, Stage.class, String.class, Context.class);

        if (stages.isEmpty()) return null;

        if (stages.size() == 1) {
            var singleStage = stages.get(0);
            stateMachineBuilder
                    .internalTransition()
                    .within(singleStage)
                    .on("GENERATE_" + singleStage.getName())
                    .when(
                            new Condition<>() {
                                @Override
                                public boolean isSatisfied(Context context) {
                                    return singleStage.getExecutingStageCondition().test(context);
                                }

                                @Override
                                public String name() {
                                    return singleStage.getName();
                                }
                            })
                    .callMethod("generate");
        } else {
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
        }

        stateMachineBuilder.defineFinalState(stages.get(stages.size()-1));
        return stateMachineBuilder.newStateMachine(stages.get(0));
    }
}
