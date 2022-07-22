package com.github.origami.plugin.processing.configuration.schema;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.origami.plugin.processing.configuration.context.ProcessingContext;
import com.github.origami.generator.DefaultGenerator;
import com.github.origami.plugin.processing.configuration.context.properties.PropertyName;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class AddControllerMethodImplementationSchemaTest {
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.github.origami.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private static final String SPRING_HTTP_STATUS_200 = "org.springframework.http.HttpStatus.OK";
    private static final String ABSTRACT_SERVICE_NAME = "TestModelService";
    private static final String ABSTRACT_SERVICE_METHOD_NAME = "serviceTest";
    private static final String SERVICE_FIELD_NAME = ABSTRACT_SERVICE_NAME.toLowerCase();
    private static ClassOrInterfaceDeclaration controllerDeclarationAfterAdditionMethod;
    private static MethodDeclaration controllerMethodDeclaration;
    private static ReturnStmt controllerReturnStmt;
    private static final String CONTROLLER_METHOD_DECLARATION = String.join("\n",
            "@RequestMapping(name=\"\", value = \"/tests\",\n",
            "        produces = { \"application/json\" }, \n",
            "        method = RequestMethod.GET)",
            "        default ResponseEntity<List<" + modelClassName + ">> testModelsGet() {",
            "            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);",
            "        }");
    private static final String ABSTRACT_SERVICE_METHOD_DECLARATION = String.join("\n",
            "public abstract List<", modelClassName.toString(), "> ", ABSTRACT_SERVICE_METHOD_NAME, "();");
    private static final String CONTROLLER_DECLARATION = String.join("\n",
            "@RestController",
            "public class TestController {",
            "}");

    @BeforeAll
    static void initGenerator() throws Exception {
        var controllerParser = new JavaParser();
        var parsingControllerDeclarationResult =
                controllerParser.parseTypeDeclaration(CONTROLLER_DECLARATION);
        ClassOrInterfaceDeclaration controllerDeclarationBeforeAdditionMethod;

        if (parsingControllerDeclarationResult.isSuccessful() &&
                parsingControllerDeclarationResult.getResult().isPresent()) {
            controllerDeclarationBeforeAdditionMethod =
                    parsingControllerDeclarationResult.getResult().get().asClassOrInterfaceDeclaration();
        } else {
            throw new Exception("Cannot parse code: " + CONTROLLER_DECLARATION + "\n" +
                    parsingControllerDeclarationResult.getProblems());
        }

        var controllerMethodParser = new JavaParser();
        var parsingControllerMethodDeclarationResult =
                controllerMethodParser.parseMethodDeclaration(CONTROLLER_METHOD_DECLARATION);

        if (parsingControllerMethodDeclarationResult.isSuccessful() &&
                parsingControllerMethodDeclarationResult.getResult().isPresent()) {
            controllerMethodDeclaration = parsingControllerMethodDeclarationResult.getResult().get();
        } else {
            throw new Exception("Cannot parse code: " + CONTROLLER_METHOD_DECLARATION + "\n" +
                    parsingControllerMethodDeclarationResult.getProblems());
        }

        var serviceMethodParser = new JavaParser();
        var parsingServiceMethodDeclarationResult =
                serviceMethodParser.parseMethodDeclaration(ABSTRACT_SERVICE_METHOD_DECLARATION);
        MethodDeclaration serviceMethodDeclaration;

        if (parsingServiceMethodDeclarationResult.isSuccessful() &&
                parsingServiceMethodDeclarationResult.getResult().isPresent()) {
            serviceMethodDeclaration = parsingServiceMethodDeclarationResult.getResult().get();
        } else {
            throw new Exception("Cannot parse code: " + ABSTRACT_SERVICE_METHOD_DECLARATION + "\n" +
                    parsingServiceMethodDeclarationResult.getProblems());
        }

        var context =
                ProcessingContext.of(
                        Map.ofEntries(
                                Map.entry(PropertyName.PIPELINE.name(), controllerMethodDeclaration),
                                Map.entry(PropertyName.PIPELINE_ID.name(), modelClassName),
                                Map.entry(PropertyName.ABSTRACT_SERVICE_NAME.name(), ABSTRACT_SERVICE_NAME),
                                Map.entry(PropertyName.ABSTRACT_SERVICE_METHOD_DECLARATION.name(), serviceMethodDeclaration)));
        var generator =
                DefaultGenerator.builder()
                        .processingSchema(InternalProcessingSchema.AddControllerMethodImplementation)
                        .build();

        controllerDeclarationAfterAdditionMethod = (ClassOrInterfaceDeclaration) generator
                .generate(controllerDeclarationBeforeAdditionMethod, context);

        controllerReturnStmt = new ReturnStmt()
                .setExpression(
                        new ObjectCreationExpr()
                                .setType(controllerMethodDeclaration.getTypeAsString())
                                .setArguments(
                                        NodeList.nodeList(
                                                new MethodCallExpr()
                                                        .setScope(new NameExpr().setName(SERVICE_FIELD_NAME))
                                                        .setName(serviceMethodDeclaration.getName())
                                                        .setArguments(
                                                                controllerMethodDeclaration
                                                                        .getParameters()
                                                                        .stream()
                                                                        .map(Parameter::getNameAsExpression)
                                                                        .collect(NodeList.toNodeList())),
                                                new TypeExpr(
                                                        new ClassOrInterfaceType()
                                                                .setName(SPRING_HTTP_STATUS_200)))));
    }

    @Test
    @DisplayName("😎")
    void testControllerMethodImplementation() {
        var generatedMethods = controllerDeclarationAfterAdditionMethod
                .getMethodsBySignature(
                        controllerMethodDeclaration.getSignature().getName(),
                        controllerMethodDeclaration
                                .getSignature()
                                .getParameterTypes()
                                .stream()
                                .map(type -> type.asClassOrInterfaceType().getNameAsString())
                                .toArray(String[]::new));

        Assertions.assertEquals(
                1,
                generatedMethods.size(),
                "Generated controller method does not exist");

        var generatedMethod = generatedMethods.get(0);
        Assertions.assertTrue(
                generatedMethod.isPublic(),
                "Generated controller method is not public");
        Assertions.assertTrue(
                generatedMethod.isAnnotationPresent(Override.class),
                "Generated controller method is not marked with @Override");
        Assertions.assertTrue(
                generatedMethod.getBody().isPresent(),
                "Generated controller method has not body");
        Assertions.assertTrue(
                generatedMethod.getBody().get().getStatements().isNonEmpty(),
                "Generated controller method body has not statements inside");
        Assertions.assertEquals(
                1,
                generatedMethod.getBody().get().getStatements().size(),
                "Generated controller method has more than 1 statement inside");
        Assertions.assertTrue(
                generatedMethod.getBody().get().getStatements().get(0).isReturnStmt(),
                "Generated controller method body has not return statement");
        Assertions.assertEquals(
                controllerReturnStmt,
                generatedMethod.getBody().get().getStatements().get(0).asReturnStmt(),
                "Generated controller method has not expected body");
    }
}
