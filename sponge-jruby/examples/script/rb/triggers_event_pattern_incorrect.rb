# Sponge Knowledge Base
# Triggers - Incorrect event pattern

class TriggerAPattern < Trigger
    def onConfigure
        self.withEvent("a.**")
    end
    def onRun(event)
    end
end
