package com.home.servicegenerator.plugin.processing.configuration;

import com.home.servicegenerator.plugin.processing.configuration.stages.ProcessingPlan;
import com.home.servicegenerator.plugin.processing.processor.strategy.NamingStrategy;
import com.home.servicegenerator.plugin.processing.processor.strategy.ProcessingStrategy;

public class DefaultProcessingConfiguration implements ProcessingConfiguration {
    private static final ProcessingConfiguration CONFIGURATION = new DefaultProcessingConfiguration();
    private static ProcessingPlan processingPlan;
    private static ProcessingStrategy processingStrategy;
    private static NamingStrategy namingStrategy;
    
    private DefaultProcessingConfiguration() {
    }

    public static ProcessingConfiguration configuration() {
        return CONFIGURATION;
    }

    @Override
    public ProcessingConfiguration processingPlan(final ProcessingPlan plan) {
        DefaultProcessingConfiguration.processingPlan = plan;
        return CONFIGURATION;
    }

    @Override
    public ProcessingConfiguration processingStrategy(final ProcessingStrategy strategy) {
        DefaultProcessingConfiguration.processingStrategy = strategy;
        return CONFIGURATION;
    }

    @Override
    public ProcessingConfiguration namingStrategy(final NamingStrategy strategy) {
        DefaultProcessingConfiguration.namingStrategy = strategy;
        return CONFIGURATION;
    }

    @Override
    public ProcessingPlan getProcessingPlan() {
        return processingPlan;
    }

    @Override
    public ProcessingStrategy getProcessingStrategy() {
        return processingStrategy;
    }

    @Override
    public NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }
}

