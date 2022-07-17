package com.home.origami.plugin.processing.configuration.strategy.processing;

import com.home.origami.plugin.PluginConfiguration;
import com.home.origami.plugin.processing.configuration.context.ProcessingContext;
import com.home.origami.plugin.processing.registry.ProcessingUnitRegistry;
import com.home.origami.plugin.metadata.filter.MetaDataFilter;
import com.home.origami.plugin.metadata.model.ComponentType;
import com.home.origami.plugin.metadata.model.ProcessingUnitMetaDataModel;
import com.home.origami.plugin.utils.ResolverUtils;
import com.home.origami.api.context.Context;
import com.home.origami.plugin.processing.configuration.context.properties.PropertyName;
import com.home.origami.plugin.processing.configuration.stages.Stage;
import com.home.origami.plugin.processing.configuration.strategy.PipelineStriping;
import com.home.origami.plugin.processing.statemachine.ProcessingStateMachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.util.Map;
import java.util.stream.Collectors;

public class PipelineIdBasedProcessingStrategy implements ProcessingStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(PipelineIdBasedProcessingStrategy.class);

    @Override
    public void process(Stage initialStage, AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine, PluginConfiguration configuration) {
        // Process stages
        for (var controllerUnit : ProcessingUnitRegistry.find(MetaDataFilter.of(String.format("{ \"type\": \"%s\" }", ComponentType.CONTROLLER)))) {
            for (var pipeline : PipelineStriping.makeStriping(controllerUnit.getCompilationUnit())) {
                var modelNames =
                        ProcessingUnitRegistry.findMetadata(MetaDataFilter.of(String.format("{ \"type\": \"%s\" }", ComponentType.MODEL)))
                                .stream()
                                .map(ProcessingUnitMetaDataModel::getName)
                                .collect(Collectors.toUnmodifiableList());

                //TODO change lookupPipelineId method to use String instead of Name!
                var pipelineIdResolveResult = ResolverUtils.lookupPipelineId(pipeline, modelNames);

                if (pipelineIdResolveResult.isEmpty()) continue;

                //TODO refactor using Name in schema
                stateMachine
                        .getAllStates()
                        .forEach(stage ->
                            stage.getProcessingData()
                                    .putAll(
                                            Map.of(
                                                    PropertyName.PIPELINE.name(), pipeline,
                                                    PropertyName.PIPELINE_ID.name(), pipelineIdResolveResult.get(),
                                                    PropertyName.CONTROLLER_UNIT.name(), controllerUnit)));

                stateMachine
                        .fire(
                                "GENERATE_" + initialStage.getName(),
                                ProcessingContext.of(initialStage.getProcessingData()));
            }
        }
    }
}
