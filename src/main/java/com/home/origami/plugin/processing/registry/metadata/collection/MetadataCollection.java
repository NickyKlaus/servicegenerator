package com.home.origami.plugin.processing.registry.metadata.collection;

import com.home.origami.plugin.db.collection.Collection;
import com.home.origami.plugin.db.filter.ObjectFilterMapper;
import com.home.origami.plugin.processing.registry.metadata.model.ProcessingUnitMetadataModel;

public class MetadataCollection implements Collection<ProcessingUnitMetadataModel> {
    private final ObjectFilterMapper filterMapper;

    public MetadataCollection(ObjectFilterMapper filterMapper) {
        this.filterMapper = filterMapper;
    }

    @Override
    public String getCollectionName() {
        return "metadata";
    }

    @Override
    public Class<ProcessingUnitMetadataModel> getType() {
        return ProcessingUnitMetadataModel.class;
    }

    @Override
    public ObjectFilterMapper getFilterMapper() {
        return filterMapper;
    }
}
