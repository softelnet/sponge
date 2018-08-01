# Sponge Knowledge base
# Loading knowledge bases

class Trigger1 < Trigger
    def onConfigure
        self.displayName = "Trigger1, file2"
        self.event = "e1"
    end
    def onRun(event)
        #self.logger.debug("file2: Received event {}", event)
        $eventCounter.get(self.displayName).incrementAndGet()
    end
end

class Trigger2 < Trigger
    def onConfigure
        self.displayName = "Trigger2, file2"
        self.event = "e2"
    end
    def onRun(event)
        #self.logger.debug("file2: Received event {}", event)
        $eventCounter.get(self.displayName).incrementAndGet()
    end
end

# Execute immediately while loading
$sponge.enableAll(Trigger1, Trigger2)

def onShutdown
    $sponge.logger.debug("onShutdown, file2")
end
