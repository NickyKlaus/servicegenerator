package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.plugin.AbstractServiceGeneratorMojo;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.PluginConfigurationMapper;

import java.util.List;

public class ProcessingContainer {
    private final List<Processor> processors;

    public ProcessingContainer(ProcessingConfiguration... processingConfigurations) {
        this.processors = new ProcessorConfigurator(processingConfigurations).configure();
    }

    public Starter prepare(AbstractServiceGeneratorMojo mojo) {
        return new Starter(processors, PluginConfigurationMapper.toPluginConfiguration(mojo));
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
