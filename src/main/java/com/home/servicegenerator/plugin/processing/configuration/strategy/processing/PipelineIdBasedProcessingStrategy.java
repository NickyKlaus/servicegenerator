package com.home.servicegenerator.plugin.processing.configuration.strategy.processing;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.configuration.context.properties.PropertyName;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.configuration.strategy.PipelineStriping;
import com.home.servicegenerator.plugin.processing.container.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import com.home.servicegenerator.plugin.processing.container.registry.ProjectUnitsRegistry;
import com.home.servicegenerator.plugin.processing.container.scanner.UnitScanner;
import com.home.servicegenerator.plugin.utils.FileUtils;
import com.home.servicegenerator.plugin.utils.ResolverUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PipelineIdBasedProcessingStrategy implements ProcessingStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(PipelineIdBasedProcessingStrategy.class);
    @Override
    public void process(Stage initialStage, AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine, PluginConfiguration configuration) {
        final Map<String, ProcessingUnit> controllerIndex = new HashMap<>();
        final List<Name> availableModelNames = new ArrayList<>();

        try {
            // Scan available controllers, models and configurations
            final UnitScanner scanner = new UnitScanner(configuration);
            final List<CompilationUnit> models = scanner.scanModel();
            for (CompilationUnit unit : models) {
                final ProcessingUnit processingUnit = ProcessingUnit.convert(unit);
                ProjectUnitsRegistry.register(processingUnit);
            }

            availableModelNames.addAll(scanner.getModelNames(models));

            for (CompilationUnit unit : scanner.scanController()) {
                final ProcessingUnit processingUnit = ProcessingUnit.convert(unit);
                controllerIndex.put(processingUnit.getId(), processingUnit);
                ProjectUnitsRegistry.register(processingUnit);
            }

            for (CompilationUnit unit : scanner.scanConfiguration()) {
                final ProcessingUnit processingUnit = ProcessingUnit.convert(unit);
                ProjectUnitsRegistry.register(processingUnit);
            }
        } catch (IOException | MojoFailureException ex) {
            LOG.error("Error: cannot prepare components", ex);
        }

        // Process stages
        for (Map.Entry<String, ProcessingUnit> controllerUnit : controllerIndex.entrySet()) {
            for (MethodDeclaration pipeline : PipelineStriping.makeStriping(controllerUnit.getValue().getCompilationUnit())) {
                final Optional<Name> pipelineIdResolveResult = ResolverUtils.lookupPipelineId(pipeline, availableModelNames);
                if (pipelineIdResolveResult.isEmpty()) continue;

                stateMachine
                        .getAllStates()
                        .forEach(stage ->
                            stage.getProcessingData()
                                    .putAll(
                                            Map.of(
                                                    PropertyName.PIPELINE.name(), pipeline,
                                                    PropertyName.PIPELINE_ID.name(), pipelineIdResolveResult.get(),
                                                    PropertyName.CONTROLLER_UNIT.name(), controllerUnit.getValue())));

                stateMachine
                        .fire(
                                "GENERATE_" + initialStage.getName(),
                                ProcessingContext.of(initialStage.getProcessingData()));
            }
        }
    }
}
