package com.home.servicegenerator.plugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.*;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.plugin.generator.DefaultClassGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Goal which generates microservice based on declared logic.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ServiceGeneratorPlugin extends AbstractServiceGeneratorMojo {
    private static final String JAVA_EXT = "java";

    private CompilationUnit parseSourceFile(final File source) throws MojoFailureException {
        if (source == null || !Files.exists(source.toPath(), LinkOption.NOFOLLOW_LINKS)) {
            return new CompilationUnit();
        }
        try {
            ParseResult<CompilationUnit> baseUnitParsingResult = new JavaParser().parse(source);
            if (baseUnitParsingResult.isSuccessful() && baseUnitParsingResult.getResult().isPresent()) {
                getLog().info("Parse result successful: " + baseUnitParsingResult.isSuccessful());
                return baseUnitParsingResult.getResult().get();
            }
        } catch (FileNotFoundException e) {
            throw new MojoFailureException("Cannot find source file " + source.getAbsolutePath(), e);
        }
        return new CompilationUnit();
    }

    private CompilationUnit generate(final CompilationUnit baseUnit, final ASTProcessingSchema processingSchema) {
        return (CompilationUnit)DefaultClassGenerator.builder()
                .processingSchema(processingSchema)
                .build()
                .generate(baseUnit, null);
    }

    private void save(final CompilationUnit targetCompilationUnit, final Path targetLocation) throws MojoFailureException {
        targetCompilationUnit
                .setStorage(targetLocation, Charset.defaultCharset())
                .getStorage()
                .orElseThrow(() -> new MojoFailureException("Cannot write generated class into " + targetLocation))
                .save();
    }

    private void execute_(final Transformation transformation) throws MojoFailureException {
        final File sourceClassFile =
                StringUtils.isBlank(transformation.getBaseClassName()) ?
                        null :
                        createFileLocation(
                                transformation.getBaseClassLocation(),
                                transformation.getBaseClassName(),
                                "",
                                JAVA_EXT).toFile();
        final Path targetClassPath =
                createFileLocation(
                        getProjectOutputDirectory().getAbsolutePath(),
                        transformation.getBaseClassName(),
                        transformation.getPostfix(),
                        JAVA_EXT
                );

        try {
            Object _processingSchemaInstance = URLClassLoader.newInstance(
                    new URL[]{transformation.getProcessingSchemaLocation().toURI().toURL()}, getClass().getClassLoader())
                    .loadClass(transformation.getProcessingSchemaClass())
                    .getDeclaredConstructor()
                    .newInstance();
            ASTProcessingSchema schema;
            if (_processingSchemaInstance instanceof ASTProcessingSchema) {
                schema = (ASTProcessingSchema) _processingSchemaInstance;
            } else {
                throw new MojoFailureException("Invalid processing schema found: " + _processingSchemaInstance);
            }
            getLog().info("AST schema: " + schema);

            getLog().info("Generation interrupted because target class <" + targetClassPath + "> already exists.");
            save(
                    generate(parseSourceFile(sourceClassFile), schema),
                    targetClassPath
            );
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException e) {
            getLog().error("Cannot create generated class into " + targetClassPath, e);
            throw new MojoFailureException("Cannot create generated class into " + targetClassPath, e);
        }
    }

    @Override
    public void execute() throws MojoFailureException {
        final List<Transformation> classTransformations = Arrays.asList(getTransformations());
        if (!classTransformations.isEmpty()) {
            for (final Transformation transformation : classTransformations) {
                execute_(transformation);
            }
        } else {
            throw new MojoFailureException("Cannot find transformations for processing classes!");
        }
    }
}
