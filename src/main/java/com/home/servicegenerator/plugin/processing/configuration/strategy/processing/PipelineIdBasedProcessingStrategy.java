package com.home.servicegenerator.plugin.processing.configuration.strategy.processing;

import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.configuration.context.properties.PropertyName;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.configuration.strategy.PipelineStriping;
import com.home.servicegenerator.plugin.processing.registry.meta.filter.MetadataFilter;
import com.home.servicegenerator.plugin.processing.registry.Registry;
import com.home.servicegenerator.plugin.processing.registry.meta.model.ComponentType;
import com.home.servicegenerator.plugin.processing.registry.meta.model.ProcessingUnitMetaModel;
import com.home.servicegenerator.plugin.processing.statemachine.ProcessingStateMachine;
import com.home.servicegenerator.plugin.utils.ResolverUtils;
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
        for (var controllerUnit : Registry.find(MetadataFilter.of(String.format("{ \"type\": \"%s\" }", ComponentType.CONTROLLER)))) {
            for (var pipeline : PipelineStriping.makeStriping(controllerUnit.getCompilationUnit())) {
                var modelNames =
                        Registry.findMetadata(MetadataFilter.of(String.format("{ \"type\": \"%s\" }", ComponentType.MODEL)))
                                .stream()
                                .map(ProcessingUnitMetaModel::getName)
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
