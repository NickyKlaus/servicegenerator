package com.home.origami.plugin.metadata.model;

import com.home.origami.plugin.db.model.Model;

import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.common.mapper.Mappable;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Embedded;
import org.dizitart.no2.repository.annotations.Index;
import org.dizitart.no2.repository.annotations.Indices;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Objects;

public class Dependency implements Mappable, Model {
    @Embedded(fieldName = "groupId", order = 0)
    private String groupId;
    @Embedded(fieldName = "artifactId", order = 1)
    private String artifactId;
    @Embedded(fieldName = "version", order = 2)
    private String version;
    private String type = "jar";
    private String classifier;
    private String scope;
    private String systemPath;
    private Exclusion[] exclusions;
    private String optional;

    public Dependency() {}

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSystemPath() {
        return systemPath;
    }

    public void setSystemPath(String systemPath) {
        this.systemPath = systemPath;
    }

    public Exclusion[] getExclusions() {
        return exclusions;
    }

    public void setExclusions(Exclusion[] exclusions) {
        this.exclusions = exclusions;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public static Dependency of(String groupId, String artifactId) {
        var dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        return dependency;
    }

    public static Dependency of(String groupId, String artifactId, String version) {
        var dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        return dependency;
    }

    public static Dependency of(String description) {
        Objects.requireNonNull(description, "Dependency descriptor must not be null");
        if (!description.contains(":") || StringUtils.split(description, ":").length < 2 ||
                StringUtils.isEmpty(StringUtils.split(description, ":")[0]) ||
                StringUtils.isEmpty(StringUtils.split(description, ":")[1])) {
            throw new IllegalArgumentException("Dependency descriptor must contain at least non empty groupId and artifactId separated by a colon");
        }
        final String[] descriptorParts = StringUtils.split(description, ":");
        final Dependency dependency =
                Dependency.of(StringUtils.split(description, ":")[0],
                        StringUtils.split(description, ":")[1]);
        if (descriptorParts.length > 2 &&
                StringUtils.isNoneEmpty(StringUtils.split(description, ":")[2])) {
            dependency.setVersion(StringUtils.split(description, ":")[2]);
        }
        return dependency;
    }

    public static Node createDependencyNode(final Dependency dependency, final Document document) {
        Objects.requireNonNull(dependency, "Dependency must not be null");
        Objects.requireNonNull(document, "XML document node must not be null");

        final Element dependencyElement = document.createElement("dependency");
        final Element groupIdElement = document.createElement("groupId");
        groupIdElement.appendChild(document.createTextNode(dependency.getGroupId()));
        final Element artifactIdElement = document.createElement("artifactId");
        artifactIdElement.appendChild(document.createTextNode(dependency.getArtifactId()));
        dependencyElement.appendChild(groupIdElement);
        dependencyElement.appendChild(artifactIdElement);
        if (StringUtils.isNoneEmpty(dependency.getVersion())) {
            final Element versionElement = document.createElement("version");
            versionElement.appendChild(document.createTextNode(dependency.getVersion()));
            dependencyElement.appendChild(versionElement);
        }
        return dependencyElement;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Dependency that = (Dependency) other;
        return groupId.equals(that.groupId) && artifactId.equals(that.artifactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

    @Override
    public org.dizitart.no2.collection.Document write(NitriteMapper mapper) {
        var document = org.dizitart.no2.collection.Document.createDocument();
        document.put("groupId", getGroupId());
        document.put("argumentId", getArtifactId());
        document.put("version", getVersion());
        document.put("type", getType());
        document.put("scope", getScope());
        document.put("classifier", getClassifier());
        document.put("systemPath", getSystemPath());
        document.put("optional", getOptional());
        if (getExclusions() != null && getExclusions().length != 0) {
            document.put("exclusions", getExclusions());
        }
        return document;
    }

    @Override
    public void read(NitriteMapper mapper, org.dizitart.no2.collection.Document document) {
        if (document != null) {
            setGroupId(document.get("groupId", String.class));
            setArtifactId(document.get("argumentId", String.class));
            setVersion(document.get("version", String.class));
            setType(document.get("type", String.class));
            setScope(document.get("scope", String.class));
            setClassifier(document.get("classifier", String.class));
            setSystemPath(document.get("systemPath", String.class));
            setOptional(document.get("optional", String.class));
            var exclusions = document.get("exclusions", Exclusion[].class);
            if (exclusions != null && exclusions.length != 0) {
                setExclusions(exclusions);
            }
        }
    }
}
