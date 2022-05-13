package com.home.servicegenerator.plugin.processing.strategy;

import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.context.properties.ProcessingProperty;
import com.home.servicegenerator.plugin.processing.context.properties.PropertyName;
import com.home.servicegenerator.plugin.processing.processor.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import com.home.servicegenerator.plugin.processing.registry.ProjectUnitsRegistry;
import com.home.servicegenerator.plugin.processing.scanner.UnitScanner;
import com.home.servicegenerator.plugin.utils.FileUtils;
import com.home.servicegenerator.plugin.utils.ResolverUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PipelineIdBasedProcessingStrategy implements ProcessingStrategy {
    private final PluginConfiguration configuration;

    public PipelineIdBasedProcessingStrategy(PluginConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Consumer<AbstractStateMachine<ProcessingStateMachine, Stage, String, Context>> getStrategy() {
        return (AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine) -> {
            final Map<String, ProcessingUnit> controllerIndex = new HashMap<>();
            final Map<String, ProcessingUnit> configurationIndex = new HashMap<>();
            final Map<String, ProcessingUnit> modelIndex = new HashMap<>();
            final List<Name> availableModelNames = new ArrayList<>();

            // Scan available controllers, models and configurations
            try {
                var scanner = new UnitScanner(configuration);
                var models = scanner.scanModel();
                for (var unit : models) {
                    var processingUnit = ProcessingUnit.convert(unit);
                    modelIndex.put(processingUnit.getId(), processingUnit);
                    ProjectUnitsRegistry.register(processingUnit);
                }

                availableModelNames.addAll(scanner.getModelNames(models));

                for (var unit : scanner.scanController()) {
                    var processingUnit = ProcessingUnit.convert(unit);
                    controllerIndex.put(processingUnit.getId(), processingUnit);
                    ProjectUnitsRegistry.register(processingUnit);
                }

                for (var unit : scanner.scanConfiguration()) {
                    var processingUnit = ProcessingUnit.convert(unit);
                    configurationIndex.put(processingUnit.getId(), processingUnit);
                    ProjectUnitsRegistry.register(processingUnit);
                }
            } catch (IOException | MojoFailureException ex) {
                //TODO: logs
                //throw new MojoFailureException("Cannot prepare available components", ioe);
            }

            // Create folders for components
            try {
                Files.createDirectory(
                        FileUtils.createDirPath(
                                configuration.getProjectOutputDirectory().toString(),
                                configuration.getSourcesLocation().toString(),
                                configuration.getBasePackage(),
                                "repository"));
                Files.createDirectories(
                        FileUtils.createDirPath(
                                configuration.getProjectOutputDirectory().toString(),
                                configuration.getSourcesLocation().toString(),
                                configuration.getBasePackage(),
                                "service.impl"));
            } catch (IOException ioe) {
                //TODO logs
            }

            // Process stages
            for (var controllerUnit : controllerIndex.entrySet()) {
                for (var pipeline : PipelineStriping.makeStriping(controllerUnit.getValue().getCompilationUnit())) {
                    var pipelineIdResolveResult = ResolverUtils.lookupPipelineId(pipeline, availableModelNames);
                    if (pipelineIdResolveResult.isEmpty()) continue;

                    // A fully qualified name of an available model class that the pipeline based on
                    var pipelineId = pipelineIdResolveResult.get();
                    stateMachine.getInitialState().getProcessingData().put(PropertyName.PIPELINE.name(), pipeline);
                    stateMachine.getInitialState().getProcessingData().put(PropertyName.PIPELINE_ID.name(), pipelineId);
                    stateMachine.start(
                            ProcessingContext.of(
                                    stateMachine.getInitialState().getProcessingData()));
                }
            }

            stateMachine.terminate();
        };
    }
}
