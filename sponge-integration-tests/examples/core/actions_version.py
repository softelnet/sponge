"""
Sponge Knowledge base
Action version
"""

class VersionedAction(Action):
    def onConfigure(self):
        self.version = 12
    def onCall(self):
        return None

class NonVersionedAction(Action):
    def onCall(self):
        return None