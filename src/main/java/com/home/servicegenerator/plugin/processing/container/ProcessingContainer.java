package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.plugin.AbstractServiceGeneratorMojo;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.PluginConfigurationMapper;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configurator.ProcessorConfigurator;
import com.home.servicegenerator.plugin.processing.processor.Processor;

public class ProcessingContainer {
    private final Processor processor;

    public ProcessingContainer(ProcessingConfiguration processingConfiguration) {
        this.processor = new ProcessorConfigurator(processingConfiguration).configure();
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
