package com.home.servicegenerator.plugin.processing.processor;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.processing.events.ProcessingEvent;
import com.home.servicegenerator.plugin.processing.events.UnitProcessedEvent;
import com.home.servicegenerator.plugin.processing.registry.ProjectUnitsRegistry;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.action.Actions;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.builders.StateMachineStateBuilder;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionBuilder;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.model.StateData;

import java.util.stream.Collectors;

@Configuration
@EnableStateMachine
public class ProcessorConfigurator {
    private final ProcessingConfiguration processingConfiguration;
    private final Action<Stage, String> generationAction = (ctx) -> {
        var processingStage = ctx.getSource().getId();
        try {
            var unit = (CompilationUnit) DefaultGenerator.builder()
                    .processingSchema(processingStage.getSchema())
                    .build()
                    .generate(
                            ProjectUnitsRegistry
                                    .getOrDefault(
                                            processingStage.getSourceLocation().toString(),
                                            () -> new ProcessingUnit(
                                                    processingStage.getSourceLocation().toString(),
                                                    new CompilationUnit()
                                                            .setStorage(processingStage.getSourceLocation())))
                                    .getCompilationUnit(),
                            processingStage.getContext());
            ProjectUnitsRegistry.register(ProcessingUnit.convert(unit));
        } catch (MojoFailureException mojoFailureException) {
            ctx.getStateMachine().setStateMachineError(mojoFailureException);
        }
    };

    public ProcessorConfigurator(ProcessingConfiguration processingConfiguration) {
        this.processingConfiguration = processingConfiguration;
    }

    @Bean
    public StateMachineTransitionConfigurer<Stage, String> transitionConfigurer() {
        var transitionBuilder = new StateMachineTransitionBuilder<Stage, String>();
        synchronized (processingConfiguration.getProcessingPlan().getProcessingStages()) {
            var it = processingConfiguration.getProcessingPlan().getProcessingStages().listIterator();
            Stage current, next;

            while (it.hasNext()) {
                current = it.next();
                next = current.isRepeatable() ? current : it.hasNext() ? it.next() : current;
                transitionBuilder.addTransition(
                        current,
                        next,
                        current,
                        current.getName(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
            }
        }
        return transitionBuilder;
    }

    @Bean
    public StateMachineStateConfigurer<Stage, String> stateConfigurer() {
        var statesBuilder = new StateMachineStateBuilder<Stage, String>();
        synchronized (processingConfiguration.getProcessingPlan().getProcessingStages()) {
            var states =
                    processingConfiguration
                            .getProcessingPlan()
                            .getProcessingStages()
                            .stream()
                            .map(StateData<Stage, String>::new)
                            .collect(Collectors.toList());
            if (!states.isEmpty()) {
                states.forEach(data -> data.getExitActions().add(Actions.from(generationAction)));
                states.get(0).setInitial(true);
            }
            statesBuilder.addStateData(states);
        }
        return statesBuilder;
    }
}
