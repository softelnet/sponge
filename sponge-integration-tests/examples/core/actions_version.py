"""
Sponge Knowledge Base
Action version
"""

class VersionedAction(Action):
    def onConfigure(self):
        self.withVersion(12)
    def onCall(self):
        return None

class NonVersionedAction(Action):
    def onCall(self):
        return None