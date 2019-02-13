"""
Sponge Knowledge base
Processors enhanced metadata
"""

class EdvancedMetaAction(Action):
    def onConfigure(self):
        self.withFeatures({"isVisibleMethod":"isVisible"})
    def onCall(self, text):
        return text.upper()
    def isVisible(self, context):
        return context == "day"
