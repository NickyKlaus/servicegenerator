package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.plugin.AbstractServiceGeneratorMojo;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.PluginConfigurationMapper;

public class ProcessingContainer {
    private final Processor processor;

    public ProcessingContainer(ProcessingConfiguration... processingConfigurations) {
        this.processor = new ProcessorConfigurator(processingConfigurations).configure();
    }

    public Starter prepare(AbstractServiceGeneratorMojo mojo) {
        return new Starter(processor, PluginConfigurationMapper.toPluginConfiguration(mojo));
    }

    public static class Starter {
        private final Processor processor;
        private final PluginConfiguration pluginConfiguration;

        private Starter(Processor processor, PluginConfiguration pluginConfiguration) {
            this.processor = processor;
            this.pluginConfiguration = pluginConfiguration;
        }

        public void start() {
            processor.process(pluginConfiguration);
        }
    }
}
