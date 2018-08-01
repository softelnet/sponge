# Sponge Knowledge base
# Global variable access

java_import java.util.concurrent.atomic.AtomicInteger
java_import java.util.concurrent.atomic.AtomicBoolean

def onInit
    # Variables for assertions only
    $sponge.setVariable("test1", AtomicBoolean.new(false))
    $sponge.setVariable("test2", AtomicBoolean.new(false))
    $sponge.setVariable("stopped", AtomicBoolean.new(false))
end

class E1 < Trigger
    def onConfigure
        self.event = "e"
    end
    def onRun(event)
        $sponge.getVariable("test1").set(true)
        $sponge.getVariable("test2").set(true)
    end
end

class E2 < Trigger
    def onConfigure
        self.event = "e"
    end
    def onRun(event)
        $sponge.getVariable("test1").set(false)
        $sponge.getVariable("test2").set(false)
    end
end

class E3 < Trigger
    def onConfigure
        self.event = "e"
    end
    def onRun(event)
        $sponge.getVariable("test1").set(true)
        $sponge.getVariable("test2").set(true)
    end
end

class E4 < Trigger
    def onConfigure
        self.event = "e"
    end
    def onRun(event)
        $sponge.getVariable("test1").set(false)
        $sponge.getVariable("test2").set(false)
    end
end

class Stop < Trigger
    def onConfigure
        self.event = "stop"
    end
    def onRun(event)
        $sponge.getVariable("stopped").set(true)
    end
end

def onStartup
    $sponge.event("e").sendAfter(0, 1)
    $sponge.event("stop").sendAfter(Duration.ofMinutes(15))
end
