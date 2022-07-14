package com.home.origami.plugin.processing.registry.metadata.model;

import com.home.origami.plugin.db.model.Model;

public class Document {
    private final Class<? extends Model> type;
    private final Model metadata;

    public Document(Model metadata) {
        this.type = metadata.getClass();
        this.metadata = metadata;
    }

    public Class<? extends Model> getType() {
        return type;
    }

    public Model getMetadata() {
        return metadata;
    }
}
