package com.home.servicegenerator.plugin.processing;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.processing.injection.ConstantBasedDataInjectionStrategy;
import com.home.servicegenerator.plugin.processing.injection.DataInjectionStrategy;

import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.AddControllerMethodImplementation;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.AddRepositoryMethod;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.AddServiceAbstractMethod;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.AddServiceMethodImplementation;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.CreateAbstractService;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.CreateRepository;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.CreateServiceImplementation;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.EditConfiguration;
import static com.home.servicegenerator.plugin.schemas.InnerProcessingSchema.InjectServiceIntoController;

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
        /*@Override
        public CompilationUnit process() {
            return getCompilationUnit();
        }*/

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
    private Class<? extends DataInjectionStrategy> dataInjectionStrategy = ConstantBasedDataInjectionStrategy.class;

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

    public void setDataInjectionStrategy(Class<? extends DataInjectionStrategy> dataInjectionStrategy) {
        this.dataInjectionStrategy = dataInjectionStrategy;
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

    public Class<? extends DataInjectionStrategy> getDataInjectionStrategy() {
        return dataInjectionStrategy;
    }

    @Override
    public CompilationUnit process() {
        return ((CompilationUnit) DefaultGenerator.builder()
                .processingSchema(getSchema())
                .build()
                .generate(getCompilationUnit(), getContext()));
    }
}
