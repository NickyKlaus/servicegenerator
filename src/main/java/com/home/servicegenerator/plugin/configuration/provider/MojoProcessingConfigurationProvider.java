package com.home.servicegenerator.plugin.configuration.provider;

import java.nio.file.Path;

public final class MojoProcessingConfigurationProvider implements ProcessingConfigurationProvider {
    private final Path baseMojoDescriptorPath;

    public MojoProcessingConfigurationProvider(final Path baseMojoDescriptorPath) {
        this.baseMojoDescriptorPath = baseMojoDescriptorPath;
    }

    @Override
    public Path getDescriptorPath() {
        return this.baseMojoDescriptorPath;
    }
}
