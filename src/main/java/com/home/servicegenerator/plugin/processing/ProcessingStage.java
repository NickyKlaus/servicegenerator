package com.home.servicegenerator.plugin.processing;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.generator.DefaultGenerator;

import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.AddControllerMethodImplementation;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.AddRepositoryMethod;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.AddServiceAbstractMethod;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.AddServiceMethodImplementation;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.CreateAbstractService;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.CreateRepository;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.CreateServiceImplementation;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.EditConfiguration;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.InjectServiceIntoController;

public enum ProcessingStage implements Processable {

    CREATE_REPOSITORY {
        @Override
        public ProcessingStage setSchema(ASTProcessingSchema schema) {
            return this;
        }

        @Override
        public ASTProcessingSchema getSchema() {
            return CreateRepository;
        }
    },

    CREATE_ABSTRACT_SERVICE {
        @Override
        public ProcessingStage setSchema(ASTProcessingSchema schema) {
            return this;
        }

        @Override
        public ASTProcessingSchema getSchema() {
            return CreateAbstractService;
        }
    },

    CREATE_SERVICE_IMPLEMENTATION {
        @Override
        public ProcessingStage setSchema(ASTProcessingSchema schema) {
            return this;
        }

        @Override
        public ASTProcessingSchema getSchema() {
            return CreateServiceImplementation;
        }
    },

    INJECT_SERVICE_INTO_CONTROLLER {
        @Override
        public ProcessingStage setSchema(ASTProcessingSchema schema) {
            return this;
        }

        @Override
        public ASTProcessingSchema getSchema() {
            return InjectServiceIntoController;
        }
    },

    EDIT_CONFIGURATION {
        @Override
        public ProcessingStage setSchema(ASTProcessingSchema schema) {
            return this;
        }

        @Override
        public ASTProcessingSchema getSchema() {
            return EditConfiguration;
        }
    },

    //Trivial. No changes expected.
    ADD_REPOSITORY_METHOD {
        @Override
        public CompilationUnit process() {
            return getCompilationUnit();
        }

        @Override
        public ProcessingStage setSchema(ASTProcessingSchema schema) {
            return this;
        }

        @Override
        public ASTProcessingSchema getSchema() {
            return AddRepositoryMethod;
        }
    },

    ADD_SERVICE_ABSTRACT_METHOD {
        @Override
        public ProcessingStage setSchema(ASTProcessingSchema schema) {
            return this;
        }

        @Override
        public ASTProcessingSchema getSchema() {
            return AddServiceAbstractMethod;
        }
    },

    ADD_SERVICE_METHOD_IMPLEMENTATION {
        @Override
        public ProcessingStage setSchema(ASTProcessingSchema schema) {
            return this;
        }

        @Override
        public ASTProcessingSchema getSchema() {
            return AddServiceMethodImplementation;
        }
    },

    ADD_CONTROLLER_METHOD_IMPLEMENTATION {
        @Override
        public ProcessingStage setSchema(ASTProcessingSchema schema) {
            return this;
        }

        @Override
        public ASTProcessingSchema getSchema() {
            return AddControllerMethodImplementation;
        }
    },

    PROCESS_OUTER_SCHEMA {

    },
    ;

    private ASTProcessingSchema schema;
    private Context context;
    private CompilationUnit compilationUnit;

    public ProcessingStage setSchema(ASTProcessingSchema schema) {
        this.schema = schema;
        return this;
    }

    public ProcessingStage setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
        return this;
    }

    public ProcessingStage setContext(Context context) {
        this.context = context;
        return this;
    }

    public ASTProcessingSchema getSchema() {
        return schema;
    }

    public Context getContext() {
        return context;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    @Override
    public CompilationUnit process() {
        return ((CompilationUnit) DefaultGenerator.builder()
                .processingSchema(getSchema())
                .build()
                .generate(getCompilationUnit(), getContext()));
    }
}
