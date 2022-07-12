package com.home.servicegenerator.plugin.processing.configuration.schema;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.home.servicegenerator.api.Generator;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.configuration.context.properties.PropertyName;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class InjectServiceIntoControllerSchemaTest {
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.home.servicegenerator.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private final static String ABSTRACT_SERVICE_PACKAGE_NAME = "com.home.service";
    private final static String ABSTRACT_SERVICE_NAME = modelClassName.getIdentifier() + "Service";
    private final static String SERVICE_FIELD_NAME = ABSTRACT_SERVICE_NAME.toLowerCase();
    private static AssignExpr serviceAssignment;
    private static ClassOrInterfaceDeclaration controllerDeclarationAfterInjectionService;
    private static final String CONTROLLER_CLASS_DECLARATION = String.join("\n",
                    "@RestController",
                    "public class TestController {",
                    "    private final HttpServletRequest request;",
                    "    public TestController(HttpServletRequest request) {",
                    "        this.request = request;",
                    "    }",
                    "}"
    );

    @BeforeAll
    static void initGenerator() throws Exception {
        final JavaParser parser = new JavaParser();
        final ParseResult<TypeDeclaration<?>> parsingControllerDeclarationResult = parser.parseTypeDeclaration(CONTROLLER_CLASS_DECLARATION);
        ClassOrInterfaceDeclaration controllerDeclarationBeforeInjectionService;

        if (parsingControllerDeclarationResult.isSuccessful() && parsingControllerDeclarationResult.getResult().isPresent()) {
            controllerDeclarationBeforeInjectionService =
                    parsingControllerDeclarationResult.getResult().get().asClassOrInterfaceDeclaration();
        } else {
            throw new Exception("Cannot parse code: " + CONTROLLER_CLASS_DECLARATION + "\n" + parsingControllerDeclarationResult.getProblems());
        }

        final Context context =
                ProcessingContext.of(
                        Map.ofEntries(
                                Map.entry(PropertyName.PIPELINE_ID.name(), modelClassName),
                                Map.entry(PropertyName.ABSTRACT_SERVICE_PACKAGE_NAME.name(),
                                        ABSTRACT_SERVICE_PACKAGE_NAME)/*,
                                Map.entry(PropertyName.ABSTRACT_SERVICE_NAME.name(),
                                        ABSTRACT_SERVICE_NAME)*/
                        ));
        final Generator generator =
                DefaultGenerator
                        .builder()
                        .processingSchema(InternalProcessingSchema.InjectServiceIntoController)
                        .build();

        controllerDeclarationAfterInjectionService = (ClassOrInterfaceDeclaration) generator
                .generate(controllerDeclarationBeforeInjectionService, context);
        serviceAssignment =
                new AssignExpr(
                        new FieldAccessExpr()
                                .setScope(new ThisExpr())
                                .setName(SERVICE_FIELD_NAME),
                        new NameExpr(SERVICE_FIELD_NAME),
                        AssignExpr.Operator.ASSIGN);
    }

    @Test
    @DisplayName("😎")
    void testInjectServiceIntoController() {
        Assertions.assertTrue(
                controllerDeclarationAfterInjectionService
                        .getFieldByName(SERVICE_FIELD_NAME)
                        .isPresent(),
                "Edited controller class has not field " + SERVICE_FIELD_NAME);
        Assertions.assertTrue(
                controllerDeclarationAfterInjectionService
                        .getFieldByName(SERVICE_FIELD_NAME)
                        .get()
                        .isPrivate(),
                "Field " + SERVICE_FIELD_NAME + " is not private");
        Assertions.assertTrue(
                controllerDeclarationAfterInjectionService
                        .getFieldByName(SERVICE_FIELD_NAME)
                        .get()
                        .isFinal(),
                "Field " + SERVICE_FIELD_NAME + " is not final");
        Assertions.assertEquals(
                ABSTRACT_SERVICE_NAME,
                controllerDeclarationAfterInjectionService
                        .getFieldByName(SERVICE_FIELD_NAME)
                        .get()
                        .getVariable(0)
                        .getType()
                        .toString(),
                "Field " + SERVICE_FIELD_NAME + " has not type " + ABSTRACT_SERVICE_NAME);

        final Predicate<ConstructorDeclaration> isNotDefaultConstructor =
                (ConstructorDeclaration constructorDeclaration) -> constructorDeclaration.getParameters().isNonEmpty();

        Assertions.assertEquals(
                controllerDeclarationAfterInjectionService
                        .getConstructors()
                        .stream()
                        .filter(isNotDefaultConstructor)
                        .count(),
                controllerDeclarationAfterInjectionService
                        .getConstructors()
                        .stream()
                        .filter(constructorDeclaration -> constructorDeclaration.getParameterByType(ABSTRACT_SERVICE_NAME).isPresent())
                        .count(),
                "Edited controller class has not constructor with argument of type " + ABSTRACT_SERVICE_NAME);
        Assertions.assertTrue(
                controllerDeclarationAfterInjectionService
                        .getConstructors()
                        .stream()
                        .filter(isNotDefaultConstructor)
                        .allMatch(constructorDeclaration -> {
                                final Optional<Parameter> parameter =
                                        constructorDeclaration
                                                .getParameterByType(ABSTRACT_SERVICE_NAME);
                                return parameter.isPresent() && SERVICE_FIELD_NAME.equals(parameter.get().getNameAsString());
                        }),
                "Non-default constructor of edited controller class has not argument with name " + SERVICE_FIELD_NAME);

        final Predicate<ConstructorDeclaration> constructorHasServiceAssignmentStatement =
                (ConstructorDeclaration constructorDeclaration) ->
                        1 == constructorDeclaration
                                .getBody()
                                .asBlockStmt()
                                .getStatements()
                                .stream()
                                .filter(statement ->
                                        trimToEmpty(serviceAssignment.toString() + ";").equals(trimToEmpty(statement.toString())))
                                .count();
        Assertions.assertTrue(
                controllerDeclarationAfterInjectionService
                        .getConstructors()
                        .stream()
                        .filter(isNotDefaultConstructor)
                        .allMatch(constructorHasServiceAssignmentStatement),
                "Non-default constructor of edited controller class has not such service field assertion statement: " +
                        trimToEmpty(serviceAssignment.toString() + ";"));
    }
}
