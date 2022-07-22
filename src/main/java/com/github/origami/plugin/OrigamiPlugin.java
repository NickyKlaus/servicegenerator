package com.github.origami.plugin;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.origami.plugin.processing.ProcessingUnit;
import com.github.origami.plugin.processing.configuration.DefaultProcessingConfiguration;
import com.github.origami.plugin.processing.configuration.ProcessingConfiguration;
import com.github.origami.plugin.processing.configuration.context.properties.Storage;
import com.github.origami.plugin.utils.FileUtils;
import com.github.origami.plugin.utils.MethodNormalizer;
import com.github.origami.plugin.utils.NormalizerUtils;
import com.github.origami.plugin.db.DBClient;
import com.github.origami.api.ASTProcessingSchema;
import com.github.origami.plugin.processing.configuration.stages.ProcessingPlan;
import com.github.origami.plugin.processing.configuration.stages.ProcessingStage;
import com.github.origami.plugin.processing.configuration.strategy.processing.SequentialProcessingStrategy;
import com.github.origami.plugin.processing.container.ProcessingContainer;
import com.github.origami.plugin.processing.configuration.context.properties.PropertyName;
import com.github.origami.plugin.processing.configuration.stages.InternalProcessingStage;
import com.github.origami.plugin.processing.configuration.strategy.matchmethod.MatchWithRestEndpointMethodStrategy;
import com.github.origami.plugin.processing.configuration.strategy.matchmethod.MatchingMethodStrategy;
import com.github.origami.plugin.processing.configuration.strategy.naming.PipelineIdBasedNamingStrategy;
import com.github.origami.plugin.processing.configuration.strategy.processing.PipelineIdBasedProcessingStrategy;

import io.swagger.codegen.v3.cli.cmd.Generate;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.origami.plugin.processing.configuration.context.properties.PropertyName.*;
import static java.lang.String.format;

/**
 * Goal which generates microservice based on declared logic.
 */
@Mojo(name = "generate-service", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class OrigamiPlugin extends AbstractServiceGeneratorMojo {
    private static final String CONTEXT_PREFERENCE_IS_NOT_SET_ERROR_MESSAGE = "%s is not set";

    private ProcessingPlan processingPlan() {
        return ProcessingPlan
                .processingPlan()
                .stage(
                        InternalProcessingStage.CREATE_REPOSITORY
                                .setSourceLocation(
                                        (ctx) ->
                                                //TODO: default base package, preset component package, naming strategy
                                                FileUtils.createFilePath(
                                                        getProjectOutputDirectory(),
                                                        getSourcesDirectory(),
                                                        getBasePackage(),
                                                        "repository",
                                                        ctx.get(PIPELINE_ID.name(), Name.class).getIdentifier() + "Repository").toString())
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.DB_TYPE.name(),
                                                        getDbType()),
                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".repository"),
                                                Map.entry(PropertyName.REPOSITORY_ID_CLASS_NAME.name(),
                                                        Long.class.getSimpleName())))
                                .postProcessingAction(
                                        ctx ->
                                                ctx.getProperties()
                                                        .put(
                                                                ABSTRACT_SERVICE_METHOD_DECLARATION.name(),
                                                                getMethodMatchedWithPipeline(
                                                                        ctx.get(PIPELINE.name(), MethodDeclaration.class),
                                                                        ctx.get(DB_TYPE.name(), Storage.DbType.class)
                                                                                .getRepositoryImplementationMethodDeclarations(),
                                                                        ctx.get(PIPELINE_ID.name(), Name.class),
                                                                        new MatchWithRestEndpointMethodStrategy()
                                                                ).orElseThrow(
                                                                        () -> new IllegalArgumentException(
                                                                                format(
                                                                                        CONTEXT_PREFERENCE_IS_NOT_SET_ERROR_MESSAGE,
                                                                                        ABSTRACT_SERVICE_METHOD_DECLARATION.name()))))))
                .stage(
                        InternalProcessingStage.CREATE_ABSTRACT_SERVICE
                                .setSourceLocation(
                                        (ctx) ->
                                                FileUtils.createFilePath(
                                                        getProjectOutputDirectory(),
                                                        getSourcesDirectory(),
                                                        getBasePackage(),
                                                        "service",
                                                        ctx.get(PIPELINE_ID.name(), Name.class).getIdentifier() + "Service"
                                                ).toString())
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".service"),
                                                Map.entry(PropertyName.DB_TYPE.name(),
                                                        getDbType()))))
                .stage(
                        InternalProcessingStage.INJECT_SERVICE_INTO_CONTROLLER
                                .setComponentPackage(getControllerPackage())
                                .setSourceLocation(
                                        ctx -> ctx.get(PropertyName.CONTROLLER_UNIT.name(), ProcessingUnit.class).getId())
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".service"))))
                .stage(
                        InternalProcessingStage.CREATE_SERVICE_IMPLEMENTATION
                                .setSourceLocation(
                                        (ctx) ->
                                                FileUtils.createFilePath(
                                                        getProjectOutputDirectory(),
                                                        getSourcesDirectory(),
                                                        getBasePackage(),
                                                        "service.impl",
                                                        ctx.get(PIPELINE_ID.name(), Name.class).getIdentifier() + "ServiceImpl").toString())
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".service"),
                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".repository"),
                                                Map.entry(PropertyName.DB_TYPE.name(),
                                                        getDbType()))))
                .stage(
                        InternalProcessingStage.ADD_SERVICE_ABSTRACT_METHOD
                                .setSourceLocation(
                                        (ctx) ->
                                                FileUtils.createFilePath(
                                                        getProjectOutputDirectory(),
                                                        getSourcesDirectory(),
                                                        getBasePackage(),
                                                        "service",
                                                        ctx.get(PIPELINE_ID.name(), Name.class).getIdentifier() + "Service").toString()))
                .stage(
                        InternalProcessingStage.ADD_SERVICE_METHOD_IMPLEMENTATION
                                .setSourceLocation(
                                        (ctx) ->
                                                FileUtils.createFilePath(
                                                        getProjectOutputDirectory(),
                                                        getSourcesDirectory(),
                                                        getBasePackage(),
                                                        "service.impl",
                                                        ctx.get(PIPELINE_ID.name(), Name.class).getIdentifier() + "ServiceImpl").toString())
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".repository"))))
                .stage(
                        InternalProcessingStage.EDIT_CONFIGURATION
                                .setComponentPackage(getConfigurationPackage())
                                .setSourceLocation(
                                        (ctx) ->
                                                FileUtils.createFilePath(
                                                        getProjectOutputDirectory(),
                                                        getSourcesDirectory(),
                                                        getBasePackage(),
                                                        getConfigurationPackage(),
                                                        "Swagger2SpringBoot").toString())
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.BASE_PACKAGE.name(),
                                                        getBasePackage()),
                                                Map.entry(PropertyName.DB_TYPE.name(),
                                                        getDbType()),
                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".repository"))))
                .stage(
                        InternalProcessingStage.ADD_CONTROLLER_METHOD_IMPLEMENTATION
                                .setSourceLocation((ctx) ->
                                        ctx.get(PropertyName.CONTROLLER_UNIT.name(), ProcessingUnit.class).getId()));
    }

    private ProcessingConfiguration internalProcessingConfiguration() {
        return DefaultProcessingConfiguration
                .configuration()
                .processingPlan(processingPlan())
                .processingStrategy(new PipelineIdBasedProcessingStrategy())
                .namingStrategy(new PipelineIdBasedNamingStrategy());
    }

    private ProcessingConfiguration externalProcessingConfiguration() throws MojoFailureException {
        var externalProcessingPlan = ProcessingPlan.processingPlan();

        for (var transformation : getTransformations()) {
            var sourceClassPath = Optional.<Path>empty();
            var targetClassPath = Optional.<Path>empty();

            if (StringUtils.isNoneEmpty(transformation.getSourceClassPackage()) &&
                    StringUtils.isNoneEmpty(transformation.getSourceClassName())) {
                sourceClassPath =
                        Optional.of(
                                FileUtils.createFilePath(
                                        StringUtils.isEmpty(transformation.getSourceDirectory()) ? getProjectBaseDirectory() : transformation.getSourceDirectory(),
                                        StringUtils.isEmpty(transformation.getSourceDirectory()) ? getSourcesDirectory() : "",
                                        "",
                                        transformation.getSourceClassPackage(),
                                        transformation.getSourceClassName()));
            }

            if (sourceClassPath.isPresent()) {
                if (transformation.getSourceClassPackage().equals(transformation.getTargetClassPackage()) &&
                        transformation.getSourceClassName().equals(transformation.getTargetClassName())) {
                    targetClassPath = sourceClassPath;
                } else {
                    targetClassPath = Optional.of(
                            FileUtils.createFilePath(
                                    getProjectOutputDirectory(),
                                    getSourcesDirectory(),
                                    "",
                                    transformation.getTargetClassPackage(),
                                    transformation.getTargetClassName()));
                }
            } else if (StringUtils.isNoneEmpty(transformation.getTargetClassPackage()) &&
                    StringUtils.isNoneEmpty(transformation.getTargetClassName())) {
                targetClassPath = Optional.of(
                        FileUtils.createFilePath(
                                StringUtils.isEmpty(transformation.getTargetDirectory()) ? getProjectOutputDirectory() : transformation.getTargetDirectory(),
                                StringUtils.isEmpty(transformation.getTargetDirectory()) ? getSourcesDirectory() : "",
                                "",
                                transformation.getTargetClassPackage(),
                                transformation.getTargetClassName()));
            } else {
                throw new MojoFailureException("Cannot find target path for generated classes");
            }

            try (var classLoader = URLClassLoader.newInstance(new URL[]{transformation.getProcessingSchemaLocation().toURI().toURL()}, getClass().getClassLoader())) {
                var processingSchemaInstance =
                        classLoader
                                .loadClass(transformation.getProcessingSchemaClass())
                                .getDeclaredConstructor()
                                .newInstance();

                if (processingSchemaInstance instanceof ASTProcessingSchema) {
                    var externalStage = ProcessingStage
                            .of((ASTProcessingSchema)processingSchemaInstance)
                            .setSourceLocation(targetClassPath.get().toString())
                            .setProcessingData(
                                    transformation
                                            .getTransformationProperties()
                                            .stream()
                                            .collect(Collectors.toUnmodifiableMap(
                                                    TransformationProperty::getName,
                                                    TransformationProperty::getValue,
                                                    (s, a) -> s)));

                    externalProcessingPlan.stage(externalStage);
                } else {
                    throw new MojoFailureException("Invalid processing schema found: " + processingSchemaInstance);
                }
            } catch (IOException | ClassNotFoundException | ClassCastException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException | InvocationTargetException e) {
                getLog().error("Cannot create generated class into " + targetClassPath.get(), e);
                throw new MojoFailureException("Cannot create generated class into " + targetClassPath.get(), e);
            }
        }

        return DefaultProcessingConfiguration
                .configuration()
                .processingPlan(externalProcessingPlan)
                .processingStrategy(new SequentialProcessingStrategy())
                .namingStrategy(new PipelineIdBasedNamingStrategy());
    }

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

    private Optional<MethodDeclaration> getMethodMatchedWithPipeline(
            final MethodDeclaration pipeline,
            final List<MethodDeclaration> checkedMethods,
            final Name pipelineId,
            final MatchingMethodStrategy matchMethodStrategy
    ) {
        return checkedMethods
                .stream()
                .filter(checkedMethod -> matchMethodStrategy
                        .matchMethods(pipelineId.getIdentifier())
                        .test(pipeline, checkedMethod))
                .map(m -> MethodNormalizer.denormalize(m, pipelineId.getIdentifier(), NormalizerUtils.REPLACING_MODEL_TYPE_SYMBOL))
                .findFirst();
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
