package com.home.servicegenerator.plugin.configuration;

import com.home.servicegenerator.api.ASTProcessingSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DefaultProcessingConfiguration implements ProcessingConfiguration {
    private static final ProcessingConfiguration configuration = new DefaultProcessingConfiguration();
    private static final List<ASTProcessingSchema> schemas = Collections.synchronizedList(new ArrayList<>(8));

    private DefaultProcessingConfiguration() {

    }

    public static ProcessingConfiguration and(final ASTProcessingSchema anotherSchema) {
        synchronized (schemas) {
            schemas.add(anotherSchema);
            return configuration;
        }
    }
}
