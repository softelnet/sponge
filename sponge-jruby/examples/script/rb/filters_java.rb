# Sponge Knowledge base
# Using java filters 

java_import java.util.Collections
java_import java.util.HashMap
java_import java.util.concurrent.atomic.AtomicInteger
java_import org.openksavi.sponge.examples.ShapeFilter

def onInit
    # Variables for assertions only
    $eventCounter = Collections.synchronizedMap(HashMap.new)
    $eventCounter.put("e1", AtomicInteger.new(0))
    $eventCounter.put("e2", AtomicInteger.new(0))
    $eventCounter.put("e3", AtomicInteger.new(0))
    $sponge.setVariable("eventCounter", $eventCounter)
end

class FilterTrigger < Trigger
    def onConfigure
        self.setEvents("e1", "e2", "e3")
    end
    def onRun(event)
        self.logger.debug("Processing trigger for event {}", event)
        $eventCounter.get(event.name).incrementAndGet()
    end
end

def onLoad
    $sponge.enableJava(ShapeFilter)
end

def onStartup
    $sponge.event("e1").sendAfter(100, 100)
    $sponge.event("e2").set("shape", "square").sendAfter(200, 100)
    $sponge.event("e3").set("shape", "circle").sendAfter(300, 100)
end

