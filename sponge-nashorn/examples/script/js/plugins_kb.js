/**
 * Sponge Knowledge base
 * Defining plugins in knowledge base.
 */

function onInit() {
    // Variables for assertions only
    sponge.setVariable("valueBefore", null);
    sponge.setVariable("valueAfter", null);
}

// Example plugin defined in the knowledge base.
var ScriptPlugin = Java.extend(Plugin, {
    onConfigure: function(self, configuration) {
        self.logger.debug("configuration {}", self.name);
        self.target = new function() {
            this.storedValue = configuration.getString("storedValue", "default");
            this.getStoredValue = function() {
                return this.storedValue;
            };
            this.setStoredValue = function(value) {
                this.storedValue = value;
            };
        };
    },
    onInit: function(self) {
        self.logger.debug("Initializing {}", self.name);
    },
    onStartup: function(self) {
        self.logger.debug("Starting up {}", self.name);
    }
});

var PluginTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.withEvent("e1");
    },
    onRun: function(self, event) {
    	valueBefore = scriptPlugin.target.getStoredValue();
    	self.logger.info("Plugin stored value: {}", valueBefore);
    	sponge.setVariable("valueBefore", valueBefore);
    	scriptPlugin.target.setStoredValue(event.get("value"));
    	valueAfter = scriptPlugin.target.getStoredValue();
        self.logger.info("New stored value: {}", valueAfter);
        sponge.setVariable("valueAfter", valueAfter);
    }
});

function onStartup() {
    sponge.event("e1").set("value", "Value B").send();
}
