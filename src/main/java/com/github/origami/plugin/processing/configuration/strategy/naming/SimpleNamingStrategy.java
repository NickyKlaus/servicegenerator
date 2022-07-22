package com.github.origami.plugin.processing.configuration.strategy.naming;

import com.github.origami.api.context.Context;

import java.util.function.Function;

public class SimpleNamingStrategy implements NamingStrategy {
    @Override
    public Function<String, String> getName(Context context) {
        return Function.identity();
    }
}
