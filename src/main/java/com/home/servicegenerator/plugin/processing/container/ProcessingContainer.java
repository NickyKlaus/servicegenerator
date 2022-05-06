package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.ProcessingStage;
import com.home.servicegenerator.plugin.processing.processor.Processor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProcessingContainer {
    private final Processor processor;
    private final ProcessingConfiguration processingConfiguration;

    public ProcessingContainer(Processor processor, ProcessingConfiguration processingConfiguration) {
        this.processor = processor;
        this.processingConfiguration = processingConfiguration;
    }

    public void start() {
        processor.process(processingConfiguration.getProcessingStrategy());
    }

    private void prepareProcessingPlan() {
        var extendedStages =
                processingConfiguration
                        .getProcessingPlan()
                        .getProcessingStages()
                        .stream()
                        .map(ProcessingStage::new)
                        .collect(Collectors.toList());

    }
}
