package com.home.servicegenerator.plugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.*;
import com.home.servicegenerator.api.ASTProcessingSchema;
import com.home.servicegenerator.plugin.generator.DefaultClassGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Path;


/**
 * Goal which generates microservice based on declared logic.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class ServiceGeneratorPlugin extends AbstractServiceGeneratorMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("F: "+ getAbsoluteBaseClassPath(getAbsoluteSourceDir(), getBaseServiceClass()));
        getLog().info("F2: "+ StringUtils.replaceChars(getBaseServiceClass(), ".", File.separator));

        final Path sourceClassLocation = getAbsoluteBaseClassPath(getAbsoluteSourceDir(), getBaseServiceClass());
        final Path targetClassLocation = getAbsoluteBaseClassPath(getAbsoluteSourceDir(), getBaseServiceClass());//!!!
        final File processingSchemaLocation = getProcessingSchema();//!!!

        save(
                generateBaseApiImplementation(sourceClassLocation.toFile(), processingSchemaLocation),
                targetClassLocation
        );
    }

    private CompilationUnit generateBaseApiImplementation(final File baseApiClass, final File processingSchema)
            throws MojoFailureException {
        if (isJavaClass(baseApiClass) && isJavaClass(processingSchema)) {
            try {
                ParseResult<CompilationUnit> baseApiClassParsingResult = new JavaParser().parse(baseApiClass);

                ASTProcessingSchema schema = (ASTProcessingSchema)getClass()
                        .getClassLoader()
                        .loadClass(processingSchema.getAbsolutePath())
                        .getDeclaredConstructor()
                        .newInstance();

                if (baseApiClassParsingResult.isSuccessful() && baseApiClassParsingResult.getResult().isPresent()) {
                    return (CompilationUnit)DefaultClassGenerator.builder()
                            .processingSchema(schema)
                            .build()
                            .generate(baseApiClassParsingResult.getResult().get(), null);
                } else {
                    throw new MojoFailureException("Invalid base class");
                }
            } catch (IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException |
                    IllegalAccessException | InvocationTargetException e) {
                throw new MojoFailureException("Cannot write generated class into " + getAbsoluteSourceDir());
            }
        } else {
            throw new MojoFailureException("Invalid base class");
        }
    }

    private void save(CompilationUnit targetCompilationUnit, Path targetLocation)
            throws MojoFailureException {
        targetCompilationUnit
                .setStorage(targetLocation, Charset.defaultCharset())
                .getStorage()
                .orElseThrow(() -> new MojoFailureException("Cannot write generated class into " + targetLocation))
                .save();
    }
}
