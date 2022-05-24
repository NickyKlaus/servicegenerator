package com.home.servicegenerator.plugin.processing.processor.statemachine;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.processing.container.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.container.registry.ProjectUnitsRegistry;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class ProcessingStateMachine extends AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingStateMachine.class);
    private final Consumer<CompilationUnit> savingAction;

    {
        savingAction = (unit) -> {
            try {
                if (unit.getStorage().isEmpty() || Objects.isNull(unit.getStorage().get().getPath())) {
                    throw new MojoFailureException("Cannot save generated class " + unit + ". There is no target path.");
                }
                if (unit.getPackageDeclaration().isPresent() && unit.getPrimaryType().isPresent()) {
                    unit
                            .getStorage()
                            .orElseThrow(() -> new MojoFailureException("Cannot write generated class " + unit))
                            .save();
                }
            } catch (MojoFailureException e) {
                LOG.error("Error: cannot save unit", e);
            }
        };
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
            ProjectUnitsRegistry.register(ProcessingUnit.convert(unit));

            if (toState != null) {
                if (fromState == toState) {
                    return;
                }
                context.getProperties().putAll(toState.getProcessingData());
                fire("GENERATE_" + toState.getName(), context);
            }
        } catch (MojoFailureException e) {
            LOG.error("Error: cannot generate unit", e);
        }
    }

    @Override
    public void afterTransitionCompleted(Stage fromState, Stage toState, String event, Context context) {
        super.afterTransitionCompleted(fromState, toState, event, context);
        fromState.getPostProcessingAction().accept(context);
    }

    @Override
    public void terminate() {
        super.terminate();
        ProjectUnitsRegistry.getAll().forEach(savingAction);
    }
}
