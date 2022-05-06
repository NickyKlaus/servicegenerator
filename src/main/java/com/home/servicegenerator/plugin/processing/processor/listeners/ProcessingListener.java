package com.home.servicegenerator.plugin.processing.processor.listeners;

import com.github.javaparser.ast.CompilationUnit;
import com.home.servicegenerator.plugin.processing.configuration.stages.Stage;
import com.home.servicegenerator.plugin.processing.engine.generator.DefaultGenerator;
import com.home.servicegenerator.plugin.processing.events.ProcessingEvent;
import com.home.servicegenerator.plugin.processing.processor.ProcessingUnit;
import com.home.servicegenerator.plugin.processing.registry.ProjectUnitsRegistry;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

public class ProcessingListener<S, E> extends StateMachineListenerAdapter<S, E> {
    /*private void generate(Stage stage) {
        try {
            var unit = (CompilationUnit) DefaultGenerator.builder()
                    .processingSchema(stage.getSchema())
                    .build()
                    .generate(
                            ProjectUnitsRegistry
                                    .getOrDefault(
                                            stage.getSourceLocation().toString(),
                                            () -> new ProcessingUnit(
                                                    stage.getSourceLocation().toString(),
                                                    new CompilationUnit()
                                                            .setStorage(stage.getSourceLocation())))
                                    .getCompilationUnit(),
                            stage.getContext());
            ProjectUnitsRegistry.register(ProcessingUnit.convert(unit));
        } catch (MojoFailureException mojoFailureException) {
            ctx.getStateMachine().setStateMachineError(mojoFailureException);
        }
    };

    @Override
    public void stateExited(State<S, E> state) {
        super.stateExited(state);
        state.
        generationAction.execute();
    }*/
}
