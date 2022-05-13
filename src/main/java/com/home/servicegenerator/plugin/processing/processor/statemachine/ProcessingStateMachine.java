package com.home.servicegenerator.plugin.processing.processor.statemachine;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.processing.processor.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.registry.ProjectUnitsRegistry;
import org.apache.maven.plugin.MojoFailureException;
import org.squirrelframework.foundation.fsm.StateMachineStatus;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ProcessingStateMachine extends AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> {
    private final BiConsumer<Stage, Context> generationAction;
    private final Consumer<CompilationUnit> savingAction;

    {
        generationAction = (stage, ctx) -> {
            try {
                final CompilationUnit unit = (CompilationUnit) DefaultGenerator.builder()
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

    void register(Stage fromState, Stage toState, String event, Context context) {
        try {
            ProjectUnitsRegistry.register(ProcessingUnit.convert((CompilationUnit) context.getProperties().get(fromState.getSourceLocation())));
            fire("GENERATE_" + toState.getName(), context);
        } catch (MojoFailureException e) {
            //
        }
    }

    void generate(Stage fromState, Stage toState, String event, Context context) {
        try {
            final CompilationUnit unit = (CompilationUnit) DefaultGenerator.builder()
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
        } catch (MojoFailureException e) {
            //
        }
    }

    @Override
    public synchronized void start(Context context) {
        super.start(context);
        fire("GENERATE_" + getInitialState().getName(), context);
    }

    @Override
    public void terminate() {
        super.terminate();
        ProjectUnitsRegistry.getAll().forEach(savingAction);
        super.setStatus(StateMachineStatus.IDLE);
    }
}
