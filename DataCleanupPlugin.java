import com.engine.pluginserver.TaskPlugin;

public class DataCleanupPlugin implements TaskPlugin {
    
    @Override
    public String getName() {
        return "DataCleanup";
    }

    @Override
    public void execute() {
        System.out.println("=========================================");
        System.out.println("[DataCleanupPlugin] EXECUTING...");
        System.out.println("[DataCleanupPlugin] Scrubbing temporary database tables...");
        System.out.println("[DataCleanupPlugin] Cleanup complete!");
        System.out.println("=========================================");
    }
}