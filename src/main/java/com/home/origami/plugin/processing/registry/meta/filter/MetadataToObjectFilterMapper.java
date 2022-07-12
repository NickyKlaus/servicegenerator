package com.home.origami.plugin.processing.registry.meta.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.HashMap;
import java.util.Map;

public class MetadataToObjectFilterMapper implements ObjectFilterMapper {
    @Override
    public ObjectFilter map(Filter filter) {
        try {
            var filterObjectMap =
                    new ObjectMapper().readValue(filter.getExpression().asString(), HashMap.class);
            return createObjectFilter(filterObjectMap);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Wrong metadata mapping: ", ex);
        }
    }

    // Zero level of includes is allowed
    private ObjectFilter createObjectFilter(Map<?, ?> filterObjectMap) {
        return ObjectFilters.and(
                filterObjectMap
                        .entrySet()
                        .stream()
                        .map(entry -> ObjectFilters.eq(entry.getKey().toString(), entry.getValue()))
                        .toArray(ObjectFilter[]::new));
    }
}
