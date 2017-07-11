# Sponge Knowledge base
# Using filters

java_import java.util.Collections
java_import java.util.HashMap
java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $eventCounter = Collections.synchronizedMap(HashMap.new)
    $eventCounter.put("blue", AtomicInteger.new(0))
    $eventCounter.put("red", AtomicInteger.new(0))
    $EPS.setVariable("eventCounter", $eventCounter)
end

class ColorFilter < Filter
    def configure
        self.event = "e1"
    end
    def accepts(event)
        self.logger.debug("Received event {}", event)
        color = event.get("color")
        if (color.nil? || color != "blue")
            self.logger.debug("rejected")
            return false
        else
            self.logger.debug("accepted")
            return true
        end
    end
end

class ColorTrigger < Trigger
    def configure
        self.event = "e1"
    end
    def run(event)
        self.logger.debug("Received event {}", event)
        puts event.get("color")
        $eventCounter.get(event.get("color")).incrementAndGet()
    end
end

def onStartup
    $EPS.event("e1").sendAfter(100)
    $EPS.event("e1").set("color", "red").sendAfter(100)
    $EPS.event("e1").set("color", "blue").sendAfter(100)
end

