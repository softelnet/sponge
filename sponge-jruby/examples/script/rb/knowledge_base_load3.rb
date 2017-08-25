# Sponge Knowledge base
# Loading knowledge bases

class Trigger1 < Trigger
    def onConfigure
        self.displayName = "Trigger1, file3"
        self.event = "e1"
    end
    def onRun(event)
        #self.logger.debug("file3: Received event {}", event)
        $eventCounter.get(self.displayName).incrementAndGet()
    end
end

class Trigger3 < Trigger
    def onConfigure
        self.displayName = "Trigger3, file3"
        self.event = "e3"
    end
    def onRun(event)
        #self.logger.debug("file3: Received event {}", event)
        $eventCounter.get(self.displayName).incrementAndGet()
    end
end

# Execute immediately while loading
$EPS.enableAll(Trigger1, Trigger3)

def onShutdown
    $EPS.logger.debug("onShutdown, file3")
end
