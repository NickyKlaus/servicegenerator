package com.home.servicegenerator.plugin.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.io.FilenameUtils.normalizeNoEndSeparator;

public final class FileUtils {
    public static Path createFilePath(
            final String projectBaseDir, final String sourceDir, final String basePackage, final String componentPackage, final String componentName
    ) {
        return Path.of(
                createDirPath(projectBaseDir, sourceDir, basePackage, componentPackage).toString(), componentName + ".java");
    }

    public static Path createDirPath(
            final String projectBaseDir, final String sourceDir, final String basePackage, final String componentPackage
    ) {
        return Paths.get(
                StringUtils.joinWith(File.separator,
                        normalizeNoEndSeparator(projectBaseDir),
                        normalizeNoEndSeparator(sourceDir),
                        normalizeNoEndSeparator(StringUtils.replaceChars(basePackage, ".", File.separator)),
                        normalizeNoEndSeparator(StringUtils.replaceChars(componentPackage, ".", File.separator))));
    }

    private FileUtils() { }
}
