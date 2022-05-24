package com.home.servicegenerator.plugin.processing.configuration.strategy.naming;

import com.home.servicegenerator.api.context.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

import static com.home.servicegenerator.plugin.processing.configuration.context.properties.PropertyName.PIPELINE_ID;

public class PipelineIdBasedNamingStrategy implements NamingStrategy {
    @Override
    public Function<String, String> getName(Context context) {
        var pipelineId = context.get(PIPELINE_ID.name(), String.class);
        return componentType -> StringUtils.uncapitalize(pipelineId) + StringUtils.capitalize(componentType);
    }
}
