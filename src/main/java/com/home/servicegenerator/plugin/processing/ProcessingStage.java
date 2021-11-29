package com.home.servicegenerator.plugin.processing;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.generator.DefaultGenerator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.AddControllerMethodImplementation;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.AddRepositoryMethod;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.AddServiceAbstractMethod;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.AddServiceMethodImplementation;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.CreateAbstractService;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.CreateRepository;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.CreateServiceImplementation;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.EditConfiguration;
import static com.home.servicegenerator.plugin.processing.schemas.InnerProcessingSchema.InjectServiceIntoController;
import static org.apache.commons.io.FilenameUtils.normalizeNoEndSeparator;

public enum ProcessingStage implements Processable {

    CREATE_REPOSITORY {
        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            if (getContext().getPipelineId().equals(context.getPipelineId())) {
                return CREATE_ABSTRACT_SERVICE
                        .setContext(context)
                        .setCompilationUnit(compilationUnit);
            }
            setContext(context);
            setCompilationUnit(compilationUnit);
            return this;
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            return nextStage(compilationUnit, context);
        }

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
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            if (getContext().getPipelineId().equals(context.getPipelineId())) {
                return CREATE_SERVICE_IMPLEMENTATION
                        .setContext(context)
                        .setCompilationUnit(compilationUnit);
            }
            setContext(context);
            setCompilationUnit(compilationUnit);
            return this;
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            return nextStage(compilationUnit, context);
        }

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
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            if (getContext().getPipelineId().equals(context.getPipelineId())) {
                return INJECT_SERVICE_INTO_CONTROLLER
                        .setContext(context)
                        .setCompilationUnit(compilationUnit);
            }
            setContext(context);
            setCompilationUnit(compilationUnit);
            return this;
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            return nextStage(compilationUnit, context);
        }

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
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            if (getContext().getPipelineId().equals(context.getPipelineId())) {
                return EDIT_CONFIGURATION
                        .setContext(context)
                        .setCompilationUnit(compilationUnit);
            }
            setContext(context);
            setCompilationUnit(compilationUnit);
            return this;
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            return nextStage(compilationUnit, context);
        }

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
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            if (getContext().getPipelineId().equals(context.getPipelineId())) {
                return ADD_REPOSITORY_METHOD
                        .setContext(context)
                        .setCompilationUnit(compilationUnit);
            }
            setContext(context);
            setCompilationUnit(compilationUnit);
            return this;
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            return nextStage(compilationUnit, context);
        }

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
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            return ADD_SERVICE_ABSTRACT_METHOD
                    .setContext(context)
                    .setCompilationUnit(compilationUnit);
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            return nextStage(compilationUnit, context);
        }

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
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            if (equalMethodSignatures(getContext().getPipeline(), context.getPipeline())) {
                return ADD_SERVICE_METHOD_IMPLEMENTATION
                        .setContext(context)
                        .setCompilationUnit(compilationUnit);
            }
            setContext(context);
            setCompilationUnit(compilationUnit);
            return this;
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            return nextStage(compilationUnit, context);
        }

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
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            if (equalMethodSignatures(getContext().getPipeline(), context.getPipeline())) {
                return ADD_CONTROLLER_METHOD_IMPLEMENTATION
                        .setContext(context)
                        .setCompilationUnit(compilationUnit);
            }
            setContext(context);
            setCompilationUnit(compilationUnit);
            return this;
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            return nextStage(compilationUnit, context);
        }

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
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            if (equalMethodSignatures(getContext().getPipeline(), context.getPipeline())) {
                return PROCESS_OUTER_SCHEMA
                        .setContext(context)
                        .setCompilationUnit(compilationUnit);
            }
            setContext(context);
            setCompilationUnit(compilationUnit);
            return this;
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            return nextStage(compilationUnit, context);
        }

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
        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context) {
            this.setCompilationUnit(compilationUnit)
                    .setSchema(schema)
                    .setContext(context);
            return this;
        }

        @Override
        public ProcessingStage nextStage(CompilationUnit compilationUnit, Context context) {
            throw new IllegalStateException("Outer stage must have registered processing schema!");
        }
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

    public abstract ProcessingStage nextStage(CompilationUnit compilationUnit, Context context);
    public abstract ProcessingStage nextStage(CompilationUnit compilationUnit, ASTProcessingSchema schema, Context context);

    private static Path createFilePath(
            final String projectBaseDir, final String basePackage, final String baseClassName, final String postfix
    ) {
        return Paths.get(
                StringUtils.join(
                        normalizeNoEndSeparator(projectBaseDir),
                        File.separator,
                        normalizeNoEndSeparator(StringUtils.replaceChars(basePackage, ".", File.separator)),
                        File.separator,
                        baseClassName,
                        postfix,
                        ".java"));
    }

    private static boolean equalMethodSignatures(final MethodDeclaration d1, final MethodDeclaration d2) {
        return d1 != null && d2 != null && d1.getSignature().equals(d2.getSignature());
    }
}
