# Sponge Knowledge Base
# Loading knowledge bases

class Trigger1 < Trigger
    def onConfigure
        self.withLabel("Trigger1, file2").withEvent("e1")
    end
    def onRun(event)
        #self.logger.debug("file2: Received event {}", event)
        $eventCounter.get(self.meta.label).incrementAndGet()
    end
end

class Trigger2 < Trigger
    def onConfigure
        self.withLabel("Trigger2, file2").withEvent("e2")
    end
    def onRun(event)
        #self.logger.debug("file2: Received event {}", event)
        $eventCounter.get(self.meta.label).incrementAndGet()
    end
end

# Execute immediately while loading
$sponge.enableAll(Trigger1, Trigger2)

def onShutdown
    $sponge.logger.debug("onShutdown, file2")
end
