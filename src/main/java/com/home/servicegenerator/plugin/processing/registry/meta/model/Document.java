package com.home.servicegenerator.plugin.processing.registry.meta.model;

public class Document {
    private final Class<? extends MetaModel> type;
    private final MetaModel metadata;

    public Document(MetaModel metadata) {
        this.type = metadata.getClass();
        this.metadata = metadata;
    }

    public Class<? extends MetaModel> getType() {
        return type;
    }

    public MetaModel getMetadata() {
        return metadata;
    }
}
