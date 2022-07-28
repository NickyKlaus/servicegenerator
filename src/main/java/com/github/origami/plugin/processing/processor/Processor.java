package com.github.origami.plugin.processing.processor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.origami.plugin.PluginConfiguration;
import com.github.origami.plugin.metadata.model.ComponentType;
import com.github.origami.plugin.metadata.model.ProcessingUnitMetaDataModel;
import com.github.origami.plugin.processing.ProcessingUnit;
import com.github.origami.plugin.processing.configuration.ProcessingConfiguration;
import com.github.origami.plugin.processing.configuration.context.ProcessingContext;
import com.github.origami.plugin.processing.container.scanner.Scanner;
import com.github.origami.plugin.processing.container.scanner.UnitScanner;
import com.github.origami.plugin.processing.registry.ProcessingUnitRegistry;
import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.stages.Stage;
import com.github.origami.plugin.processing.statemachine.ProcessingStateMachine;
import com.github.origami.plugin.utils.CompilationUnitUtils;

import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.io.IOException;
import java.util.List;

public class Processor {
    private static final Logger LOG = LoggerFactory.getLogger(Processor.class);
    private final AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine;
    private final ProcessingConfiguration processingConfiguration;

    public Processor(
            ProcessingConfiguration processingConfiguration,
            AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine
    ) {
        this.processingConfiguration = processingConfiguration;
        this.stateMachine = stateMachine;
    }

    public void process(PluginConfiguration pluginConfiguration) {
        stateMachine.start(stateMachine.getInitialState().getContext());

        registerGeneratedUnits(pluginConfiguration);

        processingConfiguration
                .getProcessingStrategy()
                .process(stateMachine, pluginConfiguration);

        stateMachine.terminate();
    }

    //TODO refactor this!
    private void registerGeneratedUnits(PluginConfiguration configuration) {
        try {
            // Scan available controllers, models and configurations
            final Scanner scanner = new UnitScanner(configuration);
            final List<CompilationUnit> models = scanner.scanModel();
            for (var unit : models) {
                var processingUnit = ProcessingUnit.convert(unit);
                var metadata = new ProcessingUnitMetaDataModel();
                metadata.setPath(processingUnit.getId());
                CompilationUnitUtils.getPrimaryTypeName(unit).ifPresent(metadata::setName);
                metadata.setType(ComponentType.MODEL.toString());
                unit.getPackageDeclaration().map(NodeWithName::getNameAsString).ifPresent(metadata::setPkg);

                ProcessingUnitRegistry.save(processingUnit, metadata);
            }

            for (var unit : scanner.scanController()) {
                final ProcessingUnit processingUnit = ProcessingUnit.convert(unit);
                var metadata = new ProcessingUnitMetaDataModel();
                metadata.setPath(processingUnit.getId());
                CompilationUnitUtils.getPrimaryTypeName(processingUnit.getCompilationUnit()).ifPresent(metadata::setName);
                metadata.setType(ComponentType.CONTROLLER.toString());
                processingUnit.getCompilationUnit().getPackageDeclaration().map(NodeWithName::getNameAsString).ifPresent(metadata::setPkg);

                ProcessingUnitRegistry.save(processingUnit, metadata);
            }

            for (var unit : scanner.scanConfiguration()) {
                final ProcessingUnit processingUnit = ProcessingUnit.convert(unit);
                var metadata = new ProcessingUnitMetaDataModel();
                metadata.setPath(processingUnit.getId());
                CompilationUnitUtils.getPrimaryTypeName(unit).ifPresent(metadata::setName);
                metadata.setType(ComponentType.CONFIGURATION.toString());
                unit.getPackageDeclaration().map(NodeWithName::getNameAsString).ifPresent(metadata::setPkg);

                ProcessingUnitRegistry.save(processingUnit, metadata);
            }
        } catch (IOException | MojoFailureException ex) {
            LOG.error("Error: cannot register generated components", ex);
        }
    }
}
