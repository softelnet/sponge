# Sponge Knowledge Base
# Concurrency

java_import java.util.concurrent.atomic.AtomicReference
java_import java.util.concurrent.TimeUnit

def onInit
    # Variables for assertions only
    $sponge.setVariable("value", AtomicReference.new(""))
end

class A < Trigger
    def onConfigure
        self.withEvent("a")
    end
    def onRun(event)
        TimeUnit::SECONDS::sleep(1)
        $sponge.getVariable("value").set("A1")
        TimeUnit::SECONDS::sleep(3)
        $sponge.getVariable("value").set("A2")
    end
end

class B < Trigger
    def onConfigure
        self.withEvent("b")
    end
    def onRun(event)
        TimeUnit::SECONDS::sleep(2)
        $sponge.getVariable("value").set("B1")
        TimeUnit::SECONDS::sleep(4)
        $sponge.getVariable("value").set("B2")
    end
end

class C < Trigger
    def onConfigure
        self.withEvent("c")
    end
    def onRun(event)
        TimeUnit::SECONDS::sleep(8)
        $sponge.getVariable("value").set("C1")
        TimeUnit::SECONDS::sleep(1)
        $sponge.getVariable("value").set("C2")
    end
end

def onStartup
    $sponge.event("a").send()
    $sponge.event("b").send()
    $sponge.event("c").send()
end
