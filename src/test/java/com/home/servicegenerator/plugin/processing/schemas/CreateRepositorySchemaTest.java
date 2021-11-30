package com.home.servicegenerator.plugin.processing.schemas;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.home.servicegenerator.api.Generator;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.context.ProcessingContext;
import com.home.servicegenerator.plugin.context.ProcessingProperty;
import com.home.servicegenerator.plugin.generator.DefaultGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CreateRepositorySchemaTest {
    private static final String SPRING_REPOSITORY = "org.springframework.stereotype.Repository";
    private static final ProcessingProperty.DbType DB_NAME = ProcessingProperty.DbType.mongo;
    private static final String SPRING_DATA_CRUD_REPOSITORY = DB_NAME.getCrudRepositoryInterfaceName();
    private static final String REPOSITORY_PACKAGE_NAME = "com.home.repository";
    private static final String REPOSITORY_NAME = "TestRepository";
    private static final String REPOSITORY_ID_CLASS = Long.class.getCanonicalName();
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.home.servicegenerator.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private static CompilationUnit repositoryClassUnit;

    @BeforeAll
    static void initGenerator() {
        final Generator generator =
                DefaultGenerator
                        .builder()
                        .processingSchema(InnerProcessingSchema.CreateRepository)
                        .build();

        final MethodDeclaration controllerMethodDeclaration = new MethodDeclaration();

        final Context context =
                new ProcessingContext(
                        modelClassName,
                        controllerMethodDeclaration,
                        Map.ofEntries(
                                Map.entry(ProcessingProperty.Name.REPOSITORY_PACKAGE_NAME, REPOSITORY_PACKAGE_NAME),
                                Map.entry(ProcessingProperty.Name.REPOSITORY_NAME, REPOSITORY_NAME),
                                Map.entry(ProcessingProperty.Name.REPOSITORY_ID_CLASS_NAME, REPOSITORY_ID_CLASS),
                                Map.entry(ProcessingProperty.Name.DB_TYPE, DB_NAME)
                        ));
        repositoryClassUnit = (CompilationUnit) generator.generate(new CompilationUnit(), context);
    }

    @Test
    @DisplayName("😎")
    void testGeneratedRepository() {
        Assertions.assertNotNull(
                repositoryClassUnit,
                "Generated repository unit is null");
        Assertions.assertTrue(
                repositoryClassUnit.getPackageDeclaration().isPresent(),
                "Generated repository unit has not package declaration");
        Assertions.assertEquals(
                REPOSITORY_PACKAGE_NAME,
                repositoryClassUnit.getPackageDeclaration().get().getNameAsString(),
                "Generated repository unit is not in package " + REPOSITORY_PACKAGE_NAME);
        Assertions.assertTrue(
                repositoryClassUnit.getInterfaceByName(REPOSITORY_NAME).isPresent(),
                "Generated repository unit has not name " + REPOSITORY_NAME);
        Assertions.assertTrue(
                repositoryClassUnit.getInterfaceByName(REPOSITORY_NAME).get().isPublic(),
                "Generated repository interface is not public");
        Assertions.assertTrue(
                repositoryClassUnit
                        .getInterfaceByName(REPOSITORY_NAME)
                        .get()
                        .getAnnotations()
                        .stream()
                        .anyMatch(annotationExpr -> SPRING_REPOSITORY.equals(annotationExpr.getNameAsString())),
                "Generated repository unit is not annotated by '@Repository'");
        Assertions.assertTrue(
                repositoryClassUnit.getInterfaceByName(REPOSITORY_NAME).get().getExtendedTypes().isNonEmpty(),
                "Generated repository interface has not super type");
        Assertions.assertTrue(
                repositoryClassUnit
                        .getInterfaceByName(REPOSITORY_NAME)
                        .get()
                        .getExtendedTypes()
                        .stream()
                        .anyMatch(classOrInterfaceType ->
                                SPRING_DATA_CRUD_REPOSITORY.equals(classOrInterfaceType.getNameAsString())),
                "Generated repository interface does not extend " + SPRING_DATA_CRUD_REPOSITORY);
        Assertions.assertEquals(
                1,
                repositoryClassUnit
                        .getInterfaceByName(REPOSITORY_NAME)
                        .get()
                        .getExtendedTypes()
                        .stream()
                        .filter(classOrInterfaceType ->
                                SPRING_DATA_CRUD_REPOSITORY.equals(classOrInterfaceType.getNameAsString()))
                        .count(),
                "Generated repository interface does not extend one " + SPRING_DATA_CRUD_REPOSITORY
        );

        final AtomicInteger indexCounter = new AtomicInteger(0);
        final Map<Integer, String> repositorySupertypeParameterIndexToNameMap = repositoryClassUnit
                .getInterfaceByName(REPOSITORY_NAME)
                .get()
                .getExtendedTypes()
                .stream()
                .filter(classOrInterfaceType ->
                        SPRING_DATA_CRUD_REPOSITORY.equals(classOrInterfaceType.getNameAsString()))
                .map(ClassOrInterfaceType::getTypeArguments)
                .filter(Optional::isPresent)
                .flatMap(types -> types.get().stream().map(Type::asClassOrInterfaceType))
                .collect(Collectors.toMap(
                        t -> indexCounter.getAndIncrement(),
                        ClassOrInterfaceType::getNameAsString));

        Assertions.assertEquals(
                2,
                repositorySupertypeParameterIndexToNameMap.size(),
                "Generated repository interface does not extend " + SPRING_DATA_CRUD_REPOSITORY +
                        " with 2 type parameters");
        Assertions.assertEquals(
                modelClassName.getIdentifier(),
                repositorySupertypeParameterIndexToNameMap.get(0),
                "Id class of super type of generated repository interface is not " + modelClassName);
        Assertions.assertEquals(
                REPOSITORY_ID_CLASS,
                repositorySupertypeParameterIndexToNameMap.get(1),
                "Id class of super type of generated repository interface is not " + REPOSITORY_ID_CLASS);
    }
}
