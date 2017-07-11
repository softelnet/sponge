/**
 * Sponge Knowledge base
 * Defining plugins in knowledge base.
 */

void onInit() {
    // Variables for assertions only
    EPS.setVariable("valueBefore", null)
    EPS.setVariable("valueAfter", null)
}

// Example plugin defined in the knowledge base.
class ScriptPlugin extends Plugin {
    String storedValue
    void configure(Configuration configuration) {
        this.storedValue = configuration.getString("storedValue", "default")
    }
    void init() {
        this.logger.debug("Initializing {}", this.name)
    }
    void onStartup() {
        this.logger.debug("Starting up {}", this.name)
    }
}


class PluginTrigger extends Trigger {
    void configure() {
        this.event = "e1"
    }
    void run(Event event) {
        def scriptPlugin = EPS.getPlugin("scriptPlugin")
        def valueBefore = scriptPlugin.getStoredValue()
    	EPS.setVariable("valueBefore", valueBefore)
    	this.logger.info("Plugin stored value: {}", valueBefore)
    	scriptPlugin.setStoredValue(event.get("value"))
        def valueAfter = scriptPlugin.getStoredValue()
        EPS.setVariable("valueAfter", valueAfter)
        this.logger.info("New stored value: {}", valueAfter)
    }
}

void onStartup() {
    EPS.event("e1").set("value", "Value B").send()
}
