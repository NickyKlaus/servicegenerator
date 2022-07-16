package com.home.origami.plugin.processing.registry;

import com.github.javaparser.ast.CompilationUnit;
import com.home.origami.plugin.processing.ProcessingUnit;
import com.home.origami.plugin.db.DBClient;
import com.home.origami.plugin.metadata.model.ProcessingUnitMetaDataModel;
import com.home.origami.plugin.db.collection.Collection;
import com.home.origami.plugin.metadata.collection.ProcessingUnitMetaDataCollection;
import com.home.origami.plugin.db.filter.Filter;
import com.home.origami.plugin.db.filter.MetaDataFilterMapper;

import org.dizitart.no2.common.WriteResult;

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
public class ProcessingUnitRegistry {
    private static final Collection<ProcessingUnitMetaDataModel> metadata = new ProcessingUnitMetaDataCollection(new MetaDataFilterMapper());
    private static final Map<String, ProcessingUnit> cache = Collections.synchronizedMap(new HashMap<>());

    private ProcessingUnitRegistry() {
    }

    public static void save(ProcessingUnit unit, ProcessingUnitMetaDataModel metadata) {
        synchronized (cache) {
            WriteResult result = ProcessingUnitRegistry.metadata.save(metadata);
            if (result.getAffectedCount() > 0) {
                cache.put(unit.getId(), unit);
            }
            DBClient.commit();
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
                    .map(ProcessingUnitMetaDataModel::getPath)
                    .filter(cache::containsKey)
                    .map(cache::get)
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    public static List<ProcessingUnitMetaDataModel> findMetadata(Filter filter) {
        synchronized (cache) {
            return metadata.find(filter);
        }
    }

    public static Optional<ProcessingUnitMetaDataModel> getMetadata(String id) {
        synchronized (cache) {
            return metadata.getByField("path", id);
        }
    }
}
