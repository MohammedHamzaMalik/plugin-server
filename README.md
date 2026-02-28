# Dynamic JVM Plugin Engine

A hot-swappable backend architecture built with Spring Boot that dynamically loads, compiles, and executes external Java code at runtime without restarting the server.



## 🎯 Overview
Standard Java applications require all `.class` files to be present on the classpath at startup. This project demonstrates a deeper manipulation of the Java Virtual Machine (JVM) by building a **Custom ClassLoader Engine**.

It utilizes a Spring Boot core server running a continuous background thread. When new compiled code (plugins) is dropped into a monitored directory, the server dynamically allocates memory, resolves the bytecode, injects it into the Spring context hierarchy, and executes it via REST APIs—achieving zero-downtime extensibility.

## 🛠️ Tech Stack & Core Mechanisms
* **Framework:** Spring Boot 3.x / Java 21
* **Memory Management:** Custom `java.lang.ClassLoader`
* **File System Monitoring:** Java NIO `WatchService`
* **Dynamic Execution:** Java Reflection API
* **Concurrency:** `CommandLineRunner`, Background Threading, `ConcurrentHashMap`

## 🧠 Architectural Highlights
1. **ClassLoader Isolation:** Overrides the standard JVM delegation model. The custom ClassLoader is designed to load raw byte arrays (`byte[]`) from external directories while maintaining the Spring Boot Application ClassLoader as its parent. This prevents `ClassCastExceptions` when mapping external code to internal interfaces.
2. **Non-Blocking I/O:** The file watcher runs on an isolated background thread to prevent blocking the primary Tomcat server thread.
3. **Thread-Safe Registry:** Utilizes `ConcurrentHashMap` to safely register and serve newly loaded plugins in a highly concurrent web environment.

## 🚀 How to Run and Test the Hot-Swap

### 1. Start the Core Server
Clone the repository and start the Spring Boot application:
```bash
./mvnw spring-boot:run
```

The server will boot up and create a ./plugins directory in the root folder, actively watching it for changes.

### 2. Verify Empty State
Check the currently loaded plugins via the REST API:
```bash
curl http://localhost:8080/api/plugins
# Expected Output: []
```

### 3. Write and Compile an External Plugin
Create a new file called DataCleanupPlugin.java completely outside the project structure:
```java
import com.engine.pluginserver.TaskPlugin;

public class DataCleanupPlugin implements TaskPlugin {
    @Override
    public String getName() { return "DataCleanup"; }

    @Override
    public void execute() {
        System.out.println("Executing dynamic cleanup routine...");
    }
}
```

Compile it against the core server's classes:

```bash
javac -cp path/to/plugin-server/target/classes DataCleanupPlugin.java
```

### 4. Trigger the Hot-Swap
Drag and drop the newly generated DataCleanupPlugin.class file into the server's ./plugins folder. Watch the Spring Boot console output:
```
New plugin file detected: plugins/DataCleanupPlugin.class
Successfully registered new plugin: DataCleanup
```

### 5. Execute via API
Trigger the newly loaded code:
```bash
curl -X POST http://localhost:8080/api/plugins/DataCleanup/run
# Expected Output: Successfully executed plugin: DataCleanup
```