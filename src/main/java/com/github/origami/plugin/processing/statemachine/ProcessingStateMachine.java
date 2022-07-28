package com.github.origami.plugin.processing.statemachine;

import com.github.javaparser.ast.CompilationUnit;
import com.github.origami.plugin.processing.ProcessingUnit;
import com.github.origami.plugin.processing.registry.ProcessingUnitRegistry;
import com.github.origami.generator.DefaultGenerator;
import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.stages.Stage;

import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.nio.file.Path;
import java.util.function.Consumer;

public class ProcessingStateMachine extends AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingStateMachine.class);
    private static final String JAVA_EXT = ".java";
    private final Consumer<ProcessingUnit> savingAction =
            (unit) -> {
                try {
                    var compilationUnit = unit.getCompilationUnit();
                    if (compilationUnit.getPackageDeclaration().isPresent() && compilationUnit.getPrimaryType().isPresent()) {
                        compilationUnit
                                .getStorage()
                                .orElseThrow(() -> new MojoFailureException("Cannot write generated class " + unit))
                                .save();
                    }
                } catch (MojoFailureException e) {
                    LOG.error("Error: cannot save unit", e);
                }
            };

    void generate(Stage fromState, Stage toState, String event, Context context) {
        try {
            fromState.getContext().getProperties().putAll(
                    context.getProperties()
            );
            if (!fromState.isNonGeneration()) {
                var unitAbsolutePath = Path.of(
                        fromState.getProcessingUnitLocation().apply(fromState.getContext()),
                        fromState.getProcessingUnitBasePackage(),
                        fromState.getProcessingUnitName().apply(fromState.getContext())) + JAVA_EXT;

                var generatedUnit = (CompilationUnit) DefaultGenerator.builder()
                        .processingSchema(fromState.getProcessingSchema())
                        .build()
                        .generate(ProcessingUnitRegistry.getOrDefault(unitAbsolutePath).getCompilationUnit(), context);

                ProcessingUnitRegistry.save(ProcessingUnit.convert(generatedUnit));
            }

            if (toState != null) {
                if (fromState == toState) {
                    return;
                }

                toState.getContext().getProperties().putAll(fromState.getContext().getProperties());

                fire("GENERATE_" + toState.getName(), toState.getContext());
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
        ProcessingUnitRegistry.getAll().forEach(savingAction);
    }
}
