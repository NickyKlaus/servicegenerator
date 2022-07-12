package com.home.origami.plugin.processing.configuration;

import com.home.origami.plugin.processing.configuration.stages.ProcessingPlan;
import com.home.origami.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.home.origami.plugin.processing.configuration.strategy.processing.ProcessingStrategy;

public class DefaultProcessingConfiguration implements ProcessingConfiguration {
    private ProcessingPlan processingPlan;
    private ProcessingStrategy processingStrategy;
    private NamingStrategy namingStrategy;
    
    private DefaultProcessingConfiguration() {
    }

    public static ProcessingConfiguration configuration() {
        return new DefaultProcessingConfiguration();
    }

    @Override
    public ProcessingConfiguration processingPlan(final ProcessingPlan plan) {
        this.processingPlan = plan;
        return this;
    }

    @Override
    public ProcessingConfiguration processingStrategy(final ProcessingStrategy strategy) {
        this.processingStrategy = strategy;
        return this;
    }

    @Override
    public ProcessingConfiguration namingStrategy(final NamingStrategy strategy) {
        this.namingStrategy = strategy;
        return this;
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

