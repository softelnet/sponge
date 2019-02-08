# Sponge Knowledge base
# Generating events by Cron

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    $scheduleEntry = nil
    $eventCounter = AtomicInteger.new(0)
    $sponge.setVariable("eventCounter", $eventCounter)
end

class CronTrigger < Trigger
    def onConfigure
        self.withEvent("cronEvent")
    end
    def onRun(event)
        $eventCounter.incrementAndGet
        self.logger.debug("Received event {}: {}", $eventCounter.get(), event.name)
        if $eventCounter.get() == 2
            self.logger.debug("removing scheduled event")
            $sponge.removeEvent($scheduleEntry)
        end
    end
end

def onStartup
    # send event every 2 seconds
    $scheduleEntry = $sponge.event("cronEvent").sendAt("0/2 * * * * ?")
end

