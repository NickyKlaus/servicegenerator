package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configurator.ProcessorConfigurator;
import com.home.servicegenerator.plugin.processing.processor.Processor;

public class ProcessingContainer {
    private final Processor processor;

    public ProcessingContainer(ProcessingConfiguration processingConfiguration) {
        var processorConfigurator = new ProcessorConfigurator(processingConfiguration);
        this.processor = processorConfigurator.configure();
    }

    public void start() {
        processor.process();
    }
}
