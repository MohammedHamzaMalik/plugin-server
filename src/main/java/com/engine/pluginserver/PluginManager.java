package com.engine.pluginserver;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PluginManager {

    // We use a ConcurrentHashMap because a web server can have multiple
    // requests hitting it at the exact same time. This prevents crashing.
    private final Map<String, TaskPlugin> activePlugins = new ConcurrentHashMap<>();

    // Method to add a newly loaded plugin to our map
    public void registerPlugin(TaskPlugin plugin) {
        activePlugins.put(plugin.getName(), plugin);
        System.out.println("Successfully registered new plugin: " + plugin.getName());
    }

    // Method to get a specific plugin by its name
    public TaskPlugin getPlugin(String name) {
        return activePlugins.get(name);
    }

    // Method to see all plugins currently loaded in memory
    public Map<String, TaskPlugin> getAllPlugins() {
        return activePlugins;
    }
}