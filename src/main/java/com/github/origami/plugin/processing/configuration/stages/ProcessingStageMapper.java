package com.github.origami.plugin.processing.configuration.stages;

import com.github.origami.api.ASTProcessingSchema;
import com.github.origami.plugin.Transformation;
import com.github.origami.plugin.TransformationProperty;
import com.github.origami.plugin.processing.configuration.ProcessingConfiguration;
import com.github.origami.plugin.processing.configuration.context.properties.ComponentType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Collectors;

public class ProcessingStageMapper {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingStageMapper.class);

    public Stage fromTransformation(Transformation transformation) {
        try (var classLoader =
                     URLClassLoader.newInstance(
                             new URL[]{transformation.getProcessingSchemaLocation().toURI().toURL()},
                             getClass().getClassLoader())) {
            var processingSchemaInstance =
                    classLoader
                            .loadClass(transformation.getProcessingSchemaClass())
                            .getDeclaredConstructor()
                            .newInstance();

            if (processingSchemaInstance instanceof ASTProcessingSchema) {
                return ProcessingStage.builder()
                        .name(transformation.getProcessingSchemaClass())
                        .processingUnitName(st -> transformation.getTargetClassName())
                        .processingUnitType(ComponentType.UNKNOWN.toString())
                        .processingUnitBasePackage(StringUtils.replaceChars(transformation.getTargetClassPackage(), ".", File.separator))
                        .processingUnitLocation(ctx -> transformation.getTargetDirectory())
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
                LOG.error("Invalid processing schema found: " + transformation.getProcessingSchemaLocation());
            }
        } catch (IOException | ClassNotFoundException | ClassCastException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("Cannot map transformation to processing stage", e);
        }
        return null;
    }

    public Stage fromStage(Stage stage, ProcessingConfiguration configuration) {
        return ProcessingStage.builder()
                .name(stage.getName())
                .nonGeneration(stage.isNonGeneration())
                .processingSchema(stage.getProcessingSchema())
                .context(stage.getContext())
                .processingUnitBasePackage(stage.getProcessingUnitBasePackage())
                .processingUnitLocation(ctx -> StringUtils.replaceChars(configuration.getBaseLocation(), ".", File.separator))
                .processingUnitName(stage.getProcessingUnitName())
                .processingUnitType(stage.getProcessingUnitType())
                .postProcessingAction(stage.getPostProcessingAction())
                .namingStrategy(stage.getNamingStrategy())
                .executingCondition(stage.getExecutingCondition())
                .build();
    }
}
