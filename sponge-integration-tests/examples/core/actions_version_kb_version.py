"""
Sponge Knowledge Base
Action version and knowledge base version
"""

def onLoad():
    sponge.kb.version = 2

class VersionedAction(Action):
    def onConfigure(self):
        self.withVersion(12)
    def onCall(self):
        return None

class NonVersionedAction(Action):
    def onCall(self):
        return None