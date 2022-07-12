package com.home.servicegenerator.plugin.processing.registry.meta;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.plugin.processing.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.registry.Registry;
import com.home.servicegenerator.plugin.processing.registry.meta.filter.Filter;
import com.home.servicegenerator.plugin.processing.registry.meta.filter.MetadataFilter;
import com.home.servicegenerator.plugin.processing.registry.meta.filter.StringFilterExpression;
import com.home.servicegenerator.plugin.processing.registry.meta.model.ProcessingUnitMetaModel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DbTest {
    private static final String TEST_PATH = "/test/path";
    private static final CompilationUnit TEST_UNIT = new CompilationUnit();
    private static final ProcessingUnit TEST_PROCESSING_UNIT = new ProcessingUnit(TEST_PATH, TEST_UNIT);
    private static final ProcessingUnitMetaModel TEST_META = new ProcessingUnitMetaModel(TEST_PATH);
    private static final Filter REGISTRY_FILTER = new MetadataFilter(new StringFilterExpression(String.format("{ \"path\": \"%s\" }", TEST_PATH)));

    @BeforeAll
    static void prepareEnv() {
        TEST_META.setPath(TEST_PATH);
        Registry.save(TEST_PROCESSING_UNIT, TEST_META);
    }

    @AfterAll
    static void destroyEnv() {
        Registry.INSTANCE.close();
    }

    @Test
    void testWriteToRegistry() {
        var path = "/another/path";
        var compilationUnit = new CompilationUnit();
        var processingUnit = new ProcessingUnit(path, compilationUnit);

        Registry.save(processingUnit);

        var readProcessingUnit = Registry.get(path);

        Assertions.assertNotNull(
                processingUnit,
                "Value from Registry cannot be null");
        Assertions.assertEquals(
                path,
                readProcessingUnit.getId(),
                "Id of the registered unit is not equal to the written one");
        Assertions.assertEquals(
                processingUnit,
                readProcessingUnit,
                "Value from Registry is not equal to the written one");
    }

    @Test
    void testReadOneFromRegistry() {
        var processingUnit = Registry.get(TEST_PATH);

        Assertions.assertNotNull(
                processingUnit,
                "Value from Registry cannot be null");
        Assertions.assertEquals(
                TEST_PATH,
                processingUnit.getId(),
                "Id of the registered unit is not equal to the written one");
        Assertions.assertEquals(
                TEST_PROCESSING_UNIT,
                processingUnit,
                "Value from Registry is not equal to the written one");
    }

    @Test
    void testReadAllFromRegistry() {
        var processingUnits = Registry.getAll();

        Assertions.assertNotNull(
                processingUnits,
                "Values from Registry cannot be null");
        Assertions.assertFalse(
                processingUnits.isEmpty(),
                "Registry cannot be empty");
        Assertions.assertEquals(
                TEST_PROCESSING_UNIT,
                processingUnits.get(0),
                "Value from Registry is not equal to the written one");
    }

    @Test
    void testFindFromRegistry() {
        var processingUnits = Registry.find(REGISTRY_FILTER);

        Assertions.assertNotNull(
                processingUnits,
                "Values from Registry cannot be null");
        Assertions.assertFalse(
                processingUnits.isEmpty(),
                "Registry cannot be empty");
        Assertions.assertEquals(
                processingUnits.size(),
                1,
                "Registry contains more than one value");
        Assertions.assertEquals(
                TEST_PROCESSING_UNIT,
                processingUnits.get(0),
                "Value from Registry is not equal to the written one");
    }
}
