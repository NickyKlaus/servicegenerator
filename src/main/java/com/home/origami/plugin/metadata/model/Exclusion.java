package com.home.origami.plugin.metadata.model;

import com.home.origami.plugin.db.model.Model;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.Mappable;
import org.dizitart.no2.common.mapper.NitriteMapper;

import java.util.Objects;

public class Exclusion implements Mappable, Model {
    private String groupId;
    private String artifactId;

    public Exclusion() {
    }

    public Exclusion(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public static Exclusion of(String groupId, String artifactId) {
        return new Exclusion(groupId, artifactId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exclusion exclusion = (Exclusion) o;
        return groupId.equals(exclusion.groupId) && artifactId.equals(exclusion.artifactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

    @Override
    public Document write(NitriteMapper mapper) {
        var document = Document.createDocument();
        document.put("groupId", getGroupId());
        document.put("artifactId", getArtifactId());
        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        if (document != null) {
            setGroupId(document.get("groupId", String.class));
            setArtifactId(document.get("artifactId", String.class));
        }
    }
}
