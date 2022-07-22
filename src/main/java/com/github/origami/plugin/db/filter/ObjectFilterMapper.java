package com.github.origami.plugin.db.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.dizitart.no2.filters.FluentFilter;

import java.util.HashMap;
import java.util.Map;

public class ObjectFilterMapper implements FilterMapper {
    @Override
    public org.dizitart.no2.filters.Filter map(Filter filter) {
        try {
            var filterObjectMap =
                    new ObjectMapper().readValue(filter.getExpression().asString(), HashMap.class);
            return createFilter(filterObjectMap);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Wrong mapping: ", ex);
        }
    }

    // Zero level of includes is allowed
    private org.dizitart.no2.filters.Filter createFilter(Map<?, ?> filterObjectMap) {
        var filters =
                filterObjectMap
                        .entrySet()
                        .stream()
                        .map(entry -> FluentFilter.where(entry.getKey().toString()).eq(entry.getValue()))
                        .toArray(org.dizitart.no2.filters.Filter[]::new);
        if (filters.length == 1) {
            return filters[0];
        }
        return org.dizitart.no2.filters.Filter.and(filters);
    }
}
