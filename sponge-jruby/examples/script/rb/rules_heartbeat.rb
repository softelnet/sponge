# Sponge Knowledge base
# Heartbeat

java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    $hearbeatEventEntry = nil
    $EPS.setVariable("soundTheAlarm", AtomicBoolean.new(false))
end

class HeartbeatFilter < Filter
    def onConfigure
        self.event = "heartbeat"
    end
    def onInit
        @heartbeatCounter = 0
    end
    def onAccept(event)
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
    def onConfigure
        self.events = ["heartbeat h1", "heartbeat h2 :none"]
        self.addConditions("h2", lambda { |rule, event|
            return rule.firstEvent.get("source") == event.get("source")
        })
        self.duration = Duration.ofSeconds(2)
    end
    def onRun(event)
        $EPS.event("alarm").set("severity", 1).send()
    end
end

class AlarmTrigger < Trigger
    def onConfigure
        self.event = "alarm"
    end
    def onRun(event)
        puts "Sound the alarm!"
        $EPS.getVariable("soundTheAlarm").set(true)
    end
end

def onStartup
    $hearbeatEventEntry = $EPS.event("heartbeat").set("source", "Host1").sendAfter(100, 1000)
end

