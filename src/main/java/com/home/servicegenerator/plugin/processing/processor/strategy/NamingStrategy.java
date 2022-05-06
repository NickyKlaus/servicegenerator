package com.home.servicegenerator.plugin.processing.processor.strategy;

import java.util.function.Function;

public interface NamingStrategy {
    Function<?, String> getName();
}
