package com.home.origami.plugin.processing.configuration;

import com.home.origami.plugin.processing.configuration.stages.ProcessingPlan;
import com.home.origami.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.home.origami.plugin.processing.configuration.strategy.processing.ProcessingStrategy;

public interface ProcessingConfiguration {
    ProcessingConfiguration processingPlan(final ProcessingPlan plan);
    ProcessingConfiguration processingStrategy(final ProcessingStrategy strategy);
    ProcessingConfiguration namingStrategy(final NamingStrategy strategy);
    ProcessingPlan getProcessingPlan();
    ProcessingStrategy getProcessingStrategy();
    NamingStrategy getNamingStrategy();
}
