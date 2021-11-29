package com.home.servicegenerator.plugin.processing.schemas;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.Generator;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.context.ProcessingContext;
import com.home.servicegenerator.plugin.context.ProcessingProperty;
import com.home.servicegenerator.plugin.generator.DefaultGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class AddRepositoryMethodSchemaTest {
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.home.servicegenerator.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private static final String REPOSITORY_PACKAGE_NAME = "com.home.repository";
    private static final String REPOSITORY_NAME = "TestRepository";
    private static final ProcessingProperty.StorageType DB_NAME = ProcessingProperty.StorageType.mongo;
    private static final String REPOSITORY_ID_CLASS = Long.class.getCanonicalName();

    private static CompilationUnit repositoryUnitAfterCreating;
    private static CompilationUnit repositoryUnitAfterAddingMethod;

    @BeforeAll
    static void initGenerator() {
        final Generator repositoryGenerator =
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
                                Map.entry(ProcessingProperty.Name.DB_TYPE, DB_NAME)));

        repositoryUnitAfterCreating = (CompilationUnit) repositoryGenerator
                .generate(new CompilationUnit(), context);

        final Generator repositoryMethodGenerator =
                DefaultGenerator
                        .builder()
                        .processingSchema(InnerProcessingSchema.AddRepositoryMethod)
                        .build();
        //Trivial. Use methods of Spring Data repository by default.
        repositoryUnitAfterAddingMethod = (CompilationUnit) repositoryMethodGenerator
                .generate(
                        repositoryUnitAfterCreating,
                        new ProcessingContext(
                                modelClassName,
                                controllerMethodDeclaration,
                                Map.of()
                        )
                );
    }

    @Test
    void testAddRepositoryMethod() {
        Assertions.assertNotNull(
                repositoryUnitAfterCreating,
                "Repository unit is null after creating");
        Assertions.assertNotNull(
                repositoryUnitAfterAddingMethod,
                "Repository unit is null after adding method");
        Assertions.assertEquals(
                repositoryUnitAfterCreating,
                repositoryUnitAfterAddingMethod,
                "Repository unit was changed after trivial no-changing operation");
    }
}
