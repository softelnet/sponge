# Sponge Knowledge base
# Using filters for deduplication of events.

java_import java.util.Collections
java_import java.util.HashMap
java_import java.util.concurrent.atomic.AtomicInteger

java_import org.openksavi.sponge.core.library.Deduplication

def onInit
    # Variables for assertions only
    $eventCounter = Collections.synchronizedMap(HashMap.new)
    $eventCounter.put("e1-blue", AtomicInteger.new(0))
    $eventCounter.put("e1-red", AtomicInteger.new(0))
    $eventCounter.put("e2-blue", AtomicInteger.new(0))
    $eventCounter.put("e2-red", AtomicInteger.new(0))
    $EPS.setVariable("eventCounter", $eventCounter)
end

class ColorDeduplicationFilter < Filter
    def configure
        self.event = "e1"
    end
    def init
        @deduplication = Deduplication.new("color")
        @deduplication.cacheBuilder.maximumSize(1000)
    end
    def accepts(event)
        return @deduplication.accepts(event)
    end
end

class ColorTrigger < Trigger
    def configure
        self.events = ["e1", "e2"]
    end
    def run(event)
        self.logger.debug("Received event {}", event)
        $eventCounter.get(event.name + "-" + event.get("color")).incrementAndGet()
    end
end

def onStartup
    $EPS.event("e1").set("color", "red").send()
    $EPS.event("e1").set("color", "blue").send()
    $EPS.event("e2").set("color", "red").send()
    $EPS.event("e2").set("color", "blue").send()

    $EPS.event("e1").set("color", "red").send()
    $EPS.event("e1").set("color", "blue").send()
    $EPS.event("e2").set("color", "red").send()
    $EPS.event("e2").set("color", "blue").send()
end

