package com.home.servicegenerator.plugin;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.plugin.processing.configuration.DefaultProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.ProcessingConfiguration;
import com.home.servicegenerator.plugin.processing.configuration.stages.ProcessingPlan;
import com.home.servicegenerator.plugin.processing.container.ProcessingContainer;
import com.home.servicegenerator.plugin.processing.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.context.properties.PropertyName;
import com.home.servicegenerator.plugin.processing.configuration.stages.InnerProcessingStage;
import com.home.servicegenerator.plugin.processing.processor.MatchWithRestEndpointMethodStrategy;
import com.home.servicegenerator.plugin.processing.processor.MatchingMethodStrategy;
import com.home.servicegenerator.plugin.processing.strategy.PipelineIdBasedNamingStrategy;
import com.home.servicegenerator.plugin.processing.strategy.PipelineIdBasedProcessingStrategy;
import com.home.servicegenerator.plugin.utils.MethodNormalizer;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.home.servicegenerator.plugin.utils.FileUtils.createFilePath;
import static com.home.servicegenerator.plugin.utils.NormalizerUtils.REPLACING_MODEL_TYPE_SYMBOL;

/**
 * Goal which generates microservice based on declared logic.
 */
@Mojo(name = "service-generator", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ServiceGeneratorPlugin extends AbstractServiceGeneratorMojo {
    private static final String POM_XML = "pom.xml";
    private static final String POM_XML_BACKUP = "pom.xml.bak";
    private final ProcessingContainer processingContainer;

    public ProcessingConfiguration processingConfiguration() {
        return DefaultProcessingConfiguration
                .configuration()
                .processingPlan(processingPlan())
                .processingStrategy(
                        new PipelineIdBasedProcessingStrategy(
                                PluginConfigurationMapper.toPluginConfiguration(this)))
                .namingStrategy(new PipelineIdBasedNamingStrategy());
    }

    public ProcessingPlan processingPlan() {
        return ProcessingPlan
                .processingPlan()
                .stage(
                        InnerProcessingStage.CREATE_REPOSITORY
                                /*.setSourceLocation(
                                        (ctx) -> {
                                            var pipelineId = ctx.getExtendedState().get("pipelineId", Name.class);
                                            if (pipelineId != null) {
                                                return createFilePath(
                                                        getProjectOutputDirectory().toString(),
                                                        getSourcesDirectory().toString(),
                                                        getBasePackage(),
                                                        "repository",
                                                        pipelineId.getIdentifier() + "Repository").toString();
                                            }
                                            return null;
                                        })*/
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.DB_TYPE.name(),
                                                        getDbType()),
                                                            /*Map.entry(PropertyName.REPOSITORY_NAME,
                                                                    pipelineId.getIdentifier() + "Repository"),*/
                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".repository"),
                                                Map.entry(PropertyName.REPOSITORY_ID_CLASS_NAME.name(),
                                                        Long.class.getSimpleName()))))
                .stage(
                        InnerProcessingStage.CREATE_ABSTRACT_SERVICE
                                /*.setSourceLocation(
                                        (ctx) -> {
                                            var pipelineId = ctx.getExtendedState().get("pipelineId", Name.class);
                                            if (pipelineId != null) {
                                                return createFilePath(
                                                        getProjectOutputDirectory().toString(),
                                                        getSourcesDirectory().toString(),
                                                        getBasePackage(),
                                                        "service",
                                                        pipelineId.getIdentifier() + "Service").toString();
                                            }
                                            return null;
                                        })*/
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".service"),
                                                /*Map.entry(PropertyName.ABSTRACT_SERVICE_NAME,
                                                        pipelineId.getIdentifier() + "Service"),*/
                                                Map.entry(PropertyName.DB_TYPE.name(),
                                                        getDbType()))))
                .stage(
                        InnerProcessingStage.INJECT_SERVICE_INTO_CONTROLLER
                                /*.setSourceLocation(
                                        (ctx) -> {
                                            var pipelineId = ctx.getExtendedState().get("pipelineId", Name.class);
                                            if (pipelineId != null) {
                                                return createFilePath(
                                                        getProjectOutputDirectory().toString(),
                                                        getSourcesDirectory().toString(),
                                                        getBasePackage(),
                                                        getControllerPackage(),
                                                        pipelineId.getIdentifier() + "Repository").toString();
                                            }
                                            return null;
                                        })*/
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".service")/*,
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_NAME,
                                                        pipelineId.getIdentifier() + "Service")*/)))
                .stage(
                        InnerProcessingStage.CREATE_SERVICE_IMPLEMENTATION
                                /*.setSourceLocation(
                                        (ctx) -> {
                                            var pipelineId = ctx.getExtendedState().get("pipelineId", Name.class);
                                            if (pipelineId != null) {
                                                return createFilePath(
                                                        getProjectOutputDirectory().toString(),
                                                        getSourcesDirectory().toString(),
                                                        getBasePackage(),
                                                        "service",
                                                        pipelineId.getIdentifier() + "ServiceImpl").toString();
                                            }
                                            return null;
                                        })*/
                                .setProcessingData(
                                        Map.ofEntries(
                                                        Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME.name(),
                                                                getBasePackage() + ".service"),
                                                       /* Map.entry(PropertyName.ABSTRACT_SERVICE_NAME,
                                                                pipelineId.getIdentifier() + "Service"),*/
                                                        /*Map.entry(PropertyName.REPOSITORY_NAME,
                                                                pipelineId.getIdentifier() + "Repository"),*/
                                                        Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(),
                                                                getBasePackage() + ".repository"),
                                                        Map.entry(PropertyName.DB_TYPE.name(),
                                                                getDbType()
                                ))))
                .stage(
                        InnerProcessingStage.ADD_SERVICE_ABSTRACT_METHOD
                                /*.setSourceLocation(
                                        (ctx) -> {
                                            var pipelineId = ctx.getExtendedState().get("pipelineId", Name.class);
                                            if (pipelineId != null) {
                                                return createFilePath(
                                                        getProjectOutputDirectory().toString(),
                                                        getSourcesDirectory().toString(),
                                                        getBasePackage(),
                                                        "service",
                                                        pipelineId.getIdentifier() + "Service").toString();
                                            }
                                            return null;
                                        })*/
                                /*.setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION.name(),
                                                        abstractServiceMethodDeclaration.get())
                                )*/)
                .stage(
                        InnerProcessingStage.ADD_SERVICE_METHOD_IMPLEMENTATION
                                /*.setSourceLocation(
                                        (ctx) -> {
                                            var pipelineId = ctx.getExtendedState().get("pipelineId", Name.class);
                                            if (pipelineId != null) {
                                                return createFilePath(
                                                        getProjectOutputDirectory().toString(),
                                                        getSourcesDirectory().toString(),
                                                        getBasePackage(),
                                                        "service",
                                                        pipelineId.getIdentifier() + "ServiceImpl").toString();
                                            }
                                            return null;
                                        }
                                )*/
                                .setProcessingData(
                                        Map.ofEntries(
                                                /*Map.entry(PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION,
                                                        abstractServiceMethodDeclaration.get()),*/
                                                /*Map.entry(PropertyName.REPOSITORY_METHOD_DECLARATION,
                                                        abstractServiceMethodDeclaration.get()),*/
                                                /*Map.entry(PropertyName.REPOSITORY_NAME,
                                                        pipelineId.getIdentifier() + "Repository"),*/
                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".repository"))
                                )
                                /*.setContext(
                                        (ctx) -> {
                                            var pipelineId = ctx.getExtendedState().get("pipelineId", Name.class);
                                            var pipeline = ctx.getExtendedState().get("pipeline", MethodDeclaration.class);
                                            var abstractServiceMethodDeclaration =
                                                    getMethodMatchedWithPipeline(
                                                            pipeline,
                                                            getDbType().getRepositoryImplementationMethodDeclarations(),
                                                            pipelineId,
                                                            new MatchWithRestEndpointMethodStrategy());
                                            if (pipelineId != null && pipeline != null && abstractServiceMethodDeclaration.isPresent()) {
                                                return new ProcessingContext(
                                                        pipelineId,
                                                        pipeline,
                                                        Map.ofEntries(
                                                                Map.entry(PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION,
                                                                        abstractServiceMethodDeclaration.get()),
                                                                Map.entry(PropertyName.REPOSITORY_METHOD_DECLARATION,
                                                                        abstractServiceMethodDeclaration.get()),
                                                                Map.entry(PropertyName.REPOSITORY_NAME,
                                                                        pipelineId.getIdentifier() + "Repository"),
                                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME,
                                                                        getBasePackage() + ".repository")));
                                            }
                                            return null;
                                        })*/)
                .stage(
                        InnerProcessingStage.EDIT_CONFIGURATION
                                /*.setSourceLocation(
                                        (ctx) -> {
                                            var configurationClass =
                                                    ctx.getExtendedState().get("configurationClass", String.class);
                                            if (configurationClass != null) {
                                                return createFilePath(
                                                        getProjectOutputDirectory().toString(),
                                                        getSourcesDirectory().toString(),
                                                        getBasePackage(),
                                                        getConfigurationPackage(),
                                                        configurationClass).toString();
                                            }
                                            return null;
                                        }
                                )*/
                                .setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.DB_TYPE.name(),
                                                        getDbType()),
                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(),
                                                        getBasePackage() + ".repository"))
                                ))
                .stage(
                        InnerProcessingStage.ADD_CONTROLLER_METHOD_IMPLEMENTATION
                                /*.setSourceLocation(
                                        (ctx) -> {
                                            var pipelineId = ctx.getExtendedState().get("pipelineId", Name.class);
                                            if (pipelineId != null) {
                                                return createFilePath(
                                                        getProjectOutputDirectory().toString(),
                                                        getSourcesDirectory().toString(),
                                                        getBasePackage(),
                                                        getControllerPackage(),
                                                        pipelineId.getIdentifier() + "Repository").toString();
                                            }
                                            return null;
                                        }
                                )*/
                                /*.setProcessingData(
                                        Map.ofEntries(
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_NAME,
                                                        pipelineId.getIdentifier() + "Service"),
                                                Map.entry(PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION,
                                                        abstractServiceMethodDeclaration.get())
                                )*/
                                /*.setContext(
                                        (ctx) -> {
                                            var pipelineId = ctx.getExtendedState().get("pipelineId", Name.class);
                                            var pipeline = ctx.getExtendedState().get("pipeline", MethodDeclaration.class);
                                            var abstractServiceMethodDeclaration =
                                                    getMethodMatchedWithPipeline(
                                                            pipeline,
                                                            getDbType().getRepositoryImplementationMethodDeclarations(),
                                                            pipelineId,
                                                            new MatchWithRestEndpointMethodStrategy());
                                            if (pipelineId != null && pipeline != null && abstractServiceMethodDeclaration.isPresent()) {
                                                return new ProcessingContext(
                                                        pipelineId,
                                                        pipeline,
                                                        ));
                                            }
                                            return null;
                                        })*/
                );
    }

    public ServiceGeneratorPlugin(ProcessingContainer processingContainer) {
        this.processingContainer = new ProcessingContainer(processingConfiguration());
    }

    private static Optional<MethodDeclaration> getMethodMatchedWithPipeline(
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
                .map(m -> MethodNormalizer.denormalize(m, pipelineId.getIdentifier(), REPLACING_MODEL_TYPE_SYMBOL))
                .findFirst();
    }

    private void save(final CompilationUnit compilationUnit) throws MojoFailureException {
        if (compilationUnit.getStorage().isEmpty() || Objects.isNull(compilationUnit.getStorage().get().getPath()) ||
                !Files.exists(compilationUnit.getStorage().get().getPath())) {
            getLog().error("Cannot save generated class " + compilationUnit + ". There is no target path.");
            throw new MojoFailureException("Cannot save generated class " + compilationUnit + ". There is no target path.");
        }
        if (compilationUnit.getPackageDeclaration().isPresent() && compilationUnit.getPrimaryType().isPresent()) {
            compilationUnit
                    .getStorage()
                    .orElseThrow(() -> new MojoFailureException("Cannot write generated class " + compilationUnit))
                    .save();
        }
    }

    //TODO: unify saving procedure for inner/outer schemas (rewrite/create)
    private void save(CompilationUnit targetCompilationUnit, Path targetLocation) throws MojoFailureException {
        var targetCharset =
                targetCompilationUnit.getStorage().isPresent() ?
                        targetCompilationUnit.getStorage().get().getEncoding() :
                        Charset.defaultCharset();
        try {
            var targetPath = Files.writeString(
                    targetLocation,
                    targetCompilationUnit.toString(),
                    targetCharset,
                    StandardOpenOption.CREATE);
            targetCompilationUnit
                    .setStorage(targetLocation, targetCharset)
                    .getStorage()
                    .orElseThrow(() -> new MojoFailureException("Cannot write generated class into " + targetLocation.getFileName()))
                    .save();
            getLog().info("Save generated class into " + targetPath);
        } catch (IOException e) {
            getLog().error("Cannot write generated class into " + targetLocation.getFileName(), e);
            throw new MojoFailureException("Cannot write generated class into " + targetLocation.getFileName(), e);
        }
    }

   /* private void executeInnerTransformations() throws MojoFailureException {
        try {
            var storageType = getDbType();

            var _indexPipelineIdToRepositoryUnit = new HashMap<String, CompilationUnit>();
            // Global index of inner generated abstract services
            var _indexPipelineIdToAbstractServiceUnit = new HashMap<String, CompilationUnit>();
            // Global index of inner generated services
            var _indexPipelineIdToServiceImplementationUnit = new HashMap<String, CompilationUnit>();

            for (CompilationUnit controllerUnit : controllerUnits) {
                for (var pipeline : restEndpoints) {
                    var pipelineIdResolveResult =
                            ResolverUtils.lookupPipelineId(pipeline, availableModelsNames);
                    if (pipelineIdResolveResult.isPresent()) {
                        // A fully qualified name of an available model class that the pipeline deals with
                        var pipelineId = pipelineIdResolveResult.get();

                        // 1. Create repository phase
                        if (!_indexPipelineIdToRepositoryUnit.containsKey(pipelineId.toString())) {
                            var repositoryUnit = InnerProcessingStage.CREATE_REPOSITORY
                                    .setCompilationUnit(
                                            emptyUnit.apply(
                                                    createFilePath(
                                                            getProjectOutputDirectory().toString(),
                                                            getSourcesDirectory().toString(),
                                                            getBasePackage(),
                                                            "repository",
                                                            pipelineId.getIdentifier() + "Repository")))
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(PropertyName.DB_TYPE,
                                                                    storageType),
                                                            Map.entry(PropertyName.REPOSITORY_NAME,
                                                                    pipelineId.getIdentifier() + "Repository"),
                                                            Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME,
                                                                    getBasePackage() + ".repository"),
                                                            Map.entry(PropertyName.REPOSITORY_ID_CLASS_NAME,
                                                                    Long.class.getSimpleName()))))
                                    .process();
                            save(repositoryUnit);
                            //TODO: add to save operation
                            _indexPipelineIdToRepositoryUnit.put(pipelineId.toString(), repositoryUnit);
                        }

                        // 2. Create abstract service phase and inject service into controller phase
                        if (!_indexPipelineIdToAbstractServiceUnit.containsKey(pipelineId.toString())) {
                            var abstractServiceUnit = InnerProcessingStage.CREATE_ABSTRACT_SERVICE
                                    .setCompilationUnit(
                                            new CompilationUnit()
                                                    .setStorage(
                                                            createFilePath(
                                                                    getProjectOutputDirectory().toString(),
                                                                    getSourcesDirectory().toString(),
                                                                    getBasePackage(),
                                                                    "service",
                                                                    pipelineId.getIdentifier() + "Service")
                                                    ))
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME,
                                                                    getBasePackage() + ".service"),
                                                            Map.entry(PropertyName.ABSTRACT_SERVICE_NAME,
                                                                    pipelineId.getIdentifier() + "Service"),
                                                            Map.entry(PropertyName.DB_TYPE,
                                                                    storageType))))
                                    .process();
                            save(abstractServiceUnit,
                                    );
                            _indexPipelineIdToAbstractServiceUnit.put(pipelineId.toString(), abstractServiceUnit);

                            var editedControllerUnit = InnerProcessingStage.INJECT_SERVICE_INTO_CONTROLLER
                                    .setCompilationUnit(controllerUnit)
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME,
                                                                    getBasePackage() + ".service"),
                                                            Map.entry(PropertyName.ABSTRACT_SERVICE_NAME,
                                                                    pipelineId.getIdentifier() + "Service"))))
                                    .process();
                            save(editedControllerUnit,
                                    createFilePath(
                                            getProjectOutputDirectory().toString(),
                                            getSourcesDirectory().toString(),
                                            getBasePackage(),
                                            getControllerPackage(),
                                    FilenameUtils.removeExtension(controllerUnit.getStorage().get().getFileName())));
                        }

                        // 3. Create service implementation phase
                        if (!_indexPipelineIdToServiceImplementationUnit.containsKey(pipelineId.toString())) {
                            var serviceUnit = InnerProcessingStage.CREATE_SERVICE_IMPLEMENTATION
                                    .setCompilationUnit(new CompilationUnit())
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME,
                                                                    getBasePackage() + ".service"),
                                                            Map.entry(PropertyName.ABSTRACT_SERVICE_NAME,
                                                                    pipelineId.getIdentifier() + "Service"),
                                                            Map.entry(PropertyName.REPOSITORY_NAME,
                                                                    pipelineId.getIdentifier() + "Repository"),
                                                            Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME,
                                                                    getBasePackage() + ".repository"),
                                                            Map.entry(PropertyName.DB_TYPE,
                                                                    storageType))))
                                    .process();
                            save(serviceUnit,
                                    createFilePath(
                                            getProjectOutputDirectory().toString(),
                                            getSourcesDirectory().toString(),
                                            getBasePackage(),
                                            "service.impl",
                                            pipelineId.getIdentifier() + "ServiceImpl"));
                            _indexPipelineIdToServiceImplementationUnit.put(pipelineId.toString(), serviceUnit);
                        }

                        // 4. Add service abstract method phase
                        var abstractServiceMethodDeclaration = Optional.<MethodDeclaration>empty();

                        if (_indexPipelineIdToAbstractServiceUnit.containsKey(pipelineId.toString())) {
                            abstractServiceMethodDeclaration =
                                    getMethodMatchedWithPipeline(
                                            pipeline,
                                            storageType.getRepositoryImplementationMethodDeclarations(),
                                            pipelineId,
                                            new MatchWithRestEndpointMethodStrategy());

                            if (abstractServiceMethodDeclaration.isPresent()) {
                                var abstractServiceUnit = InnerProcessingStage.ADD_SERVICE_ABSTRACT_METHOD
                                        .setCompilationUnit(_indexPipelineIdToAbstractServiceUnit.get(pipelineId.toString()))
                                        .setContext(
                                                new ProcessingContext(
                                                        pipelineId,
                                                        //resolve method type and signature: signature is from controller, type is from repository
                                                        pipeline,
                                                        Map.ofEntries(
                                                                Map.entry(PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION,
                                                                        abstractServiceMethodDeclaration.get()))))
                                        .process();
                                save(abstractServiceUnit,
                                        createFilePath(
                                                getProjectOutputDirectory().toString(),
                                                getSourcesDirectory().toString(),
                                                getBasePackage(),
                                                "service",
                                                pipelineId.getIdentifier() + "Service"));
                            }
                        }

                        // 5. Add service implementation method phase
                        if (_indexPipelineIdToAbstractServiceUnit.containsKey(pipelineId.toString())) {
                            if (abstractServiceMethodDeclaration.isPresent()) {
                                var editedServiceUnit = InnerProcessingStage.ADD_SERVICE_METHOD_IMPLEMENTATION
                                        .setCompilationUnit(_indexPipelineIdToServiceImplementationUnit.get(pipelineId.toString()))
                                        .setContext(
                                                new ProcessingContext(
                                                        pipelineId,
                                                        pipeline,
                                                        Map.ofEntries(
                                                                Map.entry(PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION,
                                                                        abstractServiceMethodDeclaration.get()),
                                                                Map.entry(PropertyName.REPOSITORY_METHOD_DECLARATION,
                                                                        abstractServiceMethodDeclaration.get()),
                                                                Map.entry(PropertyName.REPOSITORY_NAME,
                                                                        pipelineId.getIdentifier() + "Repository"),
                                                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME,
                                                                        getBasePackage() + ".repository"))))
                                        .process();
                                save(editedServiceUnit,
                                        createFilePath(
                                                getProjectOutputDirectory().toString(),
                                                getSourcesDirectory().toString(),
                                                getBasePackage(),
                                                "service.impl",
                                                pipelineId.getIdentifier() + "ServiceImpl"));
                            }
                        }

                        // 6. Add controller method implementation phase
                        if (abstractServiceMethodDeclaration.isPresent()) {
                            var editedControllerUnit = InnerProcessingStage.ADD_CONTROLLER_METHOD_IMPLEMENTATION
                                    .setCompilationUnit(controllerUnit)
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(PropertyName.ABSTRACT_SERVICE_NAME,
                                                                    pipelineId.getIdentifier() + "Service"),
                                                            Map.entry(PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION,
                                                                    abstractServiceMethodDeclaration.get()))))
                                    .process();
                            save(editedControllerUnit);
                        }
                    }
                }
            }

            // 7. Edit configuration phase
            var editedConfigurationUnit = InnerProcessingStage.EDIT_CONFIGURATION
                    .setCompilationUnit(configurationUnit)
                    .setContext(
                            new ProcessingContext(
                                    null,
                                    null,
                                    Map.ofEntries(
                                            Map.entry(PropertyName.DB_TYPE,
                                                    storageType),
                                            Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME,
                                                    getBasePackage() + ".repository"))))
                    .process();
            save(editedConfigurationUnit);
        } catch (IOException ioe) {
            getLog().error("Cannot parse generated components: " + ioe);
            throw new MojoFailureException("Cannot parse generated components", ioe);
        } catch (ClassCastException ce) {
            getLog().error("Incompatible types: <" + SPRING_REST_CONTROLLER_ANNOTATION_NAME_FULL +
                    "> and <Class<? extends Annotation>>");
            throw new MojoFailureException("Incompatible types: <" + SPRING_REST_CONTROLLER_ANNOTATION_NAME_FULL +
                    "> and <Class<? extends Annotation>>", ce);
        }
    }*/

    /*private void executeOuterTransformations() throws MojoFailureException {
        var classTransformations = getTransformations();
        if (!classTransformations.isEmpty()) {
            for (var transformation : classTransformations) {
                var sourceClassPath = Optional.<Path>empty();
                var targetClassPath = Optional.<Path>empty();

                if (StringUtils.isNoneEmpty(transformation.getSourceClassPackage()) &&
                        StringUtils.isNoneEmpty(transformation.getSourceClassName())) {
                    sourceClassPath =
                            Optional.of(
                                    createFilePath(
                                            StringUtils.isEmpty(transformation.getSourceDirectory()) ?
                                                    getProjectBaseDirectory() :
                                                    transformation.getSourceDirectory(),
                                            StringUtils.isEmpty(transformation.getSourceDirectory()) ?
                                                    getSourcesDirectory().toString() :
                                                    "",
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
                                createFilePath(
                                        getProjectOutputDirectory().toString(),
                                        getSourcesDirectory().toString(),
                                        "",
                                        transformation.getTargetClassPackage(),
                                        transformation.getTargetClassName()));
                    }
                } else if (StringUtils.isNoneEmpty(transformation.getTargetClassPackage()) &&
                        StringUtils.isNoneEmpty(transformation.getTargetClassName())) {
                    targetClassPath = Optional.of(
                            createFilePath(
                                    StringUtils.isEmpty(transformation.getTargetDirectory()) ?
                                            getProjectOutputDirectory().toString() :
                                            transformation.getTargetDirectory(),
                                    StringUtils.isEmpty(transformation.getTargetDirectory()) ?
                                            getSourcesDirectory().toString() :
                                            "",
                                    "",
                                    transformation.getTargetClassPackage(),
                                    transformation.getTargetClassName()));
                } else {
                    throw new MojoFailureException("Cannot find target path for generated classes");
                }

                try {
                    var processingSchemaInstance =
                            URLClassLoader
                                    .newInstance(
                                            new URL[]{transformation.getProcessingSchemaLocation().toURI().toURL()},
                                            getClass().getClassLoader())
                                    .loadClass(transformation.getProcessingSchemaClass())
                                    .getDeclaredConstructor()
                                    .newInstance();

                    if (processingSchemaInstance instanceof ASTProcessingSchema) {
                        var baseUnit =
                                sourceClassPath.isPresent() ? parse(sourceClassPath.get()).clone() : new CompilationUnit();
                        var processedUnit = InnerProcessingStage.PROCESS_OUTER_SCHEMA
                                .setCompilationUnit(baseUnit)
                                .setSchema((ASTProcessingSchema)processingSchemaInstance)
                                .setContext(new OuterSchemaContext(
                                        transformation
                                                .getTransformationProperties()
                                                .stream()
                                                .collect(Collectors.toUnmodifiableMap(
                                                        TransformationProperty::getName,
                                                        TransformationProperty::getValue,
                                                        (s, a) -> s))))
                                .process();
                        save(processedUnit, targetClassPath.get());
                        getLog().info(
                                String.format("Processed schema: %s",
                                        String.join(
                                                File.separator,
                                                transformation.getProcessingSchemaLocation().toString(),
                                                transformation.getProcessingSchemaClass())) +
                                        "\nBase class: " +
                                        sourceClassPath +
                                        "\nTarget class: " +
                                        targetClassPath
                        );
                    } else {
                        throw new MojoFailureException("Invalid processing schema found: " + processingSchemaInstance);
                    }
                } catch (IOException | ClassNotFoundException | ClassCastException | NoSuchMethodException | InstantiationException |
                        IllegalAccessException | InvocationTargetException e) {
                    getLog().error("Cannot create generated class into " + targetClassPath.get(), e);
                    throw new MojoFailureException("Cannot create generated class into " + targetClassPath.get(), e);
                }
            }
        }
    }*/

    /*private void prepareProjectDescriptor() throws MojoFailureException {
        var DEP_SPRING_BOOT_STARTER_WEB = "org.springframework.boot:spring-boot-starter-web:2.5.3";
        var DEP_GUAVA = "com.google.guava:guava:31.0.1-jre";

        var dependenciesToAdd =
                getTransformations()
                        .stream()
                        .flatMap(t -> t.getDependencies().stream())
                        .map(d -> new Dependency(d.getGroupId(), d.getArtifactId(), d.getVersion()))
                        .collect(Collectors.toSet());
        dependenciesToAdd.add(Dependency.of(DEP_SPRING_BOOT_STARTER_WEB));
        dependenciesToAdd.add(Dependency.of(DEP_GUAVA));
        dependenciesToAdd.add(Dependency.of(getDbType().dependencyDescriptor()));

        var projectDescriptor =
                new File(getProjectOutputDirectory().getAbsolutePath() + File.separator + POM_XML);
        var projectDescriptorBackup =
                new File(getProjectOutputDirectory().getAbsolutePath() + System.currentTimeMillis() + File.separator + POM_XML_BACKUP);

        Document document;

        try {
            Files.copy(projectDescriptor.toPath(), projectDescriptorBackup.toPath(), COPY_ATTRIBUTES, REPLACE_EXISTING);
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(projectDescriptor);

            var xpath = XPathFactory.newInstance().newXPath();
            var dependenciesNode = (Node)xpath
                    .compile("/project/dependencies")
                    .evaluate(document, XPathConstants.NODE);

            for (var dep : dependenciesToAdd) {
                var dependencyFilterExpression =
                        String.format(
                                "/project/dependencies/dependency[./groupId[contains(.,\"%s\")] and ./artifactId[contains(.,\"%s\")]]",
                                dep.getGroupId(),
                                dep.getArtifactId());
                if (((NodeList)xpath
                        .compile(dependencyFilterExpression)
                        .evaluate(document, XPathConstants.NODESET))
                        .getLength() == 0) {
                    dependenciesNode.appendChild(Dependency.createDependencyNode(dep, document));
                }
            }
        } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException e) {
            getLog().error("Cannot edit project descriptor: " + projectDescriptor, e);
            throw new MojoFailureException("Cannot edit project descriptor: " + projectDescriptor, e);
        }

        getProject().getDependencies().stream()
                .filter(d -> d.getGroupId().contains("log4j") || d.getArtifactId().contains("log4j"))
                .forEach(d -> System.out.println("!!!log4j:"+d));

        try (var outputStream = new FileOutputStream(projectDescriptor)) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, Charset.defaultCharset().toString());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        } catch (ClassCastException | IOException | TransformerException e) {
            getLog().error("Cannot edit project descriptor: " + getProject().getFile(), e);
            throw new MojoFailureException("Cannot edit project descriptor: " + getProject().getFile(), e);
        }
    }*/

    @Override
    public void execute() throws MojoFailureException {
        //processingContainer.start();

        //executeInnerTransformations();
        //executeOuterTransformations();
        //prepareProjectDescriptor();
    }
}
