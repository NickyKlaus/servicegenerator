package com.home.servicegenerator.plugin.processing.strategy;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.javaparser.StaticJavaParser.parse;

public class PipelineStriping {
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

    private PipelineStriping() {
        // Don't need for utility class
    }

    public static List<MethodDeclaration> makeStriping(final CompilationUnit controller) {
        return controller.findAll(ClassOrInterfaceDeclaration.class, isRestController)
                .stream()
                .flatMap(c -> c.getImplementedTypes().stream())
                .flatMap(type -> {
                    if (controller.getStorage().isPresent()) {
                        var path =
                                Path.of(controller.getStorage().get().getDirectory().toString(),
                                        type.getName().getIdentifier() + JAVA_EXT);
                        if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                            try {
                                return parse(path)
                                        .findAll(MethodDeclaration.class, isMethodAnnotatedAsRestEndpoint)
                                        .stream();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return Stream.<MethodDeclaration>of();
                })
                .collect(Collectors.toUnmodifiableList());
    }
}
