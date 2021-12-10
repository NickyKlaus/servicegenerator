package com.home.servicegenerator.plugin;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.utils.SourceRoot;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.plugin.processing.context.OuterSchemaContext;
import com.home.servicegenerator.plugin.processing.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.context.ProcessingProperty;
import com.home.servicegenerator.plugin.processing.descriptor.Dependency;
import com.home.servicegenerator.plugin.processing.MatchMethodStrategy;
import com.home.servicegenerator.plugin.processing.MatchWithRestEndpointMethod;
import com.home.servicegenerator.plugin.processing.ProcessingStage;
import com.home.servicegenerator.plugin.processing.registry.ProjectUnitsRegistry;
import com.home.servicegenerator.plugin.utils.FileUtils;
import com.home.servicegenerator.plugin.utils.MethodNormalizer;
import com.home.servicegenerator.plugin.utils.ResolverUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.home.servicegenerator.plugin.utils.FileUtils.createFilePath;
import static com.home.servicegenerator.plugin.utils.NormalizerUtils.REPLACING_MODEL_TYPE_SYMBOL;
import static com.home.servicegenerator.plugin.utils.ResolverUtils.createJavaSymbolSolver;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Goal which generates microservice based on declared logic.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ServiceGeneratorPlugin extends AbstractServiceGeneratorMojo {
    private static final String JAVA_EXT = ".java";
    private static final String SPRING_REQUEST_MAPPING_ANNOTATION_NAME_FULL =
            "org.springframework.web.bind.annotation.RequestMapping";
    private static final String SPRING_REQUEST_MAPPING_ANNOTATION_NAME_SHORT = "RequestMapping";
    private static final String SPRING_REST_CONTROLLER_ANNOTATION_NAME_FULL =
            "org.springframework.web.bind.annotation.RestController";
    private static final String SPRING_REST_CONTROLLER_ANNOTATION_NAME_SHORT = "RestController";
    private static final String SPRING_APPLICATION_ANNOTATION_NAME_FULL =
            "org.springframework.boot.autoconfigure.SpringBootApplication";
    private static final String SPRING_APPLICATION_ANNOTATION_NAME_SHORT = "SpringBootApplication";

    private static final Predicate<ClassOrInterfaceDeclaration> isRestController =
            c -> c.isAnnotationPresent(SPRING_REST_CONTROLLER_ANNOTATION_NAME_FULL) ||
                    c.isAnnotationPresent(SPRING_REST_CONTROLLER_ANNOTATION_NAME_SHORT);

    private static final Predicate<MethodDeclaration> isMethodAnnotatedAsRestEndpoint =
            method -> method.isAnnotationPresent(SPRING_REQUEST_MAPPING_ANNOTATION_NAME_FULL) ||
                    method.isAnnotationPresent(SPRING_REQUEST_MAPPING_ANNOTATION_NAME_SHORT);

    private static final Predicate<CompilationUnit> hasAncestorWithRestEndpoint = (cu) ->
        cu.findAll(ClassOrInterfaceDeclaration.class, isRestController)
                .stream()
                .flatMap(c -> c.getImplementedTypes().stream())
                .anyMatch(c -> {
                    if (cu.getStorage().isPresent()) {
                        var path =
                                Path.of(cu.getStorage().get().getDirectory().toString(),
                                        c.getName().getIdentifier() + JAVA_EXT);
                        if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                            try {
                                return !parse(path)
                                        .findAll(MethodDeclaration.class, isMethodAnnotatedAsRestEndpoint).isEmpty();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return false;
                });

    private static Optional<MethodDeclaration> getMethodMatchedWithPipeline(
            final MethodDeclaration pipeline,
            final List<MethodDeclaration> checkedMethods,
            final Name pipelineId,
            final MatchMethodStrategy checkingMethodStrategy
    ) {
        return checkedMethods
                .stream()
                .filter(checkedMethod -> checkingMethodStrategy
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
            targetCompilationUnit.setStorage(targetLocation, targetCharset);
            getLog().info("Save generated class into " + targetPath);
        } catch (IOException e) {
            getLog().error("Cannot write generated class into " + targetLocation.getFileName(), e);
            throw new MojoFailureException("Cannot write generated class into " + targetLocation.getFileName(), e);
        }
    }

    private void executeInnerTransformations() throws MojoFailureException {
        try {
            var _parserConfiguration =
                    new ParserConfiguration()
                            .setSymbolResolver(
                                    createJavaSymbolSolver(
                                            FileUtils.createDirPath(
                                                    getProjectOutputDirectory().toString(),
                                                    getSourcesDirectory().toString(),
                                                    getBasePackage(),
                                                    ""))
                            );

            var innerSourceRoot = new SourceRoot(
                    FileUtils.createDirPath(
                            getProjectOutputDirectory().toString(),
                            getSourcesDirectory().toString(),
                            getBasePackage(),
                            ""),
                    _parserConfiguration);

            var storageType = getDbType();

            // Create folders for components
            Files.createDirectory(
                    FileUtils.createDirPath(
                            getProjectOutputDirectory().toString(),
                            getSourcesDirectory().toString(),
                            getBasePackage(),
                            "repository"));
            Files.createDirectories(
                    FileUtils.createDirPath(
                            getProjectOutputDirectory().toString(),
                            getSourcesDirectory().toString(),
                            getBasePackage(),
                            "service.impl"));

            // Controllers' compilation units
            var controllerUnits =
                    innerSourceRoot
                            .tryToParse(getControllerPackage())
                            .stream()
                            .filter(result -> result.isSuccessful() && result.getResult().isPresent())
                            .map(result -> result.getResult().get())
                            .filter(unit -> unit.getPackageDeclaration().isPresent() &&
                                    unit.getPackageDeclaration().get()
                                            .getNameAsString().equals(getBasePackage() + "." + getControllerPackage()) &&
                                    unit.getPrimaryType().isPresent())
                            .filter(unit -> unit.getPrimaryType().isPresent() &&
                                    (unit.getPrimaryType().get().isAnnotationPresent(SPRING_REST_CONTROLLER_ANNOTATION_NAME_FULL) ||
                                            unit.getPrimaryType().get().isAnnotationPresent(SPRING_REST_CONTROLLER_ANNOTATION_NAME_SHORT)))
                            .filter(hasAncestorWithRestEndpoint)
                            .collect(Collectors.toUnmodifiableList());

            controllerUnits
                    .stream()
                    .filter(unit -> unit.getPrimaryTypeName().isPresent())
                    .forEach(ProjectUnitsRegistry::register);

            // Models' compilation units
            var modelsUnits =
                    innerSourceRoot
                            .tryToParse(getModelPackage())
                            .stream()
                            .filter(result -> result.isSuccessful() && result.getResult().isPresent())
                            .map(result -> result.getResult().get())
                            .filter(unit -> unit.getPackageDeclaration().isPresent() &&
                                    unit.getPackageDeclaration().get()
                                            .getNameAsString().equals(getBasePackage() + "." + getModelPackage()))
                            .filter(unit -> unit.getPrimaryType().isPresent())
                            .collect(Collectors.toUnmodifiableList());

            modelsUnits
                    .stream()
                    .filter(unit -> unit.getPrimaryTypeName().isPresent())
                    .forEach(ProjectUnitsRegistry::register);

            // Should be only one Spring application component
            var configurationUnit =
                    innerSourceRoot
                            .tryToParse(getConfigurationPackage())
                            .stream()
                            .filter(result -> result.isSuccessful() && result.getResult().isPresent())
                            .map(result -> result.getResult().get())
                            .filter(unit -> unit.getPackageDeclaration().isPresent() &&
                                    unit.getPackageDeclaration().get()
                                            .getNameAsString().equals(getBasePackage() + "." + getConfigurationPackage()))
                            .filter(unit -> unit.getPrimaryType().isPresent() &&
                                    (unit.getPrimaryType().get().isAnnotationPresent(SPRING_APPLICATION_ANNOTATION_NAME_FULL) ||
                                            unit.getPrimaryType().get().isAnnotationPresent(SPRING_APPLICATION_ANNOTATION_NAME_SHORT)))
                            .findFirst()
                            .orElseThrow(() -> new MojoFailureException("Cannot find spring application class"));

            ProjectUnitsRegistry.register(configurationUnit);

            // Model classes that have been found in the sources
            final List<Name> availableModelsNames = modelsUnits
                    .stream()
                    .map(CompilationUnit::getPrimaryType)
                    .filter(Optional::isPresent)
                    .map(t -> new Name()
                            .setQualifier(new Name(getBasePackage() + "." + getModelPackage()))
                            .setIdentifier(t.get().getName().getIdentifier()))
                    .collect(Collectors.toUnmodifiableList());

            // Global index of inner generated repositories
            var _indexPipelineIdToRepositoryUnit = new HashMap<String, CompilationUnit>();
            // Global index of inner generated abstract services
            var _indexPipelineIdToAbstractServiceUnit = new HashMap<String, CompilationUnit>();
            // Global index of inner generated services
            var _indexPipelineIdToServiceImplementationUnit = new HashMap<String, CompilationUnit>();

            for (CompilationUnit controllerUnit : controllerUnits) {
                var restEndpoints =
                        controllerUnit.findAll(ClassOrInterfaceDeclaration.class, isRestController)
                                .stream()
                                .flatMap(c -> c.getImplementedTypes().stream())
                                .map(t -> {
                                    if (controllerUnit.getStorage().isPresent()) {
                                        var path =
                                                Path.of(controllerUnit.getStorage().get().getDirectory().toString(),
                                                        t.getName().getIdentifier() + JAVA_EXT);
                                        if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                                            try {
                                                return Optional.ofNullable(
                                                        parse(path)
                                                                .findAll(MethodDeclaration.class, isMethodAnnotatedAsRestEndpoint));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    return Optional.<List<MethodDeclaration>>empty();
                                })
                                .filter(Optional::isPresent)
                                .flatMap(e -> e.get().stream())
                                .collect(Collectors.toUnmodifiableList());

                for (var pipeline : restEndpoints) {
                    var pipelineIdResolveResult =
                            ResolverUtils.lookupPipelineId(pipeline, availableModelsNames);
                    if (pipelineIdResolveResult.isPresent()) {
                        // Fully qualified name of the available model class that pipeline deals with
                        var pipelineId = pipelineIdResolveResult.get();

                        // 1. Create repository phase
                        if (!_indexPipelineIdToRepositoryUnit.containsKey(pipelineId.toString())) {
                            var repositoryUnit = ProcessingStage.CREATE_REPOSITORY
                                    .setCompilationUnit(new CompilationUnit())
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(ProcessingProperty.Name.DB_TYPE,
                                                                    storageType),
                                                            Map.entry(ProcessingProperty.Name.REPOSITORY_NAME,
                                                                    pipelineId.getIdentifier() + "Repository"),
                                                            Map.entry(ProcessingProperty.Name.REPOSITORY_PACKAGE_NAME,
                                                                    getBasePackage() + ".repository"),
                                                            Map.entry(ProcessingProperty.Name.REPOSITORY_ID_CLASS_NAME,
                                                                    Long.class.getSimpleName()))))
                                    .process();
                            save(repositoryUnit,
                                    createFilePath(
                                            getProjectOutputDirectory().toString(),
                                            getSourcesDirectory().toString(),
                                            getBasePackage(),
                                    "repository",
                                            pipelineId.getIdentifier() + "Repository"));
                            _indexPipelineIdToRepositoryUnit.put(pipelineId.toString(), repositoryUnit);
                        }

                        // 2. Create abstract service phase and inject service into controller phase
                        if (!_indexPipelineIdToAbstractServiceUnit.containsKey(pipelineId.toString())) {
                            var abstractServiceUnit = ProcessingStage.CREATE_ABSTRACT_SERVICE
                                    .setCompilationUnit(new CompilationUnit())
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_PACKAGE_NAME,
                                                                    getBasePackage() + ".service"),
                                                            Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_NAME,
                                                                    pipelineId.getIdentifier() + "Service"),
                                                            Map.entry(ProcessingProperty.Name.DB_TYPE,
                                                                    storageType))))
                                    .process();
                            save(abstractServiceUnit,
                                    createFilePath(
                                            getProjectOutputDirectory().toString(),
                                            getSourcesDirectory().toString(),
                                            getBasePackage(),
                                            "service",
                                            pipelineId.getIdentifier() + "Service"));
                            _indexPipelineIdToAbstractServiceUnit.put(pipelineId.toString(), abstractServiceUnit);

                            var editedControllerUnit = ProcessingStage.INJECT_SERVICE_INTO_CONTROLLER
                                    .setCompilationUnit(controllerUnit)
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_PACKAGE_NAME,
                                                                    getBasePackage() + ".service"),
                                                            Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_NAME,
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
                            var serviceUnit = ProcessingStage.CREATE_SERVICE_IMPLEMENTATION
                                    .setCompilationUnit(new CompilationUnit())
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_PACKAGE_NAME,
                                                                    getBasePackage() + ".service"),
                                                            Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_NAME,
                                                                    pipelineId.getIdentifier() + "Service"),
                                                            Map.entry(ProcessingProperty.Name.REPOSITORY_NAME,
                                                                    pipelineId.getIdentifier() + "Repository"),
                                                            Map.entry(ProcessingProperty.Name.REPOSITORY_PACKAGE_NAME,
                                                                    getBasePackage() + ".repository"),
                                                            Map.entry(ProcessingProperty.Name.DB_TYPE,
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
                                            new MatchWithRestEndpointMethod());

                            if (abstractServiceMethodDeclaration.isPresent()) {
                                var abstractServiceUnit = ProcessingStage.ADD_SERVICE_ABSTRACT_METHOD
                                        .setCompilationUnit(_indexPipelineIdToAbstractServiceUnit.get(pipelineId.toString()))
                                        .setContext(
                                                new ProcessingContext(
                                                        pipelineId,
                                                        //resolve method type and signature: signature is from controller, type is from repository
                                                        pipeline,
                                                        Map.ofEntries(
                                                                Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_METHOD_DECLARATION,
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
                                var editedServiceUnit = ProcessingStage.ADD_SERVICE_METHOD_IMPLEMENTATION
                                        .setCompilationUnit(_indexPipelineIdToServiceImplementationUnit.get(pipelineId.toString()))
                                        .setContext(
                                                new ProcessingContext(
                                                        pipelineId,
                                                        pipeline,
                                                        Map.ofEntries(
                                                                Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_METHOD_DECLARATION,
                                                                        abstractServiceMethodDeclaration.get()),
                                                                Map.entry(ProcessingProperty.Name.REPOSITORY_METHOD_DECLARATION,
                                                                        abstractServiceMethodDeclaration.get()),
                                                                Map.entry(ProcessingProperty.Name.REPOSITORY_NAME,
                                                                        pipelineId.getIdentifier() + "Repository"),
                                                                Map.entry(ProcessingProperty.Name.REPOSITORY_PACKAGE_NAME,
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
                            var editedControllerUnit = ProcessingStage.ADD_CONTROLLER_METHOD_IMPLEMENTATION
                                    .setCompilationUnit(controllerUnit)
                                    .setContext(
                                            new ProcessingContext(
                                                    pipelineId,
                                                    pipeline,
                                                    Map.ofEntries(
                                                            Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_NAME,
                                                                    pipelineId.getIdentifier() + "Service"),
                                                            Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_METHOD_DECLARATION,
                                                                    abstractServiceMethodDeclaration.get()))))
                                    .process();
                            save(editedControllerUnit);
                        }
                    }
                }
            }

            // 7. Edit configuration phase
            var editedConfigurationUnit = ProcessingStage.EDIT_CONFIGURATION
                    .setCompilationUnit(configurationUnit)
                    .setContext(
                            new ProcessingContext(
                                    null,
                                    null,
                                    Map.ofEntries(
                                            Map.entry(ProcessingProperty.Name.DB_TYPE,
                                                    storageType),
                                            Map.entry(ProcessingProperty.Name.REPOSITORY_PACKAGE_NAME,
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
    }

    private void executeOuterTransformations() throws MojoFailureException {
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
                    var processingSchemaInstance = URLClassLoader.newInstance(
                            new URL[]{transformation.getProcessingSchemaLocation().toURI().toURL()}, getClass().getClassLoader())
                            .loadClass(transformation.getProcessingSchemaClass())
                            .getDeclaredConstructor()
                            .newInstance();

                    if (processingSchemaInstance instanceof ASTProcessingSchema) {
                        var baseUnit =
                                sourceClassPath.isPresent() ? parse(sourceClassPath.get()).clone() : new CompilationUnit();
                        var processedUnit = ProcessingStage.PROCESS_OUTER_SCHEMA
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
    }

    private void prepareProjectDescriptor() throws MojoFailureException {
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
                new File(getProjectOutputDirectory().getAbsolutePath() + File.separator + "pom.xml");
        var projectDescriptorBackup =
                new File(getProjectOutputDirectory().getAbsolutePath() + File.separator + "pom.xml.bak");

        Document document;

        try {
            Files.copy(projectDescriptor.toPath(), projectDescriptorBackup.toPath(), COPY_ATTRIBUTES, REPLACE_EXISTING);
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(projectDescriptor);

            var xpath = XPathFactory.newInstance().newXPath();
            var dependenciesNode = (Node)xpath
                    .compile("/project/dependencies")
                    .evaluate(document, XPathConstants.NODE);

            for (Dependency dep : dependenciesToAdd) {
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
    }

    @Override
    public void execute() throws MojoFailureException {
        executeInnerTransformations();
        executeOuterTransformations();
        prepareProjectDescriptor();
    }
}
