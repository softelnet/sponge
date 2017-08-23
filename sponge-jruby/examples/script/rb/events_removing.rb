# Sponge Knowledge base
# Removing scheduled events

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    $eventEntry = nil
    $eventCounter = AtomicInteger.new(0)
    $EPS.setVariable("eventCounter", $eventCounter)
    $EPS.setVariable("allowNumber", 2)
end

class Trigger1 < Trigger
    def onConfigure
        self.event = "e1"
    end
    def onRun(event)
        	$eventCounter.incrementAndGet
        self.logger.debug("Received event {}, counter: {}", event.name, $eventCounter)
        if $eventCounter.get() > $EPS.getVariable("allowNumber")
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
        $EPS.removeEvent($eventEntry)
    end
end

def onStartup
    start = 500
    interval = 1000
    $eventEntry = $EPS.event("e1").sendAfter(start, interval)
    $EPS.event("e2").sendAfter(interval * $EPS.getVariable("allowNumber"))
end

