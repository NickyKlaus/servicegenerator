package com.home.servicegenerator.plugin.processing.processor;

import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.processor.strategy.ProcessingStrategy;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Component
public class Processor {
    private final StateMachine<Stage, String> stateMachine;

    public Processor(StateMachine<Stage, String> stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void process(ProcessingStrategy processingStrategy) {
        processingStrategy.getStrategy().accept(stateMachine);
        stateMachine.startReactively().block();
    }
}
