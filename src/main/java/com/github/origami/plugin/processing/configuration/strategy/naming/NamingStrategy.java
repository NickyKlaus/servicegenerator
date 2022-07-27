package com.github.origami.plugin.processing.configuration.strategy.naming;

import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.stages.Stage;

import java.util.function.BiFunction;

public interface NamingStrategy {
    BiFunction<Stage, Context, String> getName();
}
