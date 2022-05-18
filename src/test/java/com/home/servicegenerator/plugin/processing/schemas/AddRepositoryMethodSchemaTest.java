package com.home.servicegenerator.plugin.processing.schemas;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.api.Generator;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.context.properties.PropertyName;
import com.home.servicegenerator.plugin.processing.context.properties.Storage;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.schemas.InnerProcessingSchema;
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
    private static final Storage.DbType DB_NAME = Storage.DbType.mongo;
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
                ProcessingContext.of(
                        Map.ofEntries(
                                Map.entry(PropertyName.PIPELINE.name(), controllerMethodDeclaration),
                                Map.entry(PropertyName.PIPELINE_ID.name(), modelClassName),
                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(), REPOSITORY_PACKAGE_NAME),
                                Map.entry(PropertyName.REPOSITORY_NAME.name(), REPOSITORY_NAME),
                                Map.entry(PropertyName.REPOSITORY_ID_CLASS_NAME.name(), REPOSITORY_ID_CLASS),
                                Map.entry(PropertyName.DB_TYPE.name(), DB_NAME)));

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
                        ProcessingContext.of(
                                Map.ofEntries(
                                        Map.entry(PropertyName.PIPELINE.name(), controllerMethodDeclaration),
                                        Map.entry(PropertyName.PIPELINE_ID.name(), modelClassName)
                                )
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
