package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;

import java.nio.file.Path;

public interface Stage {
    ASTProcessingSchema getSchema();
    Context getContext();
    Path getSourceLocation();
    boolean isRepeatable();
    String getName();
}
