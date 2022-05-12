package com.home.servicegenerator.plugin.processing.strategy;

import java.util.function.Function;

public interface NamingStrategy {
    Function<?, String> getName();
}
