package com.github.origami.plugin.processing.configuration.strategy.naming;

import com.github.javaparser.ast.expr.Name;
import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.stages.Stage;

import java.util.function.BiFunction;

import static com.github.origami.plugin.processing.configuration.context.properties.PropertyName.PIPELINE_ID;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class PipelineIdBasedNamingStrategy implements NamingStrategy {
    @Override
    public BiFunction<Stage, Context, String> getName() {
        return (stage, ctx) -> 
                capitalize(ctx.get(PIPELINE_ID.name(), Name.class).getIdentifier()) + 
                        capitalize(stage.getProcessingUnitType());
    }
}
