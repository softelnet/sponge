# Sponge Knowledge base
# Loading knowledge bases
# Note that auto-enable is turned off in the configuration.

java_import java.util.Collections
java_import java.util.HashMap
java_import java.util.concurrent.atomic.AtomicInteger

def onInit
    # Variables for assertions only
    $eventCounter = Collections.synchronizedMap(HashMap.new)
    $eventCounter.put("Trigger1, file1", AtomicInteger.new(0))
    $eventCounter.put("Trigger2, file1", AtomicInteger.new(0))
    $eventCounter.put("Trigger1, file2", AtomicInteger.new(0))
    $eventCounter.put("Trigger2, file2", AtomicInteger.new(0))
    $eventCounter.put("Trigger1, file3", AtomicInteger.new(0))
    $eventCounter.put("Trigger3, file3", AtomicInteger.new(0))
    $EPS.setVariable("eventCounter", $eventCounter)
end

class Trigger1 < Trigger
    def onConfigure
        self.displayName = "Trigger1, file1"
        self.event = "e1"
    end
    def onRun(event)
        #self.logger.debug("file1: Received event {}", event)
        $eventCounter.get(self.displayName).incrementAndGet()
    end
end


class Trigger2 < Trigger
    def onConfigure
        self.displayName = "Trigger2, file1"
        self.event = "e2"
    end
    def onRun(event)
        #self.logger.debug("file1: Received event {}", event)
        $eventCounter.get(self.displayName).incrementAndGet()
    end
end

class LoadKbFile < Trigger
    def onConfigure
        self.event = "loadKbFile"
    end
    def onRun(event)
        kbFile = event.get("kbFile")
        $EPS.kb.load(kbFile)
        self.logger.info("File {} loaded", kbFile)
    end
end

def onLoad
    $EPS.enableAll(Trigger1, Trigger2, LoadKbFile)
end

def onStartup
    $EPS.logger.debug("onStartup, file1: {}, triggers: {}", $EPS.info, $EPS.engine.triggers)
    $EPS.event("e1").sendAfter(0, 500)
    $EPS.event("e2").sendAfter(0, 500)

    $EPS.event("loadKbFile").set("kbFile", "examples/script/rb/knowledge_base_load2.rb").sendAfter(2000)
    $EPS.event("loadKbFile").set("kbFile", "examples/script/rb/knowledge_base_load3.rb").sendAfter(5000)
end

def onShutdown
    $EPS.logger.debug("onShutdown, file1")
end

