package com.home.servicegenerator.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.io.FilenameUtils.getExtension;

public abstract class AbstractServiceGeneratorMojo extends AbstractMojo {

    /**
     * The Maven Project object.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * Location of the output directory.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/service-generator",
            property = "service.generator.maven.plugin.output")
    private File projectOutputDirectory;

    /**
     * The base directory of the project.
     */
    @Parameter(defaultValue = "${basedir}", required = true, readonly = true)
    private File baseDir;

    /**
     * The base java class or interface that declares service with business logic for generating microservice.
     */
    @Parameter(defaultValue = "${baseServiceClass}", required = true, readonly = true)
    private String baseServiceClass;

    /**
     * Location of the project sources directory.
     */
    @Parameter(defaultValue = "${sourceDir}", readonly = true)
    private File sourceDir;

    /**
     * Processing schema of an abstract syntax tree of the base class which defines the actions being applied for
     * corresponding node of the base one.
     */
    @Parameter(defaultValue = "${processingSchema}", required = true, readonly = true)
    private File processingSchema;

    public MavenProject getProject() {
        return project;
    }

    public File getProjectOutputDirectory() {
        return projectOutputDirectory;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public String getBaseServiceClass() {
        return baseServiceClass;
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public File getProcessingSchema() {
        return processingSchema;
    }

    public Path getAbsoluteSourceDir() {
        return Paths.get(getBaseDir().getAbsolutePath(), File.separator, getSourceDir().getAbsolutePath());
    }

    public Path getAbsoluteBaseClassPath(final Path sourceDir, final String fullyQualifiedBaseClassName) {
        String url = sourceDir.toString() + File.separator +
                StringUtils.replaceChars(fullyQualifiedBaseClassName, ".", File.separator) + ".java";
        return Paths.get(url);
    }

    /**
     * Get file by its path
     * @param path Path of the file
     * @return File object by the file path
     */
    protected File getBaseClassFile(final Path path) {
        return path.toFile();
    }

    /**
     * Check if a file has '.class' extension.
     * @param file Path of file to check
     * @return boolean result of checking
     */
    protected boolean isJavaClass(final File file) {
        return file != null && file.isFile() && "java".equals(getExtension(file.getName()));
    }
}
