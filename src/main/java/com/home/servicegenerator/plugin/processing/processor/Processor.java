package com.home.servicegenerator.plugin.processing.processor;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.api.context.Context;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.processing.processor.statemachine.ProcessingStateMachine;
import com.home.servicegenerator.plugin.processing.strategy.ProcessingStrategy;
import com.home.servicegenerator.plugin.processing.registry.ProjectUnitsRegistry;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public class Processor {
    private final AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine;
    private final ProcessingStrategy processingStrategy;

    public Processor(ProcessingStrategy processingStrategy, AbstractStateMachine<ProcessingStateMachine, Stage, String, Context> stateMachine) {
        this.processingStrategy = processingStrategy;
        this.stateMachine = stateMachine;
    }

    public void process(ProcessingStrategy processingStrategy) {
        processingStrategy.getStrategy().accept(stateMachine);
    }
}
