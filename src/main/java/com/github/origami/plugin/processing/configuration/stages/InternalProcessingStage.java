package com.github.origami.plugin.processing.configuration.stages;

import com.github.origami.plugin.processing.configuration.context.ProcessingContext;
import com.github.origami.plugin.processing.configuration.schema.InternalProcessingSchema;
import com.github.origami.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.github.origami.plugin.processing.configuration.strategy.naming.PipelineIdBasedNamingStrategy;
import com.github.origami.plugin.processing.configuration.strategy.naming.SimpleNamingStrategy;
import com.github.origami.api.ASTProcessingSchema;
import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.context.properties.ComponentPackage;
import com.github.origami.plugin.processing.configuration.context.properties.ComponentType;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public enum InternalProcessingStage implements Stage {

    CREATE_REPOSITORY {
        @Override
        public ASTProcessingSchema getSchema() {
            return InternalProcessingSchema.CreateRepository;
        }

        @Override
        public String getComponentPackage() {
            return ComponentPackage.REPOSITORY.toString();
        }

        @Override
        public String getComponentType() {
            return ComponentType.REPOSITORY.toString();
        }

        @Override
        public NamingStrategy getNamingStrategy() {
            return new PipelineIdBasedNamingStrategy();
        }
    },

    CREATE_ABSTRACT_SERVICE {
        @Override
        public ASTProcessingSchema getSchema() {
            return InternalProcessingSchema.CreateAbstractService;
        }

        @Override
        public String getComponentPackage() {
            return ComponentPackage.SERVICE.toString();
        }

        @Override
        public String getComponentType() {
            return ComponentType.SERVICE.toString();
        }

        @Override
        public NamingStrategy getNamingStrategy() {
            return new PipelineIdBasedNamingStrategy();
        }
    },

    CREATE_SERVICE_IMPLEMENTATION {
        @Override
        public ASTProcessingSchema getSchema() {
            return InternalProcessingSchema.CreateServiceImplementation;
        }

        @Override
        public String getComponentPackage() {
            return ComponentPackage.SERVICE_IMPLEMENTATION.toString();
        }

        @Override
        public String getComponentType() {
            return ComponentType.SERVICE_IMPLEMENTATION.toString();
        }

        @Override
        public NamingStrategy getNamingStrategy() {
            return new PipelineIdBasedNamingStrategy();
        }
    },

    INJECT_SERVICE_INTO_CONTROLLER {
        @Override
        public ASTProcessingSchema getSchema() {
            return InternalProcessingSchema.InjectServiceIntoController;
        }
    },

    EDIT_CONFIGURATION {
        @Override
        public ASTProcessingSchema getSchema() {
            return InternalProcessingSchema.EditConfiguration;
        }
    },

    ADD_SERVICE_ABSTRACT_METHOD {
        @Override
        public ASTProcessingSchema getSchema() {
            return InternalProcessingSchema.AddServiceAbstractMethod;
        }

        @Override
        public String getComponentPackage() {
            return ComponentPackage.SERVICE.toString();
        }

        @Override
        public String getComponentType() {
            return ComponentType.SERVICE.toString();
        }

        @Override
        public NamingStrategy getNamingStrategy() {
            return new PipelineIdBasedNamingStrategy();
        }
    },

    ADD_SERVICE_METHOD_IMPLEMENTATION {
        @Override
        public ASTProcessingSchema getSchema() {
            return InternalProcessingSchema.AddServiceMethodImplementation;
        }

        @Override
        public String getComponentPackage() {
            return ComponentPackage.SERVICE_IMPLEMENTATION.toString();
        }

        @Override
        public String getComponentType() {
            return ComponentType.SERVICE_IMPLEMENTATION.toString();
        }

        @Override
        public NamingStrategy getNamingStrategy() {
            return new PipelineIdBasedNamingStrategy();
        }
    },

    ADD_CONTROLLER_METHOD_IMPLEMENTATION {
        @Override
        public ASTProcessingSchema getSchema() {
            return InternalProcessingSchema.AddControllerMethodImplementation;
        }
    },
    ;

    private ASTProcessingSchema schema;
    private final Context context = new ProcessingContext();
    private String sourceLocation;
    private Function<Context, String> sourceLocationProvider;
    private Predicate<Context> executionCondition = ctx -> true;
    private Consumer<Context> postProcessingAction = ctx -> {};
    private String componentPackage = StringUtils.EMPTY;
    private String componentType = StringUtils.EMPTY;
    private NamingStrategy namingStrategy = new SimpleNamingStrategy();
    private String componentName = "Component";

    @Override
    public Stage setComponentName(String componentName) {
        this.componentName = componentName;
        return this;
    }

    @Override
    public String getComponentName() {
        return this.componentName;
    }
    @Override
    public NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }

    @Override
    public Stage setNamingStrategy(NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
        return this;
    }

    @Override
    public Stage setComponentPackage(String packageName) {
        this.componentPackage = packageName;
        return this;
    }

    @Override
    public String getComponentPackage() {
        return this.componentPackage;
    }

    @Override
    public Stage setComponentType(String componentType) {
        this.componentType = componentType;
        return this;
    }

    @Override
    public String getComponentType() {
        return this.componentType;
    }


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
    public Stage setSchema(ASTProcessingSchema schema) {
        this.schema = schema;
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
        this.postProcessingAction = action;
        return this;
    }

    @Override
    public Consumer<Context> getPostProcessingAction() {
        return postProcessingAction;
    }
}
