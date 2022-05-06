package com.home.servicegenerator.plugin.processing.processor.strategy;

import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.events.ProcessingEvent;
import org.springframework.statemachine.StateMachine;

import java.util.function.Consumer;

@FunctionalInterface
public interface ProcessingStrategy {
    Consumer<StateMachine<Stage, String>> getStrategy();
}
