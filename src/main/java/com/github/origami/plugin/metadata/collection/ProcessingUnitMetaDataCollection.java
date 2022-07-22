package com.github.origami.plugin.metadata.collection;

import com.github.origami.plugin.db.collection.Collection;
import com.github.origami.plugin.db.filter.FilterMapper;
import com.github.origami.plugin.metadata.model.ProcessingUnitMetaDataModel;

public class ProcessingUnitMetaDataCollection implements Collection<ProcessingUnitMetaDataModel> {
    private final FilterMapper filterMapper;

    public ProcessingUnitMetaDataCollection(FilterMapper filterMapper) {
        this.filterMapper = filterMapper;
    }

    @Override
    public String getCollectionName() {
        return "metadata";
    }

    @Override
    public Class<ProcessingUnitMetaDataModel> getType() {
        return ProcessingUnitMetaDataModel.class;
    }

    @Override
    public FilterMapper getFilterMapper() {
        return filterMapper;
    }
}
