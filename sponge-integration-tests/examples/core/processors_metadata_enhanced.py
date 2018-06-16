"""
Sponge Knowledge base
Processors enhanced metadata
"""

class EdvancedMetaAction(Action):
    def onConfigure(self):
        self.meta = {"isVisibleMethod":"isVisible"}
    def onCall(self, args):
        return args[0].upper()
    def isVisible(self, context):
        return context == "day"
