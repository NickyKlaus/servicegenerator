package com.home.servicegenerator.plugin.processing.registry.meta.collection;

import com.home.servicegenerator.plugin.processing.registry.meta.Db;
import com.home.servicegenerator.plugin.processing.registry.meta.filter.Filter;
import com.home.servicegenerator.plugin.processing.registry.meta.filter.ObjectFilterMapper;
import com.home.servicegenerator.plugin.processing.registry.meta.model.MetaModel;

import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.List;
import java.util.Optional;

public interface Collection<T extends MetaModel> {
    String getCollectionName();

    Class<T> getType();

    ObjectFilterMapper getFilterMapper();

    default List<T> getAll() {
        return Db.getRepository(getCollectionName(), getType()).find().toList();
    }

    default List<T> find(String fieldName, Object fieldValue) {
        return Db
                .getRepository(getCollectionName(), getType())
                .find(ObjectFilters.eq(fieldName, fieldValue))
                .toList();
    }

    default List<T> find(Filter filter) {
        return Db
                .getRepository(getCollectionName(), getType())
                .find(getFilterMapper().map(filter))
                .toList();
    }

    default Optional<T> getByField(String fieldName, Object fieldValue) {
        return Optional.ofNullable(
                Db.getRepository(getCollectionName(), getType())
                        .find(ObjectFilters.eq(fieldName, fieldValue))
                        .firstOrDefault());
    }

    default WriteResult save(T item) {
        return Db
                .getRepository(getCollectionName(), getType())
                .update(item, true);
    }

    default WriteResult save(T[] items) {
        return Db
                .getRepository(getCollectionName(), getType())
                .insert(items);
    }
}
