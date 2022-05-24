package com.home.servicegenerator.plugin.processing.configuration.strategy.naming;

import com.home.servicegenerator.api.context.Context;

import java.util.function.Function;

public interface NamingStrategy {
    Function<String, String> getName(Context context);
}
