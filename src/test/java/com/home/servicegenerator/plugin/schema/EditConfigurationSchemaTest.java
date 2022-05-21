package com.home.servicegenerator.plugin.schema;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.home.servicegenerator.api.Generator;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.context.ProcessingContext;
import com.home.servicegenerator.plugin.processing.configuration.context.properties.PropertyName;
import com.home.servicegenerator.plugin.processing.configuration.context.properties.Storage;
import com.home.servicegenerator.plugin.engine.generator.DefaultGenerator;
import org.apache.commons.lang3.StringUtils;
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
    private static final String SPRING_DATA_MONGO =
            "EnableMongoRepositories";
    private static final String SPRING_DATA_CASSANDRA =
            "EnableCassandraRepositories";
    private static final String REPOSITORY_PACKAGE_NAME = "com.home.repository";
    private static final List<String> repositoriesBasePackageNames = List.of(REPOSITORY_PACKAGE_NAME);
    private static ClassOrInterfaceDeclaration configurationDeclarationBeforeEditing;
    private static CompilationUnit configurationUnitBeforeEditing;
    private static final String CONFIGURATION_DECLARATION = String.join("\n",
            "package com.home.test;",
            "@org.springframework.boot.autoconfigure.SpringBootApplication",
            "public class TestApplication {",
            "    public static void main(String[] args) throws Exception {",
            "        new SpringApplication(TestApplication.class).run(args);",
            "    }",
            "}");

    private static final Map<Storage.DbType, AnnotationExpr> annotationMap =
            new EnumMap<>(Storage.DbType.class);

    static {
        annotationMap.put(
                Storage.DbType.mongo,
                new NormalAnnotationExpr(
                        new Name(SPRING_DATA_MONGO),
                        NodeList.nodeList(
                                new MemberValuePair(
                                        "basePackages",
                                        new ArrayInitializerExpr(
                                                repositoriesBasePackageNames
                                                        .stream()
                                                        .map(StringLiteralExpr::new)
                                                        .collect(NodeList.toNodeList()))))));
        annotationMap.put(
                Storage.DbType.cassandra,
                new NormalAnnotationExpr(
                        new Name(SPRING_DATA_CASSANDRA),
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
        final ParseResult<CompilationUnit> parsingConfigurationUnitResult =
                configurationParser.parse(CONFIGURATION_DECLARATION);
        if (parsingConfigurationUnitResult.isSuccessful() &&
                parsingConfigurationUnitResult.getResult().isPresent()) {
            configurationUnitBeforeEditing = parsingConfigurationUnitResult.getResult().get();
            if (configurationUnitBeforeEditing.getTypes().size() > 0) {
                configurationDeclarationBeforeEditing =
                        configurationUnitBeforeEditing.getType(0).asClassOrInterfaceDeclaration();
            }
        } else {
            throw new Exception("Cannot parse code: " + CONFIGURATION_DECLARATION + "\n" +
                    parsingConfigurationUnitResult.getProblems());
        }
    }

    private CompilationUnit generate(Storage.DbType storageType) {
        final Context context =
                ProcessingContext.of(
                        Map.ofEntries(
                                Map.entry(PropertyName.PIPELINE_ID.name(), modelClassName),
                                Map.entry(PropertyName.DB_TYPE.name(), storageType),
                                Map.entry(PropertyName.REPOSITORY_PACKAGE_NAME.name(), REPOSITORY_PACKAGE_NAME)
                        ));

        final Generator generator =
                DefaultGenerator
                        .builder()
                        .processingSchema(InternalProcessingSchema.EditConfiguration)
                        .build();
        return (CompilationUnit) generator
                .generate(configurationUnitBeforeEditing.clone(), context);
    }

    @ParameterizedTest(name = "Test for Spring Data annotation for {0} into configuration")
    @DisplayName("😎")
    @EnumSource(value = Storage.DbType.class)
    void testAddSpringDataAnnotationsForDbIntoConfiguration(Storage.DbType storageType) {
        var configurationUnitAfterEditing = generate(storageType);
        Assertions.assertEquals(
                1,
                configurationUnitAfterEditing.getImports().size() -
                        configurationUnitBeforeEditing.getImports().size(),
                "Number of added annotation imports is not 1");
        var _name = StringUtils.split(storageType.dbRepositoryConfigAnnotationClass(), ".");
        Assertions.assertEquals(
                _name[_name.length-1],
                configurationUnitAfterEditing.getImport(0).getName().getIdentifier(),
                "There is not import " + storageType.dbRepositoryConfigAnnotationClass());
        Assertions.assertTrue(
                configurationUnitAfterEditing.getTypes().size() == 1 &&
                        configurationUnitAfterEditing.getType(0).isClassOrInterfaceDeclaration(),
                "Not one class declaration in the unit");
        Assertions.assertTrue(
                configurationUnitAfterEditing.getClassByName("TestApplication").isPresent(),
                "There is not primary class in the unit");

        var configurationDeclarationAfterEditing =
                configurationUnitAfterEditing.getClassByName("TestApplication").get();
        Assertions.assertEquals(
                1,
                configurationDeclarationAfterEditing.getAnnotations().size() -
                        configurationDeclarationBeforeEditing.getAnnotations().size(),
                "Number of added annotations is not 1");

        var annotationName = annotationMap.get(storageType).getNameAsString();
        Assertions.assertTrue(
                configurationDeclarationAfterEditing.getAnnotationByName(annotationName).isPresent(),
                "There is not expected annotation " + annotationName + " in edited configuration");
        Assertions.assertEquals(
                annotationMap.get(storageType).asAnnotationExpr(),
                configurationDeclarationAfterEditing.getAnnotationByName(annotationName).get(),
                "There is not expected annotation " + annotationMap.get(storageType).asAnnotationExpr() +
                        " in edited configuration");
    }

    @ParameterizedTest(name = "Test for Spring Data annotation if no required db type had been set")
    @DisplayName("😎")
    @NullSource
    void configuringRepositoriesProcedureShouldBeFailedIfNoRequiredDbTypeHadBeenSet(Storage.DbType storageType) {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> generate(storageType),
                "NullPointerException was not thrown if no required value of db type had been set");
    }
}
