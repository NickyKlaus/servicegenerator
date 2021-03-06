package com.github.origami.plugin.processing.configuration.schema;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.origami.plugin.processing.configuration.context.ProcessingContext;
import com.github.origami.plugin.processing.configuration.context.properties.Storage;
import com.github.origami.generator.DefaultGenerator;
import com.github.origami.plugin.processing.configuration.context.properties.PropertyName;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CreateRepositorySchemaTest {
    private static final String SPRING_REPOSITORY = "Repository";
    private static final Storage.DbType DB_NAME = Storage.DbType.mongo;
    private static final String SPRING_DATA_CRUD_REPOSITORY = DB_NAME.getCrudRepositoryInterfaceName();
    private static final String REPOSITORY_PACKAGE_NAME = "com.github.repository";
    private static final String REPOSITORY_NAME = "TestModelRepository";
    private static final String REPOSITORY_ID_CLASS = Long.class.getCanonicalName();
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.github.origami.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private static final String CONTROLLER_METHOD_DECLARATION = "public List<TestModel> getTestModels();";
    private static CompilationUnit repositoryClassUnit;

    @BeforeAll
    static void initGenerator() {
        var parsingMethodResult =
                new JavaParser().parseMethodDeclaration(CONTROLLER_METHOD_DECLARATION);
        if (parsingMethodResult.isSuccessful() && parsingMethodResult.getResult().isPresent()) {
            var generator =
                    DefaultGenerator
                            .builder()
                            .processingSchema(InternalProcessingSchema.CreateRepository)
                            .build();
            var controllerMethodDeclaration = parsingMethodResult.getResult().get();
            var context =
                    new ProcessingContext(
                            Map.ofEntries(
                                    Map.entry(PropertyName.PIPELINE.name(), controllerMethodDeclaration),
                                    Map.entry(PropertyName.PIPELINE_ID.name(), modelClassName),
                                    Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(), REPOSITORY_PACKAGE_NAME),
                                    Map.entry(PropertyName.REPOSITORY_NAME.name(), REPOSITORY_NAME),
                                    Map.entry(PropertyName.REPOSITORY_ID_CLASS_NAME.name(), REPOSITORY_ID_CLASS),
                                    Map.entry(PropertyName.DB_TYPE.name(), DB_NAME)
                            ));
            repositoryClassUnit = (CompilationUnit) generator.generate(new CompilationUnit(), context);
        }
    }

    @Test
    @DisplayName("????")
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

        var indexCounter = new AtomicInteger(0);
        var repositorySupertypeParameterIndexToNameMap = repositoryClassUnit
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
