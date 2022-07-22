package com.github.origami.plugin.processing.configuration.stages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ProcessingPlan {
    private final List<Stage> stages = Collections.synchronizedList(new ArrayList<>());

    private ProcessingPlan() {
    }

    public static ProcessingPlan processingPlan() {
        return new ProcessingPlan();
    }

    public ProcessingPlan stage(final Stage stage) {
        stages.add(stage);
        return this;
    }

    public List<Stage> getProcessingStages() {
        return Collections.unmodifiableList(stages);
    }
}
