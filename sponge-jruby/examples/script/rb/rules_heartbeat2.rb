# Sponge Knowledge Base
# Heartbeat 2

java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    $sponge.setVariable("soundTheAlarm", AtomicBoolean.new(false))
end

# Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule < Rule
    def onConfigure
        self.withEvents(["heartbeat h1", "heartbeat h2 :none"]).withDuration( Duration.ofSeconds(2))
    end
    def onRun(event)
        self.logger.info("Sound the alarm!")
        $sponge.getVariable("soundTheAlarm").set(true)
    end
end

def onStartup
    $sponge.event("heartbeat").send()
    $sponge.event("heartbeat").sendAfter(1000)
end
