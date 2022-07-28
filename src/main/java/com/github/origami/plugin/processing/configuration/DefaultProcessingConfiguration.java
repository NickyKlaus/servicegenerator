package com.github.origami.plugin.processing.configuration;

import com.github.origami.plugin.processing.configuration.stages.ProcessingPlan;
import com.github.origami.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.github.origami.plugin.processing.configuration.strategy.processing.ProcessingStrategy;

public class DefaultProcessingConfiguration implements ProcessingConfiguration {
    private String baseLocation;
    private ProcessingPlan processingPlan;
    private ProcessingStrategy processingStrategy;
    private NamingStrategy namingStrategy;
    
    private DefaultProcessingConfiguration() {
    }

    public static ProcessingConfiguration configuration() {
        return new DefaultProcessingConfiguration();
    }

    @Override
    public ProcessingConfiguration baseLocation(String baseLocation) {
        this.baseLocation = baseLocation;
        return this;
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
    public String getBaseLocation() {
        return baseLocation;
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

