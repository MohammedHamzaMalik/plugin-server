package com.engine.pluginserver;

public interface TaskPlugin {
    // This tells us what the plugin is called
    String getName();

    // This is the actual work the plugin will do when triggered
    void execute();
}