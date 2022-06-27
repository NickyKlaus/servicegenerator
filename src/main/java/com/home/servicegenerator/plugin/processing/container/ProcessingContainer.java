package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.plugin.AbstractServiceGeneratorMojo;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.PluginConfigurationMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessingContainer {
    private final List<Processor> processors;

    public ProcessingContainer(ProcessingConfiguration... processingConfigurations) {
        this.processors = new ProcessorConfigurator(validate(processingConfigurations)).configure();
    }

    public Starter prepare(AbstractServiceGeneratorMojo mojo) {
        return new Starter(processors, PluginConfigurationMapper.toPluginConfiguration(mojo));
    }

    private static List<ProcessingConfiguration> validate(ProcessingConfiguration... processingConfigurations) {
        return Arrays
                .stream(processingConfigurations)
                .filter(
                        config -> !(config == null ||
                                config.getProcessingPlan() == null ||
                                config.getProcessingPlan().getProcessingStages().isEmpty()))
                .collect(Collectors.toUnmodifiableList());
    }

    public static class Starter {
        private final List<Processor> processors;
        private final PluginConfiguration pluginConfiguration;

        private Starter(List<Processor> processors, PluginConfiguration pluginConfiguration) {
            this.processors = processors;
            this.pluginConfiguration = pluginConfiguration;
        }

        public void start() {
            processors.forEach(processor -> processor.process(pluginConfiguration));
        }
    }
}
