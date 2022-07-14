package com.home.origami.plugin.processing.registry;

import com.github.javaparser.ast.CompilationUnit;
import com.home.origami.plugin.db.DBClient;
import com.home.origami.plugin.processing.ProcessingUnit;
import com.home.origami.plugin.processing.registry.ProcessingUnitRegistry;
import com.home.origami.plugin.processing.registry.metadata.filter.MetadataFilter;
import com.home.origami.plugin.processing.registry.metadata.model.ProcessingUnitMetadataModel;
import com.home.origami.plugin.db.filter.Filter;
import com.home.origami.plugin.db.filter.StringFilterExpression;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProcessingUnitRegistryTest {
    private static final String TEST_PATH = "/test/path";
    private static final CompilationUnit TEST_UNIT = new CompilationUnit();
    private static final ProcessingUnit TEST_PROCESSING_UNIT = new ProcessingUnit(TEST_PATH, TEST_UNIT);
    private static final ProcessingUnitMetadataModel TEST_META = new ProcessingUnitMetadataModel(TEST_PATH);
    private static final Filter REGISTRY_FILTER = new MetadataFilter(new StringFilterExpression(String.format("{ \"path\": \"%s\" }", TEST_PATH)));

    @BeforeAll
    static void prepareEnv() {
        TEST_META.setPath(TEST_PATH);
        ProcessingUnitRegistry.save(TEST_PROCESSING_UNIT, TEST_META);
    }

    @AfterAll
    static void destroyEnv() {
        DBClient.INSTANCE.close();
    }

    @Test
    void testWriteToRegistry() {
        var path = "/another/path";
        var compilationUnit = new CompilationUnit();
        var processingUnit = new ProcessingUnit(path, compilationUnit);

        ProcessingUnitRegistry.save(processingUnit);

        var readProcessingUnit = ProcessingUnitRegistry.get(path);

        Assertions.assertNotNull(
                processingUnit,
                "Value from ProcessingUnitRegistry cannot be null");
        Assertions.assertEquals(
                path,
                readProcessingUnit.getId(),
                "Id of the registered unit is not equal to the written one");
        Assertions.assertEquals(
                processingUnit,
                readProcessingUnit,
                "Value from ProcessingUnitRegistry is not equal to the written one");
    }

    @Test
    void testReadOneFromRegistry() {
        var processingUnit = ProcessingUnitRegistry.get(TEST_PATH);

        Assertions.assertNotNull(
                processingUnit,
                "Value from ProcessingUnitRegistry cannot be null");
        Assertions.assertEquals(
                TEST_PATH,
                processingUnit.getId(),
                "Id of the registered unit is not equal to the written one");
        Assertions.assertEquals(
                TEST_PROCESSING_UNIT,
                processingUnit,
                "Value from ProcessingUnitRegistry is not equal to the written one");
    }

    @Test
    void testReadAllFromRegistry() {
        var processingUnits = ProcessingUnitRegistry.getAll();

        Assertions.assertNotNull(
                processingUnits,
                "Values from ProcessingUnitRegistry cannot be null");
        Assertions.assertFalse(
                processingUnits.isEmpty(),
                "ProcessingUnitRegistry cannot be empty");
        Assertions.assertEquals(
                TEST_PROCESSING_UNIT,
                processingUnits.get(0),
                "Value from ProcessingUnitRegistry is not equal to the written one");
    }

    @Test
    void testFindFromRegistry() {
        var processingUnits = ProcessingUnitRegistry.find(REGISTRY_FILTER);

        Assertions.assertNotNull(
                processingUnits,
                "Values from ProcessingUnitRegistry cannot be null");
        Assertions.assertFalse(
                processingUnits.isEmpty(),
                "ProcessingUnitRegistry cannot be empty");
        Assertions.assertEquals(
                processingUnits.size(),
                1,
                "ProcessingUnitRegistry contains more than one value");
        Assertions.assertEquals(
                TEST_PROCESSING_UNIT,
                processingUnits.get(0),
                "Value from ProcessingUnitRegistry is not equal to the written one");
    }
}
