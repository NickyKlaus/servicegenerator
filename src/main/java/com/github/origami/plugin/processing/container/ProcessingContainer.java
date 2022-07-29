package com.github.origami.plugin.processing.container;

import com.github.origami.plugin.AbstractServiceGeneratorMojo;
import com.github.origami.plugin.PluginConfiguration;
import com.github.origami.plugin.PluginConfigurationMapper;
import com.github.origami.plugin.db.DBClient;
import com.github.origami.plugin.processing.configuration.ProcessingConfiguration;
import com.github.origami.plugin.processing.processor.Processor;
import com.github.origami.plugin.processing.processor.ProcessorConfigurator;

import org.apache.maven.plugin.MojoFailureException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessingContainer {
    private static List<Processor> processors = List.of();

    private ProcessingContainer() {
    }

    public static ProcessingContainer container(ProcessingConfiguration... processingConfigurations) throws MojoFailureException {
        ProcessingContainer.processors = new ProcessorConfigurator(filterValid(processingConfigurations)).configure();
        return new ProcessingContainer();
    }

    public Starter prepare(AbstractServiceGeneratorMojo mojo) {
        return new Starter(processors, PluginConfigurationMapper.toPluginConfiguration(mojo));
    }

    private static List<ProcessingConfiguration> filterValid(ProcessingConfiguration... processingConfigurations) throws MojoFailureException {
        var valid =
                Arrays.stream(processingConfigurations)
                        .filter(config -> config != null && config.getProcessingPlan() != null && !config.getProcessingPlan().getProcessingStages().isEmpty())
                        .collect(Collectors.toUnmodifiableList());
        if (valid.isEmpty()) {
            throw new MojoFailureException("No valid processing configuration found!");
        }
        return valid;
    }

    public static class Starter {
        private final List<Processor> processors;
        private final PluginConfiguration pluginConfiguration;

        private Starter(List<Processor> processors, PluginConfiguration pluginConfiguration) {
            this.processors = processors;
            this.pluginConfiguration = pluginConfiguration;
        }

        public void start() {
            try (DBClient.INSTANCE) {
                processors.forEach(processor -> processor.process(pluginConfiguration));
            }
        }
    }
}
