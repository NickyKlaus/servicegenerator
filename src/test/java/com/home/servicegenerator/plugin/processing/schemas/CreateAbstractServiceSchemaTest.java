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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class CreateAbstractServiceSchemaTest {
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.home.servicegenerator.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private static final String SERVICE_PACKAGE_NAME = "com.home.service";
    private static final String SERVICE_NAME = "TestService";
    private static final ProcessingProperty.DbType DB_TYPE = ProcessingProperty.DbType.mongo;
    private static CompilationUnit serviceClassUnit;

    @BeforeAll
    static void initGenerator() {
        final Generator generator =
                DefaultGenerator
                        .builder()
                        .processingSchema(InnerProcessingSchema.CreateAbstractService)
                        .build();
        final MethodDeclaration controllerMethodDeclaration = new MethodDeclaration();
        final Context context =
                new ProcessingContext(
                        modelClassName,
                        controllerMethodDeclaration,
                        Map.ofEntries(
                                Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_PACKAGE_NAME, SERVICE_PACKAGE_NAME),
                                Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_NAME, SERVICE_NAME),
                                Map.entry(ProcessingProperty.Name.DB_TYPE, DB_TYPE)));
        serviceClassUnit = (CompilationUnit) generator.generate(new CompilationUnit(), context);
    }

    @Test
    @DisplayName("😎")
    void testGeneratedAbstractService() {
        Assertions.assertNotNull(
                serviceClassUnit,
                "Generated service unit is null");
        Assertions.assertTrue(
                serviceClassUnit.getPackageDeclaration().isPresent(),
                "Generated service unit has not package declaration");
        Assertions.assertEquals(
                SERVICE_PACKAGE_NAME,
                serviceClassUnit.getPackageDeclaration().get().getNameAsString(),
                "Generated service unit is not in package " + SERVICE_PACKAGE_NAME);
        Assertions.assertTrue(
                serviceClassUnit.getInterfaceByName(SERVICE_NAME).isPresent(),
                "Generated service unit has not name " + SERVICE_NAME);
        Assertions.assertTrue(
                serviceClassUnit.getInterfaceByName(SERVICE_NAME).get().isPublic(),
                "Generated service interface is not public");
    }
}
