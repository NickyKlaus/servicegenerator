package com.home.servicegenerator.plugin.processing.registry;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.plugin.processing.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.registry.meta.Db;
import com.home.servicegenerator.plugin.processing.registry.meta.collection.Collection;
import com.home.servicegenerator.plugin.processing.registry.meta.collection.MetadataCollection;
import com.home.servicegenerator.plugin.processing.registry.meta.filter.Filter;
import com.home.servicegenerator.plugin.processing.registry.meta.filter.MetadataToObjectFilterMapper;
import com.home.servicegenerator.plugin.processing.registry.meta.model.ProcessingUnitMetaModel;

import org.dizitart.no2.WriteResult;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Processing units registry
 */
public class Registry implements AutoCloseable {
    public static final Registry INSTANCE = new Registry();
    private static final Collection<ProcessingUnitMetaModel> metadata = new MetadataCollection(new MetadataToObjectFilterMapper());
    private static final Map<String, ProcessingUnit> cache = Collections.synchronizedMap(new HashMap<>());

    private Registry() {
    }

    public void close() {
        synchronized (cache) {
            Db.close();
        }
    }

    public static void save(ProcessingUnit unit, ProcessingUnitMetaModel metadata) {
        synchronized (cache) {
            WriteResult result = Registry.metadata.save(metadata);
            if (result.getAffectedCount() > 0) {
                cache.put(unit.getId(), unit);
            }
            Db.commit();
        }
    }

    public static void save(ProcessingUnit unit) {
        synchronized (cache) {
            cache.put(unit.getId(), unit);
        }
    }

    public static ProcessingUnit get(String path) {
        synchronized (cache) {
            return cache.get(path);
        }
    }

    public static ProcessingUnit getOrDefault(String path) {
        synchronized (cache) {
            return cache.getOrDefault(path, new ProcessingUnit(path, new CompilationUnit().setStorage(Path.of(path))));
        }
    }

    public static List<ProcessingUnit> getAll() {
        synchronized (cache) {
            return cache.values().stream().collect(Collectors.toUnmodifiableList());
        }
    }

    public static List<ProcessingUnit> find(Filter filter) {
        synchronized (cache) {
            return metadata
                    .find(filter)
                    .stream()
                    .map(ProcessingUnitMetaModel::getPath)
                    .filter(cache::containsKey)
                    .map(cache::get)
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    public static List<ProcessingUnitMetaModel> findMetadata(Filter filter) {
        synchronized (cache) {
            return metadata.find(filter);
        }
    }

    public static Optional<ProcessingUnitMetaModel> getMetadata(String id) {
        synchronized (cache) {
            return metadata.getByField("path", id);
        }
    }
}
