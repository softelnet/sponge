# Sponge Knowledge base
# Triggers - Incorrect event pattern

class TriggerAPattern < Trigger
    def onConfigure
        self.event = "a.**"
    end
    def onRun(event)
    end
end
