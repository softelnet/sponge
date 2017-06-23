# Sponge Knowledge base
# Concurrency

java_import java.util.concurrent.atomic.AtomicReference
java_import java.util.concurrent.TimeUnit

def onInit
    # Variables for assertions only
    $EPS.setVariable("value", AtomicReference.new(""))
end

class A < Trigger
    def configure
        self.eventName = "a"
    end
    def run(event)
        TimeUnit::SECONDS::sleep(1)
        $EPS.getVariable("value").set("A1")
        TimeUnit::SECONDS::sleep(3)
        $EPS.getVariable("value").set("A2")
    end
end

class B < Trigger
    def configure
        self.eventName = "b"
    end
    def run(event)
        TimeUnit::SECONDS::sleep(2)
        $EPS.getVariable("value").set("B1")
        TimeUnit::SECONDS::sleep(4)
        $EPS.getVariable("value").set("B2")
    end
end

class C < Trigger
    def configure
        self.eventName = "c"
    end
    def run(event)
        TimeUnit::SECONDS::sleep(8)
        $EPS.getVariable("value").set("C1")
        TimeUnit::SECONDS::sleep(1)
        $EPS.getVariable("value").set("C2")
    end
end

def onStartup
    $EPS.event("a").send()
    $EPS.event("b").send()
    $EPS.event("c").send()
end
