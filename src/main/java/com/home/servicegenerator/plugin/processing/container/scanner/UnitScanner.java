package com.home.servicegenerator.plugin.processing.container.scanner;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.utils.SourceRoot;
import com.home.servicegenerator.plugin.PluginConfiguration;
import com.home.servicegenerator.plugin.utils.FileUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.home.servicegenerator.plugin.utils.ResolverUtils.createJavaSymbolSolver;

public class UnitScanner implements Scanner {
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

    private static final Predicate<CompilationUnit> hasAncestorWithRestEndpoint = cu ->
            cu.findAll(ClassOrInterfaceDeclaration.class, isRestController)
                    .stream()
                    .flatMap(c -> c.getImplementedTypes().stream())
                    .anyMatch(c -> {
                        if (cu.getStorage().isPresent()) {
                            final Path path =
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

    private final PluginConfiguration configuration;
    private final SourceRoot sourceRoot;

    public UnitScanner(PluginConfiguration configuration) throws IOException {
        this.configuration = configuration;
        this.sourceRoot = prepareSourceRoot(configuration);
    }

    private static SourceRoot prepareSourceRoot(final PluginConfiguration configuration) throws IOException {
        return new SourceRoot(
                        FileUtils.createDirPath(
                                configuration.getProjectOutputDirectory(),
                                configuration.getSourcesLocation(),
                                configuration.getBasePackage(),
                                ""),
                        new ParserConfiguration()
                                .setSymbolResolver(
                                        createJavaSymbolSolver(
                                                FileUtils.createDirPath(
                                                        configuration.getProjectOutputDirectory(),
                                                        configuration.getSourcesLocation(),
                                                        configuration.getBasePackage(),
                                                        ""))));
    }

    @Override
    public List<CompilationUnit> scanController() throws MojoFailureException {
        final HashSet<CompilationUnit> units = new HashSet<>(parseControllerUnit(configuration.getControllerPackage()));
        return units.stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<CompilationUnit> scanModel() throws MojoFailureException {
        final HashSet<CompilationUnit> units = new HashSet<>(parseModelUnit(configuration.getModelPackage()));
        return units.stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<CompilationUnit> scanConfiguration() throws MojoFailureException {
        final HashSet<CompilationUnit> units = new HashSet<>(parseConfigurationUnit(configuration.getConfigurationPackage()));
        return units.stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Name> getModelNames(List<CompilationUnit> modelUnits) {
        // Model classes that have been found in the sources
        return modelUnits
                .stream()
                .filter(unit -> unit.getPackageDeclaration().isPresent() && unit.getPrimaryTypeName().isPresent())
                .map(unit ->
                        new Name()
                                .setQualifier(unit.getPackageDeclaration().get().getName())
                                .setIdentifier(unit.getPrimaryTypeName().get()))
                .collect(Collectors.toUnmodifiableList());
    }

    private List<CompilationUnit> parseConfigurationUnit(String pkg) throws MojoFailureException {
        try {
            // Should be only one Spring application component
            return sourceRoot
                    .tryToParse(pkg)
                    .stream()
                    .filter(result -> result.isSuccessful() && result.getResult().isPresent())
                    .map(result -> result.getResult().get())
                    .filter(unit -> unit.getPackageDeclaration().isPresent() &&
                            unit.getPackageDeclaration().get()
                                    .getNameAsString().equals(configuration.getBasePackage() + "." + pkg))
                    .filter(unit -> unit.getPrimaryType().isPresent() &&
                            (unit.getPrimaryType().get().isAnnotationPresent(SPRING_APPLICATION_ANNOTATION_NAME_FULL) ||
                                    unit.getPrimaryType().get().isAnnotationPresent(SPRING_APPLICATION_ANNOTATION_NAME_SHORT)))
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException ioe) {
            throw new MojoFailureException("Cannot parse configuration class", ioe);
        }
    }

    private List<CompilationUnit> parseControllerUnit(String pkg) throws MojoFailureException {
        try {
            // Controllers' compilation units
            return sourceRoot
                    .tryToParse(pkg)
                    .stream()
                    .filter(result -> result.isSuccessful() && result.getResult().isPresent())
                    .map(result -> result.getResult().get())
                    .filter(unit -> unit.getPackageDeclaration().isPresent() &&
                            unit.getPackageDeclaration().get().getNameAsString().equals(configuration.getBasePackage() + "." + pkg) &&
                            unit.getPrimaryType().isPresent())
                    .filter(unit -> unit.getPrimaryType().isPresent() &&
                            (unit.getPrimaryType().get().isAnnotationPresent(SPRING_REST_CONTROLLER_ANNOTATION_NAME_FULL) ||
                                    unit.getPrimaryType().get().isAnnotationPresent(SPRING_REST_CONTROLLER_ANNOTATION_NAME_SHORT)))
                    .filter(hasAncestorWithRestEndpoint)
                    .collect(Collectors.toUnmodifiableList());
        } catch(IOException ioe) {
            throw new MojoFailureException("Cannot parse controller class", ioe);
        }
    }

    private List<CompilationUnit> parseModelUnit(String pkg) throws MojoFailureException {
        try {
            // Models' compilation units
            return sourceRoot
                    .tryToParse(pkg)
                    .stream()
                    .filter(result -> result.isSuccessful() && result.getResult().isPresent())
                    .map(result -> result.getResult().get())
                    .filter(unit -> unit.getPackageDeclaration().isPresent() &&
                            unit.getPackageDeclaration().get()
                                    .getNameAsString().equals(configuration.getBasePackage() + "." + pkg))
                    .filter(unit -> unit.getPrimaryType().isPresent())
                    .collect(Collectors.toUnmodifiableList());
        } catch(IOException ioe) {
            throw new MojoFailureException("Cannot parse model class", ioe);
        }
    }
}
