# Sponge Knowledge base
# Heartbeat 2

java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    $EPS.setVariable("soundTheAlarm", AtomicBoolean.new(false))
end

# Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule < Rule
    def configure
        self.events = ["heartbeat h1", "heartbeat h2 :none"]
        self.duration = Duration.ofSeconds(2)
    end
    def run(event)
        self.logger.info("Sound the alarm!")
        $EPS.getVariable("soundTheAlarm").set(true)
    end
end

def onStartup
    $EPS.event("heartbeat").send()
    $EPS.event("heartbeat").sendAfter(1000)
end
