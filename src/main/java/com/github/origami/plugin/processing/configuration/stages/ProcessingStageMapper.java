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
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.getIfEmpty;

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
                        .processingUnitBasePackage(transformation.getTargetClassPackage())
                        .processingUnitLocation(
                                ctx -> Path.of(
                                        transformation.getTargetDirectory(),
                                        StringUtils.replaceChars(transformation.getTargetClassPackage(), ".", File.separator),
                                        transformation.getSourceClassName()).toString())
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
        var _stage = ProcessingStage.builder()
                .name(stage.getName())
                .processingSchema(stage.getProcessingSchema())
                .context(stage.getContext())
                .processingUnitBasePackage(StringUtils.replaceChars(configuration.getBaseLocation(), ".", "/"))
                .processingUnitLocation(stage.getProcessingUnitLocation())
                //.processingUnitName(stage.getProcessingUnitName())
                .processingUnitType(stage.getProcessingUnitType())
                .postProcessingAction(stage.getPostProcessingAction())
                .namingStrategy(stage.getNamingStrategy())
                .executingCondition(stage.getExecutingCondition())
                .build();
        LOG.info("!!!STAGE:"+_stage.getProcessingUnitBasePackage());
        return _stage;
    }
}
