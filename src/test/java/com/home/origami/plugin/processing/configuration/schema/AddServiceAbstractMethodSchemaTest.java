package com.home.origami.plugin.processing.configuration.schema;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.home.origami.plugin.processing.configuration.context.ProcessingContext;
import com.home.origami.api.Generator;
import com.home.origami.api.context.Context;
import com.home.origami.plugin.processing.configuration.context.properties.PropertyName;
import com.home.origami.plugin.processing.engine.DefaultGenerator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddServiceAbstractMethodSchemaTest {
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.home.origami.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private static final String ABSTRACT_SERVICE_DECLARATION = "public interface TestService {}";
    private static final String ABSTRACT_SERVICE_METHOD_DECLARATION_RETURN_TYPE = "List";
    private static final String ABSTRACT_SERVICE_METHOD_DECLARATION_METHOD_NAME = "testModelsGet";
    private static final String ABSTRACT_SERVICE_METHOD_DECLARATION =
            ABSTRACT_SERVICE_METHOD_DECLARATION_RETURN_TYPE +
                    "<" + modelClassName.toString() + ">" +
                    " " + ABSTRACT_SERVICE_METHOD_DECLARATION_METHOD_NAME + "();";
    private static ClassOrInterfaceDeclaration abstractServiceDeclaration;
    private static MethodDeclaration abstractServiceMethodDeclaration;

    @BeforeAll
    static void initGenerator() throws Exception{
        final JavaParser parser = new JavaParser();
        final ParseResult<MethodDeclaration> parsingServiceMethodDeclarationResult =
                parser.parseMethodDeclaration(ABSTRACT_SERVICE_METHOD_DECLARATION);

        if (parsingServiceMethodDeclarationResult.isSuccessful() &&
                parsingServiceMethodDeclarationResult.getResult().isPresent()) {
            abstractServiceMethodDeclaration = parsingServiceMethodDeclarationResult.getResult().get();
        } else {
            throw new Exception("Cannot parse code: " + ABSTRACT_SERVICE_METHOD_DECLARATION + "\n" +
                    parsingServiceMethodDeclarationResult.getProblems());
        }

        final ParseResult<TypeDeclaration<?>> parsingServiceDeclarationResult =
                parser.parseTypeDeclaration(ABSTRACT_SERVICE_DECLARATION);

        if (parsingServiceDeclarationResult.isSuccessful() && parsingServiceDeclarationResult.getResult().isPresent()) {
            final Context context =
                    ProcessingContext.of(
                            Map.ofEntries(
                                    Map.entry(PropertyName.PIPELINE.name(), abstractServiceMethodDeclaration),
                                    Map.entry(PropertyName.PIPELINE_ID.name(), modelClassName),
                                    Map.entry(
                                            PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION.name(),
                                            abstractServiceMethodDeclaration)));
            final Generator generator =
                    DefaultGenerator
                            .builder()
                            .processingSchema(InternalProcessingSchema.AddServiceAbstractMethod)
                            .build();
            abstractServiceDeclaration = (ClassOrInterfaceDeclaration) generator
                    .generate(
                            parsingServiceDeclarationResult
                                    .getResult()
                                    .get()
                                    .asClassOrInterfaceDeclaration(),
                            context);
        } else {
            throw new Exception("Cannot parse code: " + ABSTRACT_SERVICE_DECLARATION);
        }
    }

    @Test
    @DisplayName("😎")
    void testAddGeneratedServiceMethod() {
        Assertions.assertFalse(
                abstractServiceDeclaration.getMethods().isEmpty(),
                "There are no generated methods in TestService interface");
        Assertions.assertEquals(
                1,
                abstractServiceDeclaration.getMethods().size(),
                "There is more than 1 generated methods in TestService interface");
        final List<MethodDeclaration> methodDeclarations =
                abstractServiceDeclaration.getMethodsBySignature(
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
                "There is not generated method in TestService with expected signature: " +
                        ABSTRACT_SERVICE_METHOD_DECLARATION_METHOD_NAME + "()");
        Assertions.assertTrue(
                methodDeclarations.get(0).isPublic(),
                "Generated method in TestService interface is not public");
        Assertions.assertTrue(
                methodDeclarations.get(0).isAbstract(),
                "Generated method in TestService interface is not abstract");
        Assertions.assertEquals(
                ABSTRACT_SERVICE_METHOD_DECLARATION_RETURN_TYPE,
                methodDeclarations
                        .get(0)
                        .getType()
                        .asClassOrInterfaceType()
                        .getNameAsString(),
                "Generated method in TestService interface has not expected return type: " +
                        ABSTRACT_SERVICE_METHOD_DECLARATION_RETURN_TYPE);

        final List<MethodDeclaration> methodDeclarationsWithExpectedSignature =
                abstractServiceDeclaration.getMethodsBySignature(
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
        Assertions.assertTrue(
                methodDeclarationsWithExpectedSignature
                        .get(0)
                        .getType()
                        .asClassOrInterfaceType()
                        .getTypeArguments()
                        .isPresent(),
                "Generated method in TestService interface has not generic return type");
        Assertions.assertEquals(
                1,
                methodDeclarationsWithExpectedSignature
                        .get(0)
                        .getType()
                        .asClassOrInterfaceType()
                        .getTypeArguments()
                        .get()
                        .stream()
                        .map(type -> type.asClassOrInterfaceType().getNameAsString())
                        .count(),
                "Generated method in TestService interface has not generic return type with one type parameter");
        Assertions.assertEquals(
                modelClassName.toString(),
                methodDeclarationsWithExpectedSignature
                        .get(0)
                        .getType()
                        .asClassOrInterfaceType()
                        .getTypeArguments()
                        .get()
                        .stream()
                        .map(type -> type.asClassOrInterfaceType().getNameWithScope())
                        .collect(Collectors.toList())
                        .get(0),
                "Generated method in TestService interface has not generic return type parameterized by " +
                        modelClassName);
    }
}
