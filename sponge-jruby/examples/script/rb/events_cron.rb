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
        self.logger.debug("Received event {}: {}", $eventCounter.get() + 1, event.name)
        if $eventCounter.get() + 1 == 2
            self.logger.debug("removing scheduled event")
            $EPS.removeEvent($scheduleEntry)
        end
        $eventCounter.incrementAndGet
    end
end

def onStartup
    # send event every second
    $scheduleEntry = $EPS.event("cronEvent").sendAt("0/1 * * * * ?")
end

