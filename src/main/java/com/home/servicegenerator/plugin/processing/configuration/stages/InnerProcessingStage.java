package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.processing.events.ProcessingEvent;
import com.home.servicegenerator.plugin.processing.processor.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.registry.ProjectUnitsRegistry;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.action.Actions;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
    private Supplier<Context> context;
    private Supplier<Path> sourceLocation;
    private boolean repeatable;
    private Action<Stage, String> action;

    public InnerProcessingStage setSchema(final ASTProcessingSchema schema) {
        this.schema = schema;
        return this;
    }

    public InnerProcessingStage setSourceLocation(final Supplier<Path> sourceLocation) {
        this.sourceLocation = sourceLocation;
        return this;
    }

    /*public InnerProcessingStage setContext(final Context context) {
        this.context = context;
        return this;
    }*/

    public InnerProcessingStage setContext(final Supplier<Context> contextSupplier) {
        this.context = contextSupplier;
        return this;
    }

    public InnerProcessingStage repeatable() {
        this.repeatable = true;
        return this;
    }

    public Action<Stage, String> getAction() {
        return action;
    }

    public InnerProcessingStage action(final Action<Stage, String> action) {
        this.action = action;
        return this;
    }

    @Override
    public ASTProcessingSchema getSchema() {
        return schema;
    }

    @Override
    public Context getContext() {
        return context.get();
    }

    @Override
    public Path getSourceLocation() {
        return sourceLocation.get();
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
