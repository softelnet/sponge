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
    def onConfigure
        self.event = "e1"
    end
    def onAccept(event)
        self.logger.debug("Received event {}", event)
        color = event.getOrDefault("color", nil)
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
    def onConfigure
        self.event = "e1"
    end
    def onRun(event)
        self.logger.debug("Received event {}", event)
        puts event.get("color")
        $eventCounter.get(event.get("color")).incrementAndGet()
    end
end

def onStartup
    $EPS.event("e1").send()
    $EPS.event("e1").set("color", "red").send()
    $EPS.event("e1").set("color", "blue").send()
end

