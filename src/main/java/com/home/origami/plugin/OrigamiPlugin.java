package com.home.origami.plugin;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.origami.plugin.db.DBClient;
import com.home.origami.plugin.metadata.model.Dependency;
import com.home.origami.plugin.processing.ProcessingUnit;
import com.home.origami.plugin.processing.configuration.DefaultProcessingConfiguration;
import com.home.origami.plugin.processing.configuration.ProcessingConfiguration;
import com.home.origami.plugin.processing.configuration.context.properties.Storage;
import com.home.origami.plugin.utils.FileUtils;
import com.home.origami.plugin.utils.MethodNormalizer;
import com.home.origami.plugin.utils.NormalizerUtils;
import com.home.origami.api.ASTProcessingSchema;
import com.home.origami.plugin.processing.configuration.stages.ProcessingPlan;
import com.home.origami.plugin.processing.configuration.stages.ProcessingStage;
import com.home.origami.plugin.processing.configuration.strategy.processing.SequentialProcessingStrategy;
import com.home.origami.plugin.processing.container.ProcessingContainer;
import com.home.origami.plugin.processing.configuration.context.properties.PropertyName;
import com.home.origami.plugin.processing.configuration.stages.InternalProcessingStage;
import com.home.origami.plugin.processing.configuration.strategy.matchmethod.MatchWithRestEndpointMethodStrategy;
import com.home.origami.plugin.processing.configuration.strategy.matchmethod.MatchingMethodStrategy;
import com.home.origami.plugin.processing.configuration.strategy.naming.PipelineIdBasedNamingStrategy;
import com.home.origami.plugin.processing.configuration.strategy.processing.PipelineIdBasedProcessingStrategy;

import io.swagger.codegen.v3.cli.cmd.Generate;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static com.home.origami.plugin.processing.configuration.context.properties.PropertyName.*;
import static java.lang.String.format;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Goal which generates microservice based on declared logic.
 */
@Mojo(name = "generate-service", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class OrigamiPlugin extends AbstractServiceGeneratorMojo {
    private static final String CONTEXT_PREFERENCE_IS_NOT_SET_ERROR_MESSAGE = "%s is not set";
    private static final String POM_XML = "pom.xml";
    private static final String POM_XML_BACKUP = "pom.xml.bak";

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

    private void prepareProjectDescriptor() throws MojoFailureException {
        var DEP_SPRING_BOOT_STARTER_WEB = "org.springframework.boot:spring-boot-starter-web:2.6.7";
        var DEP_GUAVA = "com.google.guava:guava:31.1-jre";

        var dependenciesToAdd =
                getTransformations()
                        .stream()
                        .flatMap(t -> t.getDependencies().stream())
                        .map(d -> Dependency.of(d.getGroupId(), d.getArtifactId(), d.getVersion()))
                        .collect(Collectors.toSet());
        dependenciesToAdd.add(Dependency.of(DEP_SPRING_BOOT_STARTER_WEB));
        dependenciesToAdd.add(Dependency.of(DEP_GUAVA));
        dependenciesToAdd.add(Dependency.of(getDbType().dependencyDescriptor()));

        var projectDescriptor =
                new File(getProjectOutputDirectory() + File.separator + POM_XML);
        var projectDescriptorBackup =
                new File(getProjectOutputDirectory() + File.separator + System.currentTimeMillis() + POM_XML_BACKUP);

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
                        format("/project/dependencies/dependency[./groupId[contains(.,\"%s\")] and ./artifactId[contains(.,\"%s\")]]",
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

    private void t() {

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

            prepareProjectDescriptor();
        }
    }
}
