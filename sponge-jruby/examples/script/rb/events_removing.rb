# Sponge Knowledge base
# Removing scheduled events

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    $eventEntry = nil
    $eventCounter = AtomicInteger.new(0)
    $sponge.setVariable("eventCounter", $eventCounter)
    $sponge.setVariable("allowNumber", 2)
end

class Trigger1 < Trigger
    def onConfigure
        self.event = "e1"
    end
    def onRun(event)
        	$eventCounter.incrementAndGet
        self.logger.debug("Received event {}, counter: {}", event.name, $eventCounter)
        if $eventCounter.get() > $sponge.getVariable("allowNumber")
        	self.logger.debug("This line should not be displayed!")
        end
    end
end

class Trigger2 < Trigger
    def onConfigure
        self.event = "e2"
    end
    def onRun(event)
        self.logger.debug("Removing entry")
        $sponge.removeEvent($eventEntry)
    end
end

def onStartup
    start = 500
    interval = 1000
    $eventEntry = $sponge.event("e1").sendAfter(start, interval)
    $sponge.event("e2").sendAfter(interval * $sponge.getVariable("allowNumber"))
end

