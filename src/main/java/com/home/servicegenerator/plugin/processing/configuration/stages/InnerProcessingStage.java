package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.api.ASTProcessingSchema;

import java.util.Map;

import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.AddControllerMethodImplementation;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.AddServiceAbstractMethod;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.AddServiceMethodImplementation;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.CreateAbstractService;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.CreateRepository;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.CreateServiceImplementation;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.EditConfiguration;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.InjectServiceIntoController;

public enum InnerProcessingStage implements Stage {

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

    PROCESS_OUTER_SCHEMA {
    },
    ;

    private ASTProcessingSchema schema;
    private Map<String, Object> processingData;
    private String sourceLocation;
    private boolean repeatable;

    public InnerProcessingStage setSchema(final ASTProcessingSchema schema) {
        this.schema = schema;
        return this;
    }

    @Override
    public InnerProcessingStage setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
        return this;
    }

    public InnerProcessingStage repeatable() {
        this.repeatable = true;
        return this;
    }

    @Override
    public ASTProcessingSchema getSchema() {
        return schema;
    }

    @Override
    public Map<String, Object> getProcessingData() {
        return processingData;
    }

    @Override
    public InnerProcessingStage setProcessingData(Map<String, Object> processingData) {
        this.processingData = processingData;
        return this;
    }


    @Override
    public String getSourceLocation() {
        return sourceLocation;
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    @Override
    public String getName() {
        return this.name();
    }
}
