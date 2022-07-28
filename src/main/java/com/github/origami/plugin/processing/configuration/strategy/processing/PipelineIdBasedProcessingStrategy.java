package com.github.origami.plugin.processing.configuration.strategy.processing;

import com.github.origami.plugin.PluginConfiguration;
import com.github.origami.plugin.db.filter.Filter;
import com.github.origami.plugin.metadata.filter.MetaDataFilter;
import com.github.origami.plugin.metadata.model.ComponentType;
import com.github.origami.plugin.metadata.model.ProcessingUnitMetaDataModel;
import com.github.origami.plugin.processing.configuration.context.ProcessingContext;
import com.github.origami.plugin.processing.registry.ProcessingUnitRegistry;
import com.github.origami.plugin.utils.ResolverUtils;
import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.context.properties.PropertyName;
import com.github.origami.plugin.processing.configuration.stages.Stage;
import com.github.origami.plugin.processing.configuration.strategy.PipelineStriping;
import com.github.origami.plugin.processing.statemachine.ProcessingStateMachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.util.Map;
import java.util.stream.Collectors;

public class PipelineIdBasedProcessingStrategy implements ProcessingStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(PipelineIdBasedProcessingStrategy.class);
    private static final Filter ALL_CONTROLLERS = MetaDataFilter.of(String.format("{ \"type\": \"%s\" }", ComponentType.CONTROLLER));
    private static final Filter ALL_MODELS = MetaDataFilter.of(String.format("{ \"type\": \"%s\" }", ComponentType.MODEL));

    @Override
    public void process(AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine, PluginConfiguration configuration) {
        // Process stages
        for (var controllerUnitMeta : ProcessingUnitRegistry.findMetadata(ALL_CONTROLLERS)) {
            for (var pipeline : PipelineStriping.makeStriping(ProcessingUnitRegistry.get(controllerUnitMeta.getPath()).getCompilationUnit())) {
                var modelNames =
                        ProcessingUnitRegistry.findMetadata(ALL_MODELS)
                                .stream()
                                .map(ProcessingUnitMetaDataModel::getName)
                                .collect(Collectors.toUnmodifiableList());

                var pipelineIdResolveResult = ResolverUtils.lookupPipelineId(pipeline, modelNames);

                if (pipelineIdResolveResult.isEmpty()) continue;

                var initialContext =
                        ProcessingContext.of(
                                stateMachine
                                        .getInitialState()
                                        .getContext()
                                        .getProperties());
                initialContext
                        .getProperties()
                        .putAll(
                                Map.of(
                                        PropertyName.PIPELINE.name(), pipeline,
                                        PropertyName.PIPELINE_ID.name(), pipelineIdResolveResult.get(),
                                        PropertyName.CONTROLLER_UNIT_NAME.name(), controllerUnitMeta.getName()));
                stateMachine
                        .fire(
                                "GENERATE_" + stateMachine.getInitialState().getName(),
                                initialContext);
            }
        }
    }
}
