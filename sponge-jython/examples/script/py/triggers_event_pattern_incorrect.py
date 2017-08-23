"""
Sponge Knowledge base
Triggers - Incorrect event pattern
"""

class TriggerAPattern(Trigger):
    def onConfigure(self):
        self.event = "a.**"
    def onRun(self, event):
        pass
