package com.github.origami.plugin.processing.configuration.stages;

import com.github.origami.api.ASTProcessingSchema;
import com.github.origami.plugin.Transformation;
import com.github.origami.plugin.TransformationProperty;
import com.github.origami.plugin.processing.configuration.context.properties.ComponentType;

import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Collectors;

public class TransformationToProcessingStageMapper {
    public Stage toStage(Transformation transformation) throws MojoFailureException {
        try (var classLoader = URLClassLoader.newInstance(new URL[]{transformation.getProcessingSchemaLocation().toURI().toURL()}, getClass().getClassLoader())) {
            var processingSchemaInstance =
                    classLoader
                            .loadClass(transformation.getProcessingSchemaClass())
                            .getDeclaredConstructor()
                            .newInstance();

            if (processingSchemaInstance instanceof ASTProcessingSchema) {
                return ProcessingStage.builder()
                        .name(transformation.getProcessingSchemaClass())
                        .processingUnitName(transformation.getTargetClassName())
                        .processingUnitType(ComponentType.UNKNOWN.toString())
                        .processingUnitBasePackage(transformation.getTargetClassPackage())
                        .processingUnitLocation(transformation.getTargetDirectory())
                        .processingSchema((ASTProcessingSchema) processingSchemaInstance)
                        .context(
                                transformation
                                        .getTransformationProperties()
                                        .stream()
                                        .collect(Collectors.toUnmodifiableMap(
                                                TransformationProperty::getName,
                                                TransformationProperty::getValue,
                                                (s, a) -> s)))
                        .build();
            } else {
                throw new MojoFailureException("Invalid processing schema found: " + processingSchemaInstance);
            }
        } catch (IOException | ClassNotFoundException | ClassCastException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new MojoFailureException("Cannot map transformation to processing stage", e);
        }
    }
}
