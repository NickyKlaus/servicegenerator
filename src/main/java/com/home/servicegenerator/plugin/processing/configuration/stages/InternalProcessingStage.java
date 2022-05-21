package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.context.ProcessingContext;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.home.servicegenerator.plugin.schema.InternalProcessingSchema.AddControllerMethodImplementation;
import static com.home.servicegenerator.plugin.schema.InternalProcessingSchema.AddServiceAbstractMethod;
import static com.home.servicegenerator.plugin.schema.InternalProcessingSchema.AddServiceMethodImplementation;
import static com.home.servicegenerator.plugin.schema.InternalProcessingSchema.CreateAbstractService;
import static com.home.servicegenerator.plugin.schema.InternalProcessingSchema.CreateRepository;
import static com.home.servicegenerator.plugin.schema.InternalProcessingSchema.CreateServiceImplementation;
import static com.home.servicegenerator.plugin.schema.InternalProcessingSchema.EditConfiguration;
import static com.home.servicegenerator.plugin.schema.InternalProcessingSchema.InjectServiceIntoController;

public enum InternalProcessingStage implements Stage {

    CREATE_REPOSITORY {
        @Override
        public ASTProcessingSchema getSchema() {
            return CreateRepository;
        }
    },

    CREATE_ABSTRACT_SERVICE {
        @Override
        public ASTProcessingSchema getSchema() {
            return CreateAbstractService;
        }
    },

    CREATE_SERVICE_IMPLEMENTATION {
        @Override
        public ASTProcessingSchema getSchema() {
            return CreateServiceImplementation;
        }
    },

    INJECT_SERVICE_INTO_CONTROLLER {
        @Override
        public ASTProcessingSchema getSchema() {
            return InjectServiceIntoController;
        }
    },

    EDIT_CONFIGURATION {
        @Override
        public ASTProcessingSchema getSchema() {
            return EditConfiguration;
        }
    },

    ADD_SERVICE_ABSTRACT_METHOD {
        @Override
        public ASTProcessingSchema getSchema() {
            return AddServiceAbstractMethod;
        }
    },

    ADD_SERVICE_METHOD_IMPLEMENTATION {
        @Override
        public ASTProcessingSchema getSchema() {
            return AddServiceMethodImplementation;
        }
    },

    ADD_CONTROLLER_METHOD_IMPLEMENTATION {
        @Override
        public ASTProcessingSchema getSchema() {
            return AddControllerMethodImplementation;
        }
    },

    PROCESS_EXTERNAL_SCHEMA {
    },
    ;

    private ASTProcessingSchema schema;
    private Context context = new ProcessingContext();
    private String sourceLocation;
    private Function<Context, String> sourceLocationProvider;
    private Predicate<Context> executionCondition = ctx -> true;
    private Consumer<Context> afterProcessedAction = ctx -> {};

    @Override
    public Stage setSourceLocation(String sourceLocation) {
        this.sourceLocationProvider = null;
        this.sourceLocation = sourceLocation;
        return this;
    }

    @Override
    public Stage setSourceLocation(Function<Context, String> locationProvider) {
        this.sourceLocation = null;
        this.sourceLocationProvider = locationProvider;
        return this;
    }

    @Override
    public ASTProcessingSchema getSchema() {
        return schema;
    }

    @Override
    public Map<String, Object> getProcessingData() {
        return context.getProperties();
    }

    @Override
    public Stage setProcessingData(Map<String, Object> processingData) {
        this.context.getProperties().putAll(processingData);
        return this;
    }

    @Override
    public String getSourceLocation() {
        return sourceLocationProvider == null ? sourceLocation : sourceLocationProvider.apply(context);
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public Predicate<Context> getExecutingStageCondition() {
        return executionCondition;
    }

    @Override
    public Stage setExecutingStageCondition(Predicate<Context> executionCondition) {
        this.executionCondition = executionCondition;
        return this;
    }

    @Override
    public Stage postProcessingAction(Consumer<Context> action) {
        this.afterProcessedAction = action;
        return this;
    }

    @Override
    public Consumer<Context> getPostProcessingAction() {
        return afterProcessedAction;
    }
}
