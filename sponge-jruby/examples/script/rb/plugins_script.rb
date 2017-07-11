# Sponge Knowledge base
# Defining plugins in knowledge base.

def onInit
    # Variables for assertions only
    $EPS.setVariable("valueBefore", nil)
    $EPS.setVariable("valueAfter", nil)
end

# Example plugin defined in the knowledge base.
class ScriptPlugin < Plugin
    def configure(configuration)
        @storedValue = configuration.getString("storedValue", "default")
    end
    def init
        self.logger.debug("Initializing {}", self.name)
    end
    def onStartup
        self.logger.debug("Starting up {}", self.name)
    end
    def getStoredValue
        return @storedValue
    end
    def setStoredValue(value)
        @storedValue = value
    end
end


class PluginTrigger < Trigger
    def configure
        self.event = "e1"
    end
    def run(event)
    	valueBefore = $scriptPlugin.getStoredValue()
    	self.logger.info("Plugin stored value: {}", valueBefore)
        $EPS.setVariable("valueBefore", valueBefore)

    	$scriptPlugin.setStoredValue(event.get("value"))
        valueAfter = $scriptPlugin.getStoredValue()
        self.logger.info("New stored value: {}", valueAfter)
        $EPS.setVariable("valueAfter", valueAfter)
    end
end

def onStartup
    $EPS.event("e1").set("value", "Value B").send()
end
