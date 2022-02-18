package com.home.servicegenerator.plugin.processing.container;

import com.home.servicegenerator.plugin.configuration.ProcessingConfiguration;

public final class ProcessingContainer {
    private final ProcessingConfiguration processingConfiguration;

    private ProcessingContainer(final ProcessingContainer.Builder builder) {
        this.processingConfiguration = builder.processingConfiguration;
    }

    public static ProcessingContainer.Builder builder() {
        return new ProcessingContainer.Builder();
    }

    public static final class Builder {
        private ProcessingConfiguration processingConfiguration;

        private Builder() {
        }

        public ProcessingContainer.Builder configuration(final ProcessingConfiguration processingConfiguration) {
            if (processingConfiguration == null) {
                throw new IllegalArgumentException("Processing configuration cannot be null!");
            }
            this.processingConfiguration = processingConfiguration;
            return this;
        }



        public ProcessingContainer build() {
            return new ProcessingContainer(this);
        }
    }
}
