package com.home.origami.plugin.processing.configuration.schema;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.home.origami.generator.DefaultGenerator;
import com.home.origami.generator.Generator;
import com.home.origami.plugin.processing.configuration.context.ProcessingContext;
import com.home.origami.api.context.Context;
import com.home.origami.plugin.processing.configuration.context.properties.PropertyName;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddServiceMethodImplementationSchemaTest {
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.home.origami.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private static final String REPOSITORY_NAME = "TestRepository";
    private static MethodDeclaration abstractServiceMethodDeclaration;
    private static ClassOrInterfaceDeclaration serviceImplementationDeclarationAfterAdditionMethod;
    private static final String SERVICE_IMPLEMENTATION_DECLARATION =
            "public class TestServiceImpl implements com.home.service.TestService {}";
    private static final String REPOSITORY_METHOD_DECLARATION =
            "public List<" + modelClassName + "> repoTestMethod();";
    private static final String ABSTRACT_SERVICE_METHOD_DECLARATION_RETURN_TYPE = "List";
    private static final String ABSTRACT_SERVICE_METHOD_DECLARATION_METHOD_NAME = "testModelsGet";
    private static final String ABSTRACT_SERVICE_METHOD_DECLARATION =
            "public abstract " + ABSTRACT_SERVICE_METHOD_DECLARATION_RETURN_TYPE +
                    "<" + modelClassName + ">" +
                    " " + ABSTRACT_SERVICE_METHOD_DECLARATION_METHOD_NAME + "();";

    @BeforeAll
    static void initGenerator() throws Exception {
        final JavaParser parser = new JavaParser();
        final ParseResult<TypeDeclaration<?>> parsingServiceDeclarationResult =
                parser.parseTypeDeclaration(SERVICE_IMPLEMENTATION_DECLARATION);
        ClassOrInterfaceDeclaration serviceImplementationDeclarationBeforeAdditionMethod;

        if (parsingServiceDeclarationResult.isSuccessful() && parsingServiceDeclarationResult.getResult().isPresent()) {
            serviceImplementationDeclarationBeforeAdditionMethod =
                    parsingServiceDeclarationResult.getResult().get().asClassOrInterfaceDeclaration();
        } else {
            throw new Exception("Cannot parse code: " + SERVICE_IMPLEMENTATION_DECLARATION + "\n" +
                    parsingServiceDeclarationResult.getProblems());
        }

        final ParseResult<MethodDeclaration> parsingServiceMethodDeclarationResult =
                parser.parseMethodDeclaration(ABSTRACT_SERVICE_METHOD_DECLARATION);

        if (parsingServiceMethodDeclarationResult.isSuccessful() &&
                parsingServiceMethodDeclarationResult.getResult().isPresent()) {
            abstractServiceMethodDeclaration = parsingServiceMethodDeclarationResult.getResult().get();
        } else {
            throw new Exception("Cannot parse code: " + ABSTRACT_SERVICE_METHOD_DECLARATION + "\n" +
                    parsingServiceMethodDeclarationResult.getProblems());
        }

        final ParseResult<MethodDeclaration> parsingRepositoryMethodDeclarationResult =
                parser.parseMethodDeclaration(REPOSITORY_METHOD_DECLARATION);

        MethodDeclaration repositoryMethodDeclaration;
        if (parsingRepositoryMethodDeclarationResult.isSuccessful() &&
                parsingRepositoryMethodDeclarationResult.getResult().isPresent()) {
            repositoryMethodDeclaration = parsingRepositoryMethodDeclarationResult.getResult().get();
        } else {
            throw new Exception("Cannot parse code: " + REPOSITORY_METHOD_DECLARATION + "\n" +
                    parsingRepositoryMethodDeclarationResult.getProblems());
        }

        final Context context =
                ProcessingContext.of(
                        Map.ofEntries(
                                Map.entry(PropertyName.PIPELINE.name(), abstractServiceMethodDeclaration),
                                Map.entry(PropertyName.PIPELINE_ID.name(), modelClassName),
                                Map.entry(PropertyName.REPOSITORY_NAME.name(),
                                        REPOSITORY_NAME),
                                Map.entry(PropertyName.REPOSITORY_METHOD_DECLARATION.name(),
                                        repositoryMethodDeclaration),
                                Map.entry(PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION.name(),
                                        abstractServiceMethodDeclaration)
                        ));

        final Generator generator =
                DefaultGenerator
                        .builder()
                        .processingSchema(InternalProcessingSchema.AddServiceMethodImplementation)
                        .build();
        serviceImplementationDeclarationAfterAdditionMethod =
                (ClassOrInterfaceDeclaration) generator
                        .generate(serviceImplementationDeclarationBeforeAdditionMethod, context);
    }

    @Test
    @DisplayName("😎")
    void testAddGeneratedServiceMethodImplementation() {
        Assertions.assertNotNull(
                serviceImplementationDeclarationAfterAdditionMethod,
                "Generated service implementation unit is null");
        Assertions.assertFalse(
                serviceImplementationDeclarationAfterAdditionMethod.getMethods().isEmpty(),
                "There are no generated methods in service implementation class");
        Assertions.assertEquals(
                1,
                serviceImplementationDeclarationAfterAdditionMethod.getMethods().size(),
                "There is more than 1 generated methods in service implementation class");
        final List<MethodDeclaration> methodDeclarations =
                serviceImplementationDeclarationAfterAdditionMethod.getMethodsBySignature(
                        ABSTRACT_SERVICE_METHOD_DECLARATION_METHOD_NAME,
                        abstractServiceMethodDeclaration
                                .getParameters()
                                .stream()
                                .map(parameter ->
                                        parameter
                                                .getType()
                                                .asClassOrInterfaceType()
                                                .getNameAsString())
                                .toArray(String[]::new));
        Assertions.assertEquals(
                1,
                methodDeclarations.size(),
                "There is not generated method in service implementation with expected signature: " +
                        ABSTRACT_SERVICE_METHOD_DECLARATION_METHOD_NAME + "()");
        Assertions.assertTrue(
                methodDeclarations.get(0).isPublic(),
                "Generated method in service implementation class is not public");
        Assertions.assertFalse(
                methodDeclarations.get(0).isAbstract(),
                "Generated method in service implementation class is abstract");

        final ClassOrInterfaceType generatedMethodReturnType = methodDeclarations
                .get(0)
                .getType()
                .asClassOrInterfaceType();

        Assertions.assertEquals(
                ABSTRACT_SERVICE_METHOD_DECLARATION_RETURN_TYPE,
                generatedMethodReturnType.getNameAsString(),
                "Generated method in service implementation class has not expected return type: " +
                        ABSTRACT_SERVICE_METHOD_DECLARATION_RETURN_TYPE);
        Assertions.assertTrue(
                generatedMethodReturnType.getTypeArguments().isPresent() &&
                        1 == generatedMethodReturnType.getTypeArguments().get().size(),
                "Generated method in service implementation class has not generic return type with one type parameter");
        Assertions.assertEquals(
                modelClassName.toString(),
                generatedMethodReturnType
                        .getTypeArguments()
                        .get()
                        .stream()
                        .map(type -> type.asClassOrInterfaceType().getNameWithScope())
                        .collect(Collectors.toList())
                        .get(0),
                "Generated method in service implementation class has not generic return type parameterized by " +
                        modelClassName);
    }
}
