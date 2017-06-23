/**
 * Sponge Knowledge base
 * Defining plugins in knowledge base.
 */

function onInit() {
    // Variables for assertions only
    EPS.setVariable("valueBefore", null);
    EPS.setVariable("valueAfter", null);
}

// Example plugin defined in the knowledge base.
var ScriptPlugin = Java.extend(Plugin, {
    configure: function(self, configuration) {
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
    init: function(self) {
        self.logger.debug("Initializing {}", self.name);
    },
    onStartup: function(self) {
        self.logger.debug("Starting up {}", self.name);
    }
});

var PluginTrigger = Java.extend(Trigger, {
    configure: function(self) {
        self.eventName = "e1";
    },
    run: function(self, event) {
    	valueBefore = scriptPlugin.target.getStoredValue();
    	self.logger.info("Plugin stored value: {}", valueBefore);
    	EPS.setVariable("valueBefore", valueBefore);
    	scriptPlugin.target.setStoredValue(event.get("value"));
    	valueAfter = scriptPlugin.target.getStoredValue();
        self.logger.info("New stored value: {}", valueAfter);
        EPS.setVariable("valueAfter", valueAfter);
    }
});

function onStartup() {
    EPS.event("e1").set("value", "Value B").send();
}
