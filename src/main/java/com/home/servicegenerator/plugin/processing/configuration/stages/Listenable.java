package com.home.servicegenerator.plugin.processing.configuration.stages;

import com.home.servicegenerator.plugin.processing.processor.listeners.ProcessingListener;

import java.util.List;

public interface Listenable {
    List<ProcessingListener> getProcessingListeners();
    void addProcessingListeners(ProcessingListener... listeners);
}
