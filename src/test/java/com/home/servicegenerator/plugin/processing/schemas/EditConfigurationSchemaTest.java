package com.home.servicegenerator.plugin.processing.schemas;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.home.servicegenerator.api.Generator;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.context.ProcessingContext;
import com.home.servicegenerator.plugin.context.ProcessingProperty;
import com.home.servicegenerator.plugin.generator.DefaultGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EditConfigurationSchemaTest {
    private static final Name modelClassName =
            new Name()
                    .setQualifier(new Name("com.home.servicegenerator.plugin.schemas"))
                    .setIdentifier(TestModel.class.getSimpleName());
    private static final String SPRING_DATA_ENABLE_MONGO =
            "org.springframework.data.mongodb.repository.config.EnableMongoRepositories";
    private static final String SPRING_DATA_ENABLE_CASSANDRA =
            "org.springframework.data.cassandra.repository.config.EnableCassandraRepositories";
    private static final String REPOSITORY_PACKAGE_NAME = "com.home.repository";
    private static final List<String> repositoriesBasePackageNames = List.of(REPOSITORY_PACKAGE_NAME);
    private static ClassOrInterfaceDeclaration configurationDeclarationBeforeEditing;
    private static final String CONFIGURATION_DECLARATION = String.join("\n",
            "@SpringBootApplication",
            "@ComponentScan(basePackages = { \"com.home.repository\", \"com.home.service\"})",
            "public class TestApplication {",
            "    public static void main(String[] args) throws Exception {",
            "        new SpringApplication(TestApplication.class).run(args);",
            "    }",
            "}");
    private static final Map<ProcessingProperty.StorageType, AnnotationExpr> annotationMap =
            new EnumMap<>(ProcessingProperty.StorageType.class);

    static {
        annotationMap.put(
                ProcessingProperty.StorageType.mongo,
                new NormalAnnotationExpr(
                        new Name(SPRING_DATA_ENABLE_MONGO),
                        NodeList.nodeList(
                                new MemberValuePair(
                                        "basePackages",
                                        new ArrayInitializerExpr(
                                                repositoriesBasePackageNames
                                                        .stream()
                                                        .map(StringLiteralExpr::new)
                                                        .collect(NodeList.toNodeList()))))));
        annotationMap.put(
                ProcessingProperty.StorageType.cassandra,
                new NormalAnnotationExpr(
                        new Name(SPRING_DATA_ENABLE_CASSANDRA),
                        NodeList.nodeList(
                                new MemberValuePair(
                                        "basePackages",
                                        new ArrayInitializerExpr(
                                                repositoriesBasePackageNames
                                                        .stream()
                                                        .map(StringLiteralExpr::new)
                                                        .collect(NodeList.toNodeList()))))));
        /*annotationMap.put(
                ProcessingContext.DbType.elasticsearch,
                null);*/
    }

    @BeforeAll
    static void initGenerator() throws Exception {
        final JavaParser configurationParser = new JavaParser();
        final ParseResult<TypeDeclaration<?>> parsingConfigurationDeclarationResult =
                configurationParser.parseTypeDeclaration(CONFIGURATION_DECLARATION);
        if (parsingConfigurationDeclarationResult.isSuccessful() &&
                parsingConfigurationDeclarationResult.getResult().isPresent()) {
            configurationDeclarationBeforeEditing =
                    parsingConfigurationDeclarationResult.getResult().get().asClassOrInterfaceDeclaration();
        } else {
            throw new Exception("Cannot parse code: " + CONFIGURATION_DECLARATION + "\n" +
                    parsingConfigurationDeclarationResult.getProblems());
        }
    }

    private ClassOrInterfaceDeclaration generate(ProcessingProperty.StorageType storageType) {
        final Context context =
                new ProcessingContext(
                        modelClassName,
                        null,
                        Map.ofEntries(
                                Map.entry(ProcessingProperty.Name.DB_TYPE, storageType),
                                Map.entry(ProcessingProperty.Name.REPOSITORY_PACKAGE_NAME, REPOSITORY_PACKAGE_NAME)
                        ));

        final Generator generator =
                DefaultGenerator
                        .builder()
                        .processingSchema(InnerProcessingSchema.EditConfiguration)
                        .build();
        return (ClassOrInterfaceDeclaration) generator
                .generate(configurationDeclarationBeforeEditing, context);
    }

    @ParameterizedTest(name = "Test for Spring Data annotation for {0} into configuration")
    @DisplayName("😎")
    @EnumSource(value = ProcessingProperty.StorageType.class)
    void testAddSpringDataAnnotationsForDbIntoConfiguration(ProcessingProperty.StorageType storageType) {
        var configurationDeclarationAfterEditing = generate(storageType);
        Assertions.assertEquals(
                1,
                configurationDeclarationAfterEditing.getAnnotations().size() -
                        configurationDeclarationBeforeEditing.getAnnotations().size(),
                "Number of added annotations is not 1");

        final String annotationName = annotationMap.get(storageType).getNameAsString();
        Assertions.assertTrue(
                generate(storageType).getAnnotationByName(annotationName).isPresent(),
                "There is not expected annotation " + annotationName + " in edited configuration");
        Assertions.assertEquals(
                annotationMap.get(storageType).asAnnotationExpr(),
                generate(storageType).getAnnotationByName(annotationName).get(),
                "There is not expected annotation " + annotationMap.get(storageType).asAnnotationExpr() +
                        " in edited configuration");
    }

    @ParameterizedTest(name = "Test for Spring Data annotation if no required db type had been set")
    @DisplayName("😎")
    @NullSource
    void configuringRepositoriesProcedureShouldBeFailedIfNoRequiredDbTypeHadBeenSet(ProcessingProperty.StorageType storageType) {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> generate(storageType),
                "NullPointerException was not thrown if no required value of db type had been set");
    }
}
