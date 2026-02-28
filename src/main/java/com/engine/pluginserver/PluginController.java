package com.engine.pluginserver;

import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/plugins")
public class PluginController {

    private final PluginManager pluginManager;

    public PluginController(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    // Endpoint 1: See all currently loaded plugins
    @GetMapping
    public Set<String> getLoadedPlugins() {
        return pluginManager.getAllPlugins().keySet();
    }

    // Endpoint 2: Trigger a specific plugin by name
    @PostMapping("/{name}/run")
    public String runPlugin(@PathVariable String name) {
        TaskPlugin plugin = pluginManager.getPlugin(name);

        if (plugin != null) {
            plugin.execute();
            return "Successfully executed plugin: " + name;
        } else {
            return "Plugin not found: " + name;
        }
    }
}