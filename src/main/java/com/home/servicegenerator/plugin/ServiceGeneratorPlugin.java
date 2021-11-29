package com.home.servicegenerator.plugin;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.utils.SourceRoot;
import com.home.servicegenerator.plugin.context.ProcessingContext;
import com.home.servicegenerator.plugin.context.ProcessingProperty;
import com.home.servicegenerator.plugin.processing.MatchWithRestEndpointMethod;
import com.home.servicegenerator.plugin.processing.ProcessingStage;
import com.home.servicegenerator.plugin.utils.FileUtils;
import com.home.servicegenerator.plugin.utils.ResolverUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.home.servicegenerator.plugin.context.ProcessingProperty.StorageType.getMethodMatchedWithPipeline;
import static com.home.servicegenerator.plugin.utils.ResolverUtils.createJavaSymbolSolver;

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
                                return !StaticJavaParser
                                        .parse(path)
                                        .findAll(MethodDeclaration.class, isMethodAnnotatedAsRestEndpoint).isEmpty();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return false;
                });

/*    private CompilationUnit parseSourceFile(final File source) throws MojoFailureException {
        if (source == null || !Files.exists(source.toPath(), LinkOption.NOFOLLOW_LINKS)) {
            return new CompilationUnit();
        }
        try {
            ParseResult<CompilationUnit> baseUnitParsingResult = new JavaParser().parse(source);
            if (baseUnitParsingResult.isSuccessful() && baseUnitParsingResult.getResult().isPresent()) {
                getLog().info("Parse result successful: " + baseUnitParsingResult.isSuccessful());
                return baseUnitParsingResult.getResult().get();
            }
        } catch (FileNotFoundException e) {
            throw new MojoFailureException("Cannot find source file " + source.getAbsolutePath(), e);
        }
        return new CompilationUnit();
    }*/

    //Generate whole java class source representation
    /*private CompilationUnit generate(CompilationUnit baseUnit, ASTProcessingSchema processingSchema, Context context) {
        return (CompilationUnit) DefaultGenerator.builder()
                .processingSchema(processingSchema)
                .build()
                .generate(baseUnit, context);
    }*/

    private void save(final CompilationUnit compilationUnit) throws MojoFailureException {
        if (
                compilationUnit.getPackageDeclaration().isPresent() &&
                        compilationUnit.getPrimaryType().isPresent()
        ) {
            compilationUnit
                    .getStorage()
                    .orElseThrow(() -> new MojoFailureException("Cannot write generated class " + compilationUnit))
                    .save();
        }
    }

    private void save(CompilationUnit targetCompilationUnit, Path targetLocation) throws MojoFailureException {
        targetCompilationUnit
                .setStorage(targetLocation, Charset.defaultCharset())
                .getStorage()
                .orElseThrow(() -> new MojoFailureException("Cannot write generated class into " + targetLocation))
                .save();
    }

    /*private void executeOuterTransformations() throws MojoFailureException {
        final List<Transformation> classTransformations = Arrays.asList(getTransformations());
        if (!classTransformations.isEmpty()) {
            //TODO: add state-machine for inner transformations
            for (final Transformation transformation : classTransformations) {

                final File sourceClassFile =
                        StringUtils.isBlank(transformation.getBaseClassName()) ?
                                null :
                                createFilePath(
                                        transformation.getBaseClassPackage(),
                                        transformation.getBaseClassName(),
                                        "",
                                        JAVA_EXT).toFile();
                final Path targetClassPath =
                        createFilePath(
                                getProjectOutputDirectory().getAbsolutePath(),
                                transformation.getBaseClassName(),
                                transformation.getPostfix(),
                                JAVA_EXT
                        );

                try {
                    Object _processingSchemaInstance = URLClassLoader.newInstance(
                            new URL[]{transformation.getProcessingSchemaLocation().toURI().toURL()}, getClass().getClassLoader())
                            .loadClass(transformation.getProcessingSchemaClass())
                            .getDeclaredConstructor()
                            .newInstance();
                    ASTProcessingSchema schema;
                    if (_processingSchemaInstance instanceof ASTProcessingSchema) {
                        schema = (ASTProcessingSchema) _processingSchemaInstance;
                    } else {
                        throw new MojoFailureException("Invalid processing schema found: " + _processingSchemaInstance);
                    }
                    getLog().info("AST schema: " + schema);

                    //Context context = new ProcessingContext("", new MethodDeclaration(), new HashSet<>());

                    getLog().info("Generation interrupted because target class <" + targetClassPath + "> already exists.");
                    *//*save(
                            generate(parseSourceFile(sourceClassFile), schema, context),
                            targetClassPath
                    );*//*
                } catch (IOException | ClassNotFoundException | ClassCastException | NoSuchMethodException | InstantiationException |
                        IllegalAccessException | InvocationTargetException e) {
                    getLog().error("Cannot create generated class into " + targetClassPath, e);
                    throw new MojoFailureException("Cannot create generated class into " + targetClassPath, e);
                }
            }
        } else {
            throw new MojoFailureException("Cannot find transformations for processing classes!");
        }
    }*/

    private void executeInnerTransformations() throws MojoFailureException {
        try {
            var _parserConfiguration =
                    new ParserConfiguration()
                            .setStoreTokens(true)
                            .setSymbolResolver(
                                    createJavaSymbolSolver(
                                            /*Path.of("/Users/klaus/.m2/repository/org/springframework/spring-web/5.3.9/spring-web-5.3.9.jar"),
                                            */FileUtils.createDirPath(
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

            StaticJavaParser.setConfiguration(_parserConfiguration);

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
                                                        StaticJavaParser
                                                                .parse(path)
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
                                    FileUtils.createFilePath(
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
                                    FileUtils.createFilePath(
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
                                    FileUtils.createFilePath(
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
                                    FileUtils.createFilePath(
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
                                        FileUtils.createFilePath(
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
                                        FileUtils.createFilePath(
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
            var contextProperties = new HashMap<ProcessingProperty.Name, Object>();
            contextProperties.put(
                    ProcessingProperty.Name.DB_TYPE,
                    storageType);
            contextProperties.put(
                    ProcessingProperty.Name.REPOSITORY_PACKAGE_NAME,
                    getBasePackage() + ".repository");
            var editedConfigurationUnit = ProcessingStage.EDIT_CONFIGURATION
                    .setCompilationUnit(configurationUnit)
                    .setContext(
                            new ProcessingContext(
                                    null,
                                    null,
                                    contextProperties))
                    .process();
            save(editedConfigurationUnit);
        } catch (IOException ioe) {
            getLog().error("Cannot parse generated components: " + ioe);
            throw new MojoFailureException("Cannot parse generated components", ioe);
        } catch (ClassCastException ce) {
            getLog().error("Incompatible types: <" + SPRING_REST_CONTROLLER_ANNOTATION_NAME_FULL + "> and <Class<? extends Annotation>>");
            throw new MojoFailureException("Incompatible types: <" + SPRING_REST_CONTROLLER_ANNOTATION_NAME_FULL + "> and <Class<? extends Annotation>>", ce);
        }
    }

    @Override
    public void execute() throws MojoFailureException {
        executeInnerTransformations();
    }

    /*private static List<MethodDeclaration> getAncestorsRestEndpoints(
            final CompilationUnit compilationUnit,
            final Predicate<MethodDeclaration> isRestEndpointMethodPredicate,
            final JavaParserFacade javaParserFacade
    ) {
        return compilationUnit
                .getTypes()
                .stream()
                .filter(TypeDeclaration::isClassOrInterfaceDeclaration)
                .filter(t -> !t.getName().getIdentifier().startsWith("java."))
                .map(BodyDeclaration::asClassOrInterfaceDeclaration)
                //.filter(ResolvedType::isReferenceType)
                .flatMap(t -> t.resolve().asReferenceType().getAncestors().stream())
                .filter(r ->
                        r.getAllMethodsVisibleToInheritors()
                                .stream()
                                .anyMatch(resolvedMethodDeclaration -> resolvedMethodDeclaration.toAst().isPresent() &&
                                        isRestEndpointMethodPredicate.test(resolvedMethodDeclaration.toAst().get())))
                .flatMap(p -> p.getAllMethodsVisibleToInheritors().stream())
                .map(ResolvedMethodDeclaration::toAst)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());
    }*/

}
