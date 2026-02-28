package com.engine.pluginserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PluginClassLoader extends ClassLoader {
    private final String pluginDirectory;

    // CRITICAL: Notice the 'ClassLoader parent' in the constructor
    public PluginClassLoader(String pluginDirectory, ClassLoader parent) {
        super(parent); // We pass Spring's ClassLoader up the chain
        this.pluginDirectory = pluginDirectory;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classBytes = loadClassFromFile(name);

        if (classBytes == null) {
            throw new ClassNotFoundException("Could not find " + name);
        }

        return defineClass(name, classBytes, 0, classBytes.length);
    }

    private byte[] loadClassFromFile(String className) {
        String filePath = pluginDirectory + File.separator + className + ".class";
        try (FileInputStream fis = new FileInputStream(filePath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            int data;
            while ((data = fis.read()) != -1) {
                baos.write(data);
            }
            return baos.toByteArray();

        } catch (IOException e) {
            return null;
        }
    }
}