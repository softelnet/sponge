"""
Sponge Knowledge Base
Test - KB from an archive file
"""

class Action2FromArchive(Action):
    def onCall(self, arg):
        return arg.lower()

