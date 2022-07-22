package com.github.origami.plugin.processing.configuration.strategy.naming;

import com.github.origami.api.context.Context;

import java.util.function.Function;

public interface NamingStrategy {
    Function<String, String> getName(Context context);
}
