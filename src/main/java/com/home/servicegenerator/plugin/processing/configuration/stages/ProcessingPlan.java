package com.home.servicegenerator.plugin.processing.configuration.stages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ProcessingPlan {
    private static final ProcessingPlan PROCESSING_PLAN = new ProcessingPlan();
    private static final List<Stage> stages = Collections.synchronizedList(new ArrayList<>());

    private ProcessingPlan() {
    }

    public static ProcessingPlan processingPlan() {
        return PROCESSING_PLAN;
    }

    public ProcessingPlan stage(final InnerProcessingStage stage) {
        stages.add(stage);
        return PROCESSING_PLAN;
    }

    public List<Stage> getProcessingStages() {
        return Collections.unmodifiableList(stages);
    }
}
