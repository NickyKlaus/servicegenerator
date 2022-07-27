package com.github.origami.plugin;

import com.github.origami.plugin.processing.configuration.DefaultProcessingConfiguration;
import com.github.origami.plugin.processing.configuration.ProcessingConfiguration;
import com.github.origami.plugin.processing.configuration.stages.ProcessingStageMapper;
import com.github.origami.plugin.db.DBClient;
import com.github.origami.plugin.processing.configuration.stages.ProcessingPlan;
import com.github.origami.plugin.processing.configuration.strategy.processing.SequentialProcessingStrategy;
import com.github.origami.plugin.processing.container.ProcessingContainer;
import com.github.origami.plugin.processing.configuration.context.properties.PropertyName;
import com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage;
import com.github.origami.plugin.processing.configuration.strategy.naming.PipelineIdBasedNamingStrategy;
import com.github.origami.plugin.processing.configuration.strategy.processing.PipelineIdBasedProcessingStrategy;

import io.swagger.codegen.v3.cli.cmd.Generate;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Goal which generates microservice based on declared logic.
 */
@Mojo(name = "generate-service", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class OrigamiPlugin extends AbstractServiceGeneratorMojo {
    private ProcessingPlan internalProcessingPlan() {
        return ProcessingPlan
                .processingPlan()
                .stage(
                        InternalProcessingStage.INITIALIZE_PROCESSING_CONTEXT
                                .context(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.BASE_PACKAGE.name(), getBasePackage()),
                                                Map.entry(PropertyName.DB_TYPE.name(), getDbType()),
                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(), getBasePackage() + ".repository"),
                                                Map.entry(PropertyName.REPOSITORY_ID_CLASS_NAME.name(), Long.class.getSimpleName()),
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME.name(), getBasePackage() + ".service"))))
                .stage(InternalProcessingStage.CREATE_REPOSITORY)
                .stage(InternalProcessingStage.CREATE_ABSTRACT_SERVICE)
                .stage(InternalProcessingStage.INJECT_SERVICE_INTO_CONTROLLER.processingUnitBasePackage(getControllerPackage()))
                .stage(InternalProcessingStage.CREATE_SERVICE_IMPLEMENTATION)
                .stage(InternalProcessingStage.ADD_SERVICE_ABSTRACT_METHOD)
                .stage(InternalProcessingStage.ADD_SERVICE_METHOD_IMPLEMENTATION)
                .stage(InternalProcessingStage.EDIT_CONFIGURATION.processingUnitBasePackage(getConfigurationPackage()))
                .stage(InternalProcessingStage.ADD_CONTROLLER_METHOD_IMPLEMENTATION.processingUnitBasePackage(getControllerPackage()));
    }

    private ProcessingPlan externalProcessingPlan() {
        var transformationToProcessingStageMapper = new ProcessingStageMapper();
        return ProcessingPlan
                .processingPlan()
                .stages(
                        getTransformations()
                                .stream()
                                .map(transformationToProcessingStageMapper::fromTransformation)
                                .collect(Collectors.toUnmodifiableList()));
    }

    private ProcessingConfiguration internalProcessingConfiguration() {
        return DefaultProcessingConfiguration
                .configuration()
                .baseLocation(baseLocation())
                .processingPlan(internalProcessingPlan())
                .processingStrategy(new PipelineIdBasedProcessingStrategy())
                .namingStrategy(new PipelineIdBasedNamingStrategy());
    }

    private ProcessingConfiguration externalProcessingConfiguration() {
        return DefaultProcessingConfiguration
                .configuration()
                .baseLocation(baseLocation())
                .processingPlan(externalProcessingPlan())
                .processingStrategy(new SequentialProcessingStrategy())
                .namingStrategy(new PipelineIdBasedNamingStrategy());
    }

    /**
     * Generate microservice stub by OpenAPI/Swagger Specification
     *
     * Produces SpringApplication class with configuration, Controller, Model classes
     */
    private void generateStub() {
        Generate generate = new Generate();
        generate.setSpec(getInputSpec());
        generate.setLang("spring");
        generate.setModelPackage(getBasePackage() + "." + getModelPackage());
        generate.setInvokerPackage(getBasePackage() + "." + getConfigurationPackage());
        generate.setApiPackage(getBasePackage() + "." + getControllerPackage());
        generate.setOutput(getProjectOutputDirectory());
        generate.setArtifactId(getProject().getArtifactId());
        generate.setGroupId(getProject().getGroupId());
        generate.setLibrary("spring-boot");

        List<String> props = new ArrayList<>();
        props.add("defaultInterfaces=true");
        props.add("library=spring-boot");
        props.add("java8=true");
        props.add("dateLibrary=java8");
        props.add("jackson=true");
        generate.setAdditionalProperties(props);
        generate.run();
    }

    private String baseLocation() {
        return Path.of(
                getProjectOutputDirectory(),
                getSourcesDirectory(),
                getBasePackage()).toString();
    }

    @Override
    public void execute() throws MojoFailureException {
        try (DBClient.INSTANCE) {
            generateStub();

            new ProcessingContainer(internalProcessingConfiguration(), externalProcessingConfiguration())
                    .prepare(this)
                    .start();
        }
    }
}
