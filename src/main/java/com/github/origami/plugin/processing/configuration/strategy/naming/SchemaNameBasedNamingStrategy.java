package com.github.origami.plugin.processing.configuration.strategy.naming;

import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.stages.Stage;

import java.util.function.BiFunction;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class SchemaNameBasedNamingStrategy implements NamingStrategy {
    @Override
    public BiFunction<Stage, Context, String> getName() {
        return (stage, ctx) -> capitalize(stage.getProcessingSchema().getClass().getSimpleName());
    }
}
