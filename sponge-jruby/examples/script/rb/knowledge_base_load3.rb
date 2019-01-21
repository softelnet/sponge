# Sponge Knowledge base
# Loading knowledge bases

class Trigger1 < Trigger
    def onConfigure
        self.label = "Trigger1, file3"
        self.event = "e1"
    end
    def onRun(event)
        #self.logger.debug("file3: Received event {}", event)
        $eventCounter.get(self.label).incrementAndGet()
    end
end

class Trigger3 < Trigger
    def onConfigure
        self.label = "Trigger3, file3"
        self.event = "e3"
    end
    def onRun(event)
        #self.logger.debug("file3: Received event {}", event)
        $eventCounter.get(self.label).incrementAndGet()
    end
end

# Execute immediately while loading
$sponge.enableAll(Trigger1, Trigger3)

def onShutdown
    $sponge.logger.debug("onShutdown, file3")
end
