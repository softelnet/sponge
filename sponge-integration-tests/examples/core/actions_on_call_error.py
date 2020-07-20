"""
Sponge Knowledge Base
Action onCall error
"""

class ErrorCauseAction(Action):
    def onCall(self):
        return error_here

class TestAction(Action):
    def onCall(self):
        return sponge.call("ErrorCauseAction")

class DeepNestedTestAction(Action):
    def onCall(self):
        return sponge.call("TestAction")
