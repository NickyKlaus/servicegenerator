package com.github.origami.plugin.processing.configuration.stages;

import com.github.origami.plugin.processing.configuration.context.ProcessingContext;
import com.github.origami.api.ASTProcessingSchema;
import com.github.origami.api.context.Context;
import com.github.origami.plugin.processing.configuration.strategy.naming.NamingStrategy;
import com.github.origami.plugin.processing.configuration.strategy.naming.SchemaClassBasedNamingStrategy;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ProcessingStage implements Stage {
    private ASTProcessingSchema schema;
    private Context context = new ProcessingContext();
    private NamingStrategy namingStrategy = new SchemaClassBasedNamingStrategy();
    private String name = StringUtils.EMPTY;
    private Consumer<Context> postProcessingAction = ctx -> {};
    private Predicate<Context> executionCondition = ctx -> true;
    private String processingUnitType = StringUtils.EMPTY;
    private String processingUnitName = "Component";
    private String processingUnitLocation;
    private String processingUnitBasePackage = StringUtils.EMPTY;

    private ProcessingStage processingSchema(ASTProcessingSchema schema) {
        this.schema = schema;
        return this;
    }
    private ProcessingStage context(Map<String, Object> processingData) {
        this.context.getProperties().putAll(processingData);
        return this;
    }
    private ProcessingStage context(Context context) {
        this.context.getProperties().putAll(context.getProperties());
        return this;
    }
    private ProcessingStage processingUnitLocation(String location) {
        this.processingUnitLocation = location;
        return this;
    }
    private ProcessingStage name(String name) {
        this.name = name;
        return this;
    }
    private ProcessingStage postProcessingAction(Consumer<Context> action) {
        this.postProcessingAction = action;
        return this;
    }
    private ProcessingStage executingCondition(Predicate<Context> condition) {
        this.executionCondition = condition;
        return this;
    }
    private ProcessingStage processingUnitType(String processingUnitType) {
        this.processingUnitType = processingUnitType;
        return this;
    }
    private ProcessingStage namingStrategy(NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
        return this;
    }
    private ProcessingStage processingUnitName(String processingUnitName) {
        this.processingUnitName = processingUnitName;
        return this;
    }
    private ProcessingStage processingUnitBasePackage(String processingUnitBasePackage) {
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
        return name;
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
        return namingStrategy;
    }

    @Override
    public String getProcessingUnitName() {
        return processingUnitName;
    }

    @Override
    public String getProcessingUnitBasePackage() {
        return processingUnitBasePackage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASTProcessingSchema schema;
        private Context context = new ProcessingContext();
        private NamingStrategy namingStrategy = new SchemaClassBasedNamingStrategy();
        private String name = StringUtils.EMPTY;
        private Consumer<Context> postProcessingAction = ctx -> {};
        private Predicate<Context> executionCondition = ctx -> true;
        private String processingUnitType = StringUtils.EMPTY;
        private String processingUnitName = "Component";
        private String processingUnitLocation;
        private String processingUnitBasePackage = StringUtils.EMPTY;

        public Builder processingSchema(ASTProcessingSchema schema) {
            this.schema = schema;
            return this;
        }

        public Builder context(Map<String, Object> processingData) {
            this.context.getProperties().putAll(processingData);
            return this;
        }

        public Builder context(Context context) {
            this.context.getProperties().putAll(context.getProperties());
            return this;
        }

        public Builder processingUnitLocation(String location) {
            this.processingUnitLocation = location;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder postProcessingAction(Consumer<Context> action) {
            this.postProcessingAction = action;
            return this;
        }

        public Builder executingCondition(Predicate<Context> condition) {
            this.executionCondition = condition;
            return this;
        }

        public Builder processingUnitType(String processingUnitType) {
            this.processingUnitType = processingUnitType;
            return this;
        }

        public Builder namingStrategy(NamingStrategy namingStrategy) {
            this.namingStrategy = namingStrategy;
            return this;
        }

        public Builder processingUnitName(String processingUnitName) {
            this.processingUnitName = processingUnitName;
            return this;
        }

        public Builder processingUnitBasePackage(String processingUnitBasePackage) {
            this.processingUnitBasePackage = processingUnitBasePackage;
            return this;
        }
        public Stage build() {
            return new ProcessingStage()
                    .name(name)
                    .processingSchema(schema)
                    .context(context)
                    .postProcessingAction(postProcessingAction)
                    .executingCondition(executionCondition)
                    .namingStrategy(namingStrategy)
                    .processingUnitType(processingUnitType)
                    .processingUnitName(processingUnitName)
                    .processingUnitLocation(processingUnitLocation)
                    .processingUnitBasePackage(processingUnitBasePackage);
        }
    }
}
