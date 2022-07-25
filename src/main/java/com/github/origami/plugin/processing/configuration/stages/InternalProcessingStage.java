package com.github.origami.plugin.processing.configuration.stages;

import com.github.origami.plugin.processing.configuration.context.ProcessingContext;
import com.github.origami.plugin.processing.configuration.schema.InternalProcessingSchema;
import com.github.origami.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.github.origami.plugin.processing.configuration.strategy.naming.PipelineIdBasedNamingStrategy;
import com.github.origami.api.ASTProcessingSchema;
import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.context.properties.ComponentPackage;
import com.github.origami.plugin.processing.configuration.context.properties.ComponentType;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public enum InternalProcessingStage implements Stage {
    CREATE_REPOSITORY {
        @Override
        public ASTProcessingSchema getProcessingSchema() {
            return InternalProcessingSchema.CreateRepository;
        }

        @Override
        public String getProcessingUnitType() {
            return ComponentType.REPOSITORY.toString();
        }

        @Override
        public String getProcessingUnitBasePackage() {
            return ComponentPackage.REPOSITORY.toString();
        }
    },

    CREATE_ABSTRACT_SERVICE {
        @Override
        public ASTProcessingSchema getProcessingSchema() {
            return InternalProcessingSchema.CreateAbstractService;
        }

        @Override
        public String getProcessingUnitType() {
            return ComponentType.SERVICE.toString();
        }

        @Override
        public String getProcessingUnitBasePackage() {
            return ComponentPackage.SERVICE.toString();
        }
    },

    CREATE_SERVICE_IMPLEMENTATION {
        @Override
        public ASTProcessingSchema getProcessingSchema() {
            return InternalProcessingSchema.CreateServiceImplementation;
        }

        @Override
        public String getProcessingUnitType() {
            return ComponentType.SERVICE_IMPLEMENTATION.toString();
        }

        @Override
        public String getProcessingUnitBasePackage() {
            return ComponentPackage.SERVICE_IMPLEMENTATION.toString();
        }
    },

    INJECT_SERVICE_INTO_CONTROLLER {
        @Override
        public ASTProcessingSchema getProcessingSchema() {
            return InternalProcessingSchema.InjectServiceIntoController;
        }
    },

    EDIT_CONFIGURATION {
        @Override
        public ASTProcessingSchema getProcessingSchema() {
            return InternalProcessingSchema.EditConfiguration;
        }
    },

    ADD_SERVICE_ABSTRACT_METHOD {
        @Override
        public ASTProcessingSchema getProcessingSchema() {
            return InternalProcessingSchema.AddServiceAbstractMethod;
        }

        @Override
        public String getProcessingUnitType() {
            return ComponentType.SERVICE.toString();
        }

        @Override
        public String getProcessingUnitBasePackage() {
            return ComponentPackage.SERVICE.toString();
        }
    },

    ADD_SERVICE_METHOD_IMPLEMENTATION {
        @Override
        public ASTProcessingSchema getProcessingSchema() {
            return InternalProcessingSchema.AddServiceMethodImplementation;
        }

        @Override
        public String getProcessingUnitType() {
            return ComponentType.SERVICE_IMPLEMENTATION.toString();
        }

        @Override
        public String getProcessingUnitBasePackage() {
            return ComponentPackage.SERVICE_IMPLEMENTATION.toString();
        }
    },

    ADD_CONTROLLER_METHOD_IMPLEMENTATION {
        @Override
        public ASTProcessingSchema getProcessingSchema() {
            return InternalProcessingSchema.AddControllerMethodImplementation;
        }
    },
    ;

    private ASTProcessingSchema schema;
    private final Context context = new ProcessingContext();
    private Consumer<Context> postProcessingAction = ctx -> {};
    private Predicate<Context> executionCondition = ctx -> true;
    private String processingUnitType = StringUtils.EMPTY;
    private String processingUnitName = "Component";
    private String processingUnitLocation;
    private String processingUnitBasePackage = StringUtils.EMPTY;

    public InternalProcessingStage processingSchema(ASTProcessingSchema schema) {
        this.schema = schema;
        return this;
    }

    public InternalProcessingStage context(Map<String, Object> processingData) {
        this.context.getProperties().putAll(processingData);
        return this;
    }

    public InternalProcessingStage context(Context context) {
        this.context.getProperties().putAll(context.getProperties());
        return this;
    }

    public InternalProcessingStage processingUnitLocation(String location) {
        this.processingUnitLocation = location;
        return this;
    }

    public InternalProcessingStage name(String name) {
        // Use name() method of Enum
        return this;
    }

    public InternalProcessingStage postProcessingAction(Consumer<Context> action) {
        this.postProcessingAction = action;
        return this;
    }

    public InternalProcessingStage executingCondition(Predicate<Context> condition) {
        this.executionCondition = condition;
        return this;
    }

    public InternalProcessingStage processingUnitType(String processingUnitType) {
        this.processingUnitType = processingUnitType;
        return this;
    }

    public InternalProcessingStage namingStrategy(NamingStrategy namingStrategy) {
        return this;
    }

    public InternalProcessingStage processingUnitName(String processingUnitName) {
        this.processingUnitName = processingUnitName;
        return this;
    }

    public InternalProcessingStage processingUnitBasePackage(String processingUnitBasePackage) {
        this.processingUnitBasePackage = processingUnitBasePackage;
        return this;
    }

    @Override
    public ASTProcessingSchema getProcessingSchema() {
        return schema;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String getProcessingUnitLocation() {
        return processingUnitLocation;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Consumer<Context> getPostProcessingAction() {
        return postProcessingAction;
    }

    @Override
    public Predicate<Context> getExecutingCondition() {
        return executionCondition;
    }

    @Override
    public String getProcessingUnitType() {
        return processingUnitType;
    }

    @Override
    public NamingStrategy getNamingStrategy() {
        return new PipelineIdBasedNamingStrategy();
    }

    @Override
    public String getProcessingUnitName() {
        return processingUnitName;
    }

    @Override
    public String getProcessingUnitBasePackage() {
        return processingUnitBasePackage;
    }
}
