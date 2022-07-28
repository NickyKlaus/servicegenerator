package com.github.origami.plugin.processing.configuration;

import com.github.origami.plugin.processing.configuration.stages.ProcessingPlan;
import com.github.origami.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.github.origami.plugin.processing.configuration.strategy.processing.ProcessingStrategy;

public interface ProcessingConfiguration {
    ProcessingConfiguration baseLocation(String baseLocation);
    ProcessingConfiguration processingPlan(final ProcessingPlan plan);
    ProcessingConfiguration processingStrategy(final ProcessingStrategy strategy);
    ProcessingConfiguration namingStrategy(final NamingStrategy strategy);
    String getBaseLocation();
    ProcessingPlan getProcessingPlan();
    ProcessingStrategy getProcessingStrategy();
    NamingStrategy getNamingStrategy();
}
