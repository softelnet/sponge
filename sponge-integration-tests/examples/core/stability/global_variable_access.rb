# Sponge Knowledge base
# Global variable access

java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    # Variables for assertions only
    $EPS.setVariable("test1", AtomicBoolean.new(false))
    $EPS.setVariable("test2", AtomicBoolean.new(false))
    $EPS.setVariable("stopped", AtomicBoolean.new(false))
end

class E1 < Trigger
    def onConfigure
        self.event = "e"
    end
    def onRun(event)
        $EPS.getVariable("test1").set(true)
        $EPS.getVariable("test2").set(true)
    end
end

class E2 < Trigger
    def onConfigure
        self.event = "e"
    end
    def onRun(event)
        $EPS.getVariable("test1").set(false)
        $EPS.getVariable("test2").set(false)
    end
end

class E3 < Trigger
    def onConfigure
        self.event = "e"
    end
    def onRun(event)
        $EPS.getVariable("test1").set(true)
        $EPS.getVariable("test2").set(true)
    end
end

class E4 < Trigger
    def onConfigure
        self.event = "e"
    end
    def onRun(event)
        $EPS.getVariable("test1").set(false)
        $EPS.getVariable("test2").set(false)
    end
end

class Stop < Trigger
    def onConfigure
        self.event = "stop"
    end
    def onRun(event)
        $EPS.getVariable("stopped").set(true)
    end
end

def onStartup
    $EPS.event("e").sendAfter(0, 1)
    $EPS.event("stop").sendAfter(Duration.ofMinutes(15))
end
