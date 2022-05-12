package com.home.servicegenerator.plugin.processing.configurator;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.ProcessingPlan;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.processing.processor.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.processor.Processor;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import com.home.servicegenerator.plugin.processing.registry.ProjectUnitsRegistry;
import com.home.servicegenerator.plugin.processing.strategy.ProcessingStrategy;
import org.apache.maven.plugin.MojoFailureException;
import org.squirrelframework.foundation.fsm.Action;
import org.squirrelframework.foundation.fsm.Conditions;
import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;
import org.squirrelframework.foundation.fsm.impl.StateMachineBuilderImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        var stateMachineBuilder =
                StateMachineBuilderFactory.create(ProcessingStateMachine.class, Stage.class, String.class, Context.class);

        /*var _pairCounter = new AtomicInteger(0);
        var stagePairs = processingConfiguration
                .getProcessingPlan()
                .getProcessingStages()
                .stream()
                .reduce(
                        new ArrayList<Stage>(2),
                        (group, stage) -> {
                            if (group.size())
                            final int i = _pairCounter.getAndIncrement();
                            return (i % 2 == 0) ? i : i - 1;
                        },
                        (stages, stages2) -> {}
                )
                .collect(
                        Collectors.groupingBy(
                                item -> {
                                    final int i = _pairCounter.getAndIncrement();
                                    return (i % 2 == 0) ? i : i - 1;
                                },
                                LinkedHashMap::new,
                                Collectors.toList()))
                .values();*/

        for (var stage : processingConfiguration.getProcessingPlan().getProcessingStages()) {
            stateMachineBuilder
                    .internalTransition()
                    .within(stage)
                    .on("GENERATE_" + stage.getName())
                    .when(
                            Conditions.
                    )
                    .callMethod("generate");
            stateMachineBuilder
                    .transit()
                    .from(stage)
                    .toAny()
                    .on("REGISTER_" + stage.getName())
                    .callMethod("register");
        }
        //
        return new AbstractStateMachine<>() {
            private final ProcessingPlan processingPlan = processingConfiguration.getProcessingPlan();
            private final ProcessingStrategy processingStrategy = processingConfiguration.getProcessingStrategy();
            private final BiConsumer<Stage, Context> generationAction;
            private final Consumer<CompilationUnit> savingAction;

            {
                generationAction = (stage, ctx) -> {
                    try {
                        var unit = (CompilationUnit) DefaultGenerator.builder()
                                .processingSchema(stage.getSchema())
                                .build()
                                .generate(
                                        ProjectUnitsRegistry
                                                .getOrDefault(
                                                        stage.getSourceLocation(),
                                                        () -> new ProcessingUnit(
                                                                stage.getSourceLocation(),
                                                                new CompilationUnit()
                                                                        .setStorage(Path.of(stage.getSourceLocation()))))
                                                .getCompilationUnit(),
                                        ctx);
                        ProjectUnitsRegistry.register(ProcessingUnit.convert(unit));
                    } catch (MojoFailureException e) {
                        //
                    }
                };
            }

            {
                savingAction = (unit) -> {
                    try {
                        if (unit.getStorage().isEmpty() || Objects.isNull(unit.getStorage().get().getPath()) ||
                                !Files.exists(unit.getStorage().get().getPath())) {
                            throw new MojoFailureException("Cannot save generated class " + unit + ". There is no target path.");
                        }
                        if (unit.getPackageDeclaration().isPresent() && unit.getPrimaryType().isPresent()) {
                            unit
                                    .getStorage()
                                    .orElseThrow(() -> new MojoFailureException("Cannot write generated class " + unit))
                                    .save();
                        }
                    } catch (MojoFailureException e) {
                        //
                    }
                };
            }

            void generate(Stage fromState, Stage toState, String event, Context context) {
                try {
                    var unit = (CompilationUnit) DefaultGenerator.builder()
                            .processingSchema(fromState.getSchema())
                            .build()
                            .generate(
                                    ProjectUnitsRegistry
                                            .getOrDefault(
                                                    fromState.getSourceLocation(),
                                                    () -> new ProcessingUnit(
                                                            fromState.getSourceLocation(),
                                                            new CompilationUnit()
                                                                    .setStorage(Path.of(fromState.getSourceLocation()))))
                                            .getCompilationUnit(),
                                    context);
                    context.getProperties().put(fromState.getSourceLocation(), unit);
                    fire("REGISTER_" + fromState.getName(), context);
                    //ProjectUnitsRegistry.register(ProcessingUnit.convert(unit));
                } catch (MojoFailureException e) {
                    //
                }
            }

            void register(Stage fromState, Stage toState, String event, Context context) {
                try {
                    ProjectUnitsRegistry.register(ProcessingUnit.convert((CompilationUnit) context.getProperties().get(fromState.getSourceLocation())));
                    fire("GENERATE_" + toState.getName(), context);
                } catch (MojoFailureException e) {
                    //
                }
            }

            @Override
            public synchronized void start(Context context) {
                super.start(context);

                if (processingPlan.getProcessingStages().isEmpty()) {
                    terminate(context);
                    return;
                }

                fire("NEXT", context);
            }

            @Override
            protected void afterTransitionCompleted(Stage fromState, Stage toState, String event, Context context) {
                super.afterTransitionCompleted(fromState, toState, event, context);

                fire("NEXT");
            }

            @Override
            protected void beforeTransitionBegin(Stage fromState, String event, Context context) {
                super.beforeTransitionBegin(fromState, event, context);

                generationAction.accept(fromState, context);
            }

            @Override
            public void terminate() {
                super.terminate();

                ProjectUnitsRegistry.getAll().forEach(savingAction);
            }
        };
    }
}
