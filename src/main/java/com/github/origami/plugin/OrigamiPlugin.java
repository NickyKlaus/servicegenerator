package com.github.origami.plugin;

import com.github.javaparser.ast.expr.Name;
import com.github.origami.plugin.processing.configuration.DefaultProcessingConfiguration;
import com.github.origami.plugin.processing.configuration.ProcessingConfiguration;
import com.github.origami.plugin.processing.configuration.stages.ProcessingStageMapper;
import com.github.origami.plugin.processing.configuration.stages.ProcessingPlan;
import com.github.origami.plugin.processing.configuration.strategy.processing.SequentialProcessingStrategy;
import com.github.origami.plugin.processing.container.ProcessingContainer;
import com.github.origami.plugin.processing.configuration.context.properties.PropertyName;
import com.github.origami.plugin.processing.configuration.strategy.naming.PipelineIdBasedNamingStrategy;
import com.github.origami.plugin.processing.configuration.strategy.processing.PipelineIdBasedProcessingStrategy;

import io.swagger.codegen.v3.cli.cmd.Generate;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage.ADD_CONTROLLER_METHOD_IMPLEMENTATION;
import static com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage.ADD_SERVICE_ABSTRACT_METHOD;
import static com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage.ADD_SERVICE_METHOD_IMPLEMENTATION;
import static com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage.CREATE_ABSTRACT_SERVICE;
import static com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage.CREATE_REPOSITORY;
import static com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage.CREATE_SERVICE_IMPLEMENTATION;
import static com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage.EDIT_CONFIGURATION;
import static com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage.INITIALIZE_PROCESSING_CONTEXT;
import static com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage.INJECT_SERVICE_INTO_CONTROLLER;

/**
 * <b>Origami</b> is a Maven plugin which allows of generation Java RESTful microservices using its
 * description in OpenAPI (Swagger) Specification format from standalone JSON file or being produced by any remote service description provider.
 *
 * @author NickyKlaus
 */
@Mojo(name = "generate-service", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class OrigamiPlugin extends AbstractServiceGeneratorMojo {
    /**
     * Declares a sequence of preconfigured internal processing stages and describes a predefined generating source code of microservice
     *
     * @see com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage
     */
    private ProcessingPlan internalProcessingPlan() {
        return ProcessingPlan
                .processingPlan()
                .stage(INITIALIZE_PROCESSING_CONTEXT
                        .context(Map.ofEntries(
                                Map.entry(PropertyName.BASE_PACKAGE.name(), getBasePackage()),
                                Map.entry(PropertyName.DB_TYPE.name(), getDbType()),
                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(), getBasePackage() + ".repository"),
                                Map.entry(PropertyName.REPOSITORY_ID_CLASS_NAME.name(), Long.class.getSimpleName()),
                                Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME.name(), getBasePackage() + ".service"))))
                .stage(CREATE_REPOSITORY)
                .stage(CREATE_ABSTRACT_SERVICE)
                .stage(INJECT_SERVICE_INTO_CONTROLLER
                        .processingUnitBasePackage(getControllerPackage())
                        .processingUnitName(ctx -> ctx.get(PropertyName.CONTROLLER_UNIT_NAME.name(), Name.class).getIdentifier()))
                .stage(CREATE_SERVICE_IMPLEMENTATION)
                .stage(ADD_SERVICE_ABSTRACT_METHOD)
                .stage(ADD_SERVICE_METHOD_IMPLEMENTATION)
                .stage(EDIT_CONFIGURATION
                        .processingUnitBasePackage(getConfigurationPackage())
                        .processingUnitName("Swagger2SpringBoot"))
                .stage(ADD_CONTROLLER_METHOD_IMPLEMENTATION
                        .processingUnitBasePackage(getControllerPackage())
                        .processingUnitName(ctx -> ctx.get(PropertyName.CONTROLLER_UNIT_NAME.name(), Name.class).getIdentifier()));
    }

    /**
     * Declares a sequence of external processing stages and describes user-defined source code changes
     *
     * @see com.github.origami.plugin.processing.configuration.stages.ProcessingStage
     */
    private ProcessingPlan externalProcessingPlan() {
        var transformationToProcessingStageMapper = new ProcessingStageMapper();
        return ProcessingPlan
                .processingPlan()
                .stages(getTransformations()
                        .stream()
                        .map(transformationToProcessingStageMapper::fromTransformation)
                        .collect(Collectors.toUnmodifiableList()));
    }

    /**
     * Internal processing configuration customizes a common source code generation process
     *
     * @see PipelineIdBasedProcessingStrategy
     * @see PipelineIdBasedNamingStrategy
     */
    private ProcessingConfiguration internalProcessingConfiguration() {
        return DefaultProcessingConfiguration
                .configuration()
                .baseLocation(Path.of(
                        getProjectOutputDirectory(),
                        getSourcesDirectory(),
                        getBasePackage()).toString())
                .processingPlan(internalProcessingPlan())
                .processingStrategy(new PipelineIdBasedProcessingStrategy())
                .namingStrategy(new PipelineIdBasedNamingStrategy());
    }

    /**
     * External processing configuration customizes an user-defined source code generation process
     *
     * @see SequentialProcessingStrategy
     * @see PipelineIdBasedNamingStrategy
     */
    private ProcessingConfiguration externalProcessingConfiguration() {
        return DefaultProcessingConfiguration
                .configuration()
                .baseLocation(Path.of(
                        getProjectOutputDirectory(),
                        getSourcesDirectory()).toString())
                .processingPlan(externalProcessingPlan())
                .processingStrategy(new SequentialProcessingStrategy())
                .namingStrategy(new PipelineIdBasedNamingStrategy());
    }

    /**
     * Generates Spring Boot microservice stub by OpenAPI/Swagger Specification
     * Produces common templates of configuration, controller and model classes
     *
     * @see Generate
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

    @Override
    public void execute() throws MojoFailureException {
        generateStub();

        ProcessingContainer
                .container(internalProcessingConfiguration(), externalProcessingConfiguration())
                .prepare(this)
                .start();
    }
}
