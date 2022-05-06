package com.home.servicegenerator.plugin.processing.processor.strategy;

import java.util.function.Function;

public class PipelineIdBasedNamingStrategy implements NamingStrategy {
    @Override
    public Function<String, String> getName() {
        return Function.identity();
    }
}
