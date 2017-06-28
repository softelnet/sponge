# Sponge Knowledge base
# Heartbeat

java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    $hearbeatEventEntry = nil
    $EPS.setVariable("soundTheAlarm", AtomicBoolean.new(false))
end

class HeartbeatFilter < Filter
    def configure
        self.eventName = "heartbeat"
    end
    def init
        @heartbeatCounter = 0
    end
    def accepts(event)
        @heartbeatCounter += 1
        if @heartbeatCounter > 2
            $EPS.removeEvent($hearbeatEventEntry)
            return false
        else
            return true
        end
    end
end

# Sounds the alarm when heartbeat event stops occurring at most every 2 seconds.
class HeartbeatRule < Rule
    def configure
        self.events = ["heartbeat h1", "heartbeat h2 :none"]
        self.duration = Duration.ofSeconds(2)
    end
    def run(event)
        $EPS.event("alarm").set("severity", 1).send()
    end
end

class AlarmTrigger < Trigger
    def configure
        self.eventName = "alarm"
    end
    def run(event)
        puts "Sound the alarm!"
        $EPS.getVariable("soundTheAlarm").set(true)
    end
end

def onStartup
    $hearbeatEventEntry = $EPS.event("heartbeat").sendAfter(100, 1000)
end

