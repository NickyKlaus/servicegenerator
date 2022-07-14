package com.home.origami.plugin.db.collection;

import com.home.origami.plugin.db.DBClient;
import com.home.origami.plugin.db.filter.ObjectFilterMapper;
import com.home.origami.plugin.db.model.Model;
import com.home.origami.plugin.db.filter.Filter;

import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.List;
import java.util.Optional;

public interface Collection<T extends Model> {
    String getCollectionName();

    Class<T> getType();

    ObjectFilterMapper getFilterMapper();

    default List<T> getAll() {
        return DBClient.getRepository(getCollectionName(), getType()).find().toList();
    }

    default List<T> find(String fieldName, Object fieldValue) {
        return DBClient
                .getRepository(getCollectionName(), getType())
                .find(ObjectFilters.eq(fieldName, fieldValue))
                .toList();
    }

    default List<T> find(Filter filter) {
        return DBClient
                .getRepository(getCollectionName(), getType())
                .find(getFilterMapper().map(filter))
                .toList();
    }

    default Optional<T> getByField(String fieldName, Object fieldValue) {
        return Optional.ofNullable(
                DBClient.getRepository(getCollectionName(), getType())
                        .find(ObjectFilters.eq(fieldName, fieldValue))
                        .firstOrDefault());
    }

    default WriteResult save(T item) {
        return DBClient
                .getRepository(getCollectionName(), getType())
                .update(item, true);
    }

    default WriteResult save(T[] items) {
        return DBClient
                .getRepository(getCollectionName(), getType())
                .insert(items);
    }
}
