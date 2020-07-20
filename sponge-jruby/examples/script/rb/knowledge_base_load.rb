# Sponge Knowledge Base
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
    $sponge.setVariable("eventCounter", $eventCounter)
end

class Trigger1 < Trigger
    def onConfigure
        self.withLabel("Trigger1, file1").withEvent("e1")
    end
    def onRun(event)
        #self.logger.debug("file1: Received event {}", event)
        $eventCounter.get(self.meta.label).incrementAndGet()
    end
end


class Trigger2 < Trigger
    def onConfigure
        self.withLabel("Trigger2, file1").withEvent("e2")
    end
    def onRun(event)
        #self.logger.debug("file1: Received event {}", event)
        $eventCounter.get(self.meta.label).incrementAndGet()
    end
end

class LoadKbFile < Trigger
    def onConfigure
        self.withEvent("loadKbFile")
    end
    def onRun(event)
        kbFile = event.get("kbFile")
        $sponge.kb.load(kbFile)
        self.logger.info("File {} loaded", kbFile)
    end
end

def onLoad
    $sponge.enableAll(Trigger1, Trigger2, LoadKbFile)
end

def onStartup
    $sponge.logger.debug("onStartup, file1: {}, triggers: {}", $sponge.info, $sponge.engine.triggers)
    $sponge.event("e1").sendAfter(0, 500)
    $sponge.event("e2").sendAfter(0, 500)

    $sponge.event("loadKbFile").set("kbFile", "examples/script/rb/knowledge_base_load2.rb").sendAfter(2000)
    $sponge.event("loadKbFile").set("kbFile", "examples/script/rb/knowledge_base_load3.rb").sendAfter(5000)
end

def onShutdown
    $sponge.logger.debug("onShutdown, file1")
end

