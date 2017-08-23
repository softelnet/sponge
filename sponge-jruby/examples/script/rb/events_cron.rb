# Sponge Knowledge base
# Generating events by Cron

java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    $scheduleEntry = nil
    $eventCounter = AtomicInteger.new(0)
    $EPS.setVariable("eventCounter", $eventCounter)
end

class CronTrigger < Trigger
    def onConfigure
        self.event = "cronEvent"
    end
    def onRun(event)
        $eventCounter.incrementAndGet
        self.logger.debug("Received event {}: {}", $eventCounter.get(), event.name)
        if $eventCounter.get() == 2
            self.logger.debug("removing scheduled event")
            $EPS.removeEvent($scheduleEntry)
        end
    end
end

def onStartup
    # send event every 2 seconds
    $scheduleEntry = $EPS.event("cronEvent").sendAt("0/2 * * * * ?")
end

