package com.home.servicegenerator.plugin;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Objects;

public class Dependency {
    private String groupId;
    private String artifactId;
    private String version;

    public Dependency() {}

    public Dependency(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static Dependency of(String groupId, String artifactId) {
        return new Dependency(groupId, artifactId, null);
    }

    public static Dependency of(String groupId, String artifactId, String version) {
        return new Dependency(groupId, artifactId, version);
    }

    public static Dependency of(String description) {
        Objects.requireNonNull(description, "Dependency descriptor must not be null");
        if (!description.contains(":") || StringUtils.countMatches(description, ":") < 2 ||
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
}
