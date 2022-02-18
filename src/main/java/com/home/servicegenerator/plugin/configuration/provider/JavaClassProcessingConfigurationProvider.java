package com.home.servicegenerator.plugin.configuration.provider;

import java.nio.file.Path;

public final class JavaClassProcessingConfigurationProvider implements ProcessingConfigurationProvider {
    private final Path baseClassPath;

    public JavaClassProcessingConfigurationProvider(final Path baseClassPath) {
        this.baseClassPath = baseClassPath;
    }

    @Override
    public Path getDescriptorPath() {
        return this.baseClassPath;
    }
}
