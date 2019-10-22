# Sponge Knowledge base
# Using filter builders

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

def onLoad
    $sponge.enable(FilterBuilder.new("ColorFilter").withEvent("e1").withOnAccept {|filter, event|
        $sponge.logger.debug("Received event {}", event)
        color = event.get("color", nil)
        if (color.nil? || color != "blue")
            $sponge.logger.debug("rejected")
            false
        else
            $sponge.logger.debug("accepted")
            true
        end
    })
end

def onStartup
    $sponge.event("e1").send()
    $sponge.event("e1").set("color", "red").send()
    $sponge.event("e1").set("color", "blue").send()
end

