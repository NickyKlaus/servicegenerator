package com.home.servicegenerator.plugin.processing.processor.listeners;

import org.springframework.statemachine.listener.StateMachineListenerAdapter;

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
