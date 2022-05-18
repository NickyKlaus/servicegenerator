package com.home.servicegenerator.plugin.processing.strategy;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.context.ProcessingContext;
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
import java.util.*;
import java.util.function.BiConsumer;

public class PipelineIdBasedProcessingStrategy implements ProcessingStrategy {
    @Override
    public BiConsumer<AbstractStateMachine<ProcessingStateMachine, Stage, String, Context>, PluginConfiguration> getStrategy() {
        return (AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine, PluginConfiguration configuration) -> {
            final Map<String, ProcessingUnit> controllerIndex = new HashMap<>();
            final Map<String, ProcessingUnit> configurationIndex = new HashMap<>();
            final Map<String, ProcessingUnit> modelIndex = new HashMap<>();
            final List<Name> availableModelNames = new ArrayList<>();

            // Scan available controllers, models and configurations
            try {
                final UnitScanner scanner = new UnitScanner(configuration);
                final List<CompilationUnit> models = scanner.scanModel();
                for (CompilationUnit unit : models) {
                    final ProcessingUnit processingUnit = ProcessingUnit.convert(unit);
                    modelIndex.put(processingUnit.getId(), processingUnit);
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
            for (Map.Entry<String, ProcessingUnit> controllerUnit : controllerIndex.entrySet()) {
                for (MethodDeclaration pipeline : PipelineStriping.makeStriping(controllerUnit.getValue().getCompilationUnit())) {
                    final Optional<Name> pipelineIdResolveResult = ResolverUtils.lookupPipelineId(pipeline, availableModelNames);
                    if (pipelineIdResolveResult.isEmpty()) continue;

                    // A fully qualified name of an available model class that the pipeline based on
                    final Name pipelineId = pipelineIdResolveResult.get();
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
