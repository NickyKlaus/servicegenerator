package com.home.origami.plugin.processing.configuration.strategy.naming;

import com.github.javaparser.ast.expr.Name;
import com.home.origami.api.context.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

import static com.home.origami.plugin.processing.configuration.context.properties.PropertyName.PIPELINE_ID;

public class PipelineIdBasedNamingStrategy implements NamingStrategy {
    @Override
    public Function<String, String> getName(Context context) {
        var pipelineId = context.get(PIPELINE_ID.name(), Name.class).getIdentifier();
        return componentType -> StringUtils.capitalize(pipelineId) + StringUtils.capitalize(componentType);
    }
}
