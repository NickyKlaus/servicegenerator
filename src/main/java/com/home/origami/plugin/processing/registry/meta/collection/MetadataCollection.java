package com.home.origami.plugin.processing.registry.meta.collection;

import com.home.origami.plugin.processing.registry.meta.filter.ObjectFilterMapper;
import com.home.origami.plugin.processing.registry.meta.model.ProcessingUnitMetaModel;

public class MetadataCollection implements Collection<ProcessingUnitMetaModel> {
    private final ObjectFilterMapper filterMapper;

    public MetadataCollection(ObjectFilterMapper filterMapper) {
        this.filterMapper = filterMapper;
    }

    @Override
    public String getCollectionName() {
        return "metadata";
    }

    @Override
    public Class<ProcessingUnitMetaModel> getType() {
        return ProcessingUnitMetaModel.class;
    }

    @Override
    public ObjectFilterMapper getFilterMapper() {
        return filterMapper;
    }
}
