/**
 * Sponge Knowledge Base
 * Defining plugins in knowledge base.
 */

void onInit() {
    // Variables for assertions only
    sponge.setVariable("valueBefore", null)
    sponge.setVariable("valueAfter", null)
}

// Example plugin defined in the knowledge base.
class ScriptPlugin extends Plugin {
    String storedValue
    void onConfigure(Configuration configuration) {
        this.storedValue = configuration.getString("storedValue", "default")
    }
    void onInit() {
        this.logger.debug("Initializing {}", this.name)
    }
    void onStartup() {
        this.logger.debug("Starting up {}", this.name)
    }
}


class PluginTrigger extends Trigger {
    void onConfigure() {
        this.withEvent("e1")
    }
    void onRun(Event event) {
        def scriptPlugin = sponge.getPlugin("scriptPlugin")
        def valueBefore = scriptPlugin.getStoredValue()
    	    sponge.setVariable("valueBefore", valueBefore)
       	this.logger.info("Plugin stored value: {}", valueBefore)
        scriptPlugin.setStoredValue(event.get("value"))
        def valueAfter = scriptPlugin.getStoredValue()
        sponge.setVariable("valueAfter", valueAfter)
        this.logger.info("New stored value: {}", valueAfter)
    }
}

void onStartup() {
    sponge.event("e1").set("value", "Value B").send()
}
