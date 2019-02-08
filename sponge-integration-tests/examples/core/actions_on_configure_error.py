"""
Sponge Knowledge base
Action onConfigure error
"""

class TestAction(Action):
    def onConfigure(self):
        self.withNoArgs().withResult(ResultMeta(StringType()).label_error("Test action"))
    def onCall(self):
        return None
