# Sponge Knowledge Base
# Using filters

java_import java.util.Collections
java_import java.util.HashMap
java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $eventCounter = Collections.synchronizedMap(HashMap.new)
    $eventCounter.put("blue", AtomicInteger.new(0))
    $eventCounter.put("red", AtomicInteger.new(0))
    $sponge.setVariable("eventCounter", $eventCounter)
end

class ColorFilter < Filter
    def onConfigure
        self.withEvent("e1")
    end
    def onAccept(event)
        self.logger.debug("Received event {}", event)
        color = event.get("color", nil)
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
        self.withEvent("e1")
    end
    def onRun(event)
        self.logger.debug("Received event {}", event)
        puts event.get("color")
        $eventCounter.get(event.get("color")).incrementAndGet()
    end
end

def onStartup
    $sponge.event("e1").send()
    $sponge.event("e1").set("color", "red").send()
    $sponge.event("e1").set("color", "blue").send()
end

