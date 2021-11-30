package com.home.servicegenerator.plugin.processing.schemas;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
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

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class CreateServiceImplementationSchemaTest {
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.home.servicegenerator.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private static final String SPRING_SERVICE = "org.springframework.stereotype.Service";
    private static final String ABSTRACT_SERVICE_PACKAGE_NAME = "com.home.service";
    private static final String ABSTRACT_SERVICE_NAME = "TestService";
    private static final String GENERATED_SERVICE_NAME = ABSTRACT_SERVICE_NAME + "Impl";
    private static final String REPOSITORY_PACKAGE_NAME = "com.home.repository";
    private static final String REPOSITORY_NAME = "TestRepository";
    private static final String REPOSITORY_FIELD_NAME = REPOSITORY_NAME.toLowerCase();
    private static final ProcessingProperty.DbType DB_TYPE = ProcessingProperty.DbType.mongo;
    private static CompilationUnit serviceImplementationClassUnit;
    private static AssignExpr repositoryAssignment;

    @BeforeAll
    static void initGenerator() {
        final Generator generator =
                DefaultGenerator
                        .builder()
                        .processingSchema(InnerProcessingSchema.CreateServiceImplementation)
                        .build();

        final Context context =
                new ProcessingContext(
                        modelClassName,
                        null,
                        Map.ofEntries(
                                Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_PACKAGE_NAME,
                                        ABSTRACT_SERVICE_PACKAGE_NAME),
                                Map.entry(ProcessingProperty.Name.ABSTRACT_SERVICE_NAME,
                                        ABSTRACT_SERVICE_NAME),
                                Map.entry(ProcessingProperty.Name.REPOSITORY_PACKAGE_NAME,
                                        REPOSITORY_PACKAGE_NAME),
                                Map.entry(ProcessingProperty.Name.REPOSITORY_NAME,
                                        REPOSITORY_NAME),
                                Map.entry(ProcessingProperty.Name.DB_TYPE,
                                        DB_TYPE)));

        serviceImplementationClassUnit = (CompilationUnit) generator.generate(new CompilationUnit(), context);
        repositoryAssignment =
                new AssignExpr(
                        new FieldAccessExpr()
                                .setScope(new ThisExpr())
                                .setName(REPOSITORY_FIELD_NAME),
                        new NameExpr(REPOSITORY_FIELD_NAME),
                        AssignExpr.Operator.ASSIGN);
    }

    @Test
    @DisplayName("😎")
    void testGeneratedServiceImplementation() {
        Assertions.assertNotNull(
                serviceImplementationClassUnit,
                "Generated service implementation unit is null");
        Assertions.assertTrue(
                serviceImplementationClassUnit.getPackageDeclaration().isPresent(),
                "Generated service implementation unit has not package declaration");
        Assertions.assertEquals(
                ABSTRACT_SERVICE_PACKAGE_NAME + ".impl",
                serviceImplementationClassUnit.getPackageDeclaration().get().getNameAsString(),
                "Generated service implementation unit is not in package " + ABSTRACT_SERVICE_PACKAGE_NAME + ".impl");
        Assertions.assertTrue(
                serviceImplementationClassUnit.getClassByName(GENERATED_SERVICE_NAME).isPresent(),
                "Generated service implementation unit has not name " + ABSTRACT_SERVICE_NAME + "Impl");

        final ClassOrInterfaceDeclaration serviceDeclaration = serviceImplementationClassUnit
                .getClassByName(GENERATED_SERVICE_NAME)
                .get();

        Assertions.assertTrue(
                serviceDeclaration.isPublic(),
                "Generated service implementation class is not public");
        Assertions.assertTrue(
                serviceDeclaration
                        .getAnnotations()
                        .stream()
                        .anyMatch(annotationExpr -> SPRING_SERVICE.equals(annotationExpr.getNameAsString())),
                "Generated service implementation unit is not annotated by '@Service'");
        Assertions.assertTrue(
                serviceDeclaration
                        .getImplementedTypes()
                        .isNonEmpty(),
                "Generated service implementation class has not super type");
        Assertions.assertTrue(
                serviceDeclaration
                        .getImplementedTypes()
                        .stream()
                        .anyMatch(classOrInterfaceType ->
                                        (ABSTRACT_SERVICE_PACKAGE_NAME + "." + ABSTRACT_SERVICE_NAME)
                                                .equals(classOrInterfaceType.getNameAsString())),
                "Generated service implementation class does not implement " + ABSTRACT_SERVICE_PACKAGE_NAME +
                        "." + ABSTRACT_SERVICE_NAME);
        Assertions.assertEquals(
                1,
                serviceDeclaration
                        .getImplementedTypes()
                        .stream()
                        .filter(classOrInterfaceType ->
                                (ABSTRACT_SERVICE_PACKAGE_NAME + "." + ABSTRACT_SERVICE_NAME)
                                        .equals(classOrInterfaceType.getNameAsString()))
                        .count(),
                "Generated service implementation class does not implement one " + ABSTRACT_SERVICE_PACKAGE_NAME
                        + "." + ABSTRACT_SERVICE_NAME);
        Assertions.assertTrue(
                serviceDeclaration
                        .getFieldByName(REPOSITORY_FIELD_NAME)
                        .isPresent(),
                "Generated service implementation class has not field " + REPOSITORY_FIELD_NAME);
        Assertions.assertTrue(
                serviceDeclaration
                        .getFieldByName(REPOSITORY_FIELD_NAME)
                        .get()
                        .isPrivate(),
                "Field " + REPOSITORY_FIELD_NAME + " is not private");
        Assertions.assertTrue(
                serviceDeclaration
                        .getFieldByName(REPOSITORY_FIELD_NAME)
                        .get()
                        .isFinal(),
                "Field " + REPOSITORY_FIELD_NAME + " is not final");
        Assertions.assertEquals(
                REPOSITORY_PACKAGE_NAME + "." + REPOSITORY_NAME,
                serviceDeclaration
                        .getFieldByName(REPOSITORY_FIELD_NAME)
                        .get()
                        .getVariable(0)
                        .getType()
                        .toString(),
                "Field " + REPOSITORY_FIELD_NAME + " has not type " + REPOSITORY_PACKAGE_NAME + "." + REPOSITORY_NAME);
        Assertions.assertTrue(
                serviceDeclaration
                        .getConstructorByParameterTypes(REPOSITORY_PACKAGE_NAME + "." + REPOSITORY_NAME)
                        .isPresent(),
                "Generated service implementation class has not contructor with one argument with type " +
                        REPOSITORY_PACKAGE_NAME + "." + REPOSITORY_NAME);
        Assertions.assertTrue(
                serviceDeclaration
                        .getConstructorByParameterTypes(REPOSITORY_PACKAGE_NAME + "." + REPOSITORY_NAME)
                        .get().asConstructorDeclaration().getBody().asBlockStmt().getStatements().isNonEmpty(),
                "Constructor of generated service implementation has empty block");
        Assertions.assertEquals(
                1,
                serviceDeclaration
                        .getConstructorByParameterTypes(REPOSITORY_PACKAGE_NAME + "." + REPOSITORY_NAME)
                        .get()
                        .getBody()
                        .asBlockStmt()
                        .getStatements()
                        .stream()
                        .filter(statement ->
                                trimToEmpty(repositoryAssignment.toString() + ";")
                                        .equals(trimToEmpty(statement.toString())))
                        .count(),
                "Constructor of generated service implementation has not such repository field assertion statement: " +
                        trimToEmpty(repositoryAssignment.toString() + ";"));
    }
}
