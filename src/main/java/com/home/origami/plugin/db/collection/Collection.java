package com.home.origami.plugin.db.collection;

import com.home.origami.plugin.db.DBClient;
import com.home.origami.plugin.db.filter.FilterMapper;
import com.home.origami.plugin.db.model.Model;
import com.home.origami.plugin.db.filter.Filter;

import org.dizitart.no2.common.WriteResult;

import java.util.List;
import java.util.Optional;

import static org.dizitart.no2.filters.FluentFilter.where;

public interface Collection<T extends Model> {
    String getCollectionName();

    Class<T> getType();

    FilterMapper getFilterMapper();

    default List<T> getAll() {
        return DBClient.getRepository(getCollectionName(), getType()).find().toList();
    }

    default List<T> find(String fieldName, Object fieldValue) {
        return DBClient
                .getRepository(getCollectionName(), getType())
                .find(where(fieldName).eq(fieldValue))
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
                        .find(where(fieldName).eq(fieldValue))
                        .firstOrNull());
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
