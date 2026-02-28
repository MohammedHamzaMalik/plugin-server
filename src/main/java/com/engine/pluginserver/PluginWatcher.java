package com.engine.pluginserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.io.File;

@Component
public class PluginWatcher implements CommandLineRunner {

    private final PluginManager pluginManager;
    private final String PLUGIN_DIR = "plugins";

    // Spring automatically injects the PluginManager we made in Step 3
    public PluginWatcher(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public void run(String... args) {
        // Spin up a new background thread so we don't freeze the Spring web server
        new Thread(this::startWatching).start();
    }

    private void startWatching() {
        try {
            // 1. Create a "plugins" folder in your project root if it doesn't exist
            Path pluginPath = Paths.get(PLUGIN_DIR);
            if (!Files.exists(pluginPath)) {
                Files.createDirectories(pluginPath);
                System.out.println("Created plugins directory at: " + pluginPath.toAbsolutePath());
            }

            // 2. Set up the WatchService (The Security Guard)
            WatchService watchService = FileSystems.getDefault().newWatchService();
            pluginPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            System.out.println("Started watching for new plugins in: " + pluginPath.toAbsolutePath());

            // 3. The Infinite Loop
            while (true) {
                // .take() puts the thread to sleep until a file is dropped in the folder
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    Path fileName = (Path) event.context();

                    if (fileName.toString().endsWith(".class")) {
                        System.out.println("\nNew plugin file detected: " + fileName);
                        // Trigger the custom ClassLoader (we will build this next)
                        loadAndRegisterPlugin(fileName.toString());
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAndRegisterPlugin(String fileName) {
        try {
            // Strip ".class" from the filename to get the raw class name
            String className = fileName.replace(".class", "");

            // 1. Initialize our Custom ClassLoader.
            // Thread.currentThread().getContextClassLoader() grabs Spring's ClassLoader to act as the parent!
            PluginClassLoader loader = new PluginClassLoader(PLUGIN_DIR, Thread.currentThread().getContextClassLoader());

            // 2. Load the class into the JVM
            Class<?> pluginClass = loader.loadClass(className);

            // 3. Check if the dropped file actually implements our TaskPlugin interface
            if (TaskPlugin.class.isAssignableFrom(pluginClass)) {

                // 4. Instantiate it and register it with our PluginManager
                TaskPlugin pluginInstance = (TaskPlugin) pluginClass.getDeclaredConstructor().newInstance();
                pluginManager.registerPlugin(pluginInstance);

            } else {
                System.out.println("Ignored: " + className + " does not implement TaskPlugin");
            }
        } catch (Exception e) {
            System.out.println("Failed to load plugin: " + e.getMessage());
        }
    }
}