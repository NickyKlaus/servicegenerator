package com.home.servicegenerator.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractServiceGeneratorMojo extends AbstractMojo {

    /**
     * Mappings for locations of base classes and corresponding processing schemas (location and class name).
     * Plugin uses processing schema mapped to base class to transform it and generate resulted version of this class.
     */
    @Parameter(name = "transformations")
    private Transformation[] transformations;

    /**
     * Location of the output directory.
     */
    @Parameter(defaultValue = "${project.build.directory}/target/generated-sources/swagger/src/main/java")
    private File projectOutputDirectory;

    /**
     * The base directory of the project.
     */
    @Parameter(defaultValue = "${basedir}", required = true)
    private File baseDirectory;

    /**
     * Location of the project sources directory.
     */
    @Parameter(defaultValue = "${sourceDir}")
    private File sourcesDirectory;

    public File getProjectOutputDirectory() {
        return projectOutputDirectory;
    }

    public File getBaseDirectory() {
        return baseDirectory;
    }

    public Transformation[] getTransformations() {
        return transformations;
    }

    public File getSourcesDirectory() {
        return sourcesDirectory;
    }

    public String getAbsoluteSourceDir() {
        return StringUtils.join(getBaseDirectory().getAbsolutePath(), File.separator, getSourcesDirectory().getAbsolutePath());
    }

    public Path createFileLocation(
            final String sourceDir, final String fullyQualifiedClassName, final String postfix, final String extension
    ) {
        String url = StringUtils.join(
                sourceDir,
                File.separator,
                StringUtils.replaceChars(fullyQualifiedClassName, ".", File.separator),
                postfix,
                ".",
                extension
        );
        return Paths.get(url);
    }
}
