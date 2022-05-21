package com.home.servicegenerator.plugin.processing.configuration.strategy.naming;

import java.util.function.Function;

public interface NamingStrategy {
    Function<?, String> getName();
}
