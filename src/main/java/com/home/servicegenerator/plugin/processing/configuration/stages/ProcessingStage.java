package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.processing.events.ProcessingEvent;
import com.home.servicegenerator.plugin.processing.processor.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.registry.ProjectUnitsRegistry;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.statemachine.action.Action;

import java.nio.file.Path;

public class ProcessingStage implements Stage {
    private static final Action<Stage, String> generationAction = (ctx) -> {
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

    private final Stage wrappedStage;
    private final Action<Stage, String> action;

    public ProcessingStage(Stage stage) {
        this.wrappedStage = stage;
        this.action = generationAction;
    }

    public ProcessingStage(Stage stage, Action<Stage, String> action) {
        this.wrappedStage = stage;
        this.action = action;
    }

    @Override
    public ASTProcessingSchema getSchema() {
        return wrappedStage.getSchema();
    }

    @Override
    public Context getContext() {
        return wrappedStage.getContext();
    }

    @Override
    public Path getSourceLocation() {
        return wrappedStage.getSourceLocation();
    }

    public Action<Stage, String> getAction() {
        return action;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public String getName() {
        return toString();
    }
}
